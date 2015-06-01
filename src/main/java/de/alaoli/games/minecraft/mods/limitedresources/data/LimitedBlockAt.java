package de.alaoli.games.minecraft.mods.limitedresources.data;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class LimitedBlockAt 
{
	/********************************************************************************
	 * Attributes
	 ********************************************************************************/
	
	private LimitedBlock block;
	
	private Set<Coordinate> coordinates;
	
	/********************************************************************************
	 * Methodes - Constructor / Overrides
	 ********************************************************************************/
	
	public LimitedBlockAt( LimitedBlock block )
	{
		this.block = block;
		this.coordinates = new HashSet<Coordinate>();
	}
	
	public LimitedBlockAt( LimitedBlock block, Set<Coordinate> coordinates )
	{
		this.block = block;
		this.coordinates = coordinates;
	}	

	@Override
	public String toString() 
	{
		String result;
		Iterator<Coordinate> iter;
		Coordinate coordinate;
		
		result = this.block.toString() + "|";
		iter = this.coordinates.iterator();
		
		while( iter.hasNext() )
		{
			coordinate = iter.next(); 
			result += coordinate.toString();
			
			if( iter.hasNext() )
			{
				result += ";";
			}
		}		
		return result;
	}	
	
	/********************************************************************************
	 * Methodes - Getter / Setter
	 ********************************************************************************/
	
	public LimitedBlock getLimitedBlock() 
	{
		return this.block;
	}
	
	public Set<Coordinate> getCoordinates() 
	{
		return this.coordinates;
	}

	/********************************************************************************
	 * Methodes
	 ********************************************************************************/
	
	/**
	 * Check if Block on Coordinate still exists
	 */
	public void refreshCoordinates( World world )
	{
		int x;
		int y;
		int z;
		int metaId;
		
		Block block;
		ItemStack itemStack;
		Coordinate coordinate;
		Set<Coordinate> toRemove;
		Iterator<Coordinate> iter;
		
		iter = this.coordinates.iterator();
		toRemove = new HashSet<Coordinate>();
		
		while( iter.hasNext() )
		{
			coordinate = iter.next();
			
			x = coordinate.getX();
			y = coordinate.getY();
			z = coordinate.getZ();
			
			block = world.getBlock( x, y, z );
			metaId = world.getBlockMetadata( x, y, z );
			itemStack = new ItemStack( block, 1, metaId );
			
			//if Block or MetaId != remove Coordinate
			if( ( this.block.getItemStack().getItem().equals( itemStack.getItem() ) == false ) || 
				( this.block.getItemStack().getItemDamage() != itemStack.getItemDamage() ) )			
			{
				toRemove.add( coordinate );
			}
		}
		this.coordinates.removeAll( toRemove );
	}
	
	public boolean canPlaceBlock( World world )
	{
		this.refreshCoordinates( world );
		
		if( this.block.getLimit() > this.coordinates.size() )
		{
			return true;
		}
		else
		{
			return false;	
		}
	}	
}
