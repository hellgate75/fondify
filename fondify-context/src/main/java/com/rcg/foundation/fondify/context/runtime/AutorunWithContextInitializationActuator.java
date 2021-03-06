/**
 * 
 */
package com.rcg.foundation.fondify.context.runtime;

import com.rcg.foundation.fondify.context.ApplicationManagerImpl;
import com.rcg.foundation.fondify.core.typings.autorun.Autorun;
import com.rcg.foundation.fondify.core.typings.autorun.AutorunInitializerActuator;
import com.rcg.foundation.fondify.utils.helpers.LoggerHelper;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class AutorunWithContextInitializationActuator implements AutorunInitializerActuator {

	/**
	 * 
	 */
	public AutorunWithContextInitializationActuator() {
		super();
	}

	@Override
	public void initAutorun(Autorun autorun) throws Exception {
		if ( autorun == null ) {
			LoggerHelper.logWarn("AutorunWithContextInitializationActuator::initAutorun", 
					String.format("Null autorun in initialization for autorun extension class %s, with initialization actuator class %s", getInitializerSuperClass().getName(), AutorunWithContextInitializationActuator.class.getName()), 
					null);
			return;
		}
		if ( getInitializerSuperClass().isAssignableFrom(autorun.getClass()) ) {
			AutorunWithContext autorunContext = (AutorunWithContext) autorun;
			ApplicationManagerImpl appManager = ApplicationManagerImpl.getInstance();
			autorunContext.setContext(appManager.getSessionContext());
			autorunContext.setApplicationContext(appManager.getApplicationContext());
			autorunContext.setSession(appManager.getSession());
			autorunContext.setSessionId(appManager.getSession() != null ? appManager.getSession().getSessionId() : null);
		} else {
			LoggerHelper.logWarn("AutorunWithContextInitializationActuator::initAutorun", 
					String.format("Invalid class for autorun (requested class %s) in initialization for autorun extension class %s, with initialization actuator class %s", autorun.getClass().getName() ,getInitializerSuperClass().getName(), AutorunWithContextInitializationActuator.class.getName()), 
					null);
		}
	}

	@Override
	public Class<?> getInitializerSuperClass() {
		return AutorunWithContext.class;
	}

}
