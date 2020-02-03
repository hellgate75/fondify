/**
 * 
 */
package com.rcg.foundation.fondify.annotations;

import static com.rcg.foundation.fondify.annotations.helpers.ScannerHelper.executeProvidedBaseAnnotationExecutors;
import static com.rcg.foundation.fondify.annotations.helpers.ScannerHelper.scanBaseElementsAndStoreData;
import static com.rcg.foundation.fondify.annotations.helpers.ScannerHelper.collectModuleScanners;
import static com.rcg.foundation.fondify.annotations.helpers.ScannerHelper.executeScannerMainClasses;
import static com.rcg.foundation.fondify.annotations.helpers.ScannerHelper.getApplicationClassAnnotations;
import static com.rcg.foundation.fondify.utils.helpers.ArgumentsHelper.processArguments;
import static com.rcg.foundation.fondify.utils.helpers.ArgumentsHelper.hasArgument;
import static com.rcg.foundation.fondify.utils.helpers.ArgumentsHelper.getArgument;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.rcg.foundation.fondify.reflections.Reflections;
import com.rcg.foundation.fondify.utils.constants.ArgumentsConstants;
import com.rcg.foundation.fondify.utils.helpers.ArgumentsHelper;
import com.rcg.foundation.fondify.utils.helpers.GenericHelper;
import com.rcg.foundation.fondify.utils.helpers.LoggerHelper;
import com.rcg.foundation.fondify.utils.process.GlobalProcessTracker;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class AnnotationEngine {
	static {
		/*
		 * Exclude some libraries from system Class-Path load
		 */
		Reflections.SYSTEM_LIBRARIES_EXCLUSIONS.addAll(
				Arrays.asList("asm-tree", "j2objc-annotations", "aether-",
						"jackson-", "maven-", "slf4j-", "log4j-", "plexus-", "hazelcast-", "commons-beanutils-", 
						"guava-","common-codec-", "stax2-api-", "jakarta.", "commons-digester-", "woodstox-core-",
						"jdom2", "asm-", "commons-collections-", "commons-logging-", "commons-lang3-",
						"snakeyaml-", "sisu-", "commons-validator-", "rror_prone_annotations-",
						"procyon-", "jdependency-", "jsr305-", "animal-sniffer-", "commons-io-", "commons-codec-",
						"powermock-", "mockito-", "junit-")
		);
		/*
		 * Force includes FONDIFY libraries prefix and ensure all 
		 * libraries are ever loaded from the system Class-Path.
		 */
		Reflections.SYSTEM_LIBRARIES_FORCED_INCLUSIONS.addAll(
				Arrays.asList("fondify-")
		);
		/*
		 * Force includes FONDIFY base package prefix and ensure all 
		 * packages are ever loaded from the system Class-Path.
		 */
		Reflections.SYSTEM_PACKAGES_FORCED_INCLUSIONS.addAll(
				Arrays.asList("com.rcg.foundation.fondify")
		);
	}
	
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
	public static void run(Class<?> mainClass, Runnable disclaimerTask, Runnable preExecutionTasks, Runnable postExecutionTasks, String[] arguments) throws Exception {
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

		collectModuleScanners();
		
		executeScannerMainClasses(defaultSessionThreadName);

		List<? extends Annotation> annotations = getApplicationClassAnnotations(mainClass);

		//Executing Feature(s)
		processArguments();
		
		if ( preExecutionTasks != null ) {
			if ( ArgumentsHelper.traceAllLevels || ArgumentsHelper.traceAnnotationsLevel  ) {
				LoggerHelper.logTrace("AnnotationEngine::run", "Executing provided pre-execution tasks code ...");
			}
			//Running extra annotation seek tasks
			UUID uuid = GlobalProcessTracker.getInstance().lockProcess();
			try {
				preExecutionTasks.run();
			} catch (Exception | Error e) {
				String message = "Errors during execution of provided pre-execution tasks code!!";
				LoggerHelper.logError("AnnotationEngine::run", message,
						e);
				throw new InitializationException(message, e);
			} 
			if ( ArgumentsHelper.traceAllLevels || ArgumentsHelper.traceAnnotationsLevel  ) {
				LoggerHelper.logTrace("AnnotationEngine::run", "Execution of provided pre-execution tasks code finished!!");
			}
			GlobalProcessTracker.getInstance().releaseProcess(uuid);
		}

		while( GlobalProcessTracker.getInstance().numberOfActiveLocks() > 0 ) {
			GenericHelper.sleepThread(500);
		}
		
		//Execute available autorun components and relative extension when not declared
		if ( hasArgument(ArgumentsConstants.ENABLE_AUTORUN_EXECUTION) &&
			  getArgument(ArgumentsConstants.ENABLE_AUTORUN_EXECUTION).equalsIgnoreCase("true")) {
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
				hasArgument(ArgumentsConstants.ENABLE_CONSOLE_EXECUTION) &&
				getArgument(ArgumentsConstants.ENABLE_CONSOLE_EXECUTION).equalsIgnoreCase("true") ) {
			LoggerHelper.logInfo("AnnotationEngine::run", "Found Application Console features!!");
			
			AnnotationEngine.executorService = Executors.newFixedThreadPool(applicationConsoles.size());
			
		applicationConsoles
			.parallelStream()
				.forEach(consoleClass -> {
					AnnotationEngine.executorService.execute(new Runnable() {
		
						@Override
						public void run() {
							UUID uuid = GlobalProcessTracker.getInstance().lockProcess();
							try {
								GenericHelper.fixCurrentThreadStandardName(defaultSessionThreadName);
								ApplicationConsole console = consoleClass.newInstance();
								LoggerHelper.logInfo("AnnotationEngine::run", String.format("Executing Application Console feature: %s", consoleClass.getName()));
								console.run(arguments);
							} catch (Exception | Error ex) {
								LoggerHelper.logError("AnnotationEngine::run [console thread run]",
										String.format("Unable to execute console: %s", consoleClass), ex);
							}
							GlobalProcessTracker.getInstance().releaseProcess(uuid);
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
		
		if ( postExecutionTasks != null ) {
			if ( ArgumentsHelper.traceAllLevels || ArgumentsHelper.traceAnnotationsLevel  ) {
				LoggerHelper.logTrace("AnnotationEngine::run", "Executing provided post-execution tasks code ...");
			}
			//Running extra annotation seek tasks
			UUID uuid = GlobalProcessTracker.getInstance().lockProcess();
			try {
				postExecutionTasks.run();
			} catch (Exception | Error e) {
				String message = "Errors during execution of provided post-execution tasks code!!";
				LoggerHelper.logError("AnnotationEngine::run", message,
						e);
				throw new InitializationException(message, e);
			} 
			if ( ArgumentsHelper.traceAllLevels || ArgumentsHelper.traceAnnotationsLevel  ) {
				LoggerHelper.logTrace("AnnotationEngine::run", "Execution of provided post-execution tasks code finished!!");
			}
			GlobalProcessTracker.getInstance().releaseProcess(uuid);
		}
		
		while( GlobalProcessTracker.getInstance().numberOfActiveLocks() > 0 ) {
			GenericHelper.sleepThread(500);
		}
		
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
