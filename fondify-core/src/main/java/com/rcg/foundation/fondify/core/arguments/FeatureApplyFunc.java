/**
 * 
 */
package com.rcg.foundation.fondify.core.arguments;

import java.util.Properties;

/**
 * Feature Executing A feature
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
@FunctionalInterface
public interface FeatureApplyFunc {
	
	/**
	 * Apply arguments and execute a feature if it is
	 * required from the user
	 * @param arguments
	 */
	void apply(Properties arguments);
}
