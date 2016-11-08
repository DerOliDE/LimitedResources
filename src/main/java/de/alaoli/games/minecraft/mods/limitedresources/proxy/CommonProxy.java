package de.alaoli.games.minecraft.mods.limitedresources.proxy;

import java.util.HashSet;

import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import de.alaoli.games.minecraft.mods.limitedresources.Config;
import de.alaoli.games.minecraft.mods.limitedresources.LimitedResources;
import de.alaoli.games.minecraft.mods.limitedresources.command.LimitedResourcesCommand;
import de.alaoli.games.minecraft.mods.limitedresources.data.LimitedBlock;
import de.alaoli.games.minecraft.mods.limitedresources.event.BlockPlacingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

public class CommonProxy 
{ 
    public void preInit( FMLPreInitializationEvent event ) 
    {
    	Configuration configFile = new Configuration( event.getSuggestedConfigurationFile() );
    	
    	Config.init( configFile );
    	
    	if( Config.LimitedBlocks.isEnabled )
    	{
    		MinecraftForge.EVENT_BUS.register( new BlockPlacingEvent() );
    	}
    	LimitedResources.limitedBlocks = new HashSet<LimitedBlock>();
    }
    
    public void init( FMLInitializationEvent event )
    {
    	LimitedResources.limitedBlocks.addAll( Config.createLimitedBlockSet() );
    }

    public void serverInit( FMLServerStartingEvent event )
    {
		if( Config.LimitedBlocks.isEnabled )
		{
			event.registerServerCommand( new LimitedResourcesCommand( Config.Commands.shortAlias ) );
		}
    }
}
