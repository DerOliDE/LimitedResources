package de.alaoli.games.minecraft.mods.limitedresources;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import de.alaoli.games.minecraft.mods.limitedresources.command.LimitedResourcesCommand;
import de.alaoli.games.minecraft.mods.limitedresources.data.LimitedBlock;
import de.alaoli.games.minecraft.mods.limitedresources.event.BlockPlacingEvent;
import de.alaoli.games.minecraft.mods.limitedresources.proxy.CommonProxy;


@Mod( modid = LimitedResources.MODID, version = LimitedResources.VERSION, name = LimitedResources.NAME )
public class LimitedResources
{
	/********************************************************************************
	 * Mod Info
	 ********************************************************************************/

	public static final String MODID	= "limitedresources";
	public static final String NAME		= "Limited Resources";
	public static final String VERSION	= "1.1.0";
						
	/********************************************************************************
	 * Attributes
	 ********************************************************************************/
	
	public static Set<LimitedBlock> limitedBlocks; 
	
	/********************************************************************************
	 * Forge
	 ********************************************************************************/

	@SidedProxy(
		clientSide = "de.alaoli.games.minecraft.mods.limitedresources.proxy.ClientProxy", 
		serverSide = "de.alaoli.games.minecraft.mods.limitedresources.proxy.ServerProxy"
	)
	public static CommonProxy proxy;
		
	/********************************************************************************
	 * Methods - Forge Event Handler
	 ********************************************************************************/

    @EventHandler 
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
    
    @EventHandler
    public void init( FMLInitializationEvent event )
    {
    	proxy.registerRenderers();
    	
    	LimitedResources.limitedBlocks.addAll( Config.createLimitedBlockSet() );
    }

    @EventHandler
    public void serverInit( FMLServerStartingEvent event )
    {
		if( Config.LimitedBlocks.isEnabled )
		{
			event.registerServerCommand( new LimitedResourcesCommand( Config.Commands.shortAlias ) );
		}
    }
    
	/********************************************************************************
	 * Methods - Getter / Setter
	 ********************************************************************************/    
    
    /**
     * Search for LimitedBlock Reference by ItemStack
     * 
     * @param ItemStack
     * @return LimitedBlock|null
     */
    public static LimitedBlock getLimitedBlockByItemStack( ItemStack itemStack )
    {
    	for( LimitedBlock block : LimitedResources.limitedBlocks )
    	{
    		if( block.isLimitedBlock( itemStack ) )
    		{
    			return block;
    		}
    	}
		return null;
    }
}
