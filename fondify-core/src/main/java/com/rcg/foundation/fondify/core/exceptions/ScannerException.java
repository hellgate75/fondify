/**
 * 
 */
package com.rcg.foundation.fondify.core.exceptions;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ScannerException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8612507454507596682L;

	/**
	 * 
	 */
	public ScannerException() {
		super();
	}

	/**
	 * @param message
	 */
	public ScannerException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ScannerException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ScannerException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ScannerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
