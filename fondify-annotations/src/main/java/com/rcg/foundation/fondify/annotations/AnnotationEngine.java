/**
 * 
 */
package com.rcg.foundation.fondify.annotations;

import static com.rcg.foundation.fondify.annotations.helpers.ScannerHelper.executeProvidedBaseAnnotationExecutors;
import static com.rcg.foundation.fondify.annotations.helpers.ScannerHelper.scanBaseElementsAndStoreData;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.rcg.foundation.fondify.annotations.annotations.Application;
import com.rcg.foundation.fondify.annotations.helpers.ScannerHelper;
import com.rcg.foundation.fondify.annotations.lifecycle.ApplicationManager;
import com.rcg.foundation.fondify.annotations.lifecycle.ApplicationManagerProvider;
import com.rcg.foundation.fondify.annotations.typings.ApplicationConsole;
import com.rcg.foundation.fondify.core.Foundation;
import com.rcg.foundation.fondify.core.domain.ApplicationType;
import com.rcg.foundation.fondify.core.exceptions.InitializationException;
import com.rcg.foundation.fondify.core.helpers.BeansHelper;
import com.rcg.foundation.fondify.core.typings.runtime.ProcessStateTracker;
import com.rcg.foundation.fondify.utils.constants.ArgumentsConstants;
import com.rcg.foundation.fondify.utils.helpers.ArgumentsHelper;
import com.rcg.foundation.fondify.utils.helpers.GenericHelper;
import com.rcg.foundation.fondify.utils.helpers.LoggerHelper;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class AnnotationEngine {
	protected static ExecutorService executorService = null;
	
	private static boolean completed = false;
	private static boolean waiting = false;
	
	private static long startTime = 0l;
	
	private static double elapsedTimeSeconds = 0l;

	private static ApplicationManager applicationManager = null;
	
	public static UUID defaultSessionUUID = null;
	
	public static String defaultSessionThreadName = "";
	
	/**
	 * Private blocked constructor
	 */
	private AnnotationEngine() {
		throw new IllegalArgumentException("AnnotationEngine: Unable to make instance of Engine Class");
	}
	

	/**
	 * Run annotation engine
	 * @param mainClass Main Class
	 */
	@SuppressWarnings("unchecked")
	public static void run(Class<?> mainClass, Runnable disclaimerTask, Runnable tasks, String[] arguments) throws Exception {
		ArgumentsHelper.storeArguments(arguments);
		ScannerHelper.initializeScanningPackagesFilter(mainClass);
		BeansHelper.loadAllInterfacesImplementations();
		startTime = System.nanoTime();
		completed = false;
		Foundation.credits();


		if ( disclaimerTask != null )
			disclaimerTask.run();

		//Start New Session on this thread.
		Optional<ApplicationManagerProvider> appManProviderOpt =  BeansHelper.getImplementedType(ApplicationManagerProvider.class);
		if ( appManProviderOpt.isPresent() ) {
			applicationManager = appManProviderOpt.get().getApplicationManager();
		}
		if ( applicationManager == null ) {
			String message = "Unable to find implementations of ApplicationManagerProvider, please load specific modules, such as Fondify Context or similar";
			LoggerHelper.logError("AnnatationEngine::run", message, null);
			throw new IllegalStateException(message);
		}

		defaultSessionThreadName = GenericHelper.fixCurrentThreadStandardName(null);
		defaultSessionUUID = applicationManager.createNewSession();
		if ( defaultSessionUUID == null ) {
			String message = "Unable to create new session with current version of ApplicationManagerProvider, please load a more specific modules, such as Fondify Context or similar";
			LoggerHelper.logError("AnnatationEngine::run", message, null);
			throw new IllegalStateException(message);
		}
		
		LoggerHelper.logInfo("AnnatationEngine::run", String.format("Create new main session -> id: %s!!", defaultSessionUUID.toString()));
		
		
		if ( ! ScannerHelper.isApplicationClass(mainClass) ) {
			String message = String.format("Null or not 'Application' class for bootstrap: %s!!", mainClass);
			LoggerHelper.logError("AnnotationEngineAnnotationEngine::run", message, null);
			throw new InitializationException(message);
		}
		
		scanBaseElementsAndStoreData(defaultSessionThreadName);
		
		executeProvidedBaseAnnotationExecutors(defaultSessionThreadName);

		ScannerHelper.collectModuleScanners();

		ScannerHelper.executeScannerMainClasses(defaultSessionThreadName);
		
		if ( tasks != null ) {
			//Running extra annotation seek tasks
			try {
				tasks.run();
			} catch (Exception e) {
				String message = "Errors during execution of provided runnable code!!";
				LoggerHelper.logError("AnnotationEngine::run", message,
						e);
				throw new InitializationException(message, e);
			} 
		}

		List<? extends Annotation> annotations = ScannerHelper.getApplicationClassAnnotations(mainClass);

		//Executing Feature(s)
		ArgumentsHelper.processArguments();

		//Execute available autorun components and relative extension when not declared
		if ( ArgumentsHelper.hasArgument(ArgumentsConstants.ENABLE_AUTORUN_EXECUTION) &&
			  ArgumentsHelper.getArgument(ArgumentsConstants.ENABLE_AUTORUN_EXECUTION).equalsIgnoreCase("true")) {
			LoggerHelper.logInfo("AnnotationEngine::run", "Enabled Autorun feature!!");
			BeansHelper.executeAutorunComponents(defaultSessionThreadName);
			ProcessStateTracker.getInstance().registerNewProcessStateReference(() -> BeansHelper.executorService != null 
					  && ! BeansHelper.executorService.isShutdown() );
		}
		
		//Recovering Application Console(s)
		List<Class<? extends ApplicationConsole>> applicationConsoles = new ArrayList<>(0);
		applicationConsoles.addAll(
				annotations
				.stream()
				.filter( ann -> ann != null && Application.class.isAssignableFrom(ann.getClass()) )
				.map(ann -> {
					Application app = (Application)ann;
					ApplicationType appType = app.scope();
					List<Class<ApplicationConsole>> listofConsoles = new ArrayList<>(0);
					if ( appType == ApplicationType.CONSOLE && app.hasCommandShell()) {
						listofConsoles.addAll(
								BeansHelper.getImplementatedSubtypesOf(ApplicationConsole.class)
								.stream()
								.map(cls -> (Class<ApplicationConsole>)cls)
								.collect(Collectors.toList())
						);
					}
					return listofConsoles;
				})
				.flatMap(List::stream)
				.distinct()
				.collect(Collectors.toList())
		);

		if ( applicationConsoles.size() > 0 &&
				ArgumentsHelper.hasArgument(ArgumentsConstants.ENABLE_CONSOLE_EXECUTION) &&
				ArgumentsHelper.getArgument(ArgumentsConstants.ENABLE_CONSOLE_EXECUTION).equalsIgnoreCase("true") ) {
			LoggerHelper.logInfo("AnnotationEngine::run", "Found Application Console features!!");
			
			AnnotationEngine.executorService = Executors.newFixedThreadPool(applicationConsoles.size());
			
			applicationConsoles
				.forEach(consoleClass -> {
					AnnotationEngine.executorService.execute(new Runnable() {
		
						@Override
						public void run() {
							try {
								GenericHelper.fixCurrentThreadStandardName(defaultSessionThreadName);
								ApplicationConsole console = consoleClass.newInstance();
								LoggerHelper.logInfo("AnnotationEngine::run", String.format("Executing Application Console feature: %s", consoleClass.getName()));
								console.run(arguments);
							} catch (Exception ex) {
								LoggerHelper.logError("AnnotationEngine::run [console thread run]",
										String.format("Unable to execute console: %s", consoleClass), ex);
							}
						}
					});
				});
			AnnotationEngine.executorService.shutdown();
			ProcessStateTracker.getInstance().registerNewProcessStateReference(() -> AnnotationEngine.executorService != null 
					  && ! AnnotationEngine.executorService.isShutdown() );
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					while ( AnnotationEngine.executorService != null && 
							! AnnotationEngine.executorService.isShutdown() ) {
						GenericHelper.sleepThread(2500L);
					}
					AnnotationEngine.executorService = null;
				}
			}).start();
			
		} else {
			LoggerHelper.logWarn("AnnotationEngine::run", "No Application Console found!!", null);
		}

		completed = true;
		waiting = true;

		// Wait for Autorun executions to be completed!!
		while ( ProcessStateTracker.getInstance().isInWaitState() ) {
			GenericHelper.sleepThread(2500L);
		}
		waiting = false;
		elapsedTimeSeconds = Math.ceil((System.nanoTime() - startTime) / 1000000000);
	}
	
	public static final double getAnnotationEngineExecutionElapsedTimeSeconds() {
		return elapsedTimeSeconds;
	}
	
	/**
	 * Check if Annotation Engine execution is completed. It's useful for executing the
	 * Annotation Engine in asynchronous mode.
	 * @return (boolean) Annotation Engine completion state
	 */
	public static final boolean isStarted() {
		return completed;
	}
	
	/**
	 * Check if Annotation Engine Autorun (post start) execution is completed. It's useful for executing the
	 * Annotation Engine in asynchronous mode.
	 * @return (boolean) Annotation Engine Autorun execution completion state
	 */
	public static final boolean isWaitingForAutorunExecutions() {
		return waiting;
	}
}
