package de.alaoli.games.minecraft.mods.limitedresources.util;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.registry.GameRegistry;
import de.alaoli.games.minecraft.mods.limitedresources.Log;
import de.alaoli.games.minecraft.mods.limitedresources.data.LimitedBlock;

public class ParserUtil 
{
	/**
	 * Parse from String "<mod>:<block>[:<metaid>|:*]" to ItemStack Object
	 * MetaId * to igonore metaids.
	 * 
	 * @param String
	 * @return ItemStack
	 * @throws ParseException 
	 */
	public static ItemStack parseStringToItemStack( String itemStack ) throws ParseException
	{
		int metaid;
		Block block;
    	String modName;
    	String blockName;
    	String[] parts;
    	
    	parts = itemStack.split( "[:]");
    	
    	if( parts.length == 2 )			//without MetaID
    	{
    		modName = parts[0];
    		blockName = parts[1];
    		metaid = 0;
    	}
    	else if( parts.length == 3 )	//with MetaID
    	{
    		modName = parts[0];
    		blockName = parts[1];
    		
    		//Ignore MetaIDs
    		if( parts[ 2 ].contains( "*" ) )
    		{
    			metaid = OreDictionary.WILDCARD_VALUE;
    		}
    		else
    		{
    			metaid = Integer.parseInt( parts[2] );
    		}
    	}
    	else
    	{
    		throw new ParseException( "Can't parse entry '" + itemStack + "'.", 0 );
    	}    	
    	block = GameRegistry.findBlock( modName, blockName );
    	
    	if( block == null )
    	{
    		throw new ParseException( "Block '" + modName + ":" + blockName  + "' doesn't exist.", 0 );
    	}
    	return new ItemStack( block, 1, metaid );
	}
	
	/**
	 * Parse from String "<mod>:<block>[:<metaid>|:*]" to ItemStack Object
	 * MetaId * to igonore metaids.
	 * 
	 * @param String
	 * @return LimitedBlock
	 * @throws ParseException
	 */
	public static LimitedBlock parseStringToLimitedBlock( String limitedBlock ) throws ParseException
	{ 
		boolean ignoreMetaId = false;
    	String[] parts = limitedBlock.split( "[=]");
    	
    	if( parts.length < 2 )
    	{
    		throw new ParseException( "Can't parse entry '" + limitedBlock + "'.", 0 );
    	}
    	ItemStack itemStack = ParserUtil.parseStringToItemStack( parts[ 0 ] );
    	int limit = Integer.parseInt( parts[ 1 ] );
 			
    	return new LimitedBlock( itemStack, limit );		
	}
	
	/**
	 * Parse from String BlockList to <Set>LimitedBlock> 
	 * 
	 * @param String[] 
	 * @return Set<LimitedBlock>
	 */
	public static Set<LimitedBlock> parseStringListToLimitedBlockSet( String[] blockList )
	{
		LimitedBlock block;
		Set<LimitedBlock> limitedBlocks;
		
		limitedBlocks = new HashSet<LimitedBlock>();
		
    	for( int i = 0; i < blockList.length; i++ )
    	{
    		block = null;
    		
    		try 
    		{
				block = ParserUtil.parseStringToLimitedBlock( blockList[ i ] );
			} 
    		catch ( ParseException e )
    		{
				Log.error( e.getMessage() );
			}
    		
    		if( block != null )
    		{
    			if( limitedBlocks.contains( block ) )
    			{
    				Log.warn( block.getItemStack().getUnlocalizedName() + " doubled entry in config file." );
    			}
    			else
    			{
    				limitedBlocks.add( block );
    			}
    		}
    	}
		return limitedBlocks;
	}
}
