package de.alaoli.games.minecraft.mods.limitedresources.world;

import java.util.HashMap;
import java.util.Map;

import de.alaoli.games.minecraft.mods.limitedresources.data.Coordinate;
import de.alaoli.games.minecraft.mods.limitedresources.util.NBTUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class LimitedBlockOwners extends WorldSavedData
{
	/********************************************************************************
	 * Constants
	 ********************************************************************************/
	
	public final static String ID = "LimitedBlocksOwners";
	public final static String NBT_OWNER_MAP = "NBT_OWNER_MAP";

	/********************************************************************************
	 * Attributes
	 ********************************************************************************/
	
	/**
	 * Mapping coordinate to player UUID
	 */
	private Map<Coordinate, String> owners;  
	
	/********************************************************************************
	 * Methods - Constructor
	 ********************************************************************************/
	
	public LimitedBlockOwners() 
	{
		super( LimitedBlockOwners.ID );
		
		this.owners = new HashMap<Coordinate, String>();
	}

	/********************************************************************************
	 * Abstract - WorldSavedData
	 ********************************************************************************/
	
	@Override
	public void readFromNBT( NBTTagCompound comp ) 
	{
		this.owners.clear();
		
		this.owners = NBTUtil.toLimitedBlockOwnerMap( (NBTTagList)comp.getTag( NBT_OWNER_MAP ) );
	}

	@Override
	public void writeToNBT( NBTTagCompound comp )
	{
		comp.setTag( NBT_OWNER_MAP, NBTUtil.toLimitedBlockOwnerTagList( this.owners ) );
	}

	/********************************************************************************
	 * Methods - Getter / Setter
	 ********************************************************************************/
	
	public String getPlayerUuidAt( Coordinate coordinate )
	{
		return this.owners.get( coordinate );
	}
	
	/********************************************************************************
	 * Methods 
	 ********************************************************************************/
	
	public void add( Coordinate coordinate, EntityPlayer player )
	{
		if( this.owners.containsKey( coordinate ) )
		{
			this.owners.remove( coordinate );
		}
		this.owners.put( coordinate, player.getUniqueID().toString() );
		this.markDirty();
	}
	
	public void remove( Coordinate coordinate )
	{
		this.owners.remove( coordinate );
		this.markDirty();
	}
	
	/********************************************************************************
	 * Methods - Static 
	 ********************************************************************************/
	
	public static LimitedBlockOwners get( World world )
	{
		if( world.mapStorage == null )
		{
			return null;
		}
		LimitedBlockOwners data = (LimitedBlockOwners) world.mapStorage.loadData( LimitedBlockOwners.class, LimitedBlockOwners.ID );
		
		//Initialize if null
		if( data == null )
		{
			data = new LimitedBlockOwners();
			data.markDirty();
			world.mapStorage.setData( LimitedBlockOwners.ID, data );
		}
		return data;
	}
}
