/**
 * 
 */
package com.rcg.foundation.fondify.properties.properties;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.validator.routines.UrlValidator;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rcg.foundation.fondify.core.exceptions.IOException;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;
import com.rcg.foundation.fondify.core.properties.PropertyArchive;
import com.rcg.foundation.fondify.core.properties.PropertyManager;
import com.rcg.foundation.fondify.properties.properties.SCSResponse.PropertyDetails;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class SpringConfigServerPropertyManager implements PropertyManager {

	protected static final String SYSTEM_SPRING_SERVER_KEY="spring-config-server";
	private static final String[] schemes = {"http","https"}; // DEFAULT schemes = "http", "https", "ftp"
	private static final UrlValidator urlValidator = new UrlValidator(schemes);
	private Map< String, Object> properties = new ConcurrentHashMap<String, Object>(0);

	/**
	 * 
	 */
	public SpringConfigServerPropertyManager() {
		super();
	}

	@Override
	public void load(String filePath) throws IOException {
		if ( ! filePath.startsWith(SYSTEM_SPRING_SERVER_KEY+":") ) {
			String message = String.format("URL doesn't contain protocol (spring-config-server:): %s", filePath);
			LoggerHelper.logError("SpringConfigServerPropertyManager::load", message, null);
			return;
		}
		String urlString = filePath.substring(SYSTEM_SPRING_SERVER_KEY.length() + 1);
		if ( ! urlValidator.isValid(urlString) ) {
			String message = String.format("URL is not valid: %s", urlString);
			LoggerHelper.logError("SpringConfigServerPropertyManager::load", message, null);
			return;
		}
		String profile = "profile";
		String label = "label";
		if ( profile != null && ! profile.isEmpty() ) {
			urlString += "/" + profile;
			if ( label != null && ! label.isEmpty() ) {
				urlString += "/" + label;
			}
		} else {
			String message = String.format("URL has not a profile: %s", urlString);
			LoggerHelper.logError("SpringConfigServerPropertyManager::load", message, null);
			return;
		}
		HttpURLConnection conn = null;
		boolean connected = false;
		try {
			URL url = new URL(urlString);
			conn = (HttpURLConnection) url.openConnection();
//			conn.setDoOutput(true);
			conn.setDoOutput(false);
			conn.setDoInput(true);
			conn.setDefaultUseCaches(false);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
//			conn.setRequestProperty("Content-Type", "application/json");

//			String input = "{}";
//
//			OutputStream os = conn.getOutputStream();
//			os.write(input.getBytes());
//			os.flush();

//			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED &&
//					conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
//				LoggerHelper.logError("SpringConfigServerPropertyManager::load","Failed : HTTP error code : "
//					+ conn.getResponseCode(), null);
//			}
			connected = true;
			ObjectMapper mapper = new ObjectMapper(new JsonFactory());
			SCSResponse response = mapper.readValue(conn.getInputStream(), SCSResponse.class);
			for(PropertyDetails detail: response.getPropertySources()) {
				properties.putAll(detail.getSource());
			}

		} catch (Exception ex) {
			String message = String.format("Unable to load spring url request: %s", urlString);
			LoggerHelper.logError("SpringConfigServerPropertyManager::load", message, ex);
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
