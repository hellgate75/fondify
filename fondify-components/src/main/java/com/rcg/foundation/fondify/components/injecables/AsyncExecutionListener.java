/**
 * 
 */
package com.rcg.foundation.fondify.components.injecables;

/**
 * Listener for async activation
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface AsyncExecutionListener<T, K> {

	/**
	 * Reports success state for a request, including a related response
	 * @param request execution request
	 * @param response execution response
	 */
	void reportSuccess(T request, K response);

	/**
	 * Reports failure state for a request, including a related response
	 * @param request execution request
	 * @param response execution response
	 */
	void reportFailure(T request, K response);

	/**
	 * Reports user/application abort state for a request, including a related response
	 * @param request execution request
	 * @param response execution response
	 */
	void reportAbortion(T request, K response);

	/**
	 * Reports success with warnings state for a request, including a related response
	 * @param request execution request
	 * @param response execution response
	 */
	void reportWarnings(T request, K response);
}
