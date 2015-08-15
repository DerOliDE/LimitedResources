package de.alaoli.games.minecraft.mods.limitedresources.event;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
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
	
	/********************************************************************************
	 * Methods - Forge Events
	 ********************************************************************************/

	@SubscribeEvent( priority=EventPriority.HIGHEST )
	public void onWorldLoad( WorldEvent.Load event )
	{
		if( this.owners == null )
		{
			this.owners = LimitedBlockOwners.get( event.world );
		}
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
		LimitedBlockPlayer player = LimitedBlockPlayer.get( event.getPlayer() );
		
		if( ( event.isCanceled() ) || 
			( player == null ) )
		{
			return;
		}
		ItemStack itemStack = new ItemStack( event.block, 1, event.blockMetadata );
		Coordinate coordinate = new Coordinate( event.getPlayer().dimension, event.x, event.y, event.z );
		
		player.addObserver( this.owners );
		
		for( LimitedBlock block : LimitedResources.limitedBlocks )
		{
			if( block.isLimitedBlock( itemStack ) )
			{
				//Coordinate has Owner
				if( this.owners.hasOwner( coordinate ) )
				{
					//Is Player = Owner
					if( this.owners.isOwner( coordinate, player.entityPlayer ) )
					{
						player.remove( coordinate );
						player.refresh();
					}
					else
					{
						event.getPlayer().addChatMessage( new ChatComponentText( "You are not the owner of this block." ) );
						event.setCanceled( true );
					}
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
		LimitedBlockPlayer player = LimitedBlockPlayer.get( event.player );
		
		if( ( event.isCanceled() ) || 
			( player == null ) ) 
		{
			return;
		}
		ItemStack itemStack = event.itemInHand;
		Coordinate coordinate 	= new Coordinate( 
			event.blockSnapshot.dimId, 
			event.blockSnapshot.x,
			event.blockSnapshot.y,
			event.blockSnapshot.z
		);
		player.addObserver( this.owners );
		
		for( LimitedBlock block : LimitedResources.limitedBlocks )
		{
			if( block.isLimitedBlock( itemStack ) )
			{
				//Check if block limit is reached
				if( player.canPlaceBlock( block ) )
				{
					player.refresh();
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
		player.deleteObservers();
	}	
}
