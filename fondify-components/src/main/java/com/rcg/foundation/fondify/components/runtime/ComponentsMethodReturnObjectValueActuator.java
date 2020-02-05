/**
 * 
 */
package com.rcg.foundation.fondify.components.runtime;

import java.lang.reflect.Method;

import com.rcg.foundation.fondify.annotations.typings.MethodExecutor;
import com.rcg.foundation.fondify.components.annotations.Component;
import com.rcg.foundation.fondify.components.annotations.Injectable;
import com.rcg.foundation.fondify.components.helpers.AnnotationHelper;
import com.rcg.foundation.fondify.core.typings.lifecycle.ComponentsManager;
import com.rcg.foundation.fondify.core.typings.methods.MethodReturnObjectValueActuator;
import com.rcg.foundation.fondify.utils.helpers.ArgumentsHelper;
import com.rcg.foundation.fondify.utils.helpers.GenericHelper;
import com.rcg.foundation.fondify.utils.helpers.LoggerHelper;

/**
 * Collect injectables or components from registry using
 * {@link ComponentsManager} implementation.
 * In case there is no match null will be returned and it will be discarded from the
 * following methods in the given sequence:
 * 
 * {@link com.rcg.foundation.fondify.annotations.helpers.AnnotationHelper#valueOfInjectedMethodReturnObject(Method)} then from
 * {@link MethodExecutor#execute(Object, com.rcg.foundation.fondify.core.functions.Transformer, com.rcg.foundation.fondify.core.functions.Transformer, com.rcg.foundation.fondify.core.functions.Transformer, com.rcg.foundation.fondify.core.functions.Transformer)}
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ComponentsMethodReturnObjectValueActuator implements MethodReturnObjectValueActuator{
	
	/**
	 * 
	 */
	public ComponentsMethodReturnObjectValueActuator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object valueOfMethodReturnObject(Method method) {
		Class<?> elementClass = method.getReturnType();
		String beanName = GenericHelper.initCapBeanName(elementClass.getSimpleName());
		beanName = AnnotationHelper.getClassBeanName(elementClass, beanName);
		if ( ArgumentsHelper.traceAllLevels || ArgumentsHelper.traceComponentsLevel  ) {
			LoggerHelper.logTrace("ComponentsMethodReturnObjectValueActuator::valueOfMethodReturnObject", 
								String.format("Discovering bean named: %s", beanName));
		}
		try {
			Object obj = AnnotationHelper.seekForComponentOrInjectable(beanName);
			if ( ArgumentsHelper.traceAllLevels || ArgumentsHelper.traceComponentsLevel  ) {
				LoggerHelper.logTrace("ComponentsMethodReturnObjectValueActuator::valueOfMethodReturnObject", 
						String.format("Collected bean named: %s, of type %s, with value: %s", beanName, 
														obj != null ? obj.getClass().getName() : "<NULL>", 
																obj != null ? "" + obj : "<NULL>"));
			}
			return obj;
		} catch (Exception | Error e) {
			LoggerHelper.logError("ComponentsMethodReturnObjectValueActuator::valueOfMethodReturnObject", 
					String.format("Unable to recover bean named %s, due to errors!!", beanName), 
					e);
		}
		return null;
	}

	@Override
	public boolean isAppliableTo(Method method) {
		return method.getDeclaredAnnotation(Component.class) != null ||
				method.getDeclaredAnnotation(Injectable.class) != null;
	}

}
