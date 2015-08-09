package de.alaoli.games.minecraft.mods.limitedresources.util;

import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import de.alaoli.games.minecraft.mods.limitedresources.LimitedResources;
import de.alaoli.games.minecraft.mods.limitedresources.Log;
import de.alaoli.games.minecraft.mods.limitedresources.data.Coordinate;
import de.alaoli.games.minecraft.mods.limitedresources.data.LimitedBlock;
//import de.alaoli.games.minecraft.mods.limitedresources.data.LimitedBlockAt;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTUtil 
{
	/********************************************************************************
	 * Constants
	 ********************************************************************************/

	public static final String NBT_COORDINATE_DIMID	= "DIMID";
	public static final String NBT_COORDINATE_X		= "X";
	public static final String NBT_COORDINATE_Y		= "Y";
	public static final String NBT_COORDINATE_Z		= "Z";
	
	public static final String NBT_LIMITEDBLOCK_NAME		= "NAME";
	public static final String NBT_LIMITEDBLOCK_COORDINATES	= "COORDINATES";
	
	public static final String NBT_LIMITEDBLOCKOWNER_UUID		= "UUID";
	public static final String NBT_LIMITEDBLOCKOWNER_COORDINATE	= "COORDINATE";
	
	/********************************************************************************
	 * Methods - Data to NBT
	 ********************************************************************************/
	
	/**
	 * Puts Coordinate Data in an NBTTagCompound
	 * 
	 * @param Coordinate
	 * @return NBTTagCompound
	 */
	public static NBTTagCompound toCoordinateTagCompound( Coordinate coordinate )
	{
		NBTTagCompound comp = new NBTTagCompound();
		
		comp.setInteger( NBT_COORDINATE_DIMID, coordinate.getDimId() );
		comp.setInteger( NBT_COORDINATE_X, coordinate.getX() );
		comp.setInteger( NBT_COORDINATE_Y, coordinate.getY() );
		comp.setInteger( NBT_COORDINATE_Z, coordinate.getZ() );
		
		return comp;
	}
	
	/**
	 * Puts Coordinate Set in an NBTTagList
	 * 
	 * @param Set<Coordinate>
	 * @return NBTTagList
	 */
	public static NBTTagList toCoordinateTagList( Set<Coordinate> coordinates )
	{
		NBTTagList list = new NBTTagList();
		
		for( Coordinate coordinate : coordinates )
		{
			list.appendTag( NBTUtil.toCoordinateTagCompound( coordinate ) );
		}
		return list;
	}
	
	/**
	 * Puts LimitedBlock->Coordinates Mapping in an NBTTagCompound
	 * 
	 * @param LimitedBlock
	 * @param Set<Coordinate>
	 * @return NBTTagCompound
	 */
	public static NBTTagCompound toLimitedBlockCoordinatesTagCompound( LimitedBlock block, Set<Coordinate> coordinates )
	{
		NBTTagCompound comp = new NBTTagCompound();
		
		comp.setString( NBT_LIMITEDBLOCK_NAME, block.toString() );
		comp.setTag(NBT_LIMITEDBLOCK_COORDINATES, NBTUtil.toCoordinateTagList( coordinates ) );
		
		return comp;
	}
	
	/**
	 * Puts LimitedBlocks->Coordinates Mapping in an NBTTagList
	 * 
	 * @param Map<LimitedBlock, Set<Coordinate>> 
	 * @return NBTTagList
	 */
	public static NBTTagList toLimitedBlockCoordinatesTagList( Map<LimitedBlock, Set<Coordinate>> blocks )
	{
		NBTTagList list = new NBTTagList();
		
		for( Entry<LimitedBlock, Set<Coordinate>> entry : blocks.entrySet() )
		{		
			list.appendTag( NBTUtil.toLimitedBlockCoordinatesTagCompound( entry.getKey(), entry.getValue() ) );
		}
		return list;
	}
	
	/**
	 * Puts Coordinate and the UUID of the Block Owner in an NBTTagCompound
	 * 
	 * @param Coordinate
	 * @param String
	 * @return NBTTagCompound
	 */
	public static NBTTagCompound toLimitedBlockOwnerTagCompound( Coordinate coordinate, UUID uuid )
	{
		NBTTagCompound comp = new NBTTagCompound();
		
		comp.setString( NBT_LIMITEDBLOCKOWNER_UUID, uuid.toString() );
		comp.setTag( NBT_LIMITEDBLOCKOWNER_COORDINATE, NBTUtil.toCoordinateTagCompound( coordinate ) );
		
		return comp;
	}
	
	/**
	 * Puts Coordinate->Owner Map into an NBTTagList
	 * 
	 * @param Map<Coordinate, UUID>
	 * @return NBTTagList
	 */
	public static NBTTagList toLimitedBlockOwnerTagList( Map<Coordinate, UUID> owners )
	{
		NBTTagList list = new NBTTagList();
		
		for( Entry<Coordinate, UUID> entry : owners.entrySet() )
		{
			list.appendTag( NBTUtil.toLimitedBlockOwnerTagCompound(
				(Coordinate)entry.getKey(),
				(UUID)entry.getValue()
			));
		}
		return list;
	}
	
	/********************************************************************************
	 * Methods - NBT to Data
	 ********************************************************************************/
	
	/**
	 * Gets Coordinate out of an NBTTagCompound
	 * 
	 * @param NBTTagCompound
	 * @return Coordinate
	 * @throws NBTException
	 */
	public static Coordinate toCoordinate( NBTTagCompound comp ) throws NBTException
	{
		//All four keys required!
		if( ( comp.hasKey( NBT_COORDINATE_DIMID ) == false ) ||
			( comp.hasKey( NBT_COORDINATE_X ) == false ) ||
			( comp.hasKey( NBT_COORDINATE_Y ) == false ) ||
			( comp.hasKey( NBT_COORDINATE_Z ) == false ) )
		{
			throw new NBTException( "NBTTagCompound has no coordinates." );
		}
		return new Coordinate( 
			comp.getInteger( NBT_COORDINATE_DIMID ),
			comp.getInteger( NBT_COORDINATE_X ),
			comp.getInteger( NBT_COORDINATE_Y ),
			comp.getInteger( NBT_COORDINATE_Z )
		);
	}
	
	/**
	 * Gets Coordinate Set out of an NBTTagList
	 * 
	 * @param NBTTagList
	 * @return Set<Coordinate>
	 * @throws NBTException
	 */
	public static Set<Coordinate> toCoordinateSet( NBTTagList list ) throws NBTException
	{
		Set<Coordinate> coordinates = new HashSet<Coordinate>();
		
		for( int i = 0; i < list.tagCount(); i++ )
		{
			coordinates.add( NBTUtil.toCoordinate( list.getCompoundTagAt( i ) ) );
		}
		return coordinates;
	}
	
	/**
	 * Get one LimitedBlock with Set<Coordinate> out of an NBTTagCompound
	 * 
	 * @param NBTTagCompound
	 * @return Map<LimitedBlock, Set<Coordinate>>
	 * @throws ParseException
	 * @throws NBTException
	 */
	public static Map<LimitedBlock, Set<Coordinate>> toLimitedBlockCoordinatesMap( NBTTagCompound comp ) throws ParseException, NBTException
	{
		Map<LimitedBlock, Set<Coordinate>> result = new HashMap<LimitedBlock, Set<Coordinate>>();	
		result.put(
			LimitedResources.getLimitedBlockByItemStack( ParserUtil.parseStringToItemStack( comp.getString( NBT_LIMITEDBLOCK_NAME ) ) ),
			NBTUtil.toCoordinateSet( (NBTTagList)comp.getTag( NBT_LIMITEDBLOCK_COORDINATES ) )
		);
		return result;
	}
	
	/**
	 * Get multiple LimitedBlocks with Set<Coordiante> out of an NBTTagList
	 * 
	 * @param NBTTagList
	 * @return Map<LimitedBlock, Set<Coordinate>>
	 * @throws NBTException 
	 * @throws ParseException 
	 */
	public static Map<LimitedBlock, Set<Coordinate>> toLimitedBlocksCoordinatesMap( NBTTagList list ) throws ParseException, NBTException
	{
		Map<LimitedBlock, Set<Coordinate>> result = new HashMap<LimitedBlock, Set<Coordinate>>();
		
		for( int i = 0; i < list.tagCount(); i++ )
		{
			result.putAll( NBTUtil.toLimitedBlockCoordinatesMap( list.getCompoundTagAt( i ) ) );
		}
		return result;
	}
	
	/**
	 * Get one Coordinate->Owner out of an NBTTagCompound
	 * 
	 * @param NBTTagCompound
	 * @return Map<Coordinate, String>
	 * @throws NBTException 
	 */
	public static Map<Coordinate, UUID> toLimitedBlockOwnerMap( NBTTagCompound comp ) throws NBTException
	{
		Map<Coordinate, UUID> result = new HashMap<Coordinate, UUID>();
		result.put(
			NBTUtil.toCoordinate( (NBTTagCompound) comp.getTag( NBT_LIMITEDBLOCKOWNER_COORDINATE ) ),
			UUID.fromString( comp.getString( NBT_LIMITEDBLOCKOWNER_UUID ) )
		);
		return result;
	}
	
	/**
	 * Get multiple Coordinate->Owner out of an NBTTagList
	 * 
	 * @param NBTTagList 
	 * @return Map<Coordinate, String>
	 * @throws NBTException 
	 */
	public static Map<Coordinate, UUID> toLimitedBlockOwnersMap( NBTTagList list ) throws NBTException
	{
		Map<Coordinate, UUID> result = new HashMap<Coordinate, UUID>();
		
		for( int i = 0; i < list.tagCount(); i++ )
		{
			result.putAll( NBTUtil.toLimitedBlockOwnerMap(list.getCompoundTagAt( i ) ) );
		}
		return result;
	}	
}
