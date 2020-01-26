/**
 * 
 */
package com.rcg.foundation.fondify.context;

import com.rcg.foundation.fondify.annotations.lifecycle.ApplicationManager;
import com.rcg.foundation.fondify.annotations.lifecycle.ApplicationManagerProvider;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ApplicationManagerProviderImpl implements ApplicationManagerProvider {

	/**
	 * Public constructor
	 */
	public ApplicationManagerProviderImpl() {
		super();
	}

	@Override
	public ApplicationManager getApplicationManager() {
		return ApplicationManagerImpl.getInstance();
	}

}
