/**
 * 
 */
package com.rcg.foundation.fondify.components.injecables;

import com.rcg.foundation.fondify.core.typings.Injectable;

/**
 * Requesting task executor
 * 
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface Requesting<T, K> extends Injectable {
	/**
	 * Specific Request
	 * @param request
	 * @return <K> response
	 */
	K request(T request);

}
