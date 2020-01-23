/**
 * 
 */
package com.rcg.foundation.fondify.components.injecables;

/**
 * Requesting task executor
 * 
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface Requesting<T, K> {
	/**
	 * Specific Request
	 * @param request
	 * @return <K> response
	 */
	K request(T request);

}
