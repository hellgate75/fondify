/**
 * 
 */
package com.rcg.foundation.fondify.cache.annotations.executors;

import java.util.Properties;

import com.rcg.foundation.fondify.cache.annotations.EnableCache;
import com.rcg.foundation.fondify.core.exceptions.ProcessException;
import com.rcg.foundation.fondify.core.properties.PropertyArchive;
import com.rcg.foundation.fondify.core.typings.AnnotationDeclaration;
import com.rcg.foundation.fondify.core.typings.AnnotationExecutor;
import com.rcg.foundation.fondify.core.typings.ExecutionAnswer;
import com.rcg.foundation.fondify.utils.helpers.ArgumentsHelper;
import com.rcg.foundation.fondify.utils.helpers.LoggerHelper;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class EnableCacheAnnotationExecutor implements AnnotationExecutor<EnableCache> {

	/**
	 * Default constructor
	 */
	public EnableCacheAnnotationExecutor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Class<? extends EnableCache> getAnnotationClass() {
		return EnableCache.class;
	}

	@Override
	public boolean containsResults() {
		return false;
	}

	@Override
	public String getComponentName() {
		return null;
	}

	@Override
	public ExecutionAnswer<EnableCache> executeAnnotation(AnnotationDeclaration t) throws ProcessException {
		// Properties: 
		// enable.cache=true
		// cache.provider=...., 
		// enable.cache.persitence=true
		// cache.persistence.provider=....., 
		// cache.persistence.uri=.....
		EnableCache enableCache = (EnableCache)t.getAnnotation();
		Properties properties = new Properties();
		if ( ArgumentsHelper.traceAllLevels || ArgumentsHelper.traceCacheLevel ) {
			LoggerHelper.logTrace("EnableCacheAnnotationExecutor::executeAnnotation", 
					"Enabling cache feature ...");
		}
		properties.setProperty("enable.cache", "true");
		if ( enableCache.provider() != null ) {
			if ( ArgumentsHelper.traceAllLevels || ArgumentsHelper.traceCacheLevel ) {
				LoggerHelper.logTrace("EnableCacheAnnotationExecutor::executeAnnotation", 
						String.format("Using cache provider: %s", enableCache.provider().getName()));
			}
			properties.setProperty("cache.provider", enableCache.provider().getName());
		} else {
			LoggerHelper.logWarn("EnableCacheAnnotationExecutor::executeAnnotation", 
					"Unable to assign cache provider, so using the DEFAULT cache provider!!", 
					null);
		}
		if (enableCache.enablePersistence()) {
			if ( ArgumentsHelper.traceAllLevels || ArgumentsHelper.traceCacheLevel ) {
				LoggerHelper.logTrace("EnableCacheAnnotationExecutor::executeAnnotation", 
						"Enabling cache persistence feature ...");
			}
			properties.setProperty("enable.cache.persitence", "true");
			if ( enableCache.cachePersistenceProvider() != null ) {
				if ( ArgumentsHelper.traceAllLevels || ArgumentsHelper.traceCacheLevel ) {
					LoggerHelper.logTrace("EnableCacheAnnotationExecutor::executeAnnotation", 
							String.format("Using cache persistence provider: %s", enableCache.cachePersistenceProvider().getName()));
				}
				properties.setProperty("cache.persistence.provider", enableCache.cachePersistenceProvider().getName());
				if ( enableCache.persistenceUri() != null && ! enableCache.persistenceUri().isEmpty() ) {
					if ( ArgumentsHelper.traceAllLevels || ArgumentsHelper.traceCacheLevel ) {
						LoggerHelper.logTrace("EnableCacheAnnotationExecutor::executeAnnotation", 
								String.format("Using cache persistence URI: %s", enableCache.persistenceUri()));
					}
					properties.setProperty("cache.persistence.uri", enableCache.persistenceUri());
				} else {
					LoggerHelper.logWarn("EnableCacheAnnotationExecutor::executeAnnotation", 
							String.format("Unable to assign Url for persistence provider of type, hopefully it's not needed for this provider!!", enableCache.cachePersistenceProvider().getName()), 
							null);
				}
			} else {
				LoggerHelper.logWarn("EnableCacheAnnotationExecutor::executeAnnotation", 
						"Unable to assign cache persistence provider, so using the DEFAULT cache persistence provider (NO PERSISTENCE)!!", 
						null);
			}
		}
		PropertyArchive.getInstance().registerProperties(properties);
		return new ExecutionAnswer<>(getAnnotationClass());
	}

}
