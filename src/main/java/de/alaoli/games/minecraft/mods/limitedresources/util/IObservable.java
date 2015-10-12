package de.alaoli.games.minecraft.mods.limitedresources.util;

public interface IObservable 
{
	public void addObserver( IObserver observer );
	
	public void deleteObserver( IObserver observer );
	
	public void deleteObservers();
	
	public void notifyObservers();
	
	public void notifyObservers( Object args );
}
