package de.alaoli.games.minecraft.mods.limitedresources.entities;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.alaoli.games.minecraft.mods.limitedresources.Log;
import de.alaoli.games.minecraft.mods.limitedresources.data.Coordinate;
import de.alaoli.games.minecraft.mods.limitedresources.data.LimitedBlock;
import de.alaoli.games.minecraft.mods.limitedresources.data.LimitedBlockAt;
import de.alaoli.games.minecraft.mods.limitedresources.util.NBTUtil;
import de.alaoli.games.minecraft.mods.limitedresources.util.ParserUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;
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
	 * Interface
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
	}	
	
	/**
	@Override
	public void saveNBTData( NBTTagCompound compound )
	{
		NBTTagList list;
		NBTTagString string;
		NBTTagCompound prop;
		LimitedBlockAt limitedBlockAt;
		Iterator<LimitedBlockAt> iter;
		
		list = new NBTTagList();
		prop = new NBTTagCompound();
		iter = this.limitedBlocksAt.iterator();
		
		while( iter.hasNext() )
		{
			limitedBlockAt = iter.next();
			
			if( limitedBlockAt.getCoordinates().size() > 0 )
			{
				list.appendTag( new NBTTagString( limitedBlockAt.toString() ));
			}
		}
		prop.setTag( NBT_LIMITEDBLOCKSAT, list );
		compound.setTag( EXT_PROP_NAME, prop );
	}

	@Override
	public void loadNBTData( NBTTagCompound compound )
	{
		NBTTagList list;
		String string;
		NBTTagCompound prop;
		
		prop = (NBTTagCompound) compound.getTag( EXT_PROP_NAME );
		
		if( prop.hasKey( NBT_LIMITEDBLOCKSAT ) )
		{
			list = (NBTTagList) prop.getTag( NBT_LIMITEDBLOCKSAT );
			
			for( int i = 0; i < list.tagCount(); i++ )
			{
				try 
				{
					this.limitedBlocksAt.add( ParserUtil.parseStringtoLimitedBlockAt( list.getStringTagAt( i ) ) );
				}
				catch ( ParseException e ) 
				{
					Log.error( e.getMessage() );
				}
			}
			prop.removeTag( NBT_LIMITEDBLOCKSAT );
		}
	}*/

	@Override
	public void init( Entity entity, World world ) 
	{
		//TODO Check if some blocks doesn't exists anymore (config changed)
		
	}

	/********************************************************************************
	 * Methods
	 ********************************************************************************/
		
	public EntityPlayerWithLimitedBlocks( EntityPlayer entityPlayer )
	{
		this.entityPlayer = entityPlayer;
		this.limitedBlocksAt = new HashSet<LimitedBlockAt>();
	}	

	public boolean canPlaceBlock( World world, LimitedBlock block )
	{
		LimitedBlockAt limitedBlockAt;
		Iterator<LimitedBlockAt> iter = this.limitedBlocksAt.iterator();
				
		while( iter.hasNext() )
		{
			limitedBlockAt = iter.next();
			
			if( limitedBlockAt.getLimitedBlock().equals( block ) )
			{
				return limitedBlockAt.canPlaceBlock( world );
			}			
		}
		return true;
	}
	
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
	
	public void addBlock( LimitedBlock block, Coordinate coordinate )
	{
		LimitedBlockAt limitedBlockAt = this.getLimitedBlockAt( block );
		
		
		
		if( limitedBlockAt == null )
		{
			limitedBlockAt = new LimitedBlockAt( block );
			this.limitedBlocksAt.add( limitedBlockAt );
		}
		
		if( limitedBlockAt.getCoordinates().contains( coordinate ) == false )
		{
			limitedBlockAt.getCoordinates().add( coordinate );
		}
	}
	
	public static final void register( EntityPlayer entityPlayer )
	{
		entityPlayer.registerExtendedProperties( EntityPlayerWithLimitedBlocks.EXT_PROP_NAME, new EntityPlayerWithLimitedBlocks( entityPlayer ) );
	}

	public static final EntityPlayerWithLimitedBlocks get( EntityPlayer entityPlayer )
	{
		return (EntityPlayerWithLimitedBlocks) entityPlayer.getExtendedProperties( EntityPlayerWithLimitedBlocks.EXT_PROP_NAME );
	}
}
