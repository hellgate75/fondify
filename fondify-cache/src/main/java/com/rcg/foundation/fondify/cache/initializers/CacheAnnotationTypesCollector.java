/**
 * 
 */
package com.rcg.foundation.fondify.cache.initializers;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import com.rcg.foundation.fondify.cache.annotations.EnableCache;
import com.rcg.foundation.fondify.core.typings.AnnotationTypesCollector;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class CacheAnnotationTypesCollector implements AnnotationTypesCollector {
	private List<Class<? extends Annotation>> annotations = new ArrayList<>(0);

	/**
	 * 
	 */
	public CacheAnnotationTypesCollector() {
		super();
		annotations.add(EnableCache.class);
	}

	@Override
	public List<Class<? extends Annotation>> listAnnotationTypes() {
		return annotations;
	}

}
