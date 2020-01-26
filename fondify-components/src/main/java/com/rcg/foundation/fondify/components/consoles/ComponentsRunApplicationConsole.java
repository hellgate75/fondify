/**
 * 
 */
package com.rcg.foundation.fondify.components.consoles;

import com.rcg.foundation.fondify.annotations.typings.ApplicationConsole;
import com.rcg.foundation.fondify.core.constants.ArgumentsConstants;
import com.rcg.foundation.fondify.core.helpers.ArgumentsHelper;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ComponentsRunApplicationConsole implements ApplicationConsole {

	/**
	 * 
	 */
	public ComponentsRunApplicationConsole() {
		super();
	}

	@Override
	public void run(String[] arguments) {
		LoggerHelper.logTrace("ComponentsRunApplicationConsole::run", "Executing injectable services run request..");
		if ( ArgumentsHelper.hasArgument(ArgumentsConstants.COMPONENTS_RUN_SERVICE) ) {
			String serviceName = ArgumentsHelper.getArgument(ArgumentsConstants.COMPONENTS_RUN_SERVICE);
			LoggerHelper.logTrace("ComponentsRunApplicationConsole::run", String.format("Requested service: %s, now checking availability..", serviceName));
			
		}
		LoggerHelper.logTrace("ComponentsRunApplicationConsole::run", "Completed injectable services run request!!");
	}

}
