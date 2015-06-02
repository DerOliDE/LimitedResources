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
		String result = GameRegistry.findUniqueIdentifierFor( this.itemStack.getItem() ).toString();
				
		if( this.itemStack.getItemDamage() > 0 )
		{
			result += "@" + this.itemStack.getItemDamage(); 
		}	
		return result;
	}

	@Override
	public int hashCode() 
	{
		return this.itemStack.hashCode();
	}

	@Override
	public boolean equals( Object obj ) 
	{
		boolean result = false;
	
		if( obj instanceof LimitedBlock )
		{
			ItemStack itemStack = ((LimitedBlock)obj).getItemStack();
			
			if( ( this.itemStack.getItem().equals( itemStack.getItem() ) ) &&
				( this.itemStack.getItemDamage() == itemStack.getItemDamage() ) )
			{
				result = true;
			}
		}
		return result;
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
