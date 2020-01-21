/**
 * 
 */
package com.rcg.foundation.fondify.core.exceptions;

/**
 * Exception that occurs during Stream I/O Application initialization
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class InitializationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4685687322590344371L;

	/**
	 * Default Constructor
	 */
	public InitializationException() {
		super();
	}

	/**
	 * Message, cause and options constructor
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public InitializationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Message and cause constructor
	 * @param message
	 * @param cause
	 */
	public InitializationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Message constructor
	 * @param message
	 */
	public InitializationException(String message) {
		super(message);
	}

	/**
	 * Cause constructor
	 * @param cause
	 */
	public InitializationException(Throwable cause) {
		super(cause);
	}
	
	

}
