/**
 * 
 */
package com.rcg.foundation.fondify.core.typings.methods;

import java.lang.reflect.Method;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface MethodReturnObjectValueActuator {
	/**
	 * Collects value of a method return object in presence of a specific Annotation type
	 * @param parameter
	 * @return
	 */
	Object valueOfMethodReturnObject(Method method);

	boolean isAppliableTo(Method method);
}
