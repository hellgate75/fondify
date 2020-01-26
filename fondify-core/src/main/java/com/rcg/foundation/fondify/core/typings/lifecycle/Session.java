/**
 * 
 */
package com.rcg.foundation.fondify.core.typings.lifecycle;

import java.util.List;
import java.util.UUID;

import com.rcg.foundation.fondify.core.typings.KeyPair;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface Session {
	
	List<KeyPair<String, Object>> getApplicationProperties();
	
	Object getApplicationProperty(String name);
	
	List<KeyPair<String, Object>> getSessionObjects();
	
	Object getSessionObject(String key);
	
	void registerSessionObject(String key, Object value);
	
	List<KeyPair<String, String>> getSessionProperties();
	
	String getSessionProperty(String name);

	void registerSessionProperty(String key, String value);

	UUID getSessionId();
}
