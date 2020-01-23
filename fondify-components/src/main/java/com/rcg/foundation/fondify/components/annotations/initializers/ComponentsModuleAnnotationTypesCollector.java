/**
 * 
 */
package com.rcg.foundation.fondify.components.annotations.initializers;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import com.rcg.foundation.fondify.components.annotations.Autowired;
import com.rcg.foundation.fondify.components.annotations.Component;
import com.rcg.foundation.fondify.components.annotations.Injectable;
import com.rcg.foundation.fondify.core.typings.AnnotationTypesCollector;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ComponentsModuleAnnotationTypesCollector implements AnnotationTypesCollector {
	private List<Class<? extends Annotation>> annotations = new ArrayList<>(0);
	/**
	 * 
	 */
	public ComponentsModuleAnnotationTypesCollector() {
		annotations.add(Autowired.class);
		annotations.add(Component.class);
		annotations.add(Injectable.class);
	}

	@Override
	public List<Class<? extends Annotation>> listAnnotationTypes() {
		return annotations;
	}

}
