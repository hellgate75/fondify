/**
 * 
 */
package com.rcg.foundation.fondify.core.listeners;

/**
 * Listener for async activation
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface AsyncExecutionListener<REQ, RES> {

	/**
	 * Reports success state for a request, including a related response
	 * @param request execution request
	 * @param response execution response
	 */
	void reportSuccess(REQ request, RES response);

	/**
	 * Reports failure state for a request, including a related response
	 * @param request execution request
	 * @param response execution response
	 */
	void reportFailure(REQ request, RES response);

	/**
	 * Reports user/application abort state for a request, including a related response
	 * @param request execution request
	 * @param response execution response
	 */
	void reportAbortion(REQ request, RES response);

	/**
	 * Reports success with warnings state for a request, including a related response
	 * @param request execution request
	 * @param response execution response
	 */
	void reportWarnings(REQ request, RES response);
}
