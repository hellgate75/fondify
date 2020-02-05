/**
 * 
 */
package com.rcg.foundation.fondify.core.typings.methods;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.rcg.foundation.fondify.core.functions.Transformer;
import com.rcg.foundation.fondify.core.helpers.BeansHelper;
import com.rcg.foundation.fondify.utils.helpers.GenericHelper;
import com.rcg.foundation.fondify.utils.helpers.LoggerHelper;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class MethodReturnObjectValueActuatorProvider {

	private static MethodReturnObjectValueActuatorProvider instance = null;
	
	private List<MethodReturnObjectValueActuator> actuators = new ArrayList<>(0);
	
	public static Transformer<Method, Object> methodDefaultTranslator = (param, args) -> { return null; };
	
	/**
	 * 
	 */
	private MethodReturnObjectValueActuatorProvider() {
		super();
		actuators.addAll(
				BeansHelper.getImplementedTypes(MethodReturnObjectValueActuator.class)
		);
	}
	
	public Optional<Object> tranlateMethodReturnObjectValue(Method method) {
		if ( method != null ) {
			String threadName = Thread.currentThread().getName();
			List<MethodReturnObjectValueActuator> list = actuators
				.stream()
				.filter( act -> act.isAppliableTo(method) )
				.collect(Collectors.toList());
				if (list.size() == 0) {
					return Optional.of( methodDefaultTranslator.tranform(method) );
				}
				return list
					.stream()
					.map( act -> { 
						GenericHelper.fixCurrentThreadStandardName(threadName);
						try {
							return act.valueOfMethodReturnObject(method);
						} catch (Exception e) {
							LoggerHelper.logError("MethodReurnObjectValueActuatorProvider::tranlateMethodReturnObjectValue", 
									String.format("Unable to recover method %s value with the actuator : %s due to ERRORS!!!", method.getName(), act.getClass().getName()), 
									e);
						}
						return null;
				})
				.filter(value -> value != null)
				.findFirst();
		}
		return Optional.empty();
	}
	
	public static synchronized final MethodReturnObjectValueActuatorProvider getInstance() {
		if ( instance == null ) {
			instance = new MethodReturnObjectValueActuatorProvider();
		}
		return instance;
	}

}

