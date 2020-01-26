/**
 * 
 */
package com.rcg.foundation.fondify.annotations.lifecycle;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface ApplicationManagerProvider {
	/**
	 * Recover current singleton Application Manager Type
	 * @return
	 */
	ApplicationManager getApplicationManager();
}
