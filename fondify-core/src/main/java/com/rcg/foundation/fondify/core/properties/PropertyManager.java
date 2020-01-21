/**
 * 
 */
package com.rcg.foundation.fondify.core.properties;

import java.util.Collection;

import com.rcg.foundation.fondify.core.exceptions.IOException;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface PropertyManager {

	/**
	 * @param filePath
	 * @throws IOException
	 */
	void load(String filePath) throws IOException;
	
	/**
	 * @return
	 */
	Collection<String> keys();
	
	/**
	 * @param <T>
	 * @param key
	 * @param clazz
	 * @return
	 */
	<T> T getByKey(String key, Class<? extends T> clazz);
}
