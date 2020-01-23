/**
 * 
 */
package com.rcg.foundation.fondify.components.injecables;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface InjectableExecutor<T, K> extends Executable<T, K>, Activation<T, K> {

	/**
	 * Specify if the Injectable is activation component
	 * @return <boolean> activation state
	 */
	boolean isActivation();
}
