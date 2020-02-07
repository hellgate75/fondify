/**
 * 
 */
package com.rcg.foundation.fondify.cache;

import com.rcg.foundation.fondify.cache.providers.InMemoryCacheProvider;
import com.rcg.foundation.fondify.core.properties.PropertyArchive;
import com.rcg.foundation.fondify.core.typings.cache.CachePersistence;
import com.rcg.foundation.fondify.core.typings.cache.CacheProvider;
import com.rcg.foundation.fondify.utils.helpers.GenericHelper;
import com.rcg.foundation.fondify.utils.helpers.LoggerHelper;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class CacheManager {

	private static CacheManager instance = null;
	
	private CacheProvider cacheProvider = null; 
	
	private boolean isPersistenceProvided = false;

	private CachePersistence persistenceProvider = null; 
	
	/**
	 * Private singleton Constructor
	 */
	private CacheManager() {
		super();
		// Properties: 
		// enable.cache=true
		// cache.provider=...., 
		// enable.cache.persitence=true
		// cache.persistence.provider=....., 
		// cache.persistence.uri=.....
		boolean enableCache = GenericHelper.getObjectAsBoolean(
				PropertyArchive.getInstance().getProperty("enable.cache"), 
				false);
		if ( enableCache ) {
			Class<?> providerProviderClass = GenericHelper.getObjectAsClass(
					PropertyArchive.getInstance().getProperty("cache.provider"), 
					null);
			if ( providerProviderClass == null ) {
				providerProviderClass = InMemoryCacheProvider.class;
				LoggerHelper.logWarn("CacheManager::constructor", 
									String.format("Unable to provide "), null);
			} 
			boolean enablePersistence = GenericHelper.getObjectAsBoolean(
					PropertyArchive.getInstance().getProperty("enable.cache.persitence"), 
					false);
		}
	}

}
