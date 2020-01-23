/**
 * 
 */
package com.rcg.foundation.fondify.components.injecables;

/**
 * Manage custom, virtual or composite properties, eventually using
 * other components and annotations
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface Properties<T> {
	
	/**
	 * Recover a key element
	 * @param key Element key
	 * @return <T> the value is exists or null
	 */
	T get(String key);
	
	/**
	 * Verify if a key is available in the component storage / service
	 * @param key Key to verify
	 * @return <boolean> Key existence state
	 */
	boolean has(String key);
}
