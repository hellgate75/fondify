/**
 * 
 */
package com.rcg.foundation.fondify.context.lifecycle.impl;

import java.util.List;
import java.util.Properties;

import com.rcg.foundation.fondify.core.typings.Session;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class SessionImpl implements Session {

	/**
	 * 
	 */
	public SessionImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Properties getApplicationProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getApplicationProperty(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<KeyPair<String, Object>> getSessionObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getSessionObject(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerSessionObject(String key, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public Properties getSessionProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSessionProperty(String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
