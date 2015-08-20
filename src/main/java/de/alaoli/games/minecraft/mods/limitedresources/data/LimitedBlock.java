package de.alaoli.games.minecraft.mods.limitedresources.data;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class LimitedBlock 
{
	/********************************************************************************
	 * Attributes
	 ********************************************************************************/	
	
	private ItemStack itemStack;
	
	private int limit;
	
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
			result.append( ":" );
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
			
		return this.isLimitedBlock( itemStack );
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
	
	/********************************************************************************
	 * Methodes
	 ********************************************************************************/	
	
	/**
	 * Checks if block is an limited block
	 * 
	 * @param ItemStack
	 * @return boolean
	 */
	public boolean isLimitedBlock( ItemStack itemStackA )
	{
		if( ( itemStackA == null ) ||
			( itemStackA.getItem() == null ) )
		{
			return false;
		}
		ItemStack itemStackB = this.getItemStack();
		
		//Ignore MetaId
		if( ( itemStackA.getItem().equals( itemStackB.getItem() ) ) &&
			( itemStackB.getItemDamage() == OreDictionary.WILDCARD_VALUE ) )
		{
			return true;
		}
			
		//With MetaId
		if( ( itemStackA.getItem().equals( itemStackB.getItem() ) ) &&
			( itemStackA.getItemDamage() == itemStackB.getItemDamage() ) )
		{
			return true;
		}
		return false;
	}
}
