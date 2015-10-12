package de.alaoli.games.minecraft.mods.limitedresources.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import de.alaoli.games.minecraft.mods.limitedresources.util.IObservable;
import de.alaoli.games.minecraft.mods.limitedresources.util.IObserver;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class LimitedBlockProxy extends BlockContainer implements InvocationHandler, IObservable
{
	private IObserver observer;
	
	private Block encapsulatedBlock;
	
	private boolean isEncapsulatedBlockContainer;
	
	private LimitedBlockProxy( Block encapsulatedBlock )
	{
		super( encapsulatedBlock.getMaterial() );
		
		//If true bypass BlockContainer Methods to encapsulated Block
		if( encapsulatedBlock instanceof BlockContainer )
		{
			this.isEncapsulatedBlockContainer = true;
		}
		else
		{
			this.isEncapsulatedBlockContainer = false;
		}
		this.encapsulatedBlock = encapsulatedBlock;
	}
	
	public static LimitedBlockProxy newInstance( Block encapsulatedBlock )
	{
		return (LimitedBlockProxy) Proxy.newProxyInstance(
			encapsulatedBlock.getClass().getClassLoader(),
			encapsulatedBlock.getClass().getInterfaces(),
			new LimitedBlockProxy( encapsulatedBlock )
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
	 * Interface - ITileEntityProvider
	 **********************************************************************/
	
	@Override
	public TileEntity createNewTileEntity( World world, int metaid ) 
	{
		if( this.isEncapsulatedBlockContainer )
		{
			TileEntity tile = ((BlockContainer)this.encapsulatedBlock).createNewTileEntity( world, metaid );
			
			return LimitedBlockTileProxy.newInstance( tile );
		}
		return null;
	}

	/**********************************************************************
	 * Interface - ITileEntityProvider
	 **********************************************************************/
	
	@Override
	public void addObserver( IObserver observer )
	{
		this.observer = observer;
	}

	@Override
	public void deleteObserver( IObserver observer )
	{
		this.deleteObservers();
	}

	@Override
	public void deleteObservers() 
	{
		this.observer = null;
	}

	@Override
	public void notifyObservers() 
	{
		this.observer.update( this, null );
	}

	@Override
	public void notifyObservers( Object args )
	{
		this.observer.update( this, args );
	}
	
	/**********************************************************************
	 * Overrides - BlockContainer
	 **********************************************************************/
	
	@Override
	public void onBlockAdded( World world, int x, int y, int z )
	{
		super.onBlockAdded( world, x, y, z );
		
		if( this.isEncapsulatedBlockContainer )
		{
			((BlockContainer)this.encapsulatedBlock).onBlockAdded( world, x, y, z );
		}
	}

	@Override
	public void breakBlock(World p_149749_1_, int p_149749_2_, int p_149749_3_, int p_149749_4_, Block p_149749_5_,
			int p_149749_6_) {
		// TODO Auto-generated method stub
		super.breakBlock(p_149749_1_, p_149749_2_, p_149749_3_, p_149749_4_, p_149749_5_, p_149749_6_);
	}

	@Override
	public boolean onBlockEventReceived(World p_149696_1_, int p_149696_2_, int p_149696_3_, int p_149696_4_,
			int p_149696_5_, int p_149696_6_) {
		// TODO Auto-generated method stub
		return super.onBlockEventReceived(p_149696_1_, p_149696_2_, p_149696_3_, p_149696_4_, p_149696_5_, p_149696_6_);
	}
}
