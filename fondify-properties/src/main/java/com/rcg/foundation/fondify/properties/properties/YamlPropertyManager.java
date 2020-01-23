/**
 * 
 */
package com.rcg.foundation.fondify.properties.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.yaml.snakeyaml.Yaml;

import com.rcg.foundation.fondify.core.exceptions.IOException;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;
import com.rcg.foundation.fondify.core.properties.PropertyArchive;
import com.rcg.foundation.fondify.core.properties.PropertyManager;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class YamlPropertyManager implements PropertyManager {

	private Map< String, Object> properties = new ConcurrentHashMap<String, Object>(0);
	
	/**
	 * 
	 */
	public YamlPropertyManager() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void load(String filePath) throws IOException {
		Yaml yaml = new Yaml();
		if ( filePath.startsWith("classpath:") ) {
			filePath = filePath.substring(filePath.indexOf(":")+1);
			try (InputStream ios = ClassLoader.getSystemClassLoader().getResourceAsStream(filePath)) {
				properties.putAll((Map< String, Object>) yaml.load(ios));
			} catch (Exception ex) {
				String message = String.format("Unable to load class path resource: %s", filePath);
				LoggerHelper.logError("YamlPropertyManager::load", message, ex);
			}
			
		} else if ( filePath.startsWith("url:") ) {
			filePath = filePath.substring(filePath.indexOf(":")+1);
			HttpURLConnection conn = null;
			boolean connected = false;
			try {
				URL url = new URL(filePath);
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("Cache-Control", "no-cache");
				conn.setRequestProperty("Accept", "application/json");
				if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED &&
						conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
					LoggerHelper.logError("YamlPropertyManager::load","Failed : HTTP error code : "
						+ conn.getResponseCode(), null);
				}
				connected = true;
				properties.putAll((Map< String, Object>) yaml.load(conn.getInputStream()));
			} catch (Exception ex) {
				String message = String.format("Unable to load spring url request: %s", filePath);
				LoggerHelper.logError("YamlPropertyManager::load", message, ex);
				return;
			} finally {
				if ( conn != null && connected ) {
					conn.disconnect();
				}
			}
			
		} else {
			if ( filePath.contains(":") ) {
				filePath = filePath.substring(filePath.indexOf(":")+1);
			}
			File file = new File(filePath);
			if ( ! file.exists() || !file.isFile() ) {
				String message = String.format("Invalid file to load: %s", filePath);
				LoggerHelper.logError("YamlPropertyManager::load", message, null);
				return;
			}
			try (FileInputStream ios = new FileInputStream(file)) {
				properties.putAll((Map< String, Object>) yaml.load(ios));
			} catch (Exception ex) {
				String message = String.format("Unable to load file: %s", filePath);
				LoggerHelper.logError("YamlPropertyManager::load", message, ex);
			}
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
