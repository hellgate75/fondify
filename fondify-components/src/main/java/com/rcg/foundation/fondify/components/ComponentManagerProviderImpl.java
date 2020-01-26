/**
 * 
 */
package com.rcg.foundation.fondify.components;

import com.rcg.foundation.fondify.core.typings.lifecycle.ComponentsManager;
import com.rcg.foundation.fondify.core.typings.lifecycle.ComponentManagerProvider;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ComponentManagerProviderImpl implements ComponentManagerProvider {

	private static final ComponentsManager componentManager = new ComponentsManagerImpl();
	
	/**
	 * 
	 */
	public ComponentManagerProviderImpl() {
		super();
	}

	@Override
	public ComponentsManager getComponentManager() {
		return componentManager;
	}

}
