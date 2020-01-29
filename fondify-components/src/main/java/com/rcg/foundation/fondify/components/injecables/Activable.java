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
public interface Activable<T> extends Injectable {
	/**
	 * Request activation of sleeping action
	 * @param request for activation (Injected parameter)
	 */
	void activate(T request);
	/**
	 * Request start up and passing to idle state of activable component
	 * @param request for activation
	 */
	void startAndIdle();

}
