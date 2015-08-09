package de.alaoli.games.minecraft.mods.limitedresources.command;

import java.util.ArrayList;
import java.util.List;
import de.alaoli.games.minecraft.mods.limitedresources.LimitedResources;
import de.alaoli.games.minecraft.mods.limitedresources.data.Coordinate;
import de.alaoli.games.minecraft.mods.limitedresources.data.LimitedBlock;
import de.alaoli.games.minecraft.mods.limitedresources.entity.LimitedBlockPlayer;
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
		StringBuilder result = new StringBuilder();
		
		result.append( "/" );
		result.append( this.aliases.get( 0 ) );
		result.append( " or /" );
		result.append( this.aliases.get( 1 ) );
		result.append( " " );
		result.append( SUBCOMMAND_HELP );
		result.append( " | " );
		result.append( SUBCOMMAND_LIMITS );
		result.append( " | " );
		result.append( SUBCOMMAND_WHERE );
			
		return result.toString();
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
		StringBuilder message;
		
		LimitedBlockPlayer player = LimitedBlockPlayer.get( 
			sender.getEntityWorld().getPlayerEntityByName( sender.getCommandSenderName() ) 
		);
		sender.addChatMessage( new ChatComponentText("List of all limited Blocks:"));
		
		for( LimitedBlock block : LimitedResources.limitedBlocks )
		{
			message = new StringBuilder();
			
			message.append( " - " );
			message.append( block.getLimit() );
			message.append( "x " );
			message.append( block.getItemStack().getDisplayName() );
			message.append( "." );
			
			//Player with limited blocks
			if( ( player != null ) && 
				( player.hasBlockPlaced( block ) ) )
			{
				message.append( " " );
				message.append( player.countBlocksPlaced( block ) );
				message.append( " of " );
				message.append( block.getLimit() );
				message.append( " placed." );
			}
			sender.addChatMessage( new ChatComponentText( message.toString() ) );
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
		StringBuilder message;
		
		LimitedBlockPlayer player = LimitedBlockPlayer.get( 
			sender.getEntityWorld().getPlayerEntityByName( sender.getCommandSenderName() ) 
		);
		
		for( LimitedBlock block : LimitedResources.limitedBlocks )
		{
			message = new StringBuilder();
			message.append( block.getItemStack().getDisplayName() );
			message.append( " placed at (dim, x, y, z): " );
			
			sender.addChatMessage( new ChatComponentText( message.toString() ) );
			
			//Player with limited blocks
			if( ( player != null ) && 
				( player.hasBlockPlaced( block ) ) )
			{
				for( Coordinate coordinate : player.getCoordinatsFor( block ) )
				{
					message = new StringBuilder();
					message.append( "  " );
					message.append( coordinate.toString() );
					
					sender.addChatMessage( new ChatComponentText( message.toString() ) );
				}
			}
			else
			{
				sender.addChatMessage( new ChatComponentText( "  No coordinates." ) );
			}
		}
	}	
}
