/**
 * 
 */
package com.rcg.foundation.fondify.components.runtime;

import java.lang.reflect.Parameter;

import com.rcg.foundation.fondify.components.ComponentsManagerImpl;
import com.rcg.foundation.fondify.components.annotations.Inject;
import com.rcg.foundation.fondify.components.helpers.AnnotationHelper;
import com.rcg.foundation.fondify.core.helpers.BeansHelper;
import com.rcg.foundation.fondify.core.typings.lifecycle.ComponentsManager;
import com.rcg.foundation.fondify.core.typings.parameters.ParameterValueActuator;
import com.rcg.foundation.fondify.utils.helpers.LoggerHelper;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class InjectedParameterValueCollectorActuator implements ParameterValueActuator {
	private ComponentsManager componentsManager = null;
	/**
	 * 
	 */
	public InjectedParameterValueCollectorActuator() {
		super();
		componentsManager = new ComponentsManagerImpl();
	}

	@Override
	public Object valueOfParameter(Parameter parameter) {
		try {
			String name = AnnotationHelper.getMethodParameterBeanName(parameter, parameter.getName());
			return componentsManager.getInjectableOrComponentByName(name, null);
		} catch (Exception e) {
			LoggerHelper.logError("InjectedParameterValueCollectorActuator::valueOfParameter", 
					String.format("Unable to collect parameter %s value due to ERRORS!!", parameter != null ? parameter.getName() : "<NULL>"), 
					e);
		}
		return null;
	}

	@Override
	public boolean isAppliableTo(Parameter parameter) {
		return BeansHelper.getParameterAnnotation(parameter, Inject.class) != null;
	}

}
