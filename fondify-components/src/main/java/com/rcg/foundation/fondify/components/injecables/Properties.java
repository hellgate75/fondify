/**
 * 
 */
package com.rcg.foundation.fondify.components.injecables;

import java.util.Map;
import java.util.Set;

import com.rcg.foundation.fondify.core.typings.Injectable;

/**
 * Manage custom, virtual or composite properties, eventually using
 * other components and annotations
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface Properties<T> extends Injectable {
	
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
	
	/**
	 * Retrieve the set of all available keys in the Properties component
	 * @return (Set<String>) Set of all available keys
	 * @see Set
	 */
	Set<String> keysSet();
	
	/**
	 * Retrieve the set of all available values in the Properties component
	 * @return (Set<String>) Set of all available values
	 * @see Set
	 */
	Set<String> valuesSet();
	
	/**
	 * Retrieve the set of all available entries (key, value pair) in the Properties component
	 * @return (Map.Entry<String, String>) Set of all available entries
	 * @see Set
	 * @see Map.Entry
	 */
	Set<Map.Entry<String, String>> entriesSet();
	
	/**
	 * Register all properties in the system / session / application scopes
	 */
	void registerAllProperties();
	
	/**
	 * Return state of capabilities in order to register all properties in the system / session / application scopes
	 * In case this method returns true it means all properties will be added in the reported scopes and any manual use of the
	 * component should be forbidden or denied via Exception rise, anyway the implementation is free and related to the
	 * developer / company application design.
	 * @return (boolean) properties registration capability enabled state
	 */
	boolean isGlobalPropertiesRegistrationEnabled();
}
