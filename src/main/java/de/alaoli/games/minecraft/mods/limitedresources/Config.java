package de.alaoli.games.minecraft.mods.limitedresources;

import java.util.Set;

import net.minecraftforge.common.config.Configuration;
import de.alaoli.games.minecraft.mods.limitedresources.command.LimitedResourcesCommand;
import de.alaoli.games.minecraft.mods.limitedresources.data.LimitedBlock;
import de.alaoli.games.minecraft.mods.limitedresources.util.ParserUtil;

public class Config 
{
	/********************************************************************************
	 * Constants
	 ********************************************************************************/
	
	/**
	 * Notification after placing a limited block.
	 */
	public static final int MESSAGES_NOTIFICATION_ALWAYS = 0;
	
	/**
	 * Notification only if limit is reached.
	 */
	public static final int MESSAGES_NOTIFICATION_IF_LIMIT_REACHED = 1;
	
	/********************************************************************************
	 * Attributes
	 ********************************************************************************/

	public static class LimitedBlocks
	{
		/**
		 * Limited Blocks are enabled.
		 */
		public static boolean isEnabled;
		
		/**
		 * Limited Blocks <mod>:<block>[@<metaid>]=<limit>. 
		 * Example minecraft:stone=2 allows 2 Stone placed per Player.
		 */
		public static String[] blockList;
	}
	
	public static class Commands
	{
		/**
		 * Change command alias in case of an conflict with other mod commands.
		 */
		public static String shortAlias;
	}
	
	public static class Messages
	{
		/**
		 * 0: Notification after placing a limited block.
		 * 1: Notification only if limit is reached.
		 */
		public static int notificationMode;
	}
	/********************************************************************************
	 * Methods
	 ********************************************************************************/
	
	/**
	 * Configuration initialization 
	 * 
	 * @param Configuration
	 */
	public static void init( Configuration configFile )
	{
    	configFile.load();
    	
    	//Limited Blocks
    	Config.LimitedBlocks.isEnabled = configFile.getBoolean( 
			"isEnabled", 
			"limitedBlocks", 
			true, 
			"Limited Blocks are enabled.\n" 
		);
    	Config.LimitedBlocks.blockList = configFile.getStringList(
			"blockList", 
			"limitedBlocks", 
			new String[]{}, 
			"Limited Blocks <mod>:<block>[@<metaid>]=<limit>.\n"
			+ "Example minecraft:stone=2 allows 2 Stone placed per Player.\n"
		);
    	
    	//Commands
    	Config.Commands.shortAlias = configFile.getString( 
			"shortAlias", 
			"commands", 
			LimitedResourcesCommand.COMMAND_ALIAS, 
			"Change command alias in case of an conflict with other mod commands.\n"
		);
    	
    	//Messages
    	Config.Messages.notificationMode = configFile.getInt(
			"notificationMode",
			"messages", 
			MESSAGES_NOTIFICATION_ALWAYS, 
			MESSAGES_NOTIFICATION_ALWAYS,
			MESSAGES_NOTIFICATION_IF_LIMIT_REACHED,
			"0: Notification after placing a limited block.\n"
			+ "1: Notification only if limit is reached.\n"
		);
    	
    	if( configFile.hasChanged() == true )
    	{
    		configFile.save();
    	}
	}
	
	/**
	 * Parses Config blockList to an LimitedBlock Set
	 * 
	 * @return Set<LimitedBlock>
	 */
	public static Set<LimitedBlock> createLimitedBlockSet()
	{
		return ParserUtil.parseStringListToLimitedBlockSet( Config.LimitedBlocks.blockList );
	}
}
