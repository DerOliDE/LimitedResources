package de.alaoli.games.minecraft.mods.limitedresources.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import de.alaoli.games.minecraft.mods.limitedresources.data.Coordinate;
import de.alaoli.games.minecraft.mods.limitedresources.data.LimitedBlock;
import de.alaoli.games.minecraft.mods.limitedresources.entity.LimitedBlockPlayer;
import de.alaoli.games.minecraft.mods.limitedresources.world.LimitedBlockOwners;
import de.alaoli.games.minecraft.mods.limitedresources.Config;
import de.alaoli.games.minecraft.mods.limitedresources.LimitedResources;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.MultiPlaceEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.event.world.WorldEvent;

public class BlockPlacingEvent 
{
	/********************************************************************************
	 * Attributes
	 ********************************************************************************/
	
	private LimitedBlockOwners owners;
	//private Map<UUID, LimitedBlockPlayer> players;
	
	/********************************************************************************
	 * Methods - Forge Events
	 ********************************************************************************/
	
	/**
	 * 
	 * @param WorldEvent
	 */
	@SubscribeEvent( priority=EventPriority.HIGHEST )
	public void onWorldLoad( WorldEvent.Load event )
	{
		if( this.owners == null )
		{
			this.owners = LimitedBlockOwners.get( event.world );
		}
		
		/*
		if( this.players == null )
		{
			this.players = new HashMap<UUID, LimitedBlockPlayer>();
		}*/
	}

	@SubscribeEvent( priority=EventPriority.HIGHEST )
    public void onEntityConstructing( EntityConstructing event )
    {
    	//Register PlayerPlacedBlocks Entity
    	if( ( event.entity instanceof EntityPlayer ) && 
			( LimitedBlockPlayer.get( (EntityPlayer) event.entity ) == null ) )
    	{
    		LimitedBlockPlayer.register( (EntityPlayer) event.entity );
    	}
    }
/*
	@SubscribeEvent
	public void onPlayerLoggedIn( PlayerLoggedInEvent event )
	{
		LimitedBlockPlayer player = LimitedBlockPlayer.get( event.player );
		
		if( player == null )
		{
			LimitedBlockPlayer.register( (EntityPlayer) event.player );
			player = LimitedBlockPlayer.get( event.player );
		}
		this.players.put( player.entityPlayer.getUniqueID(), player );
		player.addObserver( this.owners );
	}
	
	@SubscribeEvent
	public void onPlayerLoggedout( PlayerLoggedOutEvent event )
	{
		LimitedBlockPlayer player = this.players.get( event.player.getUniqueID() );
		
		player.deleteObservers();
		this.players.remove( player.entityPlayer.getUniqueID() );
	}
	*/
	@SubscribeEvent
	public void onPlaceEvent( PlaceEvent event )
	{
		this.placingEvent( event );
	}
	
	@SubscribeEvent
	public void onMultiPlaceEvent( MultiPlaceEvent event )
	{
		this.placingEvent( event );
	}
	
	
	@SubscribeEvent
	public void onBlockBreakEvent( BreakEvent event )
	{
		if( event.isCanceled() )
		{
			return;
		}
		LimitedBlock block;
		ItemStack itemStackLimited;
		
		Iterator<LimitedBlock> iter = LimitedResources.limitedBlocks.iterator();
		ItemStack itemStackEvent = new ItemStack( event.block, 1, event.blockMetadata );
		LimitedBlockPlayer player = LimitedBlockPlayer.get( event.getPlayer() ); 
		Coordinate coordinate = new Coordinate( event.getPlayer().dimension, event.x, event.y, event.z );
		
		player.addObserver( this.owners );
		
		if( itemStackEvent != null )
		{
			while( iter.hasNext() )
			{
				block = iter.next();
				itemStackLimited = block.getItemStack();
				
				if( ( itemStackEvent.getItem().equals( itemStackLimited.getItem() ) ) &&
					( itemStackEvent.getItemDamage() == itemStackLimited.getItemDamage() ) )
				{
					//Player == block owner
					UUID uuid = this.owners.getPlayerUuidAt( coordinate );
					
			//		if( ( uuid != null ) && 
			//			( uuid.equals( event.getPlayer().getUniqueID().toString() ) ) )
			//		{
						player.remove( coordinate );
			//		}
			//		else
			//		{
			//			event.getPlayer().addChatMessage( new ChatComponentText( "You are not the owner of this block." ) );
			//			event.setCanceled( true );
			//		}
					
				}
			}
		}
		player.deleteObservers();
	}	
	
	/********************************************************************************
	 * Methods
	 ********************************************************************************/
	
	/**
	 * Sends a "limited block placed x of y left" chat message 
	 * 
	 * @param LimitedBlockPlayer
	 * @param LimitedBlock
	 */
	private void messageBlockPlaced( LimitedBlockPlayer player, LimitedBlock block )
	{
		//Only in notification mode "always" 
		if( Config.Messages.notificationMode != Config.MESSAGES_NOTIFICATION_ALWAYS )
		{
			return;
		}
		StringBuilder message	= new StringBuilder();
		
		message.append( "[Limited Resources] " );
		message.append( block.getItemStack().getDisplayName() );
		message.append( " placed. (" );
		message.append( player.countBlocksPlaced( block ) );
		message.append( " of " );
		message.append( block.getLimit() );
		message.append( ")" );
				
		player.entityPlayer.addChatMessage( new ChatComponentText( message.toString() ) );
	}
	
	/**
	 * Sends a "limit reached" chat message 
	 * 
	 * @param LimitedBlockPlayer
	 * @param LimitedBlockAt
	 */
	private void messageBlockLimitReached( LimitedBlockPlayer player, LimitedBlock block )
	{
		StringBuilder message = new StringBuilder();
		
		message.append( "[Limited Resources] " );
		message.append( "Can't place " );
		message.append( block.getItemStack().getDisplayName() );
		message.append( " limit reached." );
		
		player.entityPlayer.addChatMessage( new ChatComponentText( message.toString() ) );
	}
	
	/**
	 * Event canceled if players block place limits is reached
	 * 
	 * @param PlaceEvent
	 */
	private void placingEvent( PlaceEvent event )
	{
		if( event.isCanceled() ) 
		{
			return;
		}
		LimitedBlock block;
		Coordinate coordinate;
		ItemStack itemStackEvent;
		ItemStack itemStackLimited;
		Iterator<LimitedBlock> iter;
		LimitedBlockPlayer player;
		
		player = LimitedBlockPlayer.get( event.player );
		iter = LimitedResources.limitedBlocks.iterator();
		itemStackEvent = event.itemInHand;

		player.addObserver( this.owners );
		
		if( itemStackEvent != null )
		{
			while( iter.hasNext() )
			{
				block = iter.next();
				itemStackLimited = block.getItemStack();
				coordinate 	= new Coordinate( 
					event.blockSnapshot.dimId, 
					event.blockSnapshot.x,
					event.blockSnapshot.y,
					event.blockSnapshot.z
				);
				
				if( ( itemStackEvent.getItem().equals( itemStackLimited.getItem() ) ) &&
					( itemStackEvent.getItemDamage() == itemStackLimited.getItemDamage() ) )
				{
					//if limited block and entity isn't a player -> cancel placing
					if( player == null )
					{
						event.setCanceled( true );
					}
					
					if( player.canPlaceBlock( block ) )
					{
						player.add( block, coordinate );
						
						this.messageBlockPlaced( player, block );
					}
					else
					{
						event.setCanceled( true );
						
						this.messageBlockLimitReached( player, block );
					}
				}
			}
		}
		player.deleteObservers();
	}	
}
