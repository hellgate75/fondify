/**
 * 
 */
package com.rcg.foundation.fondify.properties.properties;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.rcg.foundation.fondify.core.exceptions.IOException;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;
import com.rcg.foundation.fondify.core.properties.PropertyArchive;
import com.rcg.foundation.fondify.core.properties.PropertyManager;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class SystemPropertyManager implements PropertyManager {

	private Map< String, Object> properties = new ConcurrentHashMap<String, Object>(0);

	/**
	 * 
	 */
	public SystemPropertyManager() {
		super();
	}

	@Override
	public void load(String filePath) throws IOException {
		try {
			properties.putAll(System.getenv());
			System.getProperties()
				.keySet()
				.forEach( key -> properties.put((String)key, System.getProperty((String)key)));
		} catch (Exception ex) {
			String message = "Unable to load system properties";
			LoggerHelper.logError("SystemPropertyManager::load", message, ex);
			return;
		}
		PropertyArchive.getInstance().registerProperties(properties);
	}

	@Override
	public Collection<String> keys() {
		return properties.keySet();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getByKey(String key, Class<? extends T> clazz) {
		if ( ! properties.containsKey(key) ) {
			return null;
		}
		return (T) properties.get(key);
	}

}
