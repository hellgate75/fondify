/**
 * 
 */
package com.rcg.foundation.fondify.core.typings.parameters;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.rcg.foundation.fondify.core.helpers.BeansHelper;
import com.rcg.foundation.fondify.core.helpers.GenericHelper;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class ParameterValueActuatorProvider {

	private static ParameterValueActuatorProvider instance = null;
	
	List<ParameterValueActuator> actuators = new ArrayList<>(0);
	
	/**
	 * 
	 */
	private ParameterValueActuatorProvider() {
		super();
		actuators.addAll(
				BeansHelper.getImplementedTypes(ParameterValueActuator.class)
		);
	}
	
	public Optional<Object> tranlateParameterValue(Parameter parameter) {
		if ( parameter != null ) {
			String threadName = Thread.currentThread().getName();
			return actuators
				.stream()
				.filter( act -> act.isAppliableTo(parameter) )
				.map( act -> { 
					GenericHelper.fixCurrentThreadStandardName(threadName);
					try {
						return act.valueOfParameter(parameter);
					} catch (Exception e) {
						LoggerHelper.logError("AnnotationBeanActuatorProvider::registerCustomBean", 
								String.format("Unable to recover field %s value with the actuator : %s due to ERRORS!!!", parameter.getName(), act.getClass().getName()), 
								e);
					}
					return null;
				})
				.filter(value -> value != null)
				.findFirst();
		}
		return Optional.empty();
	}
	
	public static synchronized final ParameterValueActuatorProvider getInstance() {
		if ( instance == null ) {
			instance = new ParameterValueActuatorProvider();
		}
		return instance;
	}

}

