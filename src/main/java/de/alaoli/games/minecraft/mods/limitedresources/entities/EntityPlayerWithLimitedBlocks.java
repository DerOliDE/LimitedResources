package de.alaoli.games.minecraft.mods.limitedresources.entities;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.alaoli.games.minecraft.mods.limitedresources.data.Coordinate;
import de.alaoli.games.minecraft.mods.limitedresources.data.LimitedBlock;
import de.alaoli.games.minecraft.mods.limitedresources.data.LimitedBlockAt;
import de.alaoli.games.minecraft.mods.limitedresources.util.NBTUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.IExtendedEntityProperties;

public class EntityPlayerWithLimitedBlocks implements IExtendedEntityProperties
{
	/********************************************************************************
	 * Constants
	 ********************************************************************************/
	
	public final static String EXT_PROP_NAME = "de.alaoli.games.minecraft.mods.limitedresources.entities.EntityPlayerWithLimitedBlocks";
	
	public final static String NBT_LIMITEDBLOCKSAT_SET = "LIMITEDBLOCKSAT_SET";
	
	/********************************************************************************
	 * Attributes
	 ********************************************************************************/

	public EntityPlayer entityPlayer;
	
	private Set<LimitedBlockAt> limitedBlocksAt;

	/********************************************************************************
	 * Methods - Constructor
	 ********************************************************************************/
		
	public EntityPlayerWithLimitedBlocks( EntityPlayer entityPlayer )
	{
		this.entityPlayer = entityPlayer;
		this.limitedBlocksAt = new HashSet<LimitedBlockAt>();
	}	
	
	/********************************************************************************
	 * Interface - IExtendedEntityProperties
	 ********************************************************************************/
	
	@Override
	public void saveNBTData( NBTTagCompound compound ) 
	{
		NBTTagCompound prop = new NBTTagCompound();
		
		prop.setTag( NBT_LIMITEDBLOCKSAT_SET, NBTUtil.toLimitedBlockAtTagList( this.limitedBlocksAt ) );
		compound.setTag( EXT_PROP_NAME, prop );
	}

	@Override
	public void loadNBTData( NBTTagCompound compound ) 
	{
		NBTTagCompound prop = (NBTTagCompound) compound.getTag( EXT_PROP_NAME );
		this.limitedBlocksAt = NBTUtil.toLimitedBlockAtSet( (NBTTagList) prop.getTag( NBT_LIMITEDBLOCKSAT_SET ) );
		
		this.refreshAllCoordinates();
	}	
	
	@Override
	public void init( Entity entity, World world ) { /*Not used yet*/ }

	/********************************************************************************
	 * Methods - Getter / Setter
	 ********************************************************************************/
	
	public LimitedBlockAt getLimitedBlockAt( LimitedBlock block )
	{
		LimitedBlockAt limitedBlockAt;
		Iterator<LimitedBlockAt> iter = this.limitedBlocksAt.iterator();
		
		while( iter.hasNext() )
		{
			limitedBlockAt = iter.next();
			
			if( limitedBlockAt.getLimitedBlock().equals( block ) )
			{		
				return limitedBlockAt;
			}
		}
		return null;
	}
	
	/********************************************************************************
	 * Methods 
	 ********************************************************************************/
	
	/**
	 * Checks if all Coordinates for an Limited Block still exists.
	 * All non-exist coordinates will be removed.
	 * 
	 * @param LimitedBlockAt
	 */
	public void refreshCoordinates( LimitedBlockAt block )
	{
		World world;
		ItemStack itemStack;
		Coordinate coordinate;
		
		Set<Coordinate> toRemove	= new HashSet<Coordinate>();
		Iterator<Coordinate> iter	= block.getCoordinates().iterator();
		
		while( iter.hasNext() )
		{
			coordinate	= iter.next();
			world		= DimensionManager.getWorld( coordinate.getDimId() );
			itemStack	= new ItemStack(
				world.getBlock( coordinate.getX(), coordinate.getY(), coordinate.getZ() ),
				1,
				world.getBlockMetadata( coordinate.getX(), coordinate.getY(), coordinate.getZ() )	
			);
			
			//if Block or MetaId != remove Coordinate
			if( ( block.getLimitedBlock().getItemStack().getItem().equals( itemStack.getItem() ) == false ) || 
				( block.getLimitedBlock().getItemStack().getItemDamage() != itemStack.getItemDamage() ) )			
			{
				toRemove.add( coordinate );
			}
		}
		block.getCoordinates().removeAll( toRemove );
	}
	
	/**
	 * Checks if all Coordinates for all Limited Block still exists.
	 * All non-exist coordinates will be removed.
	 */
	public void refreshAllCoordinates()
	{
		Iterator<LimitedBlockAt> iter = this.limitedBlocksAt.iterator();
		
		while( iter.hasNext() )
		{
			this.refreshCoordinates( iter.next() );
		}
	}
	
	/**
	 * Checks if the Player can place this Block
	 * 
	 * @param LimitedBlock
	 * @return boolean
	 */
	public boolean canPlaceBlock( LimitedBlock block )
	{
		LimitedBlockAt limitedBlockAt;
		Iterator<LimitedBlockAt> iter = this.limitedBlocksAt.iterator();
		
		while( iter.hasNext() )
		{
			limitedBlockAt = iter.next();
			
			//If Block is listed then...
			if( limitedBlockAt.getLimitedBlock().equals( block ) )
			{
				//... refresh Coordinates
				this.refreshCoordinates( limitedBlockAt );
				
				//... check if limit is reached
				if( limitedBlockAt.getLimitedBlock().getLimit() > limitedBlockAt.getCoordinates().size() )
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Adds LimitedBlock / Coordinate
	 * 
	 * @param LimitedBlock
	 * @param Coordinate
	 */
	public void addBlock( LimitedBlock block, Coordinate coordinate )
	{
		LimitedBlockAt limitedBlockAt = this.getLimitedBlockAt( block );
		
		//First entry
		if( limitedBlockAt == null )
		{
			limitedBlockAt = new LimitedBlockAt( block );
			this.limitedBlocksAt.add( limitedBlockAt );
		}
		
		//Add Coordinate 
		if( limitedBlockAt.getCoordinates().contains( coordinate ) == false )
		{
			limitedBlockAt.getCoordinates().add( coordinate );
		}
	}
	
	/********************************************************************************
	 * Methods - Static 
	 ********************************************************************************/
	
	/**
	 * Register extendes properties to EntityPlayer
	 * 
	 * @param EntityPlayer
	 */
	public static final void register( EntityPlayer entityPlayer )
	{
		entityPlayer.registerExtendedProperties( EntityPlayerWithLimitedBlocks.EXT_PROP_NAME, new EntityPlayerWithLimitedBlocks( entityPlayer ) );
	}

	/**
	 * Get Player with extended properties.
	 * 
	 * @param EntityPlayer
	 * @return EntityPlayerWithLimitedBlocks|null
	 */
	public static final EntityPlayerWithLimitedBlocks get( EntityPlayer entityPlayer )
	{
		return (EntityPlayerWithLimitedBlocks) entityPlayer.getExtendedProperties( EntityPlayerWithLimitedBlocks.EXT_PROP_NAME );
	}
}
