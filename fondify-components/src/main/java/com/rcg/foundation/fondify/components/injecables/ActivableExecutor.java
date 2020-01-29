/**
 * 
 */
package com.rcg.foundation.fondify.components.injecables;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface ActivableExecutor<T, K> extends Executable<T, K>, Activable<T> {

	/**
	 * Specify if the Injectable is activation component
	 * @return <boolean> activation state
	 */
	boolean isActivableComponent();
}
