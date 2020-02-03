/**
 * 
 */
package com.rcg.foundation.fondify.core.typings.registry;

import java.util.ArrayList;
import java.util.List;

import com.rcg.foundation.fondify.core.helpers.BeansHelper;
import com.rcg.foundation.fondify.utils.helpers.LoggerHelper;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class AnnotationBeanActuatorProvider {

	private static AnnotationBeanActuatorProvider instance = null;
	
	@SuppressWarnings("rawtypes")
	List<AnnotationBeanActuator> actuators = new ArrayList<>(0);
	
	/**
	 * 
	 */
	private AnnotationBeanActuatorProvider() {
		super();
		actuators.addAll(
				BeansHelper.getImplementedTypes(AnnotationBeanActuator.class)
		);
	}
	
	@SuppressWarnings("unchecked")
	public <T> void registerCustomBean(T t) {
		if ( t != null ) {
			actuators
				.stream()
				.filter( act -> act.getActuatorSuperClass().isAssignableFrom(t.getClass()) )
				.forEach( act -> { 
					try {
						act.registerBean(t);
					} catch (Exception e) {
						LoggerHelper.logError("AnnotationBeanActuatorProvider::registerCustomBean", 
								String.format("Unable to register bean %s with the actuator : %s due to ERRORS!!!", t.getClass().getName(), act.getClass().getName()), 
								e);
					} 
				});
		}
	}
	
	public static synchronized final AnnotationBeanActuatorProvider getInstance() {
		if ( instance == null ) {
			instance = new AnnotationBeanActuatorProvider();
		}
		return instance;
	}

}

