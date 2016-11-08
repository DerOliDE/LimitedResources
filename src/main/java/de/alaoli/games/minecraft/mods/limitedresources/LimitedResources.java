package de.alaoli.games.minecraft.mods.limitedresources;

import java.util.Set;

import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import de.alaoli.games.minecraft.mods.limitedresources.data.LimitedBlock;
import de.alaoli.games.minecraft.mods.limitedresources.proxy.CommonProxy;


@Mod( modid = LimitedResources.MODID, version = LimitedResources.VERSION, name = LimitedResources.NAME )
public class LimitedResources
{
	/********************************************************************************
	 * Mod Info
	 ********************************************************************************/

	public static final String MODID	= "limitedresources";
	public static final String NAME		= "Limited Resources";
	public static final String VERSION	= "1.1.1";
						
	/********************************************************************************
	 * Attributes
	 ********************************************************************************/
	
	public static Set<LimitedBlock> limitedBlocks; 
	
	/********************************************************************************
	 * Forge
	 ********************************************************************************/

	@SidedProxy(
		clientSide = "de.alaoli.games.minecraft.mods.limitedresources.proxy.CommonProxy", 
		serverSide = "de.alaoli.games.minecraft.mods.limitedresources.proxy.ServerProxy"
	)
	public static CommonProxy proxy;
		
	/********************************************************************************
	 * Methods - Forge Event Handler
	 ********************************************************************************/

    @EventHandler 
    public void preInit( FMLPreInitializationEvent event ) 
    {
    	proxy.preInit( event );
    }
    
    @EventHandler
    public void init( FMLInitializationEvent event )
    {
    	proxy.init( event );
    }

    @EventHandler
    public void serverInit( FMLServerStartingEvent event )
    {
		proxy.serverInit( event );
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
