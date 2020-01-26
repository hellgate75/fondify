/**
 * 
 */
package com.rcg.foundation.fondify.components.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import com.rcg.foundation.fondify.annotations.contants.AnnotationConstants;
import com.rcg.foundation.fondify.annotations.lifecycle.ApplicationManagerProvider;
import com.rcg.foundation.fondify.annotations.typings.BeanDefinition;
import com.rcg.foundation.fondify.annotations.typings.MethodExecutor;
import com.rcg.foundation.fondify.components.annotations.Autowired;
import com.rcg.foundation.fondify.components.annotations.Inject;
import com.rcg.foundation.fondify.components.annotations.executors.InjectableExecutor;
import com.rcg.foundation.fondify.core.domain.Scope;
import com.rcg.foundation.fondify.core.exceptions.InitializationException;
import com.rcg.foundation.fondify.core.functions.Transformer;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;
import com.rcg.foundation.fondify.core.properties.PropertyArchive;
import com.rcg.foundation.fondify.core.registry.ComponentsRegistry;
import com.rcg.foundation.fondify.core.typings.AnnotationDeclaration;
import com.rcg.foundation.fondify.core.typings.AnnotationDeclarationType;
import com.rcg.foundation.fondify.core.typings.lifecycle.ComponentManagerProvider;
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
			throw new InitializationException(message, ex);
		}
		return null;
	}
	
	public static final Object tranformNameToBeanInstance(String name, Object... arguments) {
		ApplicationManagerProvider provider = null;
		Scope scope = Scope.APPLICATION;
		if ( arguments.length > 0 && Scope.class.isAssignableFrom(arguments[0].getClass())) {
			scope = (Scope) arguments[0];
		}
		Optional<ApplicationManagerProvider> providerOpt = AnnotationHelper.getImplementedType(ApplicationManagerProvider.class);
		if ( providerOpt.isPresent() ) {
			provider = providerOpt.get();
		}
		if ( provider == null ) {
			String message = "Unable to load implementation of interface ApplicationManagerProvider!!";
			LoggerHelper.logError("ApplicationHelper::tranformNameToBeanInstance", 
									message, 
									null);
			throw new InitializationException(message);
		}
		ComponentManagerProvider componentProvider = null;
		Optional<ComponentManagerProvider> componentProviderOpt = AnnotationHelper.getImplementedType(ComponentManagerProvider.class);
		if ( componentProviderOpt.isPresent() ) {
			componentProvider = componentProviderOpt.get();
		}
		if ( componentProvider == null ) {
			String message = "Unable to load implementation of interface ComponentManagerProvider!!";
			LoggerHelper.logError("ApplicationHelper::tranformNameToBeanInstance", 
									message, 
									null);
			throw new InitializationException(message);
		}
		if ( name.startsWith("?") && name.endsWith("?") ) {
			name = name.substring(1, name.length() - 1);
			if ( name.equalsIgnoreCase("sessioncontext") ) {
				return provider.getApplicationManager().getSessionContext();
			} else if ( name.equalsIgnoreCase("applicationcontext") ) {
				return provider.getApplicationManager().getApplicationContext();
			} else if ( name.equalsIgnoreCase("session") ) {
				return provider.getApplicationManager().getSession();
			} else {
				LoggerHelper.logWarn("ComponentHelper::tranformNameToBeanInstance", String.format("Undefined system/custom type: %s!!", name), null);
			}
		} else {
			try {
				return componentProvider.getComponentManager().getInjectableOrComponentByName(name, null, scope);
			} catch (Exception e) {
				String message = String.format("Error lading/creating instance of component/injectable bean named: %s!!", name);
				LoggerHelper.logError("ComponentHelper::tranformNameToBeanInstance", message, e);
				throw new InitializationException(message, e);
			}
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
						
						return definition.execute(in, (name, params) -> tranformNameToBeanInstance(name, params));
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
