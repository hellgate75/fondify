/**
 * 
 */
package com.rcg.foundation.fondify.core.typings.fields;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.rcg.foundation.fondify.core.helpers.BeansHelper;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class FieldValueActuatorProvider {

	private static FieldValueActuatorProvider instance = null;
	
	List<FieldValueActuator> actuators = new ArrayList<>(0);
	
	/**
	 * 
	 */
	private FieldValueActuatorProvider() {
		super();
		actuators.addAll(
				BeansHelper.getImplementedTypes(FieldValueActuator.class)
		);
	}
	
	public Optional<Object> tranlateFieldValue(Field field) {
		if ( field != null ) {
			field.setAccessible(true);
			return actuators
				.stream()
				.filter( act -> act.isAppliableTo(field) )
				.map( act -> { 
					try {
						return act.valueOfField(field);
					} catch (Exception e) {
						LoggerHelper.logError("AnnotationBeanActuatorProvider::registerCustomBean", 
								String.format("Unable to recover field %s value with the actuator : %s due to ERRORS!!!", field.getName(), act.getClass().getName()), 
								e);
					}
					return null;
				})
				.filter(value -> value != null)
				.findFirst();
		}
		return Optional.empty();
	}
	
	public static synchronized final FieldValueActuatorProvider getInstance() {
		if ( instance == null ) {
			instance = new FieldValueActuatorProvider();
		}
		return instance;
	}

}

