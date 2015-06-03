package de.alaoli.games.minecraft.mods.limitedresources.event;

import java.util.Iterator;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import de.alaoli.games.minecraft.mods.limitedresources.data.Coordinate;
import de.alaoli.games.minecraft.mods.limitedresources.data.LimitedBlock;
import de.alaoli.games.minecraft.mods.limitedresources.data.LimitedBlockAt;
import de.alaoli.games.minecraft.mods.limitedresources.entity.EntityPlayerWithLimitedBlocks;
import de.alaoli.games.minecraft.mods.limitedresources.LimitedResources;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.world.BlockEvent.MultiPlaceEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;

public class BlockPlacingEvent 
{	
	/********************************************************************************
	 * Methods - Forge Events
	 ********************************************************************************/
	
	@SubscribeEvent
    public void onEntityConstructing( EntityConstructing event )
    {
    	//Register PlayerPlacedBlocks Entity
    	if( ( event.entity instanceof EntityPlayer ) && 
			( EntityPlayerWithLimitedBlocks.get( (EntityPlayer) event.entity ) == null ) )
    	{
    		EntityPlayerWithLimitedBlocks.register( (EntityPlayer) event.entity );
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
		
	/********************************************************************************
	 * Methods
	 ********************************************************************************/
	
	private IChatComponent messageBlockPlaced( LimitedBlockAt block, boolean isPlaced )
	{
		int limit			= block.getLimitedBlock().getLimit();
		int placed			= block.getCoordinates().size();
		String blockName	= block.getLimitedBlock().getItemStack().getDisplayName();
		String message		= "[Limited Resources] ";
		
		//Block was placed?
		if( isPlaced )
		{
			message += blockName + " placed. (";
			message += String.valueOf( placed ) + " of " + String.valueOf( limit );
			message += ")";
		}		
		else
		{
			message += "Can't place " + blockName + " limit reached. ";
		}
		return new ChatComponentText( message );
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
		EntityPlayerWithLimitedBlocks player;
		
		player = EntityPlayerWithLimitedBlocks.get( event.player );
		iter = LimitedResources.limitedBlocks.iterator();
		itemStackEvent = event.itemInHand;
		
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
						player.addBlock( block, coordinate );
						
						player.entityPlayer.addChatMessage( this.messageBlockPlaced( player.getLimitedBlockAt( block ), true ) );
					}
					else
					{
						event.setCanceled( true );
						
						player.entityPlayer.addChatMessage( this.messageBlockPlaced( player.getLimitedBlockAt( block ), false ) );
					}
					
				}
			}
		}
	}	
}
