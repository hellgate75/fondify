/**
 * 
 */
package com.rcg.foundation.fondify.annotations;

import static com.rcg.foundation.fondify.annotations.helpers.ScannerHelper.executeProvidedBaseAnnotationExecutors;
import static com.rcg.foundation.fondify.annotations.helpers.ScannerHelper.scanBaseElementsAndStoreData;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.rcg.foundation.fondify.annotations.annotations.Application;
import com.rcg.foundation.fondify.annotations.helpers.ScannerHelper;
import com.rcg.foundation.fondify.annotations.typings.ApplicationConsole;
import com.rcg.foundation.fondify.core.Foundation;
import com.rcg.foundation.fondify.core.constants.ArgumentsConstants;
import com.rcg.foundation.fondify.core.domain.ApplicationType;
import com.rcg.foundation.fondify.core.exceptions.InitializationException;
import com.rcg.foundation.fondify.core.helpers.ArgumentsHelper;
import com.rcg.foundation.fondify.core.helpers.BeansHelper;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;
import com.rcg.foundation.fondify.core.typings.runtime.ProcessStateTracker;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class AnnotationEngine {
	protected static ExecutorService executorService = null;
	
	private static boolean completed = false;
	private static boolean waiting = false;
	
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
	public static void run(Class<?> mainClass, Runnable disclaimerTask, Runnable tasks, String[] arguments) throws Exception {
		completed = false;
		Foundation.credits();
		new Thread(disclaimerTask).start();
		if ( ! ScannerHelper.isApplicationClass(mainClass) ) {
			String message = String.format("Null or not 'Application' class for bootstrap: %s!!", mainClass);
			LoggerHelper.logError("AnnotationEngineAnnotationEngine::run", message, null);
			throw new InitializationException(message);
		}
		
		scanBaseElementsAndStoreData();
		
		executeProvidedBaseAnnotationExecutors();

		ScannerHelper.collectModuleScanners();

		ScannerHelper.executeScannerMainClasses();
		
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


		//Execute available autorun components and relative extension when not declared
		if ( ArgumentsHelper.hasArgument(ArgumentsConstants.ENABLE_AUTORUN_EXECUTION) &&
			  ArgumentsHelper.getArgument(ArgumentsConstants.ENABLE_AUTORUN_EXECUTION).equalsIgnoreCase("true")) {
			LoggerHelper.logInfo("AnnotationEngine::run", "Enabled Autorun feature!!");
			BeansHelper.executeAutorunComponents();
			ProcessStateTracker.getInstance().registerNewProcessStateReference(() -> BeansHelper.executorService != null 
					  && ! BeansHelper.executorService.isShutdown() );
		}
		
		List<Class<? extends ApplicationConsole>> applicationConsoles = new ArrayList<>(0);
		applicationConsoles.addAll(
				annotations
				.stream()
				.filter( ann -> ann != null && Application.class.isAssignableFrom(ann.getClass()) )
				.map(ann -> {
					Application app = (Application)ann;
					ApplicationType appType = app.scope();
					List<Class<? extends ApplicationConsole>> listofConsoles = new ArrayList<>(0);
					if ( appType == ApplicationType.CONSOLE && app.hasCommandShell()) {
						listofConsoles.addAll(
								ScannerHelper.collectSubTypesOf(ScannerHelper.getRefletionsByPackages(new String[0]), ApplicationConsole.class)
						);
					}
					return listofConsoles;
				})
				.flatMap(List::stream)
				.distinct()
				.collect(Collectors.toList())
		);

		if ( applicationConsoles.size() > 0 ) {
			LoggerHelper.logInfo("AnnotationEngine::run", "Found Application Console features!!");
			
			AnnotationEngine.executorService = Executors.newFixedThreadPool(applicationConsoles.size());
			
			applicationConsoles
				.forEach(consoleClass -> {
					AnnotationEngine.executorService.execute(new Runnable() {
		
						@Override
						public void run() {
							try {
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
						try {
							Thread.sleep(2500l);
						} catch (InterruptedException e) {
							//NOTHING TO DO HERE
							;
						}
					}
					AnnotationEngine.executorService = null;
				}
			}).start();
			
		} else {
			
		}

		completed = true;
		waiting = true;

		// Wait for Autorun executions to be completed!!
		while ( ProcessStateTracker.getInstance().isInWaitState() ) {
			try {
				Thread.sleep(2500l);
			} catch (InterruptedException e) {
				//NOTHING TO DO HERE
				;
			}
		}
		waiting = false;
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
