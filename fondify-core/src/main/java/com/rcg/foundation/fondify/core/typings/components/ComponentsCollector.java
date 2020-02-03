/**
 * 
 */
package com.rcg.foundation.fondify.core.typings.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.rcg.foundation.fondify.core.domain.Scope;
import com.rcg.foundation.fondify.core.helpers.BeansHelper;
import com.rcg.foundation.fondify.utils.helpers.LoggerHelper;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ComponentsCollector {
	private static final ComponentsCollector collector = new ComponentsCollector();
	
	List<ComponentsDiscoveryManager> discveryManagerList = new ArrayList<>(0);
	/**
	 * 
	 */
	private ComponentsCollector() {
		super();
		discveryManagerList.addAll(
				BeansHelper.getImplementedTypes(ComponentsDiscoveryManager.class)
		);
	}
	
	/**
	 * @param <T>
	 * @param name
	 * @param defaultValue
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> discoverComponentsWith(String name, Object defaultValue, Scope...scope) throws Exception {
		return discveryManagerList
			.stream()
			.map( ds -> {
				try {
					return (T) ds.discoverComponent(name, defaultValue, scope);
				} catch (Exception e) {
					String message = String.format("Unable create bean %s with scopes: %s from collector %s", name, scope != null ? "null" : Arrays.toString(scope), ds.getClass().getName());
					LoggerHelper.logError("ComponentsCollector::discoverComponents", message, e);
					return null;
				}
			})
			.filter( comp -> comp != null )
			.collect(Collectors.toList());
	}
	
	/**
	 * @return
	 */
	public static final ComponentsCollector getInstance() {
		return collector;
	}

}
