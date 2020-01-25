/**
 * 
 */
package com.rcg.foundation.fondify.annotations.lifecycle;

import java.util.Collection;
import java.util.UUID;

import com.rcg.foundation.fondify.core.domain.Scope;
import com.rcg.foundation.fondify.core.exceptions.LifeCycleException;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface SessionContext {
	
	/**
	 * Get a session visible property
	 * @param <T>
	 * @param property
	 * @return
	 */
	<T> T property(String property);
	
	/**
	 * Get a session visible bean, by scope
	 * @param <T>
	 * @param beanName
	 * @param scope
	 * @return
	 */
	<T> T getBean(String beanName, Scope scope);
	
	/**
	 * Get a session visible bean list
	 * @param <T>
	 * @param beanName
	 * @return
	 */
	<T> Collection<T> getBeans(String beanName);
	
	/**
	 * Get a application context
	 * @return
	 * @throws LifeCycleException
	 */
	ApplicationContext getApplicationContext() throws LifeCycleException;
	
	/**
	 * Get session unique id
	 * @return
	 */
	UUID getSessionId();
	
	/**
	 * Close current session
	 */
	void close();

}
