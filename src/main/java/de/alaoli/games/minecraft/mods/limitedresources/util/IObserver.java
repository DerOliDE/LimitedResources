package de.alaoli.games.minecraft.mods.limitedresources.util;

import java.util.Observable;

public interface IObserver 
{
	public void update( IObservable observable, Object argument );
}
