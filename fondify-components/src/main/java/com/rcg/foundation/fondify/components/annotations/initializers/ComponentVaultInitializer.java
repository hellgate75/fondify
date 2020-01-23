/**
 * 
 */
package com.rcg.foundation.fondify.components.annotations.initializers;

import com.rcg.foundation.fondify.annotations.annotations.executors.ConfigurationExecutor;
import com.rcg.foundation.fondify.components.helpers.AnnotationHelper;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;
import com.rcg.foundation.fondify.core.typings.AnnotationEngineInitializer;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ComponentVaultInitializer implements AnnotationEngineInitializer {

	/**
	 * Default public constructor
	 */
	public ComponentVaultInitializer() {
		super();
	}

	@Override
	public void initialize() {
		LoggerHelper.logTrace("ComponentVaultInitializer::initialize", "Initializing TRCG Annotation Engine Component module");
		ConfigurationExecutor.beanDefinitionProcessor = (elementClass, definition, arguments) -> {
			String beanName = AnnotationHelper.getClassBeanName(elementClass, elementClass.getSimpleName());
			AnnotationHelper.processFieldsAnnotations(elementClass, definition, beanName, AnnotationHelper::filterComponentFieldAnnotation);

			AnnotationHelper.processMethodAnnotations(elementClass, definition, beanName, AnnotationHelper::filterComponentMethodAnnotation);
		};
	}
	
	

}
