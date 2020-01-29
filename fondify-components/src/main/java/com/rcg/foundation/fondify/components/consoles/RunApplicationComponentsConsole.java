/**
 * 
 */
package com.rcg.foundation.fondify.components.consoles;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import com.rcg.foundation.fondify.annotations.typings.ApplicationConsole;
import com.rcg.foundation.fondify.components.ComponentsManagerImpl;
import com.rcg.foundation.fondify.components.helpers.AnnotationHelper;
import com.rcg.foundation.fondify.components.helpers.ComponentsHelper;
import com.rcg.foundation.fondify.components.injecables.Activable;
import com.rcg.foundation.fondify.components.injecables.ActivableExecutor;
import com.rcg.foundation.fondify.components.injecables.Executable;
import com.rcg.foundation.fondify.components.injecables.Properties;
import com.rcg.foundation.fondify.components.injecables.Requesting;
import com.rcg.foundation.fondify.components.injecables.Service;
import com.rcg.foundation.fondify.core.constants.ArgumentsConstants;
import com.rcg.foundation.fondify.core.helpers.ArgumentsHelper;
import com.rcg.foundation.fondify.core.helpers.GenericHelper;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;
import com.rcg.foundation.fondify.core.typings.Injectable;
import com.rcg.foundation.fondify.core.typings.lifecycle.ComponentsManager;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class RunApplicationComponentsConsole implements ApplicationConsole {
	ComponentsManager componentsManager = null;
	/**
	 * 
	 */
	public RunApplicationComponentsConsole() {
		super();
		componentsManager = new ComponentsManagerImpl();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void run(String[] arguments) {
		LoggerHelper.logTrace("RunApplicationComponentsConsole::run", "Executing injectable services run request..");
		String threadName = Thread.currentThread().getName();
		if ( ArgumentsHelper.hasArgument(ArgumentsConstants.COMPONENTS_RUN_SERVICE) ) {
			String serviceNames = ArgumentsHelper.getArgument(ArgumentsConstants.COMPONENTS_RUN_SERVICE);
			Arrays.asList(serviceNames.split(",")).forEach( serviceName ->{
				GenericHelper.fixCurrentThreadStandardName(threadName);
				try {
					LoggerHelper.logTrace("RunApplicationComponentsConsole::run", String.format("Requested service: %s, now checking availability..", serviceName));
					Injectable injectable = componentsManager.getInjectableByName(serviceName, null);
					if ( injectable != null ) {
						injectable = (Injectable)AnnotationHelper.scanAndProcessEntity(injectable, injectable.getClass());
						LoggerHelper.logTrace("RunApplicationComponentsConsole::run", String.format("Found service: %s, of type %s!!", serviceName, injectable.getClass().getName()));
						if ( Requesting.class.isAssignableFrom(injectable.getClass()) ) {
							// It has a response : how to manage response ?
							Optional<Method> mOpt = Arrays.asList(injectable.getClass().getDeclaredMethods()).stream().filter(m -> m.getName().equals("request")).findFirst();
							Method method = null;
							if ( mOpt.isPresent() ) {
								method = mOpt.get();
							}
							LoggerHelper.logTrace("RunApplicationComponentsConsole::run", 
									String.format("Executing Injectable Requesting of class: %s using method: %s!!", injectable.getClass().getName(), method != null ? method.getName() : "<NULL" ) );
							ComponentsHelper.executeMethodOfBean(injectable.getClass(), injectable, method);
						} else if ( Activable.class.isAssignableFrom(injectable.getClass()) || 
								(ActivableExecutor.class.isAssignableFrom(injectable.getClass()) && ((ActivableExecutor)injectable).isActivableComponent()) ) {
							Optional<Method> mOpt = Arrays.asList(injectable.getClass().getDeclaredMethods()).stream().filter(m -> m.getName().equals("activate")).findFirst();
							Method method = null;
							if ( mOpt.isPresent() ) {
								method = mOpt.get();
							}
							LoggerHelper.logTrace("RunApplicationComponentsConsole::run", 
									String.format("Executing Injectable Activable / ActivableExecutor of class: %s using method: %s!!", injectable.getClass().getName(), method != null ? method.getName() : "<NULL" ) );
							ComponentsHelper.executeMethodOfBean(injectable.getClass(), injectable, method);
							
						} else if ( Executable.class.isAssignableFrom(injectable.getClass())  || 
								(ActivableExecutor.class.isAssignableFrom(injectable.getClass()) && ! ((ActivableExecutor)injectable).isActivableComponent()) ) {
							Optional<Method> mOpt = Arrays.asList(injectable.getClass().getDeclaredMethods()).stream().filter(m -> m.getName().equals("doAction")).findFirst();
							Method method = null;
							if ( mOpt.isPresent() ) {
								method = mOpt.get();
							}
							LoggerHelper.logTrace("RunApplicationComponentsConsole::run", 
									String.format("Executing Injectable Executable / ActivableExecutor of class: %s using method: %s!!", injectable.getClass().getName(), method != null ? method.getName() : "<NULL" ) );
							ComponentsHelper.executeMethodOfBean(injectable.getClass(), injectable, method);
							
						} else if ( Properties.class.isAssignableFrom(injectable.getClass()) &&
								((Properties)injectable).isGlobalPropertiesRegistrationEnabled() ) {
							LoggerHelper.logTrace("RunApplicationComponentsConsole::run", 
									String.format("Registering Injectable Properties of class: %s!!", injectable.getClass().getName()) );
							((Properties)injectable).registerAllProperties();
						} else if ( Service.class.isAssignableFrom(injectable.getClass()) ) {
							Optional<Method> mOpt = Arrays.asList(injectable.getClass().getDeclaredMethods()).stream().filter(m -> m.getName().equals("doService")).findFirst();
							Method method = null;
							if ( mOpt.isPresent() ) {
								method = mOpt.get();
							}
							LoggerHelper.logTrace("RunApplicationComponentsConsole::run", 
									String.format("Executing Injectable Service of class: %s using method: %s!!", injectable.getClass().getName(), method != null ? method.getName() : "<NULL" ) );
							ComponentsHelper.executeMethodOfBean(injectable.getClass(), injectable, method);
						} else {
							LoggerHelper.logWarn("RunApplicationComponentsConsole::run", 
									String.format("Unable to execute an Unknown Injectable of class: %s!!", injectable.getClass()),
									null);

						}
					} else {
						LoggerHelper.logWarn("RunApplicationComponentsConsole::run", 
								String.format("Unable to locate required injectable service: %s!!", serviceName), 
								null);
					}
				} catch (Exception e) {
					LoggerHelper.logError("RunApplicationComponentsConsole::run", 
							String.format("Error executing required injectable service: %s!!", serviceName), 
							e);
				}
			});
			
		}
		LoggerHelper.logTrace("RunApplicationComponentsConsole::run", "Completed injectable services run request!!");
	}

}
