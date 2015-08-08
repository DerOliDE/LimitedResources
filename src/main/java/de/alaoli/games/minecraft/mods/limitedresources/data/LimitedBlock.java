package de.alaoli.games.minecraft.mods.limitedresources.data;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;

public class LimitedBlock 
{
	/********************************************************************************
	 * Attributes
	 ********************************************************************************/	
	
	private ItemStack itemStack;
	
	public int limit;
	
	/********************************************************************************
	 * Methodes - Constructor / Overrides
	 ********************************************************************************/
	
	public LimitedBlock( ItemStack itemStack, int limit )
	{
		this.itemStack = itemStack;
		this.limit = limit;
	}

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder(); 
				
		result.append( GameRegistry.findUniqueIdentifierFor( this.itemStack.getItem() ).toString() );
				
		if( this.itemStack.getItemDamage() > 0 )
		{
			result.append( "@" );
			result.append( this.itemStack.getItemDamage() );
		}	
		return result.toString();
	}

	@Override
	public int hashCode() 
	{
		return this.itemStack.hashCode();
	}

	@Override
	public boolean equals( Object obj ) 
	{
		ItemStack itemStack = ((LimitedBlock)obj).getItemStack();
			
		if( ( this.itemStack.getItem().equals( itemStack.getItem() ) ) &&
			( this.itemStack.getItemDamage() == itemStack.getItemDamage() ) )
		{
			return true;
		}
		return false;
	}
	
	/********************************************************************************
	 * Methodes - Getter / Setter
	 ********************************************************************************/
	
	public ItemStack getItemStack() 
	{
		return this.itemStack;
	}

	public int getLimit() 
	{
		return this.limit;
	}
}
