/**
 * 
 */
package com.rcg.foundation.fondify.core.registry;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * System Components Registry
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class ComponentsRegistry {
	private static ComponentsRegistry instance = null;
	private Map<String, StreamIORegistryItem<?>> registryMap = new ConcurrentHashMap<>(); 
	/**
	 * Default Constructor
	 */
	private ComponentsRegistry() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	private <T> StreamIORegistryItem<T> getMapItem(String className) {
		StreamIORegistryItem<T> mapX = null;
		if ( ! registryMap.containsKey(className) ) {
			mapX = new StreamIORegistryItem<T>();
		} else {
			mapX = (StreamIORegistryItem<T>)registryMap.get(className);
		}
		return mapX;
	}
	
	/**
	 * @param <T>
	 * @param className
	 * @param componentName
	 * @param component
	 */
	public synchronized final <T> void add(String className, String componentName, T component) {
		final StreamIORegistryItem<T> mapX = getMapItem(className);
		mapX.registerEntity(componentName, component);
		registryMap.put(className, mapX);
	}

	/**
	 * @param <T>
	 * @param className
	 * @param componentName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public final <T> T get(String className, String componentName) {
		return (T)getMapItem(className).getEntity(componentName);
	}


	/**
	 * @param <T>
	 * @param componentName
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final <T> T seek(String componentName) {
		List<StreamIORegistryItem> registries = registryMap
			.entrySet()
			.stream()
			.map(entry -> entry.getValue())
			.filter( item -> item.containsEntry(componentName) )
			.collect(Collectors.toList());
		if ( registries.size() == 0 )
			return null;
		return (T)registries.get(0).getEntity(componentName);
	}
	
	/**
	 * @param <T>
	 * @param className
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public final <T> Map<String, T> getAllAsMap(String className) {
		return (Map<String, T>)getMapItem(className).getEntitiesMap();
	}
	
	/**
	 * @param <T>
	 * @param className
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public final <T> Collection<T> getAll(String className) {
		return getMapItem(className).getEntitiesMap()
				.entrySet()
				.stream()
				.map( entry -> (T) entry.getValue() )
				.collect(Collectors.toList());
	}
	
	/**
	 * @return
	 */
	public final Collection<String> getClasses() {
		return registryMap.keySet();
	}
	
	/**
	 * @param className
	 * @return
	 */
	public final boolean containsClass(String className) {
		return registryMap.containsKey(className);
	}
	
	/**
	 * @param className
	 * @param componentName
	 * @return
	 */
	public final boolean containsClassComponent(String className, String componentName) {
		if ( ! containsClass(className) )
			return false;
		return getMapItem(className).containsEntry(componentName);
	}
	
	/**
	 * @param className
	 * @return
	 */
	public final Collection<String> getClassesElements(String className) {
		if ( ! containsClass(className) )
			return Arrays.asList(new String[] {});
		return getMapItem(className).getEntitiesMap().keySet();
	}
	
	/**
	 * @param <T>
	 * @param className
	 * @param map
	 */
	public synchronized final <T> void addAll(String className, Map<String, T> map) {
		final StreamIORegistryItem<T> compMap = getMapItem(className);
		map
		.forEach(compMap::registerEntity);
		registryMap.put(className, compMap);
	}

	/**
	 * @param <T>
	 * @param className
	 * @param item
	 */
	public synchronized final <T> void addAll(String className, StreamIORegistryItem<T> item) {
		final StreamIORegistryItem<T> compMap = getMapItem(className);
		item.getEntitiesMap().forEach(compMap::registerEntity);
		registryMap.put(className, compMap);
	}
	
	/**
	 * @author Fabrizio Torelli (hellgate75@gmail.com)
	 *
	 * @param <T>
	 */
	static class StreamIORegistryItem<T> {
		private Map<String, T> entitiesMap = new ConcurrentHashMap<>();

		/**
		 * Default Constructor
		 */
		protected StreamIORegistryItem() {
			super();
		}

		/**
		 * Gets the entities map
		 * @return the entitiesMap Map of registered entities
		 */
		public Map<String, T> getEntitiesMap() {
			return entitiesMap;
		}
		
		/**
		 * @return
		 */
		public Collection<String> getEntryNames() {
			return entitiesMap.keySet();
		}
		
		/**
		 * @param entryName
		 * @return
		 */
		public boolean containsEntry(String entryName) {
			return entitiesMap.containsKey(entryName);
		}
		
		/**
		 * Register a single entity into the registry
		 * @param name
		 * @param t
		 */
		public void registerEntity(String name, T t) {
			if ( name !=null && t != null && !name.isEmpty() ) {
				entitiesMap.put(name, t);
			}
		}
		
		/**
		 * Gets a single entity into the registry
		 * @param name
		 * @return
		 */
		public T getEntity(String name) {
			if ( name !=null&& !name.isEmpty() ) {
				return entitiesMap.get(name);
			}
			return null;
		}
	}

	/**
	 * @return
	 */
	public static final ComponentsRegistry getInstance() {
		if ( instance == null ) {
			instance = new ComponentsRegistry();
		}
		return instance;
	}

}
