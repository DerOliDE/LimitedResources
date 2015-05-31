package de.alaoli.games.minecraft.mods.limitedresources.events;

import java.util.Iterator;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import de.alaoli.games.minecraft.mods.limitedresources.data.Coordinate;
import de.alaoli.games.minecraft.mods.limitedresources.data.LimitedBlock;
import de.alaoli.games.minecraft.mods.limitedresources.data.LimitedBlockAt;
import de.alaoli.games.minecraft.mods.limitedresources.entities.EntityPlayerWithLimitedBlocks;
import de.alaoli.games.minecraft.mods.limitedresources.LimitedResources;
import de.alaoli.games.minecraft.mods.limitedresources.Log;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.MultiPlaceEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;

public class BlockPlacingEvent 
{	
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
		
		if( ( player != null ) && 
			( itemStackEvent != null ) )
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
					event.setCanceled( true );
					
					if( player.canPlaceBlock( event.world, block ) )
					{
						player.addBlock( block, coordinate );
						event.setCanceled( false );
						
						Log.debug( "Block placed." );	
					}
				}
			}
		}
	}
	
	/*
	private void placingEvent( PlaceEvent event )
	{
		Iterator<LimitedBlock> iter;
		ItemStack itemStack;
		LimitedBlock block;
		LimitedBlockAt blockAt;
		Coordinate coordinate;
		EntityPlayerWithLimitedBlocks player;
		
		iter 		= LimitedResources.limitedBlocks.iterator();
		itemStack	= event.itemInHand;
		
	
		
		player 		= EntityPlayerWithLimitedBlocks.get( event.player );
		coordinate 	= new Coordinate( 
			event.blockSnapshot.dimId, 
			event.blockSnapshot.x,
			event.blockSnapshot.y,
			event.blockSnapshot.z
		);

		if( player != null ) 
		{			
			while( iter.hasNext() )
			{
				block = iter.next();
	
				//Limited Block & MetaId == Placed Block & MetaId
				if( ( block.getItemStack().getItem().equals( itemStack.getItem() ) ) && 
					( block.getItemStack().getItemDamage() == itemStack.getItemDamage() ) )
				{
					event.setCanceled( true );
					
					Log.debug( "Block Place Event begins." );
					Log.debug( "Block: " + block.toString() );
					
					
					if( player.canPlaceBlock( event.world, block ) )
					{
						player.addBlock( block, coordinate );
						event.setCanceled( false );
						
						Log.debug( "Block placed." );	
					}
				
					Log.debug( "Block Place Event ends." );
				}
			}
		}		
	}
*/
	
	
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
	
}
