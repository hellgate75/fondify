/**
 * 
 */
package com.rcg.foundation.fondify.core.typings.cache;

import com.rcg.foundation.fondify.core.exceptions.IOException;

/**
 * Listen to cache changes
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface CacheChangeListener {
	
	/**
	 * Called at any time a new cache change happens.
	 * @param chengeEvent event related to the cache change
	 * @throws IOException Any exception that occurs during operation computation
	 */
	void newCacheChange(CacheChangeEvent event) throws IOException;
}
