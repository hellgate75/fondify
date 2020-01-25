/**
 * 
 */
package com.rcg.foundation.fondify.components.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import com.rcg.foundation.fondify.annotations.contants.AnnotationConstants;
import com.rcg.foundation.fondify.annotations.typings.BeanDefinition;
import com.rcg.foundation.fondify.annotations.typings.MethodExecutor;
import com.rcg.foundation.fondify.components.annotations.Autowired;
import com.rcg.foundation.fondify.components.annotations.Inject;
import com.rcg.foundation.fondify.components.annotations.executors.InjectableExecutor;
import com.rcg.foundation.fondify.core.functions.Transformer;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;
import com.rcg.foundation.fondify.core.properties.PropertyArchive;
import com.rcg.foundation.fondify.core.registry.ComponentsRegistry;
import com.rcg.foundation.fondify.core.typings.AnnotationDeclaration;
import com.rcg.foundation.fondify.core.typings.AnnotationDeclarationType;
import com.rcg.foundation.fondify.properties.annotations.Value;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class ComponentsHelper {

	/**
	 * 
	 */
	private ComponentsHelper() {
		throw new IllegalStateException("Unable to instatiatio helper class");
	}

	/**
	 * @param <T>
	 * @param beanName
	 * @param definition
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static final <T> T createNewBean(String beanName, BeanDefinition definition) {
		AnnotationDeclaration declaration = definition.getDeclaration();
		AnnotationDeclarationType typeRef = declaration.getAnnotationDeclarationType();
		try {
			switch ( typeRef ) {
				case TYPE:
					Class<? extends T> objectClass = (Class<? extends T>)declaration.getAnnotatedClass();
					T t = objectClass.newInstance();
					definition.getPropertiesReference()
						.forEach( propertyRef -> {
							String propertyName = propertyRef.getPropertyDescr();
							try {
								Field fld = objectClass.getField(propertyRef.getElemRef());
								fld.set(t, PropertyArchive.getInstance().getProperty(propertyName));
							} catch (Exception ex) {
								String message = String.format("Error creating property for bean definition : %s, property ref: %s", 
																""+definition,
																""+propertyRef);
								LoggerHelper.logError("ApplicationHelper::createNewBean", message, ex);
								throw new RuntimeException(message, ex);
							}
						});
					definition.getComponentsReference()
						.forEach( componentRef -> {
							String beanNameRef = componentRef.getElemDescriptor();
							try {
								Field fld = objectClass.getField(componentRef.getElemRef());
								fld.set(t, ComponentsRegistry.getInstance().seek(beanNameRef));
							} catch (Exception ex) {
								String message = String.format("Error creating property for bean definition : %s, component ref: %s", 
																""+definition,
																""+componentRef);
								LoggerHelper.logError("ApplicationHelper::createNewBean", message, ex);
								throw new RuntimeException(message, ex);
							}
							
						});
					return t;
				case METHOD:
					throw new RuntimeException(String.format("Bean with name %s has scope METHOD and it should be in MethodExecutor registry", beanName));
				case PARAMETER:
					throw new RuntimeException(String.format("Bean with name %s has scope PARAMETER and it should be in MethodExecutor registry", beanName));
				case FIELD:
					throw new RuntimeException(String.format("Bean with name %s has scope FIELD and it should be in BeanDefinition registry", beanName));
				case NONE:
				default:
			}
		} catch (Exception ex) {
			String message = String.format("Error creating bean for bean definition : %s", ""+definition);
			LoggerHelper.logError("ApplicationHelper::createNewBean", message, ex);
			throw new RuntimeException(message, ex);
		}
		return null;
	}

	/**
	 * @param <T>
	 * @param beanName
	 * @param executor
	 * @return
	 */
	public static final <T> T createNewBean(String beanName, MethodExecutor executor) {
		try {
			String beanNameRef = executor.getBeanName();
			executor.execute(ComponentsRegistry.getInstance().seek(beanNameRef),
					getValueExtractor(), getAutowiredTransformer(), getInjectTransformer(),
					(in, out) -> {
						String beanNameStr = executor.getBeanName();
						BeanDefinition definition = new BeanDefinition(executor.getDescriptor());
						Class<?> elementClass = executor.getDescriptor().getAnnotatedClass();
						beanNameStr = AnnotationHelper.getClassBeanName(elementClass, beanNameStr);
						definition.setScope(executor.getScope());
	
						AnnotationHelper.processFieldsAnnotations(elementClass, definition, beanNameStr, InjectableExecutor::filterComponentFieldAnnotation);
	
						AnnotationHelper.processFieldsPropertyAnnotations(elementClass, definition, beanNameStr, InjectableExecutor::filterComponentFieldValueAnnotation);
						
						AnnotationHelper.processMethodInitializationFinalizationAnnotations(beanName, elementClass, definition, InjectableExecutor::filterComponentMethodAnnotation);
						
						return definition;
					});
		} catch (Exception ex) {
			String message = String.format("Error creating bean for bean definition : %s", ""+executor);
			LoggerHelper.logError("ApplicationHelper::createNewBean", message, ex);
			throw new RuntimeException(message, ex);
		}
		return null;
		
	}

	public static final Transformer<Annotation, String> getValueExtractor() {
		return (ann, arguments) -> {
			if ( ann == null )
				return null;
			Value value = (Value) ann;
			return value.value();
		};
	}

	public static final <T> Transformer<Annotation, T> getAutowiredTransformer() {
		return (ann, arguments) -> {
			Autowired autowiredAnnotation = (Autowired) ann;
			String name = (String) arguments[0];
			if ( autowiredAnnotation.name() != null && ! autowiredAnnotation.name().isEmpty() )
				name = autowiredAnnotation.name();
			T t = ComponentsRegistry.getInstance().get(AnnotationConstants.REGISTRY_COMPONENT_REFERENCES, name);
			if ( t == null ) {
				t = ComponentsRegistry.getInstance().get(AnnotationConstants.REGISTRY_INJECTABLE_REFERENCES, name);
			}
			return t;
		};
	}

	public static final <T> Transformer<Annotation, T> getInjectTransformer() {
		return (ann, arguments) -> {
			Inject injectAnnotation = (Inject) ann;
			String name = (String) arguments[0];
			if ( injectAnnotation.name() != null && ! injectAnnotation.name().isEmpty() )
				name = injectAnnotation.name();
			T t = ComponentsRegistry.getInstance().get(AnnotationConstants.REGISTRY_COMPONENT_REFERENCES, name);
			if ( t == null ) {
				t = ComponentsRegistry.getInstance().get(AnnotationConstants.REGISTRY_INJECTABLE_REFERENCES, name);
			}
			return t;
		};
	}

	/**
	 * @param map
	 * @return
	 */
	public static final Collection<Object> createBeans(Map<String, BeanDefinition> map) {
		Queue<Object> list = new ConcurrentLinkedQueue<>();
		list.addAll(
			map.entrySet()
				.stream()
				.map( entry -> createNewBean(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList())
		);
		return list;
	}

	/**
	 * @param map
	 * @return
	 */
	public static final Collection<Object> createMethodBeans(Map<String, MethodExecutor> map) {
		Queue<Object> list = new ConcurrentLinkedQueue<>();
		list.addAll(
			map.entrySet()
				.stream()
				.map( entry -> createNewBean(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList())
		);
		return list;
	}

}
