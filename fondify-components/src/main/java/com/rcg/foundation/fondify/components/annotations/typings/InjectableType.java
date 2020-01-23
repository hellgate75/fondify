/**
 * 
 */
package com.rcg.foundation.fondify.components.annotations.typings;

import com.rcg.foundation.fondify.components.injecables.Controller;
import com.rcg.foundation.fondify.components.injecables.InjectableExecutor;
import com.rcg.foundation.fondify.components.injecables.Properties;
import com.rcg.foundation.fondify.components.injecables.Service;

/**
 * Available Injectable template type. 
 * 
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public enum InjectableType {
	/**
	 * Controller types : REST, STREAM, TCP connector, etc...
	 * 
	 * All implements {@link Controller} interface
	 */
	CONTROLLER,
	/**
	 * Service element
	 * 
	 * All implements {@link Service} interface
	 */
	SERVICE,
	/**
	 * Property dispatcher element, in order to define custom, 
	 * virtual and composite properties.
	 * 
	 * All implements {@link Properties} interface
	 */
	PROPERTIES,
	/**
	 * Custom executable injectable type (sub process, thread, custom 
	 * user business case task, middleware service, etc...).
	 * 
	 * All implements {@link InjectableExecutor} interface
	 */
	EXECUTABLE;
}
