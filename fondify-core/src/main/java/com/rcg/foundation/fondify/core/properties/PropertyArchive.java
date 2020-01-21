/**
 * 
 */
package com.rcg.foundation.fondify.core.properties;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.rcg.foundation.fondify.core.helpers.LoggerHelper;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class PropertyArchive {
	
	Map<String, Object> properties = new ConcurrentHashMap<String, Object>(0);

	private static final PropertyArchive instance = new PropertyArchive();
	
	/**
	 * Private Constructor
	 */
	private PropertyArchive() {
		super();
	}
	
	public final void registerProperties(Map<String, Object> properties) {
		this.properties.putAll(properties);
	}
	
	public final void registerProperties(Properties properties ) {
		registerProperties(properties.entrySet().stream().map(e -> new AbstractMap.SimpleEntry<String, Object>(""+e.getKey(), e.getValue())).collect(Collectors.toMap(Entry::getKey, Entry::getValue)));

	}
	
	public final boolean contains(String key) {
		if ( key == null || key.isEmpty() ) {
			return false;
		}
		return properties.containsKey(key);
	}
	
	public final Collection<String> getKeys() {
		return this.properties.keySet();
	}
	
	public final Object getProperty(String key) {
		if ( ! contains(key) ) {
			LoggerHelper.logWarn("PropertyArchive::getObject", "Property '"+key+"' is not contained into the archive!!", null);
			return null;
		}
		return properties.get(key);
	}

	public static final PropertyArchive getInstance() {
		return instance;
	}
}
