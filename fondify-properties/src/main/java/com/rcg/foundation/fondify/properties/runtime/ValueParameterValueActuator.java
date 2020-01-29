/**
 * 
 */
package com.rcg.foundation.fondify.properties.runtime;

import java.lang.reflect.Parameter;

import com.rcg.foundation.fondify.core.helpers.BeansHelper;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;
import com.rcg.foundation.fondify.core.properties.PropertyArchive;
import com.rcg.foundation.fondify.core.typings.parameters.ParameterValueActuator;
import com.rcg.foundation.fondify.properties.annotations.Value;
import com.rcg.foundation.fondify.properties.helpers.AnnotationHelper;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ValueParameterValueActuator implements ParameterValueActuator {

	/**
	 * 
	 */
	public ValueParameterValueActuator() {
		super();
	}

	@Override
	public Object valueOfParameter(Parameter parameter) {
		try {
			String name = AnnotationHelper.getMethodParameterBeanName(parameter, parameter.getName());
			return PropertyArchive.getInstance().getProperty(name);
		} catch (Exception e) {
			LoggerHelper.logError("ValueParameterValueActuator::valueOfParameter", 
					String.format("Unable to collect parameter %s value due to ERRORS!!", parameter != null ? parameter.getName() : "<NULL>"), 
					e);
		}
		return null;
	}

	@Override
	public boolean isAppliableTo(Parameter parameter) {
		return BeansHelper.getParameterAnnotation(parameter, Value.class) != null;
	}

}
