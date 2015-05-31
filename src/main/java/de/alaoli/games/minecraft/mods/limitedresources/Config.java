package de.alaoli.games.minecraft.mods.limitedresources;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import de.alaoli.games.minecraft.mods.limitedresources.data.LimitedBlock;
import de.alaoli.games.minecraft.mods.limitedresources.util.Parser;

public class Config 
{
	/********************************************************************************
	 * Attributes
	 ********************************************************************************/
	
	public static class Debug
	{
		/**
		 * Debug Messages are enabled
		 */
		public static boolean isEnabled;
	}
	
	public static class LimitedBlocks
	{
		/*
		 * LimitedBlocks are enabled
		 */
		public static boolean isEnabled;
		
		/*
		 * List of limited Blocks
		 */
		public static String[] blockList;
	}
	
	/********************************************************************************
	 * Methods
	 ********************************************************************************/
	
	public static void init( Configuration configFile )
	{
    	configFile.load();
    	
    	Config.Debug.isEnabled = configFile.getBoolean( 
			"isEnabled", 
			"debugging", 
			false, 
			"Debug Messages are enabled" 
		);
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
	
	public static Set<LimitedBlock> createLimitedBlockSet()
	{
		return Parser.parseStringListToLimitedBlockSet( Config.LimitedBlocks.blockList );
	}
}
