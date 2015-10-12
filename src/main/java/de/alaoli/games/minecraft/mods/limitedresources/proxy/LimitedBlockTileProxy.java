package de.alaoli.games.minecraft.mods.limitedresources.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class LimitedBlockTileProxy extends TileEntity implements InvocationHandler
{
	private TileEntity encapsulatedTileEntity;
	
	private LimitedBlockTileProxy( TileEntity encapsulatedTileEntity )
	{
		this.encapsulatedTileEntity = encapsulatedTileEntity;
	}
	
	public static LimitedBlockTileProxy newInstance( TileEntity encapsulatedTileEntity )
	{
		return (LimitedBlockTileProxy) Proxy.newProxyInstance(
			encapsulatedTileEntity.getClass().getClassLoader(),
			encapsulatedTileEntity.getClass().getInterfaces(),
			new LimitedBlockTileProxy( encapsulatedTileEntity )
		);
	}	
	/**********************************************************************
	 * Interface - InvocationHandler
	 **********************************************************************/
	
	@Override
	public Object invoke( Object arg0, Method arg1, Object[] arg2 ) throws Throwable 
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	/**********************************************************************
	 * Overrides - TileEntity
	 **********************************************************************/
	
	@Override
	public void readFromNBT( NBTTagCompound compound ) 
	{
		// TODO Auto-generated method stub
		super.readFromNBT(compound);
	}

	@Override
	public void writeToNBT( NBTTagCompound compound ) 
	{
		// TODO Auto-generated method stub
		super.writeToNBT( compound );
	}

	@Override
	public void updateEntity() 
	{
		// TODO Auto-generated method stub
		super.updateEntity();
	}

}
