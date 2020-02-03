/**
 * 
 */
package com.rcg.foundation.fondify.core;

import com.rcg.foundation.fondify.utils.constants.ColorConstants;
import com.rcg.foundation.fondify.utils.helpers.ArgumentsHelper;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class Foundation {

	/**
	 * 
	 */
	private Foundation() {
		throw new IllegalStateException("Foundation::constructor -> Unable to make instance of TRCG Annotation Engine Foundation class");
	}
	
	public static final void credits() {
		if ( ! ArgumentsHelper.useLogger ) {
			ColorConstants.initConsole();
			System.out.print(ColorConstants.ANSI_YELLOW);
		}
		System.out.println();
		System.out.println("-------------------------------------------------");
		System.out.println();
		System.out.println("Application Powered by TRCG Annotation Engine");
		System.out.println("(R) 2020 Author: Fabrizio Torelli");
		System.out.println();
		System.out.println("-------------------------------------------------");
		System.out.println();
		System.out.println("Disclaimer:");
		System.out.println();
		if ( ! ArgumentsHelper.useLogger ) {
			System.out.print(ColorConstants.ANSI_RESET);
			System.out.print(ColorConstants.ANSI_YELLOW + ColorConstants.ANSI_UNDERLINE);
		}
		System.out.println("All rights reserved, any commercial or production");
		System.out.println("use must be nogotiated with the author, it's mot");
		System.out.println("provided any warranty for the free use of the ");
		System.out.println("framework. The author is not resposible for any");
		System.out.println("damage for the free use of the product. For any ");
		System.out.println("further information please contact the author");
		System.out.print("at the following address: ");
		if ( ! ArgumentsHelper.useLogger ) {
			System.out.print(ColorConstants.ANSI_RESET);
			System.out.print(ColorConstants.ANSI_YELLOW);
			System.out.print(ColorConstants.ANSI_HIGH_INTENSITY);
		}
		System.out.println("hellgate75@gmail.com");
		if ( ! ArgumentsHelper.useLogger ) {
			System.out.print(ColorConstants.ANSI_RESET);
			System.out.print(ColorConstants.ANSI_YELLOW);
		}
		System.out.println("-------------------------------------------------");
		if ( ! ArgumentsHelper.useLogger ) {
			System.out.print(ColorConstants.ANSI_RESET);
		}
		System.out.println();
		System.out.println();
	}

}
