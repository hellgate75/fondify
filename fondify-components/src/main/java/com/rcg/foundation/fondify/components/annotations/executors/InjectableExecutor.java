/**
 * 
 */
package com.rcg.foundation.fondify.components.annotations.executors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.rcg.foundation.fondify.annotations.annotations.methods.Finalization;
import com.rcg.foundation.fondify.annotations.annotations.methods.Initialization;
import com.rcg.foundation.fondify.annotations.contants.AnnotationConstants;
import com.rcg.foundation.fondify.annotations.typings.BeanDefinition;
import com.rcg.foundation.fondify.annotations.typings.MethodExecutor;
import com.rcg.foundation.fondify.components.annotations.Autowired;
import com.rcg.foundation.fondify.components.annotations.Inject;
import com.rcg.foundation.fondify.components.annotations.Injectable;
import com.rcg.foundation.fondify.components.helpers.AnnotationHelper;
import com.rcg.foundation.fondify.core.domain.Scope;
import com.rcg.foundation.fondify.core.exceptions.ProcessException;
import com.rcg.foundation.fondify.core.helpers.BeansHelper;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;
import com.rcg.foundation.fondify.core.registry.ComponentsRegistry;
import com.rcg.foundation.fondify.core.typings.AnnotationDeclaration;
import com.rcg.foundation.fondify.core.typings.AnnotationDeclarationType;
import com.rcg.foundation.fondify.core.typings.AnnotationExecutor;
import com.rcg.foundation.fondify.core.typings.ExecutionAnswer;
import com.rcg.foundation.fondify.properties.annotations.Value;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class InjectableExecutor implements AnnotationExecutor<Injectable> {

	private String beanName = "Injectable";
	
	/**
	 * 
	 */
	public InjectableExecutor() {
		super();
	}

	@Override
	public Class<? extends Injectable> getAnnotationClass() {
		return Injectable.class;
	}

	@Override
	public boolean containsResults() {
		return false;
	}

	@Override
	public String getComponentName() {
		return beanName;
	}
	
	public static final boolean filterComponentMethodAnnotation(Annotation ann) {
		return Initialization.class.isAssignableFrom(ann.getClass()) ||
				Finalization.class.isAssignableFrom(ann.getClass());
	}
	
	public static final boolean filterComponentFieldAnnotation(Annotation ann) {
		return Autowired.class.isAssignableFrom(ann.getClass()) ||
				   Inject.class.isAssignableFrom(ann.getClass());
	}
	
	public static final boolean filterComponentFieldValueAnnotation(Annotation ann) {
		return Value.class.isAssignableFrom(ann.getClass());
	}
	
	@Override
	public ExecutionAnswer<Injectable> executeAnnotation(AnnotationDeclaration t) throws ProcessException {
		LoggerHelper.logTrace("InjectableExecutor::executeAnnotation(Injectable)", "Executing annotation in TRCG Annotation Engine Component Module");
		String message="";
		boolean warnings = false;
		boolean errors = false;
		ExecutionAnswer<Injectable> answer = new ExecutionAnswer<>(getAnnotationClass(), message, warnings, errors);
		Injectable injectable = (Injectable)t.getAnnotation();
		Scope scope = injectable.scope();
		beanName = t.getAnnotationDeclarationClass().getSimpleName();
		String regName = AnnotationConstants.REGISTRY_INJECTABLE_BEAN_DEFINITIONS;
		Object injectableDefinition = null;
		if ( t.getAnnotationDeclarationType() == AnnotationDeclarationType.TYPE ) {
			beanName = AnnotationHelper.getClassBeanName(t.getAnnotationDeclarationClass(), beanName);
			BeanDefinition definition = new BeanDefinition(t);
			Class<?> elementClass = t.getAnnotatedClass();
			beanName = AnnotationHelper.getClassBeanName(elementClass, elementClass.getSimpleName());
			definition.setScope(scope);

			AnnotationHelper.processFieldsAnnotations(elementClass, definition, beanName, InjectableExecutor::filterComponentFieldAnnotation);

			AnnotationHelper.processFieldsPropertyAnnotations(elementClass, definition, beanName, InjectableExecutor::filterComponentFieldValueAnnotation);
			
			AnnotationHelper.processMethodInitializationFinalizationAnnotations(beanName, elementClass, definition, InjectableExecutor::filterComponentMethodAnnotation);
			injectableDefinition = definition;
		} else if ( t.getAnnotationDeclarationType() == AnnotationDeclarationType.METHOD ) {
			regName = AnnotationConstants.REGISTRY_INJECTABLE_METHODD_DEFINITIONS;
			Method method = t.getAnnotationMethod();
			beanName = AnnotationHelper.getClassMethodBeanName(method, method.getName());
			Class<?> annotatedClass = t.getAnnotatedClass();
			String proposed = annotatedClass.getSimpleName();
			Initialization initializationAnnotation = BeansHelper.getMethodAnnotation(method, Initialization.class);
			Finalization finalizationAnnotation = BeansHelper.getMethodAnnotation(method, Finalization.class);
			beanName = AnnotationHelper.getClassMethodBeanName(method, method.getName());
			MethodExecutor executor = new MethodExecutor(t, beanName, method, initializationAnnotation, finalizationAnnotation);
			AnnotationHelper
				.getParametersRefFor(method, annotatedClass, AnnotationHelper.getClassBeanName(annotatedClass, proposed))
				.forEach(executor::addParameter);		
			injectableDefinition = executor;
		} else {
			LoggerHelper.logWarn("InjectableExecutor::executeAnnotation", 
								String.format("Unable to instantiate Bean (name: %s) for Injectable with scope: %s", 
												beanName, 
												t.getAnnotationDeclarationType().name()),
								null);
		}
		if ( injectableDefinition != null ) {
			LoggerHelper.logTrace("InjectableExecutor::executeAnnotation", 
					String.format("Registration od Bean names: %s for Injectable with scope: %s, of type: %s, in registry: %s", 
									beanName, 
									t.getAnnotationDeclarationType().name(),
									injectableDefinition != null ? injectableDefinition.getClass().getName(): "<NULL>",
									regName) );
			ComponentsRegistry.getInstance().add(regName, beanName, injectableDefinition);
		} else {
			LoggerHelper.logWarn("InjectableExecutor::executeAnnotation", 
					String.format("Unable to register Null Bean names: %s for Injectable with scope: %s, in registry: %s", 
									beanName, 
									t.getAnnotationDeclarationType().name(),
									regName),
					null);
		}
		return answer;
	}

}
