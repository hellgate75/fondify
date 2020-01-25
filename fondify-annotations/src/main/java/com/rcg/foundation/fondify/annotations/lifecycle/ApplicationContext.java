/**
 * 
 */
package com.rcg.foundation.fondify.annotations.lifecycle;

import java.util.Collection;
import java.util.List;

import com.rcg.foundation.fondify.annotations.typings.BeanDefinition;
import com.rcg.foundation.fondify.core.domain.Scope;
import com.rcg.foundation.fondify.core.exceptions.LifeCycleException;

/**
 * Define main features of the application context
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface ApplicationContext {
	
	List<BeanDefinition> seek(String componentName);
	
	<T> T property(String property);
	
	<T> T getBean(String beanName, Scope scope);
	
	<T> T getSystemElement(String elementName);
	
	<T> Collection<T> getBeans(String beanName);
	
	SessionContext sessionContext() throws LifeCycleException;
}
