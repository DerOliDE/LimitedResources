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
		Coordinate coordinate;
		Iterator<Coordinate> iter = this.coordinates.iterator();
		StringBuilder result = new StringBuilder();
		
		result.append( this.block.toString() );
		result.append( "|" );
		
		while( iter.hasNext() )
		{
			coordinate = iter.next();
			result.append( coordinate.toString() );
			
			if( iter.hasNext() )
			{
				result.append( ";" );
			}
		}		
		return result.toString();
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
