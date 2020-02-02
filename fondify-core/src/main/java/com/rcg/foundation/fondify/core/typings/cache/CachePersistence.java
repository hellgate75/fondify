/**
 * 
 */
package com.rcg.foundation.fondify.core.typings.cache;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import com.rcg.foundation.fondify.core.exceptions.IOException;

/**
 * In an extension of {@link CacheProvider} this interface provides
 * and describe capabilities to persist cache, automatically by
 * the cache management unit.  
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface CachePersistence {
	
	/**
	 * Provides a change persistence scoped cache change listener
	 * @return ({@link CacheChangeListener}) the provided cache change listener  
	 */
	CacheChangeListener getCacheEventListener();

	/**
	 * Perform full cache save to given stream.
	 * @param output writing stream
	 * @throws IOException Any exception that occurs during operation computation
	 */
	void persistAllCacheToStream(OutputStream output) throws IOException;
	
	/**
	 * Retrieve new cache changes since latest data request
	 * @return map of changes
	 */
	Map<String, Map<String, Object>> getCacheUpdatedItems();
	
	/**
	 * Retrieve new cache deletions since latest data request
	 * @return map of changes
	 */
	Map<String, String> getCacheDeletedItems();

	/**
	 * Update elements from the given input stream
	 * @param input 
	 * @throws IOException Any exception that occurs during operation computation
	 */
	void updateFromStream(InputStream input) throws IOException;

	/**
	 * @param input
	 * @throws IOException Any exception that occurs during operation computation
	 */
	void reloadFromStream(InputStream input) throws IOException;
	
	/**
	 * @param updateMap
	 */
	void updatedCacheItems(Map<String, Map<String, Object>> updateMap);
	
	/**
	 * @param deletionMap
	 */
	void deletedCacheItems(Map<String, String> deletionMap);
	
	/**
	 * @return
	 */
	boolean hasUpdatesOrDeletions();
	
	/**
	 * @return
	 */
	boolean requiresFullUpdate();
	
	/**
	 * @return
	 */
	boolean requiresFullReload();
	
	/**
	 * @return
	 */
	boolean requiresFullSave();
	
}
