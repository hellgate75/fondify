/**
 * 
 */
package com.rcg.foundation.fondify.properties.runtime;

import java.lang.reflect.Field;

import com.fasterxml.jackson.annotation.JacksonInject.Value;
import com.rcg.foundation.fondify.annotations.annotations.TransformCase;
import com.rcg.foundation.fondify.core.helpers.BeansHelper;
import com.rcg.foundation.fondify.core.properties.PropertyArchive;
import com.rcg.foundation.fondify.core.typings.fields.FieldValueActuator;
import com.rcg.foundation.fondify.utils.helpers.LoggerHelper;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ValueFieldValueCollectorActuator implements FieldValueActuator {

	/**
	 * 
	 */
	public ValueFieldValueCollectorActuator() {
		super();
	}

	@Override
	public Object valueOfField(Field field) {
		try {
			String name = field.getName();
			TransformCase tc = BeansHelper.getFieldAnnotation(field, TransformCase.class);
			if ( tc != null ) {
				name = com.rcg.foundation.fondify.annotations.helpers.AnnotationHelper.transformBeanName(name, tc);
			}
			return PropertyArchive.getInstance().getProperty(name);
		} catch (Exception e) {
			LoggerHelper.logError("ValueFieldValueCollectorActuator::valueOfParameter", 
					String.format("Unable to collect field %s value due to ERRORS!!", field != null ? field.getName() : "<NULL>"), 
					e);
		}
		return null;
	}

	@Override
	public boolean isAppliableTo(Field field) {
		return BeansHelper.getFieldAnnotation(field, Value.class) != null;
	}

}
