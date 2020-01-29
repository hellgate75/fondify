/**
 * 
 */
package com.rcg.foundation.fondify.properties.helpers;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

import com.rcg.foundation.fondify.annotations.annotations.TransformCase;
import com.rcg.foundation.fondify.core.helpers.BeansHelper;
import com.rcg.foundation.fondify.properties.annotations.Value;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class AnnotationHelper {

	/**
	 * 
	 */
	private AnnotationHelper() {
		throw new IllegalStateException("AnnotationHelper::constructor -> Unable to instatiatio helper class");
	}

	public static final String getClassFieldBeanName(Field f, String proposed) {
		String name = proposed != null && ! proposed.isEmpty() ? proposed :  f.getName();
		Value valueAnn = BeansHelper.getFieldAnnotation(f, Value.class); 
		if ( valueAnn != null && valueAnn.value() != null && ! valueAnn.value().isEmpty() ) {
			proposed = valueAnn.value();
		}
		TransformCase caseTransformer = BeansHelper.getFieldAnnotation(f, TransformCase.class);
		if ( caseTransformer != null ) {
			name = com.rcg.foundation.fondify.annotations.helpers.AnnotationHelper.transformBeanName(name, caseTransformer);
		}
		return name;
	}

	public static final String getMethodParameterBeanName(Parameter p, String proposed) {
		String name = proposed != null && ! proposed.isEmpty() ? proposed :  p.getName();
		Value valueAnn = BeansHelper.getParameterAnnotation(p, Value.class);
		if ( valueAnn != null && valueAnn.value() != null && ! valueAnn.value().isEmpty() ) {
			proposed = valueAnn.value();
		}
		TransformCase caseTransformer = BeansHelper.getParameterAnnotation(p, TransformCase.class);
		if ( caseTransformer != null ) {
			name = com.rcg.foundation.fondify.annotations.helpers.AnnotationHelper.transformBeanName(name, caseTransformer);
		}
		return name;
	}

}
