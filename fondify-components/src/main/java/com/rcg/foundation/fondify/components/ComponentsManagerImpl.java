/**
 * 
 */
package com.rcg.foundation.fondify.components;

import java.util.Arrays;
import java.util.Optional;

import com.rcg.foundation.fondify.annotations.contants.AnnotationConstants;
import com.rcg.foundation.fondify.annotations.lifecycle.ApplicationManagerProvider;
import com.rcg.foundation.fondify.annotations.typings.BeanDefinition;
import com.rcg.foundation.fondify.annotations.typings.MethodExecutor;
import com.rcg.foundation.fondify.components.annotations.executors.InjectableExecutor;
import com.rcg.foundation.fondify.components.helpers.AnnotationHelper;
import com.rcg.foundation.fondify.components.helpers.ComponentsHelper;
import com.rcg.foundation.fondify.core.domain.Scope;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;
import com.rcg.foundation.fondify.core.properties.PropertyArchive;
import com.rcg.foundation.fondify.core.registry.ComponentsRegistry;
import com.rcg.foundation.fondify.core.typings.Injectable;
import com.rcg.foundation.fondify.core.typings.lifecycle.ComponentsDiscoveryManager;
import com.rcg.foundation.fondify.core.typings.lifecycle.ComponentsManager;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ComponentsManagerImpl implements ComponentsManager, ComponentsDiscoveryManager {

	private ComponentsRegistry registry = ComponentsRegistry.getInstance();

	/**
	 * 
	 */
	public ComponentsManagerImpl() {
		super();
	}

	@Override
	public <T> T getInjectableOrComponentByName(String name, Object baseInstance, Scope... scope) throws Exception {
		T t = getInjectableByName(name, baseInstance, scope);
		if ( t == null ) {
			t = getComponentByName(name, baseInstance, scope);
		}
		return t;
	}
	
	private BeanDefinition getInjectableBeanDefinition(String name) {
		return (BeanDefinition) registry.get(AnnotationConstants.REGISTRY_INJECTABLE_BEAN_DEFINITIONS, name);
	}
	
	private MethodExecutor getInjectableMethodDefinition(String name) {
		return (MethodExecutor) registry.get(AnnotationConstants.REGISTRY_INJECTABLE_METHODD_DEFINITIONS, name);
	}
	
	private BeanDefinition getComponentBeanDefinition(String name) {
		return (BeanDefinition) registry.get(AnnotationConstants.REGISTRY_COMPONENT_BEAN_DEFINITIONS, name);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Injectable> T getInjectableByName(String name, Object baseInstance, Scope... scope) throws Exception {
		boolean isLocalScope = scope != null && scope.length > 0 && Arrays.asList(scope)
													.stream()
													.filter(sc -> sc == Scope.INSTANCE || sc == Scope.SESSION)
													.count() > 0;
		T t = isLocalScope ? null : (T) registry.get(AnnotationConstants.REGISTRY_INJECTABLE_REFERENCES, name);
		if ( t == null ) {
			t = isLocalScope ? null : (T) registry.get(AnnotationConstants.REGISTRY_INJECTABLE_METHODD_REFERENCES, name);
			if ( t == null ) {
				//Create new Injectable
				BeanDefinition definition = getInjectableBeanDefinition(name);
				if ( definition == null ) {
					MethodExecutor executor = getInjectableMethodDefinition(name);
					if ( executor == null ) {
						LoggerHelper.logWarn("ComponentsManagerImpl::getInjectableByName", String.format("Unable to discover required injectable bean named: %s", name), null);
						return null;
					} else {
						if ( scope != null && scope.length > 0 && ! Arrays.asList(scope).contains(executor.getScope()) ) {
							LoggerHelper.logWarn("ComponentsManagerImpl::getInjectableByName", String.format("Found component named %s but scope %s is not in availability list: %s!!", name, executor.getScope().name(), Arrays.toString(scope)), null);
							return null;
						}
						t = (T) executor.execute(baseInstance, 
													ComponentsHelper.getValueExtractor(), 
													ComponentsHelper.getAutowiredTransformer(), 
													ComponentsHelper.getInjectTransformer(),
													(in, out) -> {
														String beanNameStr = "";
														try {
															beanNameStr = executor.getBeanName();
															BeanDefinition beanDefinition = new BeanDefinition(executor.getDescriptor());
															Class<?> elementClass = executor.getDescriptor().getAnnotatedClass();
															beanNameStr = AnnotationHelper.getClassBeanName(elementClass, beanNameStr);
															beanDefinition.setScope(executor.getScope());
								
															AnnotationHelper.processFieldsAnnotations(elementClass, definition, beanNameStr, InjectableExecutor::filterComponentFieldAnnotation);
								
															AnnotationHelper.processFieldsPropertyAnnotations(elementClass, definition, beanNameStr, InjectableExecutor::filterComponentFieldValueAnnotation);
															
															AnnotationHelper.processMethodInitializationFinalizationAnnotations(beanNameStr, elementClass, definition, InjectableExecutor::filterComponentMethodAnnotation);
															
															return beanDefinition.execute(in, (beanName, params) -> ComponentsHelper.tranformNameToBeanInstance(beanName, params));
														} catch (Exception e) {
															LoggerHelper.logError("ComponentsManagerImpl::getInjectableByName", String.format("Unable to tranform method outcome bean named: %s!!", beanNameStr), e);
														}
														return null;
													});
						if ( ! isLocalScope )
							registry.add(AnnotationConstants.REGISTRY_INJECTABLE_METHODD_REFERENCES, name, t);
						return t;
					}
				} else {
					if ( scope != null && scope.length > 0 && ! Arrays.asList(scope).contains(definition.getScope()) ) {
						LoggerHelper.logWarn("ComponentsManagerImpl::getInjectableByName", String.format("Found component named %s but scope %s is not in availability list: %s!!", name, definition.getScope().name(), Arrays.toString(scope)), null);
						return null;
					}
					t = (T) definition.execute(baseInstance, (beanName, arguments) -> ComponentsHelper.tranformNameToBeanInstance(beanName, arguments));
					if ( ! isLocalScope )
						registry.add(AnnotationConstants.REGISTRY_INJECTABLE_REFERENCES, name, t);
					return t;
				}
			} else {
				return t;
			}
		} else {
			return t;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getComponentByName(String name, Object baseInstance, Scope... scope) throws Exception {
		boolean isLocalScope = scope != null && scope.length > 0 && Arrays.asList(scope)
				.stream()
				.filter(sc -> sc == Scope.INSTANCE || sc == Scope.SESSION)
				.count() > 0;
		T t = isLocalScope ? null : (T) registry.get(AnnotationConstants.REGISTRY_COMPONENT_REFERENCES, name);
		if ( t == null ) {
			//Create new Injectable
			BeanDefinition definition = getComponentBeanDefinition(name);
			if ( definition == null ) {
				LoggerHelper.logWarn("ComponentsManagerImpl::getComponentByName", String.format("Unable to discover required component bean named: %s", name), null);
				return null;
			} else {
				t = (T) definition.execute(baseInstance, (beanName, arguments) -> ComponentsHelper.tranformNameToBeanInstance(beanName, arguments));
				if ( ! isLocalScope )
					registry.add(AnnotationConstants.REGISTRY_COMPONENT_REFERENCES, name, t);
				return t;
			}
		} else {
			return t;
		}
	}

	@Override
	public Object getPropertyByName(String name) {
		return PropertyArchive.getInstance().getProperty(name);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getSystemObjectByName(String name) throws Exception {
		ApplicationManagerProvider provider = null;
		Optional<ApplicationManagerProvider> providerOpt = AnnotationHelper.getImplementedType(ApplicationManagerProvider.class);
		if ( providerOpt.isPresent() ) {
			provider = providerOpt.get();
		}
		if ( provider == null ) {
			String message = "Unable to load implementation of interface ApplicationManagerProvider!!";
			LoggerHelper.logError("ApplicationHelper::tranformNameToBeanInstance", 
									message, 
									null);
			throw new IllegalStateException(message);
		}
		if ( name.startsWith("?") && name.endsWith("?") ) {
			name = name.substring(1, name.length() - 1);
			if ( name.equalsIgnoreCase("sessioncontext") ) {
				return (T) provider.getApplicationManager().getSessionContext();
			} else if ( name.equalsIgnoreCase("applicationcontext") ) {
				return (T) provider.getApplicationManager().getApplicationContext();
			} else if ( name.equalsIgnoreCase("session") ) {
				return (T) provider.getApplicationManager().getSession();
			} else {
				LoggerHelper.logWarn("ComponentHelper::tranformNameToBeanInstance", String.format("Undefined system/custom type: %s!!", name), null);
				return null;
			}
		} else {
			return getInjectableOrComponentByName(name, null);
		}
	}

	@Override
	public <T> T discoverComponent(String name, Object defaultValue, Scope... scope) throws Exception {
		return getInjectableOrComponentByName(name, defaultValue, scope);
	}
}
