/**
 * 
 */
package com.rcg.foundation.fondify.core.arguments;

import java.util.Collection;
import java.util.Properties;

/**
 * Feature Matching Functional Interface
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
@FunctionalInterface
public interface FeatureMatchFunc {
	
	/**
	 * Match argument and check if a feature is
	 * required into the parameters
	 * @param descr
	 * @param arguments
	 * @return
	 */
	boolean match(Collection<ArgumentDescriptor> descr, Properties arguments);
}
