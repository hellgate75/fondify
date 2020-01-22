/**
 * 
 */
package com.rcg.foundation.fondify.annotations.typings;

import com.rcg.foundation.fondify.core.exceptions.IOException;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface ConfigFileLoader {
	
	/**
	 * @param filePath
	 * @return
	 */
	boolean matchConfigFile(String filePath);
	
	/**
	 * @param filePath
	 * @throws IOException
	 */
	void loadConfigFile(String filePath) throws IOException;
}
