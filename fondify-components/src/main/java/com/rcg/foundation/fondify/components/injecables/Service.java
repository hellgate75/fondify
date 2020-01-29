/**
 * 
 */
package com.rcg.foundation.fondify.components.injecables;

import com.rcg.foundation.fondify.core.typings.Injectable;

/**
 * Executable task element
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface Service<T> extends Injectable {
	/**
	 * Execute Service actions, without any return type required
	 * @param input
	 */
	void doService(T input);
}
