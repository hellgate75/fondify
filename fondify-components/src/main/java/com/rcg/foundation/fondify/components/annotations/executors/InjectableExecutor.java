/**
 * 
 */
package com.rcg.foundation.fondify.components.annotations.executors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.rcg.foundation.fondify.annotations.annotations.methods.Finalization;
import com.rcg.foundation.fondify.annotations.annotations.methods.Initialization;
import com.rcg.foundation.fondify.annotations.typings.BeanDefinition;
import com.rcg.foundation.fondify.annotations.typings.MethodExecutor;
import com.rcg.foundation.fondify.components.annotations.Autowired;
import com.rcg.foundation.fondify.components.annotations.Inject;
import com.rcg.foundation.fondify.components.annotations.Injectable;
import com.rcg.foundation.fondify.components.helpers.AnnotationHelper;
import com.rcg.foundation.fondify.core.domain.Scope;
import com.rcg.foundation.fondify.core.exceptions.ProcessException;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;
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
		return true;
	}

	@Override
	public String getComponentName() {
		return beanName;
	}
	
	protected static final boolean filterComponentMethodAnnotation(Annotation ann) {
		return Initialization.class.isAssignableFrom(ann.getClass()) ||
				Finalization.class.isAssignableFrom(ann.getClass());
	}
	
	protected static final boolean filterComponentFieldAnnotation(Annotation ann) {
		return Autowired.class.isAssignableFrom(ann.getClass()) ||
				   Inject.class.isAssignableFrom(ann.getClass());
	}
	
	protected static final boolean filterComponentFieldValueAnnotation(Annotation ann) {
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
		if ( t.getAnnotationDeclarationType() == AnnotationDeclarationType.TYPE ) {
			BeanDefinition definition = new BeanDefinition(t);
			Class<?> elementClass = t.getAnnotatedClass();
			beanName = elementClass.getSimpleName();
			beanName = AnnotationHelper.getClassBeanName(elementClass, elementClass.getSimpleName());
			definition.setScope(scope);

			AnnotationHelper.processFieldsAnnotations(elementClass, definition, beanName, InjectableExecutor::filterComponentFieldAnnotation);

			AnnotationHelper.processFieldsPropertyAnnotations(elementClass, definition, beanName, InjectableExecutor::filterComponentFieldValueAnnotation);
			
			AnnotationHelper.processMethodInitializationFinalizationAnnotations(beanName, elementClass, definition, InjectableExecutor::filterComponentMethodAnnotation);
			
			answer.addResult(definition);
		} else if ( t.getAnnotationDeclarationType() == AnnotationDeclarationType.METHOD ) {
			
			Method method = t.getAnnotationMethod();
			Class<?> annotatedClass = t.getAnnotatedClass();
			String proposed = annotatedClass.getSimpleName();
			Initialization initializationAnnotation = method.getAnnotation(Initialization.class);
			Finalization finalizationAnnotation = method.getAnnotation(Finalization.class);
			beanName = AnnotationHelper.getClassMethodBeanName(method, method.getName());
			MethodExecutor executor = new MethodExecutor(beanName, method, initializationAnnotation, finalizationAnnotation);
			AnnotationHelper
				.getParametersRefFor(method, annotatedClass, AnnotationHelper.getClassBeanName(annotatedClass, proposed))
				.forEach(executor::addParameter);
			
			answer.addResult(executor);
		}
		return answer;
	}

}
