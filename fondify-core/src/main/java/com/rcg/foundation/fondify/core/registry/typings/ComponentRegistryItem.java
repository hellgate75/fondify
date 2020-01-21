/**
 * 
 */
package com.rcg.foundation.fondify.core.registry.typings;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ComponentRegistryItem<T> {

	private Map<String, T> entitiesMap = new ConcurrentHashMap<>(0);

	/**
	 * Default Constructor
	 */
	public ComponentRegistryItem() {
		super();
	}

	/**
	 * Gets the entities map
	 * @return the entitiesMap Map of registered entities
	 */
	public Map<String, T> getEntitiesMap() {
		return entitiesMap;
	}
	
	/**
	 * Register a single entity into the registry
	 * @param name
	 * @param t
	 */
	public void registerEntity(String name, T t) {
		if ( name !=null && t != null && !name.isEmpty() ) {
			entitiesMap.put(name, t);
		}
	}
	
	/**
	 * Gets a single entity into the registry
	 * @param name
	 * @return
	 */
	public T getEntity(String name) {
		if ( name !=null&& !name.isEmpty() ) {
			return entitiesMap.get(name);
		}
		return null;
	}

}
