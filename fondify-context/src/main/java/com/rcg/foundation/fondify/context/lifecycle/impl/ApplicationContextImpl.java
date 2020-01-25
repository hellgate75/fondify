/**
 * 
 */
package com.rcg.foundation.fondify.context.lifecycle.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.rcg.foundation.fondify.annotations.contants.AnnotationConstants;
import com.rcg.foundation.fondify.annotations.lifecycle.ApplicationContext;
import com.rcg.foundation.fondify.annotations.lifecycle.SessionContext;
import com.rcg.foundation.fondify.annotations.typings.BeanDefinition;
import com.rcg.foundation.fondify.annotations.typings.MethodExecutor;
import com.rcg.foundation.fondify.components.helpers.ComponentsHelper;
import com.rcg.foundation.fondify.context.ApplicationManager;
import com.rcg.foundation.fondify.core.domain.Scope;
import com.rcg.foundation.fondify.core.exceptions.LifeCycleException;
import com.rcg.foundation.fondify.core.properties.PropertyArchive;
import com.rcg.foundation.fondify.core.registry.ComponentsRegistry;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ApplicationContextImpl implements ApplicationContext {
	private Map<String, Object> singletonRegistry = new ConcurrentHashMap<String, Object>(0);
	private Map<String, Object> applicationRegistry = new ConcurrentHashMap<String, Object>(0);
	private Map<String, BeanDefinition> components = new ConcurrentHashMap<String, BeanDefinition>(0);
	private Map<String, BeanDefinition> injectables = new ConcurrentHashMap<String, BeanDefinition>(0);
	private Map<String, MethodExecutor> injectablesMethod = new ConcurrentHashMap<String, MethodExecutor>(0);

	/**
	 * 
	 */
	public ApplicationContextImpl() {
		ComponentsRegistry registry = ComponentsRegistry.getInstance();
		components.putAll(registry.getAllAsMap(AnnotationConstants.REGISTRY_COMPONENT_REFERENCES));
		injectables.putAll(registry.getAllAsMap(AnnotationConstants.REGISTRY_INJECTABLE_REFERENCES));
		injectablesMethod.putAll(registry.getAllAsMap(AnnotationConstants.REGISTRY_INJECTABLE_METHODD_REFERENCES));
		applicationRegistry.putAll(
				components
				.entrySet()
				.stream()
				.collect(Collectors.toMap( entry -> entry.getKey() , entry-> ComponentsHelper.createNewBean(entry.getKey(), entry.getValue())))
		);
		applicationRegistry.putAll(
				injectablesMethod
				.entrySet()
				.stream()
				.collect(Collectors.toMap( entry -> entry.getKey() , entry-> ComponentsHelper.createNewBean(entry.getKey(), entry.getValue())))
		);
		applicationRegistry.putAll(
				injectables
				.entrySet()
				.stream()
				.collect(Collectors.toMap( entry -> entry.getKey() , entry-> ComponentsHelper.createNewBean(entry.getKey(), entry.getValue())))
		);
	}

	@Override
	public List<BeanDefinition> seek(String componentName) {
		return ComponentsRegistry.getInstance().seek(componentName);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T property(String property) {
		return (T) PropertyArchive.getInstance().getProperty(property);
	}

	private List<BeanDefinition> getBeanByName(String beanName) {
		List<BeanDefinition> list = new ArrayList<BeanDefinition>(0);
		components
			.entrySet()
			.stream()
			.filter( entry -> entry.getKey().equals(beanName) )
			.forEach( entry -> list.add(entry.getValue()) );
		injectables
		.entrySet()
		.stream()
		.filter( entry -> entry.getKey().equals(beanName) )
		.forEach( entry -> list.add(entry.getValue()) );
		return list;
	}

	private List<MethodExecutor> getBeanMethodByName(String beanName) {
		List<MethodExecutor> list = new ArrayList<MethodExecutor>(0);
		injectablesMethod
		.entrySet()
		.stream()
		.filter( entry -> entry.getKey().equals(beanName) )
		.forEach( entry -> list.add(entry.getValue()) );
		return list;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getBean(String beanName, Scope scope) {
		if ( scope == Scope.APPLICATION ) {
			return ApplicationManager.getInstance().getApplicationContext().getBean(beanName, scope);
		} else if ( scope == Scope.INSTANCE ) {
			List<BeanDefinition> beansDef = getBeanByName(beanName);
			if ( beansDef.size() > 0 ) {
				return ComponentsHelper.createNewBean(beanName, beansDef.get(0));
			} else {
				List<MethodExecutor> beanMethod = getBeanMethodByName(beanName);
				return ComponentsHelper.createNewBean(beanName, beanMethod.get(0));
			}
		}  else if ( scope == Scope.SINGLETON ) {
			Object bean = singletonRegistry.get(beanName);
			if ( bean != null ) {
				return (T) bean;
			}
			List<BeanDefinition> beansDef = getBeanByName(beanName);
			if ( beansDef.size() > 0 ) {
				bean = ComponentsHelper.createNewBean(beanName, beansDef.get(0));
			} else {
				List<MethodExecutor> beanMethod = getBeanMethodByName(beanName);
				bean = ComponentsHelper.createNewBean(beanName, beanMethod.get(0));
			}
			singletonRegistry.put(beanName, bean);
			return (T) bean;
		} else if ( scope == Scope.SESSION ){
			return sessionContext().getBean(beanName, scope);
		}
		throw new RuntimeException("Unable to identify bean scope : " + scope);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getSystemElement(String elementName) {
		return (T) ComponentsRegistry.getInstance().get(AnnotationConstants.REGISTRY_BEAN_SYSTEM_SCOPE, elementName);
	}

	@Override
	public <T> Collection<T> getBeans(String beanName) {
		List<T> list = new ArrayList<>(0);
		List<BeanDefinition> beansDef = getBeanByName(beanName);
		if ( beansDef.size() > 0 ) {
			beansDef.forEach( def -> {
				list.add(
				 ComponentsHelper.createNewBean(beanName, def)
				 );
			} );
		} else {
			List<MethodExecutor> beanMethod = getBeanMethodByName(beanName);
			beanMethod.forEach( def -> {
				list.add(
				 ComponentsHelper.createNewBean(beanName, def)
				 );
			} );
		}
		return list;
	}

	@Override
	public SessionContext sessionContext() throws LifeCycleException {
		return ApplicationManager.getInstance().getSessionContext();
	}

}
