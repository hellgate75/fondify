/**
 * 
 */
package com.rcg.foundation.fondify.properties.initializers;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import com.rcg.foundation.fondify.core.typings.AnnotationTypesCollector;
import com.rcg.foundation.fondify.properties.annotations.PropertiesSet;
import com.rcg.foundation.fondify.properties.annotations.Value;
import com.rcg.foundation.fondify.properties.annotations.WithPropertiesRoot;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class PropertiesModuleAnnotationTypesCollector implements AnnotationTypesCollector {
	private List<Class<? extends Annotation>> annotations = new ArrayList<>(0);
	/**
	 * 
	 */
	public PropertiesModuleAnnotationTypesCollector() {
		annotations.add(PropertiesSet.class);
		annotations.add(Value.class);
		annotations.add(WithPropertiesRoot.class);
	}

	@Override
	public List<Class<? extends Annotation>> listAnnotationTypes() {
		return annotations;
	}

}
