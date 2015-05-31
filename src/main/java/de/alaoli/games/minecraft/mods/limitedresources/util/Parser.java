package de.alaoli.games.minecraft.mods.limitedresources.util;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import de.alaoli.games.minecraft.mods.limitedresources.Config;
import de.alaoli.games.minecraft.mods.limitedresources.LimitedResources;
import de.alaoli.games.minecraft.mods.limitedresources.Log;
import de.alaoli.games.minecraft.mods.limitedresources.data.Coordinate;
import de.alaoli.games.minecraft.mods.limitedresources.data.LimitedBlock;
import de.alaoli.games.minecraft.mods.limitedresources.data.LimitedBlockAt;

public class Parser 
{
	/**
	 * Parse from String "<mod>:<block>[@<metaid>]" to ItemStack Object
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
    	
    	parts = itemStack.split( "[:@]");
    	
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
    		metaid = Integer.parseInt( parts[2] );
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
	 * Parse from String "<mod>:<block>[@<metaid>]=<limit>" to LimitedBlock Object
	 * 
	 * @param String
	 * @return LimitedBlock
	 * @throws ParseException
	 */
	public static LimitedBlock parseStringToLimitedBlock( String limitedBlock ) throws ParseException
	{
		int limit;
    	String[] parts;
    	ItemStack itemStack;
    	    
    	parts = limitedBlock.split( "[=]");
    	
    	if( parts.length < 2 )
    	{
    		throw new ParseException( "Can't parse entry '" + limitedBlock + "'.", 0 );
    	}
    	itemStack = Parser.parseStringToItemStack( parts[ 0 ] );
    	limit = Integer.parseInt( parts[ 1 ] );
 			
    	return new LimitedBlock( itemStack, limit );		
	}
	
	/**
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
				block = Parser.parseStringToLimitedBlock( blockList[ i ] );
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
	
	
	/**
	 * Parse from String "<dimId>,<x>,<y>,<z>" to Coordinate Object
	 * @param String
	 * @return Coordinate
	 * @throws ParseException
	 */
	public static Coordinate parseStringToCoordinate( String coordinate ) throws ParseException
	{
		int dimId;
		int x;
		int y;
		int z;
		String[] parts;
		
		parts = coordinate.split( "[,]");
		
		if( parts.length < 4 )
		{
			throw new ParseException( "Can't parse string '" + coordinate + "'.", 0 );
		}
		dimId	= Integer.parseInt( parts[ 0 ] );
		x		= Integer.parseInt( parts[ 1 ] );
		y		= Integer.parseInt( parts[ 2 ] );
		z		= Integer.parseInt( parts[ 3 ] );
		
		return new Coordinate( dimId, x, y, z );
	}
	/**
	 * Parse from String "<dimId>,<x>,<y>,<z>;<dimId>,<x>,<y>,<z>;....." to Set<Coordinate> Object 
	 * 
	 * @param coordinates
	 * @return Set<Coordinate>
	 * @throws ParseException 
	 */
	public static Set<Coordinate> parseStringToCoordinateSet( String coordinates ) throws ParseException
	{
		String[] parts;
		Set<Coordinate> result;
		
		parts = coordinates.split( "[;]");
		result = new HashSet<Coordinate>();
		
		for( String part : parts )
		{
			result.add( Parser.parseStringToCoordinate( part ) );
		}
		return result;
	}
	/**
	 * Example String "minecraft:stone|0,-223,72,212;0,-224,72,211"
	 * 
	 * @param String
	 * @return LimitedBlockAt
	 * @throws ParseException
	 */
	public static LimitedBlockAt parseStringtoLimitedBlockAt( String limitedBlockAt ) throws ParseException
	{
		String[] parts;
		LimitedBlock block;
		LimitedBlockAt blockAt;
		ItemStack itemStack;
		Set<Coordinate> coordinates;
		
		//Split into ItemStack & Coordinates
		parts = limitedBlockAt.split( "[|]");
		
		//Both must exists
		if( parts.length < 2 )
		{
			throw new ParseException( "Can't parse entry '" + limitedBlockAt + "'.", 0 );
		}
		itemStack = Parser.parseStringToItemStack( parts[ 0 ] );
		block = LimitedResources.getLimitedBlockByItemStack( itemStack );
		blockAt = new LimitedBlockAt( block );
		
		//ItemStack must exist as Limited Block
		if( block == null )
		{
			throw new ParseException( itemStack.toString() + " doesn't exists (anymore) as Limited Block.", 0 );
		}
		coordinates = Parser.parseStringToCoordinateSet( parts[ 1 ] );
		
		blockAt.getCoordinates().addAll( coordinates );

		return blockAt;
	}
}
