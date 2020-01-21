/**
 * 
 */
package com.rcg.foundation.fondify.core.exceptions;

/**
 * Exception that occurs in mapping or revert of mapping operations 
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class MappingException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2395481880951333400L;

	/**
	 * Default Constructor
	 */
	public MappingException() {
		super();
	}

	/**
	 * Message, cause and options constructor
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public MappingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Message and cause constructor
	 * @param message
	 * @param cause
	 */
	public MappingException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Message constructor
	 * @param message
	 */
	public MappingException(String message) {
		super(message);
	}

	/**
	 * Cause constructor
	 * @param cause
	 */
	public MappingException(Throwable cause) {
		super(cause);
	}
	
	

}
