/**
 * 
 */
package com.rcg.foundation.fondify.components.injecables;

import com.rcg.foundation.fondify.core.typings.Injectable;

/**
 * Activation task Executor
 * 
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface Activation<T, K> extends Injectable {
	/**
	 * Request activation of sleeping action
	 * @param request for activation
	 */
	void activate(T request);
	
	/**
	 * Add a new async activation execution listener
	 * @param listener Requested {@link AsyncExecutionListener} tracking element
	 */
	void addActivationListener(AsyncExecutionListener<T, K> listener);

}
