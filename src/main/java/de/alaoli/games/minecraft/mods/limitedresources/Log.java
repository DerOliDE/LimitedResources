package de.alaoli.games.minecraft.mods.limitedresources;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log 
{
	private static final Logger LOGGER = LogManager.getLogger( LimitedResources.MODID );
	
	public static void warn( String msg )
	{
		LOGGER.warn( msg );
	}
	
	public static void error( String msg )
	{
		LOGGER.error( msg );
	}	
	
	public static void debug( String msg )
	{
		if( Config.Debug.isEnabled )
		{
			LOGGER.debug( msg );
		}
	}
}
