/**
 * 
 */
package com.rcg.foundation.fondify.annotations.typings;

/**
 * Defines as the command shell for the annotation engine must work 
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface ApplicationConsole {
	/**
	 * Execute the command shell
	 */
	void run(String[] arguments);
}
