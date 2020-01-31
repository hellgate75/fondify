/**
 * 
 */
package com.rcg.foundation.fondify.cache;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.rcg.foundation.fondify.cache.typings.SimpleCacheItem;
import com.rcg.foundation.fondify.core.typings.cache.CacheItem;
import com.rcg.foundation.fondify.core.typings.cache.CacheProvider;


/**
 * Simple storage cache provider
 * It will provide base level of cache in order to the
 * switch parameter (-enable.cache=true) or property (enable.cache=true)
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 * @see CacheItem
 */
public class SimpleCacheProvider implements CacheProvider{
	
	Map<String, CacheItem<?>> cacheItemsMap = new ConcurrentHashMap<>(0);
	
	@SuppressWarnings("unchecked")
	private <T> CacheItem<T> getCacheItem(String cacheBucketName) {
		CacheItem<T> mapX = null;
		if ( ! cacheItemsMap.containsKey(cacheBucketName) ) {
			mapX = new SimpleCacheItem<T>();
		} else {
			mapX = (CacheItem<T>)cacheItemsMap.get(cacheBucketName);
		}
		return mapX;
	}
	
	@Override
	public synchronized final <T> void add(String cacheBucketName, String componentName, T component) {
		final CacheItem<T> mapX = getCacheItem(cacheBucketName);
		mapX.registerElement(componentName, component);
		cacheItemsMap.put(cacheBucketName, mapX);
	}

	@Override
	@SuppressWarnings("unchecked")
	public final <T> T get(String cacheBucketName, String componentName) {
		return (T)getCacheItem(cacheBucketName).getElement(componentName);
	}


	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final <T> T seek(String componentName) {
		List<CacheItem> registries = cacheItemsMap
			.entrySet()
			.stream()
			.map(entry -> entry.getValue())
			.filter( item -> item.containsElement(componentName) )
			.collect(Collectors.toList());
		if ( registries.size() == 0 )
			return null;
		return (T)registries.get(0).getElement(componentName);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public final <T> Map<String, T> getAllAsMap(String cacheBucketName) {
		return (Map<String, T>)getCacheItem(cacheBucketName).getElementsMap();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public final <T> Collection<T> getAll(String cacheBucketName) {
		return getCacheItem(cacheBucketName).getElementsMap()
				.entrySet()
				.stream()
				.map( entry -> (T) entry.getValue() )
				.collect(Collectors.toList());
	}
	
	@Override
	public final Collection<String> getCacheBucketNames() {
		return cacheItemsMap.keySet();
	}
	
	@Override
	public final boolean containsCacheBucket(String cacheBucketName) {
		return cacheItemsMap.containsKey(cacheBucketName);
	}
	
	@Override
	public final boolean containsComponentInBucket(String cacheBucketName, String componentName) {
		if ( ! containsCacheBucket(cacheBucketName) )
			return false;
		return getCacheItem(cacheBucketName).containsElement(componentName);
	}
	
	@Override
	public final Collection<String> getElementsNamesinBucket(String cacheBucketName) {
		if ( ! containsCacheBucket(cacheBucketName) )
			return Arrays.asList(new String[] {});
		return getCacheItem(cacheBucketName).getElementsMap().keySet();
	}
	
	@Override
	public synchronized final <T> void addAll(String cacheBucketName, Map<String, T> map) {
		final CacheItem<T> compMap = getCacheItem(cacheBucketName);
		map
		.forEach(compMap::registerElement);
		cacheItemsMap.put(cacheBucketName, compMap);
	}

	@Override
	public synchronized final <T> void addAll(String cacheBucketName, CacheItem<T> item) {
		final CacheItem<T> compMap = getCacheItem(cacheBucketName);
		item.getElementsMap().forEach(compMap::registerElement);
		cacheItemsMap.put(cacheBucketName, compMap);
	}
	
}
