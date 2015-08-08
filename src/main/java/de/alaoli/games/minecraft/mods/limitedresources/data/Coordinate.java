package de.alaoli.games.minecraft.mods.limitedresources.data;

public class Coordinate 
{
	/********************************************************************************
	 * Attributes
	 ********************************************************************************/
	
	private int dimId;
	
	private int x;
	
	private int y;
	
	private int z;
	
	/********************************************************************************
	 * Methodes - Constructor / Overrides
	 ********************************************************************************/
	
	public Coordinate( int dimId, int x, int y, int z )
	{
		this.dimId = dimId;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public String toString() 
	{
		String result;
		
		result  = String.valueOf( this.dimId ) + ", ";
		result += String.valueOf( this.x ) + ", ";
		result += String.valueOf( this.y ) + ", ";
		result += String.valueOf( this.z );
		
		return result;
	}
	
	@Override
	public boolean equals( Object obj ) 
	{
		if( this == obj ) 
		{
			return true;
		}
		if( obj == null )
		{
			return false;
		}
		Coordinate coordinate = (Coordinate) obj;
		
		if( ( coordinate.getDimId() == this.dimId ) &&
			( coordinate.getX() == this.x ) &&
			( coordinate.getY() == this.y ) &&
			( coordinate.getZ() == this.z ) )
		{
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() 
	{
		int hashCode = 8343;
		
		hashCode += this.dimId;
		hashCode += this.x;
		hashCode += this.y;
		hashCode += this.z;
		
		return hashCode;
	}
	
	/********************************************************************************
	 * Methodes - Getter / Setter
	 ********************************************************************************/
	
	public int getDimId() 
	{
		return this.dimId;
	}

	public int getX() 
	{
		return this.x;
	}

	public int getY()
	{
		return this.y;
	}

	public int getZ()
	{
		return this.z;
	}
}
