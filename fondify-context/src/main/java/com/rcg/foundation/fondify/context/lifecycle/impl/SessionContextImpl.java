/**
 * 
 */
package com.rcg.foundation.fondify.context.lifecycle.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.rcg.foundation.fondify.annotations.contants.AnnotationConstants;
import com.rcg.foundation.fondify.annotations.lifecycle.ApplicationContext;
import com.rcg.foundation.fondify.annotations.lifecycle.SessionContext;
import com.rcg.foundation.fondify.annotations.typings.BeanDefinition;
import com.rcg.foundation.fondify.annotations.typings.MethodExecutor;
import com.rcg.foundation.fondify.components.helpers.ComponentsHelper;
import com.rcg.foundation.fondify.context.ApplicationManagerImpl;
import com.rcg.foundation.fondify.core.domain.Scope;
import com.rcg.foundation.fondify.core.exceptions.LifeCycleException;
import com.rcg.foundation.fondify.core.properties.PropertyArchive;
import com.rcg.foundation.fondify.core.registry.ComponentsRegistry;
import com.rcg.foundation.fondify.core.typings.lifecycle.SessionSetter;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class SessionContextImpl implements SessionContext, SessionSetter {
	private Map<String, Object> sessionRegistry = new ConcurrentHashMap<String, Object>(0);
	private Map<String, BeanDefinition> components = new ConcurrentHashMap<String, BeanDefinition>(0);
	private Map<String, BeanDefinition> injectables = new ConcurrentHashMap<String, BeanDefinition>(0);
	private Map<String, MethodExecutor> injectablesMethod = new ConcurrentHashMap<String, MethodExecutor>(0);
	private UUID uuid;
	
	/**
	 * 
	 */
	public SessionContextImpl() {
		ComponentsRegistry registry = ComponentsRegistry.getInstance();
		components.putAll(registry.getAllAsMap(AnnotationConstants.REGISTRY_COMPONENT_REFERENCES));
		injectables.putAll(registry.getAllAsMap(AnnotationConstants.REGISTRY_INJECTABLE_REFERENCES));
		injectablesMethod.putAll(registry.getAllAsMap(AnnotationConstants.REGISTRY_INJECTABLE_METHODD_REFERENCES));
		sessionRegistry.putAll(
				components
				.entrySet()
				.stream()
				.collect(Collectors.toMap( entry -> entry.getKey() , entry-> ComponentsHelper.createNewBean(entry.getKey(), entry.getValue())))
		);
		sessionRegistry.putAll(
				injectablesMethod
				.entrySet()
				.stream()
				.collect(Collectors.toMap( entry -> entry.getKey() , entry-> ComponentsHelper.createNewBean(entry.getKey(), entry.getValue())))
		);
		sessionRegistry.putAll(
				injectables
				.entrySet()
				.stream()
				.collect(Collectors.toMap( entry -> entry.getKey() , entry-> ComponentsHelper.createNewBean(entry.getKey(), entry.getValue())))
		);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T property(String propertyName) {
		return (T) PropertyArchive.getInstance().getProperty(propertyName);
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
			return ApplicationManagerImpl.getInstance().getApplicationContext().getBean(beanName, scope);
		} else if ( scope == Scope.INSTANCE ) {
			List<BeanDefinition> beansDef = getBeanByName(beanName);
			if ( beansDef.size() > 0 ) {
				return ComponentsHelper.createNewBean(beanName, beansDef.get(0));
			} else {
				List<MethodExecutor> beanMethod = getBeanMethodByName(beanName);
				return ComponentsHelper.createNewBean(beanName, beanMethod.get(0));
			}
		}  else if ( scope == Scope.SINGLETON ) {
			return ApplicationManagerImpl.getInstance().getApplicationContext().getBean(beanName, scope);
		} else if ( scope == Scope.SESSION ){
			return (T) sessionRegistry.get(beanName);
		}
		throw new RuntimeException("Unable to identify bean scope : " + scope);
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
	public ApplicationContext getApplicationContext() throws LifeCycleException {
		return ApplicationManagerImpl.getInstance().getApplicationContext();
	}

	@Override
	public UUID getSessionId() {
		return uuid;
	}

	@Override
	public void close() {
		ApplicationManagerImpl.getInstance().unregisterSession(uuid);
	}

	@Override
	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
	}

	@Override
	public void setSessionId(UUID uuid) {
		this.uuid = uuid;
	}
	
	

}
