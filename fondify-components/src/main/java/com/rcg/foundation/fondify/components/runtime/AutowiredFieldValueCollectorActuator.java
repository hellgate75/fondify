/**
 * 
 */
package com.rcg.foundation.fondify.components.runtime;

import java.lang.reflect.Field;

import com.rcg.foundation.fondify.components.ComponentsManagerImpl;
import com.rcg.foundation.fondify.components.annotations.Autowired;
import com.rcg.foundation.fondify.components.helpers.AnnotationHelper;
import com.rcg.foundation.fondify.core.helpers.BeansHelper;
import com.rcg.foundation.fondify.core.typings.fields.FieldValueActuator;
import com.rcg.foundation.fondify.core.typings.lifecycle.ComponentsManager;
import com.rcg.foundation.fondify.utils.helpers.LoggerHelper;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class AutowiredFieldValueCollectorActuator implements FieldValueActuator {
	private ComponentsManager componentsManager = null;

	/**
	 * 
	 */
	public AutowiredFieldValueCollectorActuator() {
		super();
		componentsManager = new ComponentsManagerImpl();
	}

	@Override
	public Object valueOfField(Field field) {
		try {
			String name = AnnotationHelper.getClassFieldBeanName(field, field.getName());
			return componentsManager.getInjectableOrComponentByName(name, null);
		} catch (Exception e) {
			LoggerHelper.logError("AutowiredFieldValueCollectorActuator::valueOfParameter", 
					String.format("Unable to collect field %s value due to ERRORS!!", field != null ? field.getName() : "<NULL>"), 
					e);
		}
		return null;
	}

	@Override
	public boolean isAppliableTo(Field field) {
		return BeansHelper.getFieldAnnotation(field, Autowired.class) != null;
	}

}
