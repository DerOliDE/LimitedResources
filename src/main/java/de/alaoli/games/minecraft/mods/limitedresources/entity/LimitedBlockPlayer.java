package de.alaoli.games.minecraft.mods.limitedresources.entity;

import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.Map.Entry;

import de.alaoli.games.minecraft.mods.limitedresources.Log;
import de.alaoli.games.minecraft.mods.limitedresources.data.Coordinate;
import de.alaoli.games.minecraft.mods.limitedresources.data.LimitedBlock;
import de.alaoli.games.minecraft.mods.limitedresources.util.NBTUtil;
import de.alaoli.games.minecraft.mods.limitedresources.world.LimitedBlockOwners;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class LimitedBlockPlayer extends Observable implements IExtendedEntityProperties
{
	/********************************************************************************
	 * Constants
	 ********************************************************************************/
	
	public final static String EXT_PROP_NAME = "de.alaoli.games.minecraft.mods.limitedresources.entity.EntityPlayerWithLimitedBlocks";
	public final static String NBT_LIMITEDBLOCKCOORDINATES_MAP = "LIMITEDBLOCKCOORDINATESMAP";
	
	/********************************************************************************
	 * Attributes
	 ********************************************************************************/

	/**
	 * References to Player
	 */
	public EntityPlayer entityPlayer;
		
	/**
	 * Mapping LimitedBlock -> Coordinates
	 */
	private Map<LimitedBlock, Set<Coordinate>> blocks;

	/**
	 * Last Coordinate change
	 */
	private Coordinate lastChange;
	
	/**
	 * Flag if coordinates were refreshed after loading NBT data
	 */
	private boolean isRefreshed;
	
	/********************************************************************************
	 * Methods - Constructor
	 ********************************************************************************/
		
	public LimitedBlockPlayer( EntityPlayer entityPlayer )
	{
		this.entityPlayer	= entityPlayer;
		this.blocks			= new HashMap<LimitedBlock, Set<Coordinate>>();
		this.isRefreshed	= false;
	}	
	
	/********************************************************************************
	 * Interface - IExtendedEntityProperties
	 ********************************************************************************/
	
	@Override
	public void saveNBTData( NBTTagCompound compound ) 
	{
		NBTTagCompound prop = new NBTTagCompound();
		
		prop.setTag( NBT_LIMITEDBLOCKCOORDINATES_MAP, NBTUtil.toLimitedBlockCoordinatesTagList( this.blocks ) );
		compound.setTag( EXT_PROP_NAME, prop );
	}

	@Override
	public void loadNBTData( NBTTagCompound compound ) 
	{
		NBTTagCompound prop = (NBTTagCompound) compound.getTag( EXT_PROP_NAME );
		this.blocks.clear();
		
		try
		{
			this.blocks.putAll( 
				NBTUtil.toLimitedBlocksCoordinatesMap( (NBTTagList) prop.getTag( NBT_LIMITEDBLOCKCOORDINATES_MAP ) )
			);
			this.isRefreshed = false;
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
	
	@Override
	public void init( Entity entity, World world ) { /*Not used yet*/ }

	/********************************************************************************
	 * Methods - Getter / Setter
	 ********************************************************************************/
	
	/**
	 * Returns last coordinate changes
	 * 
	 * @return Coordinate|null
	 */
	public Coordinate getLastChange()
	{
		return this.lastChange;
	}
	
	/**
	 * Returns Coordinates Set for LimitedBlock
	 * 
	 * @param LimitedBlock
	 * @return Set<Coordinate>
	 */
	public Set<Coordinate> getCoordinatsFor( LimitedBlock block )
	{
		return this.blocks.get( block );
	}
	
	/********************************************************************************
	 * Methods 
	 ********************************************************************************/
	
	/**
	 * Adds a coordinate -> limited block mapping
	 * 
	 * @param Coordinate
	 * @param LimitedBlock
	 * @return boolean
	 */
	public boolean add( LimitedBlock block, Coordinate coordinate )
	{
		//Initialize Limited Block
		if( this.blocks.containsKey( block ) == false )
		{
			this.blocks.put( block, new HashSet<Coordinate>() );
		}
		Set<Coordinate> coordinates = this.blocks.get( block );
		
		if( coordinates.add( coordinate ) )
		{
			this.lastChange = coordinate;
			
			this.setChanged();
			this.notifyObservers( LimitedBlockOwners.OBSERVER_ARG_ADDED );
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Removes a coordinate -> limited block mapping
	 * 
	 * @param Coordinate
	 * @return boolean
	 */
	public boolean remove( Coordinate coordinate )
	{
		for( Entry<LimitedBlock, Set<Coordinate>> entry : this.blocks.entrySet() )
		{
			if( entry.getValue().contains( coordinate ) )
			{
				this.lastChange = coordinate;
				entry.getValue().remove( coordinate );
				
				this.setChanged();
				this.notifyObservers( LimitedBlockOwners.OBSERVER_ARG_REMOVED );
				
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if all Coordinates for all Limited Block still exists.
	 * All non-exist coordinates will be removed.
	 */
	public void refresh()
	{
		if( this.isRefreshed )
		{
			return;
		}
		World world;
		LimitedBlock block;
		ItemStack itemStack;
		Set<Coordinate> toRemove = new HashSet<Coordinate>();
		
		//Search
		for( Entry<LimitedBlock, Set<Coordinate>> entry : this.blocks.entrySet() )
		{		
			for( Coordinate coordinate : entry.getValue() )
			{
				world		= MinecraftServer.getServer().worldServerForDimension( coordinate.getDimId() );
				block		= entry.getKey();
				
				if( world != null )
				{
					itemStack	= new ItemStack(
						world.getBlock( coordinate.getX(), coordinate.getY(), coordinate.getZ() ),
						1,
						world.getBlockMetadata( coordinate.getX(), coordinate.getY(), coordinate.getZ() )	
					);
					
					//if Block or MetaId != remove Coordinate
					if( ( block == null ) ||
						( block.isLimitedBlock( itemStack ) == false ) )		
					{
						toRemove.add( coordinate );
					}				
				}
				else
				{
					Log.error( "Can't load WorldServer for Dimension '" + coordinate.getDimId() + "'" );
				}
			}
		}
		
		//Remove
		for( Coordinate remove : toRemove )
		{
			this.remove( remove );
		}
		this.isRefreshed = true;
	}

	/**
	 * Checks if block limit is reached
	 * 
	 * @param LimitedBlock
	 * @return boolean
	 */
	public boolean canPlaceBlock( LimitedBlock block )
	{
		//If Block is not listed
		if( this.blocks.containsKey( block ) == false )
		{
			return true;
		}
		
		//Check if limit is not reached
		if( block.getLimit() > this.blocks.get( block ).size() )
		{
			return true;
		}
		return false;
	}

	/**
	 * Returns how many limited blocks have been placed
	 * 
	 * @param LimitedBlock
	 * @return int
	 */
	public int countBlocksPlaced( LimitedBlock block )
	{
		return this.blocks.get( block ).size();
	}
	
	/**
	 * Returns true if player has placed 1+ blocks
	 * 
	 * @param LimitedBlock
	 * @return boolean
	 */
	public boolean hasBlockPlaced( LimitedBlock block )
	{
		if( ( this.blocks.containsKey( block ) ) && 
			( this.blocks.get( block ).size() > 0 ) )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/********************************************************************************
	 * Methods - Static 
	 ********************************************************************************/
	
	/**
	 * Register extends properties to EntityPlayer
	 * 
	 * @param EntityPlayer
	 */
	public static final void register( EntityPlayer entityPlayer )
	{
		entityPlayer.registerExtendedProperties( LimitedBlockPlayer.EXT_PROP_NAME, new LimitedBlockPlayer( entityPlayer ) );
	}

	/**
	 * Get Player with extended properties.
	 * 
	 * @param EntityPlayer
	 * @return EntityPlayerWithLimitedBlocks|null
	 */
	public static final LimitedBlockPlayer get( EntityPlayer entityPlayer )
	{
		return (LimitedBlockPlayer) entityPlayer.getExtendedProperties( LimitedBlockPlayer.EXT_PROP_NAME );
	}
}
