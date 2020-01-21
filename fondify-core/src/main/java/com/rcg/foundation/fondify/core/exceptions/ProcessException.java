/**
 * 
 */
package com.rcg.foundation.fondify.core.exceptions;

/**
 * Exception that occurs during any Process execution (Job, Step, Task) operation
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ProcessException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3499830097050857966L;

	/**
	 * Default Constructor
	 */
	public ProcessException() {
		super();
	}

	/**
	 * Message, cause and options constructor
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ProcessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Message and cause constructor
	 * @param message
	 * @param cause
	 */
	public ProcessException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Message constructor
	 * @param message
	 */
	public ProcessException(String message) {
		super(message);
	}

	/**
	 * Cause constructor
	 * @param cause
	 */
	public ProcessException(Throwable cause) {
		super(cause);
	}
	
	

}
