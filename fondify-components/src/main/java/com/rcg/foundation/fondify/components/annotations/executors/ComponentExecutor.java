/**
 * 
 */
package com.rcg.foundation.fondify.components.annotations.executors;

import java.lang.annotation.Annotation;

import com.rcg.foundation.fondify.annotations.annotations.methods.Finalization;
import com.rcg.foundation.fondify.annotations.annotations.methods.Initialization;
import com.rcg.foundation.fondify.annotations.typings.BeanDefinition;
import com.rcg.foundation.fondify.components.annotations.Autowired;
import com.rcg.foundation.fondify.components.annotations.Component;
import com.rcg.foundation.fondify.components.annotations.Inject;
import com.rcg.foundation.fondify.components.helpers.AnnotationHelper;
import com.rcg.foundation.fondify.core.domain.Scope;
import com.rcg.foundation.fondify.core.exceptions.ProcessException;
import com.rcg.foundation.fondify.core.helpers.GenericHelper;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;
import com.rcg.foundation.fondify.core.typings.AnnotationDeclaration;
import com.rcg.foundation.fondify.core.typings.AnnotationExecutor;
import com.rcg.foundation.fondify.core.typings.ExecutionAnswer;
import com.rcg.foundation.fondify.properties.annotations.Value;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ComponentExecutor implements AnnotationExecutor<Component> {

	private String beanName = "Component";
	
	/**
	 * 
	 */
	public ComponentExecutor() {
		super();
	}

	@Override
	public Class<? extends Component> getAnnotationClass() {
		return Component.class;
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
	public ExecutionAnswer<Component> executeAnnotation(AnnotationDeclaration t) throws ProcessException {
		LoggerHelper.logTrace("ComponentExecutor::executeAnnotation(Component)", "Executing annotation in TRCG Annotation Engine Component Module");
		String message="";
		boolean warnings = false;
		boolean errors = false;
		ExecutionAnswer<Component> answer = new ExecutionAnswer<>(getAnnotationClass(), message, warnings, errors);
		Component component = (Component)t.getAnnotation();
		Scope scope = component.scope();
		BeanDefinition definition = new BeanDefinition(t);
		Class<?> elementClass = t.getAnnotatedClass();
		beanName = AnnotationHelper.getClassBeanName(elementClass, GenericHelper.initCapBeanName(elementClass.getSimpleName()));
		definition.setScope(scope);

		AnnotationHelper.processFieldsAnnotations(elementClass, definition, beanName, ComponentExecutor::filterComponentFieldAnnotation);

		AnnotationHelper.processFieldsPropertyAnnotations(elementClass, definition, beanName, ComponentExecutor::filterComponentFieldValueAnnotation);
		
		AnnotationHelper.processMethodInitializationFinalizationAnnotations(beanName, elementClass, definition, ComponentExecutor::filterComponentMethodAnnotation);
		
		answer.addResult(definition);
		return answer;
	}

}
