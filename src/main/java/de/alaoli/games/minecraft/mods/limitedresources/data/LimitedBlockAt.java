package de.alaoli.games.minecraft.mods.limitedresources.data;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
}
