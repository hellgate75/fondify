/**
 * 
 */
package com.rcg.foundation.fondify.annotations;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.rcg.foundation.fondify.annotations.annotations.Application;
import com.rcg.foundation.fondify.annotations.helpers.ScannerHelper;
import com.rcg.foundation.fondify.annotations.typings.ApplicationConsole;
import com.rcg.foundation.fondify.core.Foundation;
import com.rcg.foundation.fondify.core.domain.ApplicationType;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class AnnotationEngine {
	private static ExecutorService executorService = null;

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
	public static void run(Class<?> mainClass, Runnable disclaimerTask, Runnable tasks, String[] arguments) {
		Foundation.credits();
		new Thread(disclaimerTask).start();
		if ( ! ScannerHelper.isApplicationClass(mainClass) ) {
			LoggerHelper.logError("AnnotationEngineAnnotationEngine::run", String.format("Null or not 'Application' class for bootstrap: %s!!", mainClass), null);
			return;
		}
		
		ScannerHelper.collectModuleScanners();

		ScannerHelper.executeScannerMainClasses();
		
		List<? extends Annotation> annotations = ScannerHelper.getApplicationClassAnnotations(mainClass);
		
		//Running extra annotation seek tasks
		new Thread(tasks).start();

		annotations
		.stream()
		.filter( ann -> ann != null && Application.class.isAssignableFrom(ann.getClass()) )
		.forEach(ann -> {
			Application app = (Application)ann;
			ApplicationType appType = app.scope();
			if ( appType == ApplicationType.CONSOLE && app.hasCommandShell()) {
				List<Class<? extends ApplicationConsole>> listofConsoles = ScannerHelper.collectSubTypesOf(ScannerHelper.getRefletionsByPackages(new String[0]), ApplicationConsole.class);
				if ( listofConsoles.size() == 0 ) {
					
				} else {
					if ( executorService != null ) {
						return;
					}
					executorService = Executors.newFixedThreadPool(listofConsoles.size());
					listofConsoles.forEach(consoleClass -> {
						executorService.execute(new Runnable() {
							
							@Override
							public void run() {
								try {
									ApplicationConsole console = consoleClass.newInstance();
									console.run(arguments);
								} catch (Exception ex) {
									LoggerHelper.logError("AnnotationEngine::run [console thread run]", String.format("Unable to execute console: %s", consoleClass), ex);
								}
							}
						});
					});
					executorService.shutdown();
					executorService = null;
				}
			}
		});
	
	}
}
