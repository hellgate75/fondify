/**
 * 
 */
package com.rcg.foundation.fondify.core.exceptions;

/**
 * Exception that occurs during any Module load operation
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ModuleException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -560422096939617351L;

	/**
	 * Default Constructor
	 */
	public ModuleException() {
		super();
	}

	/**
	 * Message, cause and options constructor
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ModuleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Message and cause constructor
	 * @param message
	 * @param cause
	 */
	public ModuleException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Message constructor
	 * @param message
	 */
	public ModuleException(String message) {
		super(message);
	}

	/**
	 * Cause constructor
	 * @param cause
	 */
	public ModuleException(Throwable cause) {
		super(cause);
	}
	
	

}
