/**
 * 
 */
package com.rcg.foundation.fondify.core.typings.cache;

import java.util.Collection;
import java.util.Map;

/**
 * Single element of the cache map
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface CacheItem<T extends CacheSerializable> {

	/**
	 * Gets the cached elements map
	 * @return the entitiesMap Map of registered elements
	 */
	public Map<String, T> getElementsMap();
	
	/**
	 * Retrieve all cache elements names saved in this cache item map
	 * @return
	 */
	public Collection<String> getElementNames();
	
	/**
	 * Check presence of cache element name into the cache map
	 * @param name
	 * @return
	 */
	public boolean containsElement(String name);
	
	/**
	 * Register a single element into the cache map
	 * @param name
	 * @param t
	 */
	public void registerElement(String name, T t);
	
	/**
	 * Gets a single element from the cache map
	 * @param name
	 * @return
	 */
	public T getElement(String name);
}
