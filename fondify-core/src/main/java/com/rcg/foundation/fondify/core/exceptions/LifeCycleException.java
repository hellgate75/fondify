/**
 * 
 */
package com.rcg.foundation.fondify.core.exceptions;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class LifeCycleException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6751717472207643924L;

	/**
	 * 
	 */
	public LifeCycleException() {
		super();
	}

	/**
	 * @param message
	 */
	public LifeCycleException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public LifeCycleException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public LifeCycleException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public LifeCycleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
