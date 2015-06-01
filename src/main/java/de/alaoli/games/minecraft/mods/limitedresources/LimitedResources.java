package de.alaoli.games.minecraft.mods.limitedresources;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import de.alaoli.games.minecraft.mods.limitedresources.data.LimitedBlock;
import de.alaoli.games.minecraft.mods.limitedresources.events.BlockPlacingEvent;
import de.alaoli.games.minecraft.mods.limitedresources.proxy.CommonProxy;


@Mod( modid = LimitedResources.MODID, version = LimitedResources.VERSION, name = LimitedResources.NAME )
public class LimitedResources
{
	/********************************************************************************
	 * Mod Info
	 ********************************************************************************/

	public static final String MODID	= "limitedresources";
	public static final String NAME		= "Limited Resources";
	public static final String VERSION	= "0.9.0";
						
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
    	LimitedBlock block;
    	Iterator<LimitedBlock> iter = LimitedResources.limitedBlocks.iterator();
    	
    	while( iter.hasNext() )
    	{
    		block = iter.next();
    		
			if( ( block.getItemStack().getItem().equals( itemStack.getItem() ) ) && 
				( block.getItemStack().getItemDamage() == itemStack.getItemDamage() ) )
			{
				return block;
			}
    	}
		return null;
    }
}
