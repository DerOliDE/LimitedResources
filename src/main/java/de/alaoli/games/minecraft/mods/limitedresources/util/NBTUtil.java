package de.alaoli.games.minecraft.mods.limitedresources.util;

import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.alaoli.games.minecraft.mods.limitedresources.LimitedResources;
import de.alaoli.games.minecraft.mods.limitedresources.Log;
import de.alaoli.games.minecraft.mods.limitedresources.data.Coordinate;
import de.alaoli.games.minecraft.mods.limitedresources.data.LimitedBlockAt;
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
	
	public static final String NBT_LIMITEDBLOCKAT_NAME			= "NAME";
	public static final String NBT_LIMITEDBLOCKAT_COORDINATES	= "COORDINATES";
	
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
		Iterator<Coordinate> iter = coordinates.iterator();
		
		while( iter.hasNext() )
		{
			list.appendTag( NBTUtil.toCoordinateTagCompound( iter.next() ));
		}
		return list;
	}
	
	/**
	 * Puts LimitedBlockAt in an NBTTagCompound
	 * 
	 * @param LimitedBlockAt
	 * @return NBTTagCompound
	 */
	public static NBTTagCompound toLimitedBlockAtTagCompound( LimitedBlockAt block )
	{
		NBTTagCompound comp = new NBTTagCompound();
		
		comp.setString( NBT_LIMITEDBLOCKAT_NAME, block.getLimitedBlock().toString() );
		comp.setTag( NBT_LIMITEDBLOCKAT_COORDINATES, NBTUtil.toCoordinateTagList( block.getCoordinates() ) );
		
		return comp;
	}
	
	/**
	 * Puts LimitedBlockAt Set in an NBTTagList
	 * 
	 * @param Set<LimitedBlockAt>
	 * @return NBTTagList
	 */
	public static NBTTagList toLimitedBlockAtTagList( Set<LimitedBlockAt> blocks )
	{
		LimitedBlockAt block;
		NBTTagList list = new NBTTagList();
		Iterator<LimitedBlockAt> iter = blocks.iterator();
		
		while( iter.hasNext() )
		{
			block = iter.next();

			//Only if it has coordinates
			if( block.getCoordinates().size() > 0 )
			{
				list.appendTag( NBTUtil.toLimitedBlockAtTagCompound( block ) );
			}
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
	public static NBTTagCompound toLimitedBlockOwnerTagCompound( Coordinate coordinate, String uuid )
	{
		NBTTagCompound comp = new NBTTagCompound();
		
		comp.setString( NBT_LIMITEDBLOCKOWNER_UUID, uuid );
		comp.setTag( NBT_LIMITEDBLOCKOWNER_COORDINATE, NBTUtil.toCoordinateTagCompound( coordinate ) );
		
		return comp;
	}
	
	/**
	 * Puts Coordinate->Owner Map into an NBTTagList
	 * 
	 * @param Map<Coordinate, String>
	 * @return NBTTagList
	 */
	public static NBTTagList toLimitedBlockOwnerTagList( Map<Coordinate, String> owners )
	{
		Entry entry;
		NBTTagList list = new NBTTagList();
		Iterator<Entry<Coordinate, String>> iter = owners.entrySet().iterator();
		
		while( iter.hasNext() )
		{
			entry = iter.next();
			
			list.appendTag( NBTUtil.toLimitedBlockOwnerTagCompound( (Coordinate)entry.getKey(), (String)entry.getValue() ));
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
	 * Gets LimitedBlockAt out of an NBTTagCompound
	 * 
	 * @param NBTTagCompound
	 * @return LimitedBlockAt
	 * @throws ParseException
	 * @throws NBTException
	 */
	public static LimitedBlockAt toLimitedBlockAt( NBTTagCompound comp ) throws ParseException, NBTException
	{
		//First Param: Parse NBT String to ItemStack and search LimitedBlock Reference by this ItemStack
		return new LimitedBlockAt(
			LimitedResources.getLimitedBlockByItemStack( ParserUtil.parseStringToItemStack( comp.getString( NBT_LIMITEDBLOCKAT_NAME ) ) ),
			NBTUtil.toCoordinateSet( (NBTTagList)comp.getTag( NBT_LIMITEDBLOCKAT_COORDINATES ) )
		);
	}
	
	/**
	 * Gets LimitedBlockAt Set out of an NBTTagList
	 * 
	 * @param NBTTagList
	 * @return Set<LimitedBlockAt>
	 */
	public static Set<LimitedBlockAt> toLimitedBlockAtSet( NBTTagList list )
	{
		Set<LimitedBlockAt> blocks = new HashSet<LimitedBlockAt>();
		
		for( int i = 0; i < list.tagCount(); i++ )
		{
			try 
			{
				blocks.add( NBTUtil.toLimitedBlockAt( list.getCompoundTagAt( i ) ) );
			} 
			catch ( ParseException e )
			{
				Log.error( e.getMessage() );
			} 
			catch ( NBTException e ) 
			{
				Log.error( e.getMessage() );
			}
		}
		return blocks;
	}
	
	/**
	 * Gets Coordinate->Owner Map out of an NBTTagList
	 * @param NBTTagList
	 * @return Map<Coordinate, String>
	 */
	public static Map<Coordinate, String> toLimitedBlockOwnerMap( NBTTagList list )
	{
		Map<Coordinate, String> owners = new HashMap<Coordinate, String>();
		
		
		return owners;
	}
}
