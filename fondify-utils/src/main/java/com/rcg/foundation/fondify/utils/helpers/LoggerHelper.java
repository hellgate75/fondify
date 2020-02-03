/**
 * 
 */
package com.rcg.foundation.fondify.utils.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rcg.foundation.fondify.utils.constants.ColorConstants;

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
			if ( ! ArgumentsHelper.useLoggerInsteadConsole ) {
				System.out.print(ColorConstants.ANSI_YELLOW + ColorConstants.ANSI_LOW_INTENSITY);
			}
			LOGGER.trace(String.format("%s::trace >> %s", place, message));
			if ( ! ArgumentsHelper.useLoggerInsteadConsole ) {
				System.out.print(ColorConstants.ANSI_RESET);
			}
		}
	}

	public static synchronized void logDebug(String place, String message) {
		if ( message != null && ! message.isEmpty() ) {
			if ( ! ArgumentsHelper.useLoggerInsteadConsole ) {
				System.out.print(ColorConstants.ANSI_YELLOW);
			}
			LOGGER.debug(String.format("%s::debug >> %s", place, message));
			if ( ! ArgumentsHelper.useLoggerInsteadConsole ) {
				System.out.print(ColorConstants.ANSI_RESET);
			}
		}
	}

	public static synchronized void logInfo(String place, String message) {
		if ( message != null && ! message.isEmpty() ) {
			if ( ! ArgumentsHelper.useLoggerInsteadConsole ) {
				System.out.print(ColorConstants.ANSI_WHITE);
			}
			LOGGER.info(String.format("%s::info >> %s", place, message));
			if ( ! ArgumentsHelper.useLoggerInsteadConsole ) {
				System.out.print(ColorConstants.ANSI_RESET);
			}
		}
	}

	public static synchronized void logWarn(String place, String message, Throwable throwable) {
		if ( message != null && ! message.isEmpty() ) {
			if ( ! ArgumentsHelper.useLoggerInsteadConsole ) {
				System.out.print(ColorConstants.ANSI_YELLOW + ColorConstants.ANSI_HIGH_INTENSITY);
			}
			LOGGER.warn(String.format("%s::warn >> %s", place, message));
			if ( ! ArgumentsHelper.useLoggerInsteadConsole ) {
				System.out.print(ColorConstants.ANSI_RESET);
			}
		}
		if ( throwable != null ) {
			if ( ! ArgumentsHelper.useLoggerInsteadConsole ) {
				System.out.print(ColorConstants.ANSI_RED);
			}
			LOGGER.error(String.format("%s::warn >> %s <%s> -> %s", place, throwable.getClass().getName(), throwable.getMessage(), GenericHelper.convertStackTrace(throwable.getStackTrace())));
			if ( ! ArgumentsHelper.useLoggerInsteadConsole ) {
				System.out.print(ColorConstants.ANSI_RESET);
			}
		}
	}

	public static synchronized void logError(String place, String message, Throwable throwable) {
		if ( message != null && ! message.isEmpty() ) {
			if ( ! ArgumentsHelper.useLoggerInsteadConsole ) {
				System.out.print(ColorConstants.ANSI_RED + ColorConstants.ANSI_HIGH_INTENSITY);
			}
			LOGGER.error(String.format("%s::error >> %s", place, message));
			if ( ! ArgumentsHelper.useLoggerInsteadConsole ) {
				System.out.print(ColorConstants.ANSI_RESET);
			}
		}
		if ( throwable != null ) {
			if ( ! ArgumentsHelper.useLoggerInsteadConsole ) {
				System.out.print(ColorConstants.ANSI_RED);
			}
			LOGGER.error(String.format("%s::error >> %s <%s> -> %s", place, throwable.getClass().getName(), throwable.getMessage(), GenericHelper.convertStackTrace(throwable.getStackTrace())));
			if ( ! ArgumentsHelper.useLoggerInsteadConsole ) {
				System.out.print(ColorConstants.ANSI_RESET);
			}
		}
	}
	
	public static synchronized void logText(String text) {
		if ( ! ArgumentsHelper.useLoggerInsteadConsole ) {
			System.out.print(ColorConstants.ANSI_GREEN);
		}
		LOGGER.info(text);
		if ( ! ArgumentsHelper.useLoggerInsteadConsole ) {
			System.out.print(ColorConstants.ANSI_RESET);
		}
	}
}
