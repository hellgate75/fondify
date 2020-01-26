/**
 * 
 */
package com.rcg.foundation.fondify.core.typings.registry;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface AnnotationBeanActuator<T> {
	/**
	 * Actuate custom bean registration
	 * @param t
	 * @throws Exception
	 */
	void registerBean(T t) throws Exception;
	
	/**
	 * Collect actuator bean super type reference class
	 * @return
	 */
	Class<? extends T> getActuatorSuperClass();
}
