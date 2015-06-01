package de.alaoli.games.minecraft.mods.limitedresources;

import java.util.Set;

import net.minecraftforge.common.config.Configuration;
import de.alaoli.games.minecraft.mods.limitedresources.data.LimitedBlock;
import de.alaoli.games.minecraft.mods.limitedresources.util.ParserUtil;

public class Config 
{
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
    	
    	Config.LimitedBlocks.isEnabled = configFile.getBoolean( 
			"isEnabled", 
			"limitedBlocks", 
			true, 
			"Limited Blocks are enabled." 
		);
    	Config.LimitedBlocks.blockList = configFile.getStringList(
			"blockList", 
			"limitedBlocks", 
			new String[]{}, 
			"Limited Blocks <mod>:<block>[@<metaid>]=<limit>. Example minecraft:stone=2 allows 2 Stone placed per Player."
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
