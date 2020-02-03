/**
 * 
 */
package com.rcg.foundation.fondify.components.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import com.rcg.foundation.fondify.annotations.contants.AnnotationConstants;
import com.rcg.foundation.fondify.annotations.lifecycle.ApplicationManagerProvider;
import com.rcg.foundation.fondify.annotations.typings.BeanDefinition;
import com.rcg.foundation.fondify.annotations.typings.MethodExecutor;
import com.rcg.foundation.fondify.components.ComponentsManagerImpl;
import com.rcg.foundation.fondify.components.annotations.Autowired;
import com.rcg.foundation.fondify.components.annotations.Inject;
import com.rcg.foundation.fondify.core.domain.Scope;
import com.rcg.foundation.fondify.core.exceptions.InitializationException;
import com.rcg.foundation.fondify.core.functions.Transformer;
import com.rcg.foundation.fondify.core.helpers.BeansHelper;
import com.rcg.foundation.fondify.core.properties.PropertyArchive;
import com.rcg.foundation.fondify.core.registry.ComponentsRegistry;
import com.rcg.foundation.fondify.core.typings.AnnotationDeclaration;
import com.rcg.foundation.fondify.core.typings.AnnotationDeclarationType;
import com.rcg.foundation.fondify.core.typings.lifecycle.ComponentManagerProvider;
import com.rcg.foundation.fondify.properties.annotations.Value;
import com.rcg.foundation.fondify.utils.helpers.ArgumentsHelper;
import com.rcg.foundation.fondify.utils.helpers.GenericHelper;
import com.rcg.foundation.fondify.utils.helpers.LoggerHelper;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class ComponentsHelper {

	/**
	 * 
	 */
	private ComponentsHelper() {
		throw new IllegalStateException("ComponentsHelper::constructor -> Unable to instatiatio helper class");
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
								Field fld = objectClass.getDeclaredField(propertyRef.getElemRef());
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
								Field fld = objectClass.getDeclaredField(componentRef.getElemRef());
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
		Optional<ApplicationManagerProvider> providerOpt =  BeansHelper.getImplementedType(ApplicationManagerProvider.class);
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
		Optional<ComponentManagerProvider> componentProviderOpt =  BeansHelper.getImplementedType(ComponentManagerProvider.class);
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
			if ( name.equalsIgnoreCase("arguments") ) {
				return ArgumentsHelper.getArguments();
			} else if ( name.equalsIgnoreCase("sessioncontext") ) {
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
			T instance = ComponentsRegistry.getInstance().seek(beanNameRef);
			if ( instance != null ) {
				AnnotationHelper.scanAndProcessEntity(instance, instance.getClass());
			}
			executor.execute(instance,
					getValueExtractor(), getAutowiredTransformer(), getInjectTransformer(),
					(in, out) -> {
						if ( in == null ) {
							return null;
						}
						return AnnotationHelper.scanAndProcessEntity(in, in.getClass());
//						String beanNameStr = executor.getBeanName();
//						BeanDefinition definition = new BeanDefinition(executor.getDescriptor());
//						Class<?> elementClass = executor.getDescriptor().getAnnotatedClass();
//						beanNameStr = AnnotationHelper.getClassBeanName(elementClass, beanNameStr);
//						definition.setScope(executor.getScope());
//	
//						AnnotationHelper.processFieldsAnnotations(elementClass, definition, beanNameStr, InjectableExecutor::filterComponentFieldAnnotation);
//	
//						AnnotationHelper.processFieldsPropertyAnnotations(elementClass, definition, beanNameStr, InjectableExecutor::filterComponentFieldValueAnnotation);
//						
//						AnnotationHelper.processMethodInitializationFinalizationAnnotations(beanName, elementClass, definition, InjectableExecutor::filterComponentMethodAnnotation);
//						
//						Object entity = definition.execute(in, (name, params) -> tranformNameToBeanInstance(name, params));
//						//AnnotationHelper.scanAndProcessEntity(entity, entity!=null ? entity.getClass(): null);
//						return entity;
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
	
	public static final Object executeMethodOfBean(Class<?> cls, Object instance, Method m) {
		Object response = null;
		String threadName = Thread.currentThread().getName();
		List<Object> args = Arrays.asList(m.getParameters())
			.stream()
			.map( parameter -> {
				GenericHelper.fixCurrentThreadStandardName(threadName);
				return com.rcg.foundation.fondify.annotations.helpers.AnnotationHelper
						.valueOfInjectedParameter(parameter);
			})
			
			.collect(Collectors.toList());
		try {
			if ( args.contains( null ) ) {
				LoggerHelper.logError("ComponentsHelper::executeMethodOfBean", 
						String.format("Some parameters in method %s are not injected, so not invocation is available!!", m.getName()), 
						null);
			}
			if ( ArgumentsHelper.debug ) {
				args.forEach(val -> LoggerHelper.logTrace("ComponentsHelper::executeMethodOfBean", String.format("Method named %s has parameter value : %s (type: %s)", m.getName(), val, val != null ? val.getClass().getName() : "<UNKNOWN>")));
			}
			m.setAccessible(true);
			if ( args.size() == 0  )
				response = m.invoke(instance);
			else
				response = m.invoke(instance, args.toArray());
		} catch (Exception e) {
			LoggerHelper.logError("ComponentsHelper::executeMethodOfBean", 
					String.format("Unable to execute method %s due to ERRORS!!", m.getName()), 
					e);
		}
		return response;
	}

	
	public static final void traceBeanDefinitions() {
		ComponentsManagerImpl componentsManager = new ComponentsManagerImpl();

		LoggerHelper.logText("------------");
		LoggerHelper.logText("B E A N S : ");
		LoggerHelper.logText("------------");
		LoggerHelper.logText("");
		LoggerHelper.logText("");
		LoggerHelper.logText("-------------------------");
		LoggerHelper.logText("D E C L A R A T I O N S :");
		LoggerHelper.logText("-------------------------");
		LoggerHelper.logText("");
		LoggerHelper.logText("-------------------");
		LoggerHelper.logText("INJECTABLE BEANS : ");
		LoggerHelper.logText("-------------------");
		componentsManager
			.getInjectableBeanDefinitions()
			.forEach( beanDef -> {
				AnnotationDeclaration ad = beanDef.getDeclaration();
				LoggerHelper.logText("INJECTABLE BEAN NAME : " + AnnotationHelper.getClassBeanName(ad.getAnnotatedClass(), GenericHelper.initCapBeanName(ad.getAnnotatedClass().getSimpleName())));
				LoggerHelper.logText("INJECTABLE BEAN CLASS : " + ad.getAnnotatedClass().getName());
			});
		componentsManager
		.getInjectableMethodDefinitions()
		.forEach( methodExec -> {
			AnnotationDeclaration ad = methodExec.getDescriptor();
			LoggerHelper.logText("INJECTABLE METHOD BEAN NAME : " + AnnotationHelper.getClassMethodBeanName(ad.getAnnotationMethod(), ad.getAnnotationMethod().getName()));
			LoggerHelper.logText("INJECTABLE METHOD BEAN CLASS : " + methodExec.getTargetClass().getName());
		});
		LoggerHelper.logText("");
		LoggerHelper.logText("------------------");
		LoggerHelper.logText("COMPONENT BEANS : ");
		LoggerHelper.logText("------------------");
		componentsManager
				.getComponentBeanDefinitions()
				.forEach( beanDef -> {
					AnnotationDeclaration ad = beanDef.getDeclaration();
					LoggerHelper.logText("COMPONENT BEAN NAME : " + AnnotationHelper.getClassBeanName(ad.getAnnotatedClass(), GenericHelper.initCapBeanName(ad.getAnnotatedClass().getSimpleName())));
					LoggerHelper.logText("COMPONENT BEAN CLASS : " + ad.getAnnotatedClass().getName());
			});
		
		LoggerHelper.logText("");
		LoggerHelper.logText("");
		LoggerHelper.logText("-------------------");
		LoggerHelper.logText("I N S T A N C E S :");
		LoggerHelper.logText("-------------------");
		LoggerHelper.logText("");
		LoggerHelper.logText("-------------------");
		LoggerHelper.logText("INJECTABLE BEANS : ");
		LoggerHelper.logText("-------------------");
		componentsManager
		.getInjectableBeanReferences()
		.forEach( instance -> {
			Class<?> cls = instance.getClass();
			LoggerHelper.logText("INJECTABLE BEAN NAME : " + AnnotationHelper.getClassBeanName(cls, GenericHelper.initCapBeanName(cls.getSimpleName())));
			LoggerHelper.logText("INJECTABLE BEAN CLASS : " + cls.getName());
			LoggerHelper.logText("INJECTABLE BEAN STRING IMAGE : " + instance);
		});
		LoggerHelper.logText("");
		LoggerHelper.logText("------------------");
		LoggerHelper.logText("COMPONENT BEANS : ");
		LoggerHelper.logText("------------------");
		componentsManager
		.getComponentsBeanReferences()
		.forEach( instance -> {
			Class<?> cls = instance.getClass();
			LoggerHelper.logText("COMPONENT BEAN NAME : " + AnnotationHelper.getClassBeanName(cls, GenericHelper.initCapBeanName(cls.getSimpleName())));
			LoggerHelper.logText("COMPONENT BEAN CLASS : " + cls.getName());
			LoggerHelper.logText("COMPONENT BEAN STRING IMAGE : " + instance);
		});
	}

}
