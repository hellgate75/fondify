/**
 * 
 */
package com.rcg.foundation.fondify.components;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import com.rcg.foundation.fondify.annotations.contants.AnnotationConstants;
import com.rcg.foundation.fondify.annotations.lifecycle.ApplicationManagerProvider;
import com.rcg.foundation.fondify.annotations.typings.BeanDefinition;
import com.rcg.foundation.fondify.annotations.typings.MethodExecutor;
import com.rcg.foundation.fondify.components.helpers.AnnotationHelper;
import com.rcg.foundation.fondify.components.helpers.ComponentsHelper;
import com.rcg.foundation.fondify.core.domain.Scope;
import com.rcg.foundation.fondify.core.helpers.BeansHelper;
import com.rcg.foundation.fondify.core.properties.PropertyArchive;
import com.rcg.foundation.fondify.core.registry.ComponentsRegistry;
import com.rcg.foundation.fondify.core.typings.Injectable;
import com.rcg.foundation.fondify.core.typings.components.ComponentsDiscoveryManager;
import com.rcg.foundation.fondify.core.typings.lifecycle.ComponentsManager;
import com.rcg.foundation.fondify.utils.helpers.ArgumentsHelper;
import com.rcg.foundation.fondify.utils.helpers.LoggerHelper;

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
		Optional<T> lookupResponse = Arrays.asList(1, 2)
			.parallelStream()
			.map( index -> {
				T t = null;
				try {
					if ( index % 2 == 1 ) {
						t =  getInjectableByName(true, name, baseInstance, scope);
					} else {
						t =  getComponentByName(true, name, baseInstance, scope);
					}
				} catch (Exception | Error e) {
					LoggerHelper.logError("ComponentsManagerImpl::getInjectableOrComponentByName", String.format("Errors occured during request of %s, lookng up into the registry", (index % 2 == 1 ? "Injectable" : "Component")) , e);
				}
				return t;
			})
			.filter( t -> t != null )
			.findFirst();
		if ( lookupResponse.isPresent() ) {
			if ( ArgumentsHelper.traceAllLevels || ArgumentsHelper.traceComponentsLevel ) {
				LoggerHelper.logTrace("ComponentsManagerImpl::getInjectableOrComponentByName", String.format("Successfully collected Bean or Injectable with this name: %s and scope(s): %s", name, Arrays.toString(scope)));
			}
			return lookupResponse.get(); 
		}
		LoggerHelper.logWarn("ComponentsManagerImpl::getInjectableOrComponentByName", String.format("Unable to collect any Bean or Injectable with this name: %s and scope(s): %s", name, Arrays.toString(scope)) , null);
		return null;
	}
	
	@Override
	public <T> void registerComponent(String name, T component) {
		registry.add(AnnotationConstants.REGISTRY_COMPONENT_REFERENCES, name, component);
	}
	
	@Override
	public <T extends Injectable> void registerInjectable(String name, T component) {
		registry.add(AnnotationConstants.REGISTRY_INJECTABLE_REFERENCES, name, component);
	}
	
	public BeanDefinition getInjectableBeanDefinition(String name) {
		return (BeanDefinition) registry.get(AnnotationConstants.REGISTRY_INJECTABLE_BEAN_DEFINITIONS, name);
	}
	
	public Collection<BeanDefinition> getInjectableBeanDefinitions() {
		return registry.getAll(AnnotationConstants.REGISTRY_INJECTABLE_BEAN_DEFINITIONS);
	}
	
	public MethodExecutor getInjectableMethodDefinition(String name) {
		return (MethodExecutor) registry.get(AnnotationConstants.REGISTRY_INJECTABLE_METHODD_DEFINITIONS, name);
	}
	
	public Collection<? extends Injectable> getInjectableBeanReferences() {
		return registry.getAll(AnnotationConstants.REGISTRY_INJECTABLE_REFERENCES);
	}
	
	public Collection<?> getComponentsBeanReferences() {
		return registry.getAll(AnnotationConstants.REGISTRY_COMPONENT_REFERENCES);
	}
	
	public Collection<MethodExecutor> getInjectableMethodDefinitions() {
		return registry.getAll(AnnotationConstants.REGISTRY_INJECTABLE_METHODD_DEFINITIONS);
	}
	
	public BeanDefinition getComponentBeanDefinition(String name) {
		return (BeanDefinition) registry.get(AnnotationConstants.REGISTRY_COMPONENT_BEAN_DEFINITIONS, name);
	}

	public Collection<BeanDefinition> getComponentBeanDefinitions() {
		return registry.getAll(AnnotationConstants.REGISTRY_COMPONENT_BEAN_DEFINITIONS);
	}


	@Override
	public <T extends Injectable> T getInjectableByName(String name, Object baseInstance, Scope... scope) throws Exception {
		return getInjectableByName(false, name, baseInstance, scope);
	}

	@SuppressWarnings("unchecked")
	private <T extends Injectable> T getInjectableByName(boolean supressWarnings, String name, Object baseInstance, Scope... scope) throws Exception {
		boolean isLocalScope = scope != null && scope.length > 0 && Arrays.asList(scope)
													.stream()
													.filter(sc -> sc == Scope.INSTANCE || sc == Scope.SESSION)
													.count() > 0;
		T t = isLocalScope ? null : (T) registry.get(AnnotationConstants.REGISTRY_INJECTABLE_REFERENCES, name);
		if ( t == null ) {
			//Create new Injectable
			BeanDefinition definition = getInjectableBeanDefinition(name);
			if ( definition == null ) {
				MethodExecutor executor = getInjectableMethodDefinition(name);
				if ( executor == null ) {
					if (! supressWarnings )
						LoggerHelper.logWarn("ComponentsManagerImpl::getInjectableByName", String.format("Unable to discover required injectable bean named: %s", name), null);
					return null;
				} else {
					if ( scope != null && scope.length > 0 && ! Arrays.asList(scope).contains(executor.getScope()) ) {
						if ( ! supressWarnings )
							LoggerHelper.logWarn("ComponentsManagerImpl::getInjectableByName", String.format("Found component named %s but scope %s is not in availability list: %s!!", name, executor.getScope().name(), Arrays.toString(scope)), null);
						return null;
					}
					if ( baseInstance == null ) {
						try {
							baseInstance = executor.getDescriptor().getAnnotatedClass().newInstance();
						} catch (Exception e) {
							String message = "";
							LoggerHelper.logError("ComponentsManager::getInjectableByName", message, e);
							return null;
						}
					}
					t = (T) executor.execute(baseInstance, 
												ComponentsHelper.getValueExtractor(), 
												ComponentsHelper.getAutowiredTransformer(), 
												ComponentsHelper.getInjectTransformer(),
												(in, out) -> {
													String beanNameStr = "";
													try {
														if ( in == null ) {
															return null;
														}
														return AnnotationHelper.scanAndProcessEntity(in, in.getClass());
//														beanNameStr = executor.getBeanName();
//														BeanDefinition beanDefinition = new BeanDefinition(executor.getDescriptor());
//														Class<?> elementClass = executor.getDescriptor().getAnnotatedClass();
//														beanNameStr = AnnotationHelper.getClassBeanName(elementClass, beanNameStr);
//														beanDefinition.setScope(executor.getScope());
//							
//														AnnotationHelper.processFieldsAnnotations(elementClass, definition, beanNameStr, InjectableExecutor::filterComponentFieldAnnotation);
//							
//														AnnotationHelper.processFieldsPropertyAnnotations(elementClass, definition, beanNameStr, InjectableExecutor::filterComponentFieldValueAnnotation);
//														
//														AnnotationHelper.processMethodInitializationFinalizationAnnotations(beanNameStr, elementClass, definition, InjectableExecutor::filterComponentMethodAnnotation);
//														Object entity = beanDefinition.execute(in, (beanName, params) -> ComponentsHelper.tranformNameToBeanInstance(beanName, params));
//														//AnnotationHelper.scanAndProcessEntity(entity, entity!=null ? entity.getClass(): null);
//														return entity;
													} catch (Exception e) {
														LoggerHelper.logError("ComponentsManagerImpl::getInjectableByName", String.format("Unable to tranform method outcome bean named: %s!!", beanNameStr), e);
													}
													return null;
												});
					if ( ! isLocalScope )
						registry.add(AnnotationConstants.REGISTRY_INJECTABLE_REFERENCES, name, t);
					return t;
				}
			} else {
				if ( scope != null && scope.length > 0 && ! Arrays.asList(scope).contains(definition.getScope()) ) {
					if ( ! supressWarnings )
						LoggerHelper.logWarn("ComponentsManagerImpl::getInjectableByName", String.format("Found component named %s but scope %s is not in availability list: %s!!", name, definition.getScope().name(), Arrays.toString(scope)), null);
					return null;
				}
				try {
					baseInstance = definition.getDeclaration().getAnnotatedClass().newInstance();
				} catch (Exception e) {
					LoggerHelper.logWarn("ComponentsManagerImpl::getInjectableByName", 
								String.format("Found component named %s but errors occured during preliminary instance!!", name), e);
				}
				try {
					t = (T) definition.execute(baseInstance, (beanName, arguments) -> ComponentsHelper.tranformNameToBeanInstance(beanName, arguments));
					if ( ! isLocalScope )
						registry.add(AnnotationConstants.REGISTRY_INJECTABLE_REFERENCES, name, t);
				} catch (Exception e) {
					LoggerHelper.logError("ComponentsManagerImpl::getInjectableByName", 
							String.format("Found component named %s but errors occured during BeanDefinition execution!!", name), e);
				}
				return t;
			}
		} else {
			return t;
		}
	}

	@Override
	public <T> T getComponentByName(String name, Object baseInstance, Scope... scope) throws Exception {
		return getComponentByName(false, name, baseInstance, scope);
	}
	@SuppressWarnings("unchecked")
	public <T> T getComponentByName(boolean supressWarnings, String name, Object baseInstance, Scope... scope) throws Exception {
		boolean isLocalScope = scope != null && scope.length > 0 && Arrays.asList(scope)
				.stream()
				.filter(sc -> sc == Scope.INSTANCE || sc == Scope.SESSION)
				.count() > 0;
		T t = isLocalScope ? null : (T) registry.get(AnnotationConstants.REGISTRY_COMPONENT_REFERENCES, name);
		if ( t == null ) {
			//Create new Injectable
			BeanDefinition definition = getComponentBeanDefinition(name);
			if ( definition == null ) {
				if ( ! supressWarnings )
					LoggerHelper.logWarn("ComponentsManagerImpl::getComponentByName", String.format("Unable to discover required component bean named: %s", name), null);
				return null;
			} else {
				if ( scope != null && scope.length > 0 && ! Arrays.asList(scope).contains(definition.getScope()) ) {
					if ( ! supressWarnings )
						LoggerHelper.logWarn("ComponentsManagerImpl::getInjectableByName", String.format("Found component named %s but scope %s is not in availability list: %s!!", name, definition.getScope().name(), Arrays.toString(scope)), null);
					return null;
				}
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
		Optional<ApplicationManagerProvider> providerOpt =  BeansHelper.getImplementedType(ApplicationManagerProvider.class);
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
			if ( name.equalsIgnoreCase("arguments") ) {
				return (T) ArgumentsHelper.getArguments();
			} else if ( name.equalsIgnoreCase("sessioncontext") ) {
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
