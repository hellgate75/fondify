package com.rcg.foundation.fondify.core.typings.cache;

import java.util.Collection;
import java.util.Map;

/**
 * Interface that describe features, behavior and capabilities of any
 * Cache Provider extension, used in the annotation engine.
 * It will provide be provided accordingly to the presence of the 
 * switch parameter (-enable.cache=true) or switch property (enable.cache=true)
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 * @see CacheItem
 * @see CacheSerializable
 *
 */
public interface CacheProvider {
	/**
	 * Add element at a cache bucket, eventually creating the bucket if it doesn't exist
	 * @param <T extends CacheSerializable>
	 * @param cacheBucketName name of cache bucket, containing data
	 * @param itemName name of item for the cache bucket
	 * @param item
	 */
	<T extends CacheSerializable> void add(String cacheBucketName, String itemName, T item);

	/**
	 * @param <T extends CacheSerializable>
	 * @param cacheBucketName name of cache bucket, containing data
	 * @param itemName name of item for the cache bucket
	 * @return
	 */
	<T extends CacheSerializable> T get(String cacheBucketName, String itemName);


	/**
	 * @param <T extends CacheSerializable>
	 * @param itemName name of item for the cache bucket
	 * @return
	 */
	<T extends CacheSerializable> T seek(String itemName);
	
	/**
	 * @param <T extends CacheSerializable>
	 * @param cacheBucketName name of cache bucket, containing data
	 * @return
	 */
	<T extends CacheSerializable> Map<String, T> getAllAsMap(String cacheBucketName);
	
	/**
	 * @param <T extends CacheSerializable>
	 * @param cacheBucketName name of cache bucket, containing data
	 * @return
	 */
	<T extends CacheSerializable> Collection<T> getAll(String cacheBucketName);
	
	/**
	 * Retrieve the list of cache bucket names
	 * @return list of names
	 */
	Collection<String> getCacheBucketNames();
	
	/**
	 * Checks presence of cache bucket with given name
	 * @param cacheBucketName name of cache bucket, containing data
	 * @return
	 */
	boolean containsCacheBucket(String cacheBucketName);
	
	/**
	 * Checks presence of a named item into a specific cache bucket
	 * @param cacheBucketName name of cache bucket, containing data
	 * @param itemName name of item for the cache bucket
	 * @return
	 */
	boolean containsComponentInBucket(String cacheBucketName, String itemName);
	
	/**
	 * Retrieve all cache bucket contained element names
	 * @param cacheBucketName name of cache bucket, containing data
	 * @return list of names
	 */
	Collection<String> getElementsNamesinBucket(String cacheBucketName);
	
	/**
	 * Add all elements into a named cache bucket
	 * @param <T extends CacheSerializable>
	 * @param cacheBucketName name of cache bucket, containing data
	 * @param map map of elements to add, with key names
	 */
	<T extends CacheSerializable> void addAll(String cacheBucketName, Map<String, T> map);

	/**
	 * Add all elements into a named cache bucket
	 * @param <T extends CacheSerializable>
	 * @param cacheBucketName name of cache bucket, containing data
	 * @param item {@link CacheItem} element to copy into the cache provider, using elements contained data
	 */
	<T extends CacheSerializable> void addAll(String cacheBucketName, CacheItem<T> item);

}
