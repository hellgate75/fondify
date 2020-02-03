/**
 * 
 */
package com.rcg.foundation.fondify.components;

import com.rcg.foundation.fondify.core.typings.components.ComponentManagerProvider;
import com.rcg.foundation.fondify.core.typings.lifecycle.ComponentsManager;

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
