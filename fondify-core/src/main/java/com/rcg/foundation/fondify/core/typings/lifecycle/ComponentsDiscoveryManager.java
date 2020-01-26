/**
 * 
 */
package com.rcg.foundation.fondify.core.typings.lifecycle;

import com.rcg.foundation.fondify.core.domain.Scope;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface ComponentsDiscoveryManager {
	/**
	 * @param <T>
	 * @param name
	 * @param defaultValue
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	<T> T discoverComponent(String name, Object defaultValue, Scope...scope) throws Exception;
}
