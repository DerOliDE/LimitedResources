package de.alaoli.games.minecraft.mods.limitedresources.world;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import de.alaoli.games.minecraft.mods.limitedresources.Log;
import de.alaoli.games.minecraft.mods.limitedresources.data.Coordinate;
import de.alaoli.games.minecraft.mods.limitedresources.entity.LimitedBlockPlayer;
import de.alaoli.games.minecraft.mods.limitedresources.util.NBTUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class LimitedBlockOwners extends WorldSavedData implements Observer
{
	/********************************************************************************
	 * Constants
	 ********************************************************************************/
	
	public final static String ID = "LimitedBlocksOwners";
	
	public final static String NBT_OWNER_MAP = "NBT_OWNER_MAP";

	public final static int OBSERVER_ARG_ADDED		= 0;
	public final static int OBSERVER_ARG_REMOVED	= 1;
	
	/********************************************************************************
	 * Attributes
	 ********************************************************************************/
	
	/**
	 * Mapping coordinate to player UUID
	 */
	private Map<Coordinate, UUID> owners;  
	
	/********************************************************************************
	 * Methods - Constructor
	 ********************************************************************************/
	
	public LimitedBlockOwners() 
	{
		super( LimitedBlockOwners.ID );
		
		this.owners = new HashMap<Coordinate, UUID>();
	}

	/********************************************************************************
	 * Abstract - WorldSavedData
	 ********************************************************************************/
	
	@Override
	public void readFromNBT( NBTTagCompound comp ) 
	{
		this.owners.clear();
		
		try 
		{
			this.owners.putAll( 
				NBTUtil.toLimitedBlockOwnersMap( (NBTTagList)comp.getTag( NBT_OWNER_MAP ) ) 
			);
		} 
		catch ( NBTException e ) 
		{
			Log.error( e.getMessage() );
		}
	}

	@Override
	public void writeToNBT( NBTTagCompound comp )
	{
		comp.setTag( NBT_OWNER_MAP, NBTUtil.toLimitedBlockOwnerTagList( this.owners ) );
	}

	/********************************************************************************
	 * Interface - Observer
	 ********************************************************************************/

	@Override
	public void update( Observable observable, Object argument ) 
	{
		LimitedBlockPlayer player = (LimitedBlockPlayer) observable;
		
		switch( (int)argument )
		{
			case OBSERVER_ARG_ADDED :
				this.owners.put( player.getLastChange(), player.entityPlayer.getUniqueID() );
				player.entityPlayer.addChatMessage( new ChatComponentText( "ADDED: " + player.getLastChange() ));
				break;
				
			case OBSERVER_ARG_REMOVED :
				this.owners.remove( player.getLastChange() );
				player.entityPlayer.addChatMessage( new ChatComponentText( "REMOVED: " + player.getLastChange() ));
				break;	
		}
	}
	
	/********************************************************************************
	 * Methods - Getter / Setter
	 ********************************************************************************/
	
	public UUID getPlayerUuidAt( Coordinate coordinate )
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
		this.owners.put( coordinate, player.getUniqueID() );
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
