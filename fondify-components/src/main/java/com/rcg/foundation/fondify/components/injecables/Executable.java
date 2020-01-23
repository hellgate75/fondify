/**
 * 
 */
package com.rcg.foundation.fondify.components.injecables;

/**
 * Executable task element
 * 
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface Executable<T, K> {
	/**
	 * Execute Action described in the input data
	 * @param request action request data
	 * @return <K> response
	 */
	K doAction(T request);

}
