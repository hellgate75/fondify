/**
 * 
 */
package com.rcg.foundation.fondify.cache.typings;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.rcg.foundation.fondify.core.typings.cache.CacheItem;
import com.rcg.foundation.fondify.core.typings.cache.CacheSerializable;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class InMemoryCacheItem<T extends CacheSerializable> implements CacheItem<T> {
	private Map<String, T> cacheElementsMap = new ConcurrentHashMap<>();

	/**
	 * 
	 */
	public InMemoryCacheItem() {
		super();
	}

	/**
	 * Gets the cached elements map
	 * @return the entitiesMap Map of registered entities
	 */
	@Override
	public Map<String, T> getElementsMap() {
		return cacheElementsMap;
	}
	
	/**
	 * @return
	 */
	@Override
	public Collection<String> getElementNames() {
		return cacheElementsMap.keySet();
	}
	
	/**
	 * @param entryName
	 * @return
	 */
	@Override
	public boolean containsElement(String entryName) {
		return cacheElementsMap.containsKey(entryName);
	}
	
	/**
	 * Register a single entity into the registry
	 * @param name
	 * @param t
	 */
	@Override
	public void registerElement(String name, T t) {
		if ( name !=null && t != null && !name.isEmpty() ) {
			cacheElementsMap.put(name, t);
		}
	}
	
	/**
	 * Gets a single entity into the registry
	 * @param name
	 * @return
	 */
	@Override
	public T getElement(String name) {
		if ( name !=null&& !name.isEmpty() ) {
			return cacheElementsMap.get(name);
		}
		return null;
	}

}
