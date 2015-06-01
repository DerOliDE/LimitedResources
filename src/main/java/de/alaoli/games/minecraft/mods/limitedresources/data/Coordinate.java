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
