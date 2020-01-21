/**
 * 
 */
package com.rcg.foundation.fondify.core.exceptions;

/**
 * Exception that occurs during any stream or other I/O operation
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class IOException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4685687322590344371L;

	/**
	 * Default Constructor
	 */
	public IOException() {
		super();
	}

	/**
	 * Message, cause and options constructor
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public IOException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Message and cause constructor
	 * @param message
	 * @param cause
	 */
	public IOException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Message constructor
	 * @param message
	 */
	public IOException(String message) {
		super(message);
	}

	/**
	 * Cause constructor
	 * @param cause
	 */
	public IOException(Throwable cause) {
		super(cause);
	}
	
	

}
