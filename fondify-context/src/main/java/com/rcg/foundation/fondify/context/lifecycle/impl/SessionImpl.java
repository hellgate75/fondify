/**
 * 
 */
package com.rcg.foundation.fondify.context.lifecycle.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.rcg.foundation.fondify.context.ApplicationManagerImpl;
import com.rcg.foundation.fondify.core.properties.PropertyArchive;
import com.rcg.foundation.fondify.core.typings.KeyPair;
import com.rcg.foundation.fondify.core.typings.lifecycle.Session;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class SessionImpl implements Session {
	private UUID uuid;
	private Map<String, Object> applicationProperties = new ConcurrentHashMap<String, Object>(0);
	private Map<String, String> sessionProperties = new ConcurrentHashMap<String, String>(0);
	private Map<String, Object> sessionObjects = new ConcurrentHashMap<String, Object>(0);
	/**
	 * 
	 */
	public SessionImpl() {
		super();
		uuid = ApplicationManagerImpl.getInstance().getCurrentSessionId();
		applicationProperties.putAll(
				PropertyArchive.getInstance()
				.getAllProperties()
		);
	}

	@Override
	public List<KeyPair<String, Object>> getApplicationProperties() {
		return applicationProperties.entrySet()
				.stream()
				.map(entry -> (KeyPair<String, Object>)KeyPair.newKeyPair(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
		
	}

	@Override
	public Object getApplicationProperty(String name) {
		return applicationProperties
			.get(name);
	}

	@Override
	public List<KeyPair<String, Object>> getSessionObjects() {
		return sessionObjects.entrySet()
				.stream()
				.map(entry -> (KeyPair<String, Object>)KeyPair.newKeyPair(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
	}

	@Override
	public Object getSessionObject(String key) {
		return sessionObjects.get(key);
	}

	@Override
	public void registerSessionObject(String key, Object value) {
		sessionObjects.put(key,  value);
	}

	@Override
	public List<KeyPair<String, String>> getSessionProperties() {
		return sessionProperties.entrySet()
				.stream()
				.map(entry -> (KeyPair<String, String>)KeyPair.newKeyPair(entry.getKey(), ""+entry.getValue()))
				.collect(Collectors.toList());
	}

	@Override
	public String getSessionProperty(String name) {
		return sessionProperties.get(name);
	}

	@Override
	public void registerSessionProperty(String key, String value) {
		sessionProperties.put(key,  value);
	}

	@Override
	public UUID getSessionId() {
		return uuid;
	}

}
