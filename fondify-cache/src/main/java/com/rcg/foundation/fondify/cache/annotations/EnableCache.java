/**
 * 
 */
package com.rcg.foundation.fondify.cache.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.rcg.foundation.fondify.annotations.annotations.Application;
import com.rcg.foundation.fondify.annotations.annotations.Configuration;
import com.rcg.foundation.fondify.annotations.annotations.DependsOn;
import com.rcg.foundation.fondify.core.typings.cache.CachePersistence;
import com.rcg.foundation.fondify.core.typings.cache.CacheProvider;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
@DependsOn({Application.class, Configuration.class})
/**
 * Annotation that defines enabled the cache engine, behinf the
 * annotation engine. Any provider can be defined, and event persistence
 * and persistence uri can be defined by user.
 * Configuration can even be in
 * * Application Arguments (-enable.cache=true, -cache.provider=...., -enable.cache.persitence=true, -cache.persistence.provider=....., -cache.persistence.uri=.....),
 * * Properties (enable.cache=true, cache.provider=...., enable.cache.persitence=true, cache.persistence.provider=....., cache.persistence.uri=.....).
 * 
 * Any custom argument or property can be specified in the text arguments of this annotation.
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public @interface EnableCache {
	/**
	 * Define which extension of {@link CacheProvider} class is used as default cache
	 * provider. Any framework or custom implementation can be used as {@link CacheProvider}.
	 * Enabling persistence the passed cache provider must implement the default cache persistence
	 * features described in the {@link CachePersistence} interface. This class will have a default constructor.
	 *   
	 * @return (Class<? extends {@link CacheProvider}>) In use cache provider class
	 */
	Class<? extends CacheProvider> provider() default com.rcg.foundation.fondify.cache.providers.InMemoryCacheProvider.class;

	/**
	 * Enable cache persistence on the provided uri, in order to save out of the memory the payload
	 * of cached elements. Any implemented cache provider with persistence, using {@link CachePersistence}
	 * interface, must save and read data from a source outside the JVM, in order to respect expectancies
	 * from this component. 
	 * @return (boolean) cache persistence state (default: false)
	 */
	boolean enablePersistence() default false;

	/**
	 * Custom Definition of Persistence Provider. This class will have a default constructor.
	 * @return (Class<? extends {@link CachePersistence}>) default cache persistence provider class
	 */
	Class<? extends CachePersistence> cachePersistenceProvider() default com.rcg.foundation.fondify.cache.persistence.NoCachePersistenceProvider.class;
	
	/**
	 * Defines the persistence uri if needed, it depends on the implementation
	 * of the default {@link CachePersistence} provider.
	 * @return (String) uri of source if needed
	 */
	String persistenceUri() default "";
}
