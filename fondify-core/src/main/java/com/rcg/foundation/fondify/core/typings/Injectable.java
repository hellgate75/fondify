/**
 * 
 */
package com.rcg.foundation.fondify.core.typings;

import com.rcg.foundation.fondify.core.listeners.AsyncExecutionListener;
import com.rcg.foundation.fondify.core.listeners.typings.InjectableExecutionResponse;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface Injectable {
	/**
	 * Add listener to check process state of pre/on/post execution
	 * @param listener (AsyncExecutionListener<InjectableExecutionRequest, InjectableExecutionResponse>) process state listener
	 */
	void add(AsyncExecutionListener<Object, InjectableExecutionResponse> listener);
}
