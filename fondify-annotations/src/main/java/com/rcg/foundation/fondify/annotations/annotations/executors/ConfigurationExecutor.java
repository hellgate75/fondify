/**
 * 
 */
package com.rcg.foundation.fondify.annotations.annotations.executors;

import java.util.Arrays;

import com.rcg.foundation.fondify.annotations.annotations.Configuration;
import com.rcg.foundation.fondify.annotations.annotations.TransformCase;
import com.rcg.foundation.fondify.annotations.contants.AnnotationConstants;
import com.rcg.foundation.fondify.annotations.helpers.ScriptsHelper;
import com.rcg.foundation.fondify.annotations.typings.BeanDefinition;
import com.rcg.foundation.fondify.annotations.helpers.AnnotationHelper;
import com.rcg.foundation.fondify.core.domain.Scope;
import com.rcg.foundation.fondify.core.exceptions.ProcessException;
import com.rcg.foundation.fondify.core.functions.BiProcessor;
import com.rcg.foundation.fondify.core.helpers.BeansHelper;
import com.rcg.foundation.fondify.core.helpers.GenericHelper;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;
import com.rcg.foundation.fondify.core.typings.AnnotationDeclaration;
import com.rcg.foundation.fondify.core.typings.AnnotationExecutor;
import com.rcg.foundation.fondify.core.typings.ExecutionAnswer;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ConfigurationExecutor implements AnnotationExecutor<Configuration> {
	String beanName = "Configuration";
	
	public static String configurationLoadersRegistryKey=AnnotationConstants.REGISTRY_CLASS_CONFIG_LOADERS;
	
	public static BiProcessor<Class<?>, BeanDefinition> beanDefinitionProcessor = (elementClass, definition, arguments) -> {
		LoggerHelper.logWarn("ConfigurationExecutor::init", "No customizetion for Bean Definition Processor feature, please customize with code used to execute and maintain your Method and Field Scanning feature for the single bean definition in Configuration annotation", null);
	};
	
	/**
	 * 
	 */
	public ConfigurationExecutor() {
		super();
	}

	@Override
	public Class<? extends Configuration> getAnnotationClass() {
		return Configuration.class;
	}

	@Override
	public boolean containsResults() {
		return true;
	}

	@Override
	public String getComponentName() {
		return beanName;
	}

	@Override
	public ExecutionAnswer<Configuration> executeAnnotation(AnnotationDeclaration t) throws ProcessException {
		LoggerHelper.logTrace("ConfigurationExecutor::executeAnnotation(Configuration)", "Executing annotation in TRCG Annotation Engine Annotations Module");
		String message="";
		boolean warnings = false;
		boolean errors = false;
		ExecutionAnswer<Configuration> answer = new ExecutionAnswer<>(getAnnotationClass(), message, warnings, errors);
		Configuration configuration = (Configuration)t.getAnnotation();
		Scope scope = Scope.APPLICATION;
		BeanDefinition definition = new BeanDefinition(t);
		Class<?> elementClass = t.getAnnotatedClass();
		beanName = GenericHelper.initCapBeanName(elementClass.getSimpleName());
		TransformCase caseTransformer = BeansHelper.getClassAnnotation(elementClass, TransformCase.class);
		if ( caseTransformer != null ) {
			beanName = AnnotationHelper.transformBeanName(beanName, caseTransformer);
		}
		definition.setScope(scope);
		
		beanDefinitionProcessor.process(elementClass, definition);
		
		String[] initScripts = configuration.initScripts();
		
		loadAndApplyScriptFiles(initScripts);
		
		answer.addResult(definition);
		return answer;
	}
	
	private static final void loadAndApplyScriptFiles(String[] scripts) {
		Arrays.asList(scripts).forEach(s->ScriptsHelper.loadScriptFileOrFolder(s, configurationLoadersRegistryKey));
	}

}
