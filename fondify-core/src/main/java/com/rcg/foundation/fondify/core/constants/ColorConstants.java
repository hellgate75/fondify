/**
 * 
 */
package com.rcg.foundation.fondify.core.constants;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class ColorConstants {

	/**
	 * 
	 */
	private ColorConstants() {
		throw new IllegalStateException("Unable ti make instance of Constants class");
	}
	
	public static final String	ANSI_HIGH_INTENSITY		= "\u001B[1m";
	public static final String	ANSI_LOW_INTENSITY		= "\u001B[2m";

	
	public static final String	ANSI_ITALIC				= "\u001B[3m";
	public static final String	ANSI_UNDERLINE			= "\u001B[4m";
	public static final String	ANSI_BLINK				= "\u001B[5m";
	public static final String	ANSI_RAPID_BLINK			= "\u001B[6m";
	public static final String	ANSI_REVERSE_VIDEO		= "\u001B[7m";
	public static final String	ANSI_INVISIBLE_TEXT		= "\u001B[8m";
	
	
	public static final String ANSI_RESET               = "\u001B[0m";
	public static final String ANSI_BLACK 				= "\u001B[30m";
	public static final String ANSI_RED 				= "\u001B[31m";
	public static final String ANSI_GREEN 				= "\u001B[32m";
	public static final String ANSI_YELLOW 				= "\u001B[33m";
	public static final String ANSI_BLUE 				= "\u001B[34m";
	public static final String ANSI_PURPLE 				= "\u001B[35m";
	public static final String ANSI_CYAN 				= "\u001B[36m";
	public static final String ANSI_WHITE 				= "\u001B[37m";
	
	
	public static final String ANSI_BLACK_BACKGROUND 	= "\u001B[40m";
	public static final String ANSI_RED_BACKGROUND 		= "\u001B[41m";
	public static final String ANSI_GREEN_BACKGROUND 	= "\u001B[42m";
	public static final String ANSI_YELLOW_BACKGROUND 	= "\u001B[43m";
	public static final String ANSI_BLUE_BACKGROUND 	= "\u001B[44m";
	public static final String ANSI_PURPLE_BACKGROUND 	= "\u001B[45m";
	public static final String ANSI_CYAN_BACKGROUND 	= "\u001B[46m";
	public static final String ANSI_WHITE_BACKGROUND 	= "\u001B[47m";
	
	public static final void initConsole() {
		if (System.console() == null) 
			System.setProperty("jansi.passthrough", "true");
	}

}
