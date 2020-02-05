/**
 * 
 */
package com.rcg.foundation.fondify.core.typings.parameters;

import java.lang.reflect.Parameter;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface ParameterValueActuator {
	/**
	 * Collects value of a parameter in presence of a specific Annotation type
	 * @param parameter
	 * @return
	 */
	Object valueOfParameter(Parameter parameter);
	
	/**
	 * Checks if the parameter contains the annotation related to the current translator
	 * @param parameter
	 * @return
	 */
	boolean isAppliableTo(Parameter parameter);
}
