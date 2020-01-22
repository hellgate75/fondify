/**
 * 
 */
package com.rcg.foundation.fondify.annotation;

import com.rcg.foundation.fondify.core.Foundation;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class AnnotationEngine {

	/**
	 * Private blocked constructor
	 */
	private AnnotationEngine() {
		throw new IllegalArgumentException("AnnotationEngine: Unable to make instance of Engine Class");
	}

	/**
	 * Run annotation engine
	 * @param instance Main Class
	 */
	public static void run(Class<?> instance, Runnable tasks) {
		Foundation.credits();
		new Thread(tasks).start();
	}
}
