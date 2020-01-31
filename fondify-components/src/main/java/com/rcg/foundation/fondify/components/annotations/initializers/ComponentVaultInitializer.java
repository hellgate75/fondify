/**
 * 
 */
package com.rcg.foundation.fondify.components.annotations.initializers;

import com.rcg.foundation.fondify.annotations.annotations.executors.ConfigurationExecutor;
import com.rcg.foundation.fondify.components.helpers.AnnotationHelper;
import com.rcg.foundation.fondify.core.helpers.GenericHelper;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;
import com.rcg.foundation.fondify.core.typings.AnnotationEngineInitializer;
import com.rcg.foundation.fondify.core.typings.parameters.ParameterValueActuatorProvider;

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
			String beanName = AnnotationHelper.getClassBeanName(elementClass, GenericHelper.initCapBeanName(elementClass.getSimpleName()));
			AnnotationHelper.processFieldsAnnotations(elementClass, definition, beanName, AnnotationHelper::filterComponentFieldAnnotation);

			AnnotationHelper.processMethodAnnotations(elementClass, definition, beanName, AnnotationHelper::filterComponentMethodAnnotation);
		};
		ParameterValueActuatorProvider.parametersDefaultTranslator  = (param, args) -> {
				String beanName = AnnotationHelper.getMethodParameterBeanName(param, param.getName());
				Object instance = null;
				try {
					instance = param.getType().newInstance();
				} catch (Exception e) {
					LoggerHelper.logError("ComponentVaultInitializer::initialize::[runbehalf ParameterValueActuatorProvider::parametersDefaultTranslator]", "Unable to create new instance of class: " + param.getParameterizedType().getClass().getName(), null);
					if ( param.getName().startsWith("arg") ) {
						return null;
					}
				}
				Object ret = AnnotationHelper.scanAndProcessEntity(instance, instance!= null ? instance.getClass() : null, beanName);
				if ( ret == null ) {
					beanName = AnnotationHelper.getClassBeanName(param.getType().getClass(), GenericHelper.initCapBeanName(param.getType().getClass().getSimpleName()));
					ret = AnnotationHelper.scanAndProcessEntity(instance, instance!= null ? instance.getClass() : null, beanName);
				}
				return ret;
			};
	}
	
	

}
