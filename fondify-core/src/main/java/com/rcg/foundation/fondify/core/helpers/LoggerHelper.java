/**
 * 
 */
package com.rcg.foundation.fondify.core.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rcg.foundation.fondify.core.constants.ColorConstants;

/**
 * @author Fabrizio Torelli (fabrizio.torelli@ie.verizon.com)
 *
 */
public final class LoggerHelper {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LoggerHelper.class); 

	private LoggerHelper() {
		super();
	}

	public static synchronized void logTrace(String place, String message) {
		if ( message != null && ! message.isEmpty() ) {
			if ( ! ArgumentsHelper.useLogger ) {
				System.out.print(ColorConstants.ANSI_YELLOW);
			}
			LOGGER.trace(String.format("%s::trace >> %s", place, message));
			if ( ! ArgumentsHelper.useLogger ) {
				System.out.print(ColorConstants.ANSI_RESET);
			}
		}
	}

	public static synchronized void logDebug(String place, String message) {
		if ( message != null && ! message.isEmpty() ) {
			if ( ! ArgumentsHelper.useLogger ) {
				System.out.print(ColorConstants.ANSI_YELLOW);
			}
			LOGGER.debug(String.format("%s::debug >> %s", place, message));
			if ( ! ArgumentsHelper.useLogger ) {
				System.out.print(ColorConstants.ANSI_RESET);
			}
		}
	}

	public static synchronized void logInfo(String place, String message) {
		if ( message != null && ! message.isEmpty() ) {
			if ( ! ArgumentsHelper.useLogger ) {
				System.out.print(ColorConstants.ANSI_WHITE);
			}
			LOGGER.info(String.format("%s::info >> %s", place, message));
			if ( ! ArgumentsHelper.useLogger ) {
				System.out.print(ColorConstants.ANSI_RESET);
			}
		}
	}

	public static synchronized void logWarn(String place, String message, Exception ex) {
		if ( message != null && ! message.isEmpty() ) {
			if ( ! ArgumentsHelper.useLogger ) {
				System.out.print(ColorConstants.ANSI_YELLOW);
			}
			LOGGER.warn(String.format("%s::warn >> %s", place, message));
			if ( ! ArgumentsHelper.useLogger ) {
				System.out.print(ColorConstants.ANSI_RESET);
			}
		}
		if ( ex != null ) {
			if ( ! ArgumentsHelper.useLogger ) {
				System.out.print(ColorConstants.ANSI_RED);
			}
			LOGGER.error(String.format("%s::warn >> %s -> %s", place, ex.getMessage(), GenericHelper.convertStackTrace(ex.getStackTrace())));
			if ( ! ArgumentsHelper.useLogger ) {
				System.out.print(ColorConstants.ANSI_RESET);
			}
		}
	}

	public static synchronized void logError(String place, String message, Exception ex) {
		if ( message != null && ! message.isEmpty() ) {
			System.out.print(ColorConstants.ANSI_RED);
			LOGGER.error(String.format("%s::error >> %s", place, message));
			if ( ! ArgumentsHelper.useLogger ) {
				System.out.print(ColorConstants.ANSI_RESET);
			}
		}
		if ( ex != null ) {
			if ( ! ArgumentsHelper.useLogger ) {
				System.out.print(ColorConstants.ANSI_RED);
			}
			LOGGER.error(String.format("%s::error >> %s -> %s", place, ex.getMessage(), GenericHelper.convertStackTrace(ex.getStackTrace())));
			if ( ! ArgumentsHelper.useLogger ) {
				System.out.print(ColorConstants.ANSI_RESET);
			}
		}
	}
	
	protected static synchronized void logText(String text) {
		if ( ! ArgumentsHelper.useLogger ) {
			System.out.print(ColorConstants.ANSI_GREEN);
		}
		LOGGER.info(text);
		if ( ! ArgumentsHelper.useLogger ) {
			System.out.print(ColorConstants.ANSI_RESET);
		}
	}
}
