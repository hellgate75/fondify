/**
 * 
 */
package com.rcg.foundation.fondify.properties.properties;

import java.lang.reflect.Field;

import com.rcg.foundation.fondify.core.helpers.BeansHelper;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;
import com.rcg.foundation.fondify.core.properties.PropertyArchive;
import com.rcg.foundation.fondify.core.typings.fields.FieldValueActuator;
import com.rcg.foundation.fondify.properties.annotations.Value;
import com.rcg.foundation.fondify.properties.helpers.AnnotationHelper;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ValueFieldValueActuator implements FieldValueActuator {

	/**
	 * 
	 */
	public ValueFieldValueActuator() {
		super();
	}

	@Override
	public Object valueOfField(Field field) {
		try {
			String name = AnnotationHelper.getClassFieldBeanName(field, field.getName());
			return PropertyArchive.getInstance().getProperty(name);
		} catch (Exception e) {
			LoggerHelper.logError("ValueFieldValueActuator::valueOfParameter", 
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
