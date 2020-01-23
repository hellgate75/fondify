/**
 * 
 */
package com.rcg.foundation.fondify.annotations.initalizers;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import com.rcg.foundation.fondify.annotations.annotations.Application;
import com.rcg.foundation.fondify.annotations.annotations.ComponentsScan;
import com.rcg.foundation.fondify.annotations.annotations.Configuration;
import com.rcg.foundation.fondify.annotations.annotations.Defaults;
import com.rcg.foundation.fondify.annotations.annotations.ModuleScannerConfig;
import com.rcg.foundation.fondify.annotations.annotations.ModulesScan;
import com.rcg.foundation.fondify.core.typings.AnnotationTypesCollector;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class AnnotationsModuleAnnotationTypesCollector implements AnnotationTypesCollector {
	private List<Class<? extends Annotation>> annotations = new ArrayList<>(0);
	/**
	 * 
	 */
	public AnnotationsModuleAnnotationTypesCollector() {
		annotations.add(Application.class);
		annotations.add(Configuration.class);
		annotations.add(Defaults.class);
		annotations.add(ModuleScannerConfig.class);
		annotations.add(ModulesScan.class);
		annotations.add(ComponentsScan.class);
	}

	@Override
	public List<Class<? extends Annotation>> listAnnotationTypes() {
		return annotations;
	}

}
