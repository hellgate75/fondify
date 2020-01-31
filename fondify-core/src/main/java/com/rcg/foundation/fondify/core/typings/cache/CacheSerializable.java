/**
 * 
 */
package com.rcg.foundation.fondify.core.typings.cache;

import java.io.Serializable;
import java.util.Map;

/**
 * Cache Mapping behavior of a component. It allows to map component
 * data to the cache and viceversa.
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface CacheSerializable extends Serializable {
	static String CLASS_MAP_KEY="__class";

	/**
	 * Retrieves map of fields and values to persist
	 * @return
	 */
	Map<String, Object> exportToMap();
	/**
	 * Provides map of fields and values persisted in cache
	 * @param importMap map of fields and values persisted in the cache
	 */
	void importFromMap(Map<String, Object> importMap);
}
