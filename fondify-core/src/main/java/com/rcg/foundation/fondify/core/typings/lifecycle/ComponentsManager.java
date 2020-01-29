/**
 * 
 */
package com.rcg.foundation.fondify.core.typings.lifecycle;

import com.rcg.foundation.fondify.core.domain.Scope;
import com.rcg.foundation.fondify.core.typings.Injectable;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface ComponentsManager {

	/**
	 * Register a new Custom Component manually in the registry at runtime
	 * @param <T>
	 * @param name
	 * @param component
	 */
	<T> void registerComponent(String name, T component);

	/**
	 * Register a new Custom Injectable manually in the registry at runtime
	 * @param <T>
	 * @param name
	 * @param component
	 */
	<T extends Injectable> void registerInjectable(String name, T component);

	/**
	 * Generate / Return an injectable/bean by name and scope, passing eventually a base version 
	 * of the injectable/bean object
	 * 
	 * @param <T>
	 * @param name
	 * @param baseInstance
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	<T> T getInjectableOrComponentByName(String name, Object baseInstance, Scope... scope) throws Exception;

	/**
	 * Generate / Return an injectable by name and scope, passing eventually a base version 
	 * of the injectable object
	 * 
	 * @param <T>
	 * @param name
	 * @param baseInstance
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	<T extends Injectable> T getInjectableByName(String name, Object baseInstance, Scope... scope) throws Exception;

	/**
	 * Generate / Return a bean by name and scope, passing eventually a base version 
	 * of the bean object
	 * 
	 * @param <T>
	 * @param name
	 * @param baseInstance
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	<T> T getComponentByName(String name, Object baseInstance, Scope... scope) throws Exception;

	/**
	 * Retrieve an application property
	 * 
	 * @param name
	 * @return
	 */
	Object getPropertyByName(String name);

	/**
	 * Retrieve a system property
	 * @param <T>
	 * @param name
	 * @return
	 * @throws Exception
	 */
	<T> T getSystemObjectByName(String name) throws Exception;
}
