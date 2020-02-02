/**
 * 
 */
package com.rcg.foundation.fondify.cache.persistence;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.rcg.foundation.fondify.core.exceptions.IOException;
import com.rcg.foundation.fondify.core.typings.cache.CacheChangeEvent;
import com.rcg.foundation.fondify.core.typings.cache.CacheChangeListener;
import com.rcg.foundation.fondify.core.typings.cache.CachePersistence;

/**
 * No Cache Persistence provider 
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class NoCachePersistenceProvider implements CachePersistence, CacheChangeListener {

	
	/**
	 * Default constructor
	 */
	public NoCachePersistenceProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public CacheChangeListener getCacheEventListener() {
		return this;
	}

	@Override
	public void persistAllCacheToStream(OutputStream output) throws IOException {
		// NOHIMG TO DO
	}

	@Override
	public Map<String, Map<String, Object>> getCacheUpdatedItems() {
		// TODO Auto-generated method stub
		return new HashMap<String, Map<String, Object>>(0);
	}

	@Override
	public Map<String, String> getCacheDeletedItems() {
		//NOTHING TO DO
		return new HashMap<String, String>(0);
	}

	@Override
	public void updateFromStream(InputStream input) throws IOException {
		//NOTHING TO DO
	}

	@Override
	public void reloadFromStream(InputStream input) throws IOException {
		//NOTHING TO DO
	}

	@Override
	public void updatedCacheItems(Map<String, Map<String, Object>> updateMap) {
		//NOTHING TO DO

	}

	@Override
	public void deletedCacheItems(Map<String, String> deletionMap) {
		//NOTHING TO DO
	}

	@Override
	public boolean hasUpdatesOrDeletions() {
		//NOTHING TO DO
		return false;
	}

	@Override
	public boolean requiresFullUpdate() {
		//NOTHING TO DO
		return false;
	}

	@Override
	public boolean requiresFullReload() {
		//NOTHING TO DO
		return false;
	}

	@Override
	public boolean requiresFullSave() {
		//NOTHING TO DO
		return false;
	}

	@Override
	public void newCacheChange(CacheChangeEvent event) throws IOException {
		//NOTHING TO DO
	}

}
