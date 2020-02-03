/**
 * 
 */
package com.rcg.foundation.fondify.core.properties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Properties;

import com.rcg.foundation.fondify.core.exceptions.IOException;
import com.rcg.foundation.fondify.utils.helpers.LoggerHelper;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class BasePropertyManager implements PropertyManager {

	private Properties properties = new Properties();
	
	/**
	 * 
	 */
	public BasePropertyManager() {
		super();
	}

	@Override
	public void load(String filePath) throws IOException {
		HttpURLConnection conn = null;
		boolean connected = false;
		try {
			if ( filePath.startsWith("classpath:") ) {
				filePath = filePath.substring(filePath.indexOf(":")+1);
				properties.load(ClassLoader.getSystemClassLoader().getResourceAsStream(filePath));
			} else if ( filePath.startsWith("file:") ) {
				filePath = filePath.substring(filePath.indexOf(":")+1);
				properties.load(new FileInputStream(filePath));
			} else if ( filePath.startsWith("url:") ) {
				filePath = filePath.substring(filePath.indexOf(":")+1);
				URL url = new URL(filePath);
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("Cache-Control", "no-cache");
				conn.setRequestProperty("Accept", "application/json");
				if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED &&
						conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
					LoggerHelper.logError("BasePropertyManager::load","Failed : HTTP error code : "
						+ conn.getResponseCode(), null);
				}
				connected = true;
				properties.load(conn.getInputStream());
			} else if ( ! filePath.contains(":") ) {
				properties.load(new FileInputStream(filePath));
			} else {
				String message = String.format("Unknown protocol to load file: %s", filePath);
				LoggerHelper.logError("BasePropertyManager::load", message, null);
				return;
			}
		} catch (FileNotFoundException ex) {
			String message = String.format("Unable to load file: %s", filePath);
			LoggerHelper.logError("BasePropertyManager::load", message, ex);
			return;
		} catch (java.io.IOException ex) {
			String message = String.format("Unable to load file: %s", filePath);
			LoggerHelper.logError("BasePropertyManager::load", message, ex);
			return;
		} catch (Exception ex) {
			String message = String.format("Unable to load file: %s", filePath);
			LoggerHelper.logError("BasePropertyManager::load", message, ex);
			return;
		} finally {
			if ( conn != null && connected ) {
				conn.disconnect();
			}
		}
		PropertyArchive.getInstance().registerProperties(properties);
	}

	@Override
	public Collection<String> keys() {
		return properties.stringPropertyNames();
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
