package de.alaoli.games.minecraft.mods.limitedresources.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import de.alaoli.games.minecraft.mods.limitedresources.LimitedResources;
import de.alaoli.games.minecraft.mods.limitedresources.data.Coordinate;
import de.alaoli.games.minecraft.mods.limitedresources.data.LimitedBlock;
import de.alaoli.games.minecraft.mods.limitedresources.data.LimitedBlockAt;
import de.alaoli.games.minecraft.mods.limitedresources.entity.EntityPlayerWithLimitedBlocks;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class LimitedResourcesCommand implements ICommand
{
	/********************************************************************************
	 * Constants
	 ********************************************************************************/
	
	public static final String COMMAND			= "limitedresources";
	public static final String COMMAND_ALIAS	= "lr";
	
	public static final String SUBCOMMAND_HELP		= "help";
	public static final String SUBCOMMAND_LIMITS	= "limits";
	public static final String SUBCOMMAND_WHERE		= "where";
	
	/********************************************************************************
	 * Attributes
	 ********************************************************************************/
	
	List<String> aliases;
	
	/********************************************************************************
	 * Methods - Constructor
	 ********************************************************************************/
	
	public LimitedResourcesCommand( String shortAlias )
	{
		this.aliases = new ArrayList<String>();
		this.aliases.add( COMMAND );
		this.aliases.add( shortAlias );
	}
	
	/********************************************************************************
	 * Interface - ICommand
	 ********************************************************************************/
	
	@Override
	public int compareTo( Object obj )
	{
		return 0;
	}

	@Override
	public String getCommandName() 
	{
		return COMMAND;
	}

	@Override
	public String getCommandUsage( ICommandSender sender ) 
	{
		String result;
		
		result  = "/" + this.aliases.get( 0 ) + " or /";
		result += this.aliases.get( 1 ) + " ";
		result += SUBCOMMAND_HELP + " | ";
		result += SUBCOMMAND_LIMITS + " | ";
		result += SUBCOMMAND_WHERE;
				
		return result;
	}

	@Override
	public List getCommandAliases() 
	{
		return this.aliases;
	}

	@Override
	public void processCommand( ICommandSender sender, String[] args )
	{
		if( args.length == 0 )
		{
			this.processCommandHelp( sender, args );
			return;
		}
		args[ 0 ] = args[ 0 ].toLowerCase();
		
		switch( args[ 0 ] )
		{
			case SUBCOMMAND_HELP:
				this.processCommandHelp( sender, args );
			break;
			
			case SUBCOMMAND_LIMITS:
				this.processCommandLimits( sender, args );
				break;
				
			case SUBCOMMAND_WHERE:
				this.processCommandWhere( sender, args );
				break;
				
			default:
				this.processCommandHelp( sender, args );
				break;
		}
	}

	@Override
	public boolean canCommandSenderUseCommand( ICommandSender sender ) 
	{
		return true;
	}

	@Override
	public List addTabCompletionOptions( ICommandSender sender, String[] args ) 
	{
	   List<String> list = new ArrayList<String>();
	   
	   list.add( SUBCOMMAND_HELP );
	   list.add( SUBCOMMAND_LIMITS );
	   list.add( SUBCOMMAND_WHERE );
		
	   return list;
	}

	@Override
	public boolean isUsernameIndex(String[] list, int p_82358_2_) {
		// TODO Auto-generated method stub
		return false;
	}

	/********************************************************************************
	 * Methods - Messages
	 ********************************************************************************/

	/**
	 * Only sends a "No coordinates" message
	 * 
	 * @param ICommandSender
	 */
	private void messageNoCoordinates( ICommandSender sender )
	{
		sender.addChatMessage( new ChatComponentText( "  No coordinates." ) );
	}

	/**
	 * Lists coordinates 
	 * 
	 * @param ICommandSender
	 * @param LimitedBlockAt
	 */
	private void messageLimitedBlockAt( ICommandSender sender, LimitedBlockAt block )
	{
		//No block no coordinates to list
		if( block == null )
		{
			this.messageNoCoordinates( sender );
			return;
		}
		
		if( block.getCoordinates().size() == 0 )
		{
			this.messageNoCoordinates( sender );
			return;
		}
		Iterator<Coordinate> iter = block.getCoordinates().iterator();
		
		//Lists coordinates
		while( iter.hasNext() )
		{
			sender.addChatMessage( new ChatComponentText( "  " + iter.next().toString() ) );	
		}
	}
	
	/********************************************************************************
	 * Methods - Process Commands
	 ********************************************************************************/
	
	/**
	 * Command Usage
	 * 
	 * @param ICommandSender
	 * @param String[]
	 */
	private void processCommandHelp( ICommandSender sender, String[] args )
	{
		sender.addChatMessage( new ChatComponentText( this.getCommandUsage( sender ) ) );
	}
	
	/**
	 * List of all limited Blocks and how many the player has placed yet.
	 * 
	 * @param ICommandSender
	 * @param String[]
	 */
	private void processCommandLimits( ICommandSender sender, String[] args )
	{
		String message;
		LimitedBlock block;
		LimitedBlockAt blockAt;
		
		EntityPlayerWithLimitedBlocks player = EntityPlayerWithLimitedBlocks.get( 
			sender.getEntityWorld().getPlayerEntityByName( sender.getCommandSenderName() ) 
		);
		Iterator<LimitedBlock> iter = LimitedResources.limitedBlocks.iterator();
		
		sender.addChatMessage( new ChatComponentText("List of all limited Blocks:"));
			
		while( iter.hasNext() )
		{
			block = iter.next();
			
			message  = " - ";
			message += String.valueOf( block.getLimit() ) + "x ";
			message += block.getItemStack().getDisplayName() + ".";
			
			//Player with limited blocks
			if( player != null )
			{
				blockAt = player.getLimitedBlockAt( block );
				
				//has placed limited block
				if( blockAt != null )
				{
					message += " " + String.valueOf( blockAt.getCoordinates().size() );
					message += " of ";
					message += String.valueOf( block.getLimit() ) + " placed.";
				}
			}
			sender.addChatMessage( new ChatComponentText( message ) );
		}
		
	}
	
	/**
	 * Lists where the Player has placed limited blocks
	 * 
	 * @param ICommandSender
	 * @param String[]
	 */
	private void processCommandWhere( ICommandSender sender, String[] args )
	{
		LimitedBlock block;
		
		EntityPlayerWithLimitedBlocks player = EntityPlayerWithLimitedBlocks.get( 
			sender.getEntityWorld().getPlayerEntityByName( sender.getCommandSenderName() ) 
		);
		Iterator<LimitedBlock> iter = LimitedResources.limitedBlocks.iterator();

		while( iter.hasNext() )
		{
			block = iter.next();
			
			sender.addChatMessage( new ChatComponentText( 
				block.getItemStack().getDisplayName() + " placed at (dim, x, y, z): " 
			) );
			
			//Player with limited blocks
			if( player != null )
			{
				this.messageLimitedBlockAt( sender, player.getLimitedBlockAt( block ) );
			}
			else
			{
				this.messageNoCoordinates( sender );
			}
		}
	}	
}
