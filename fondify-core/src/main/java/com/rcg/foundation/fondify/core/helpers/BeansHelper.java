/**
 * 
 */
package com.rcg.foundation.fondify.core.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.rcg.foundation.fondify.core.constants.ArgumentsConstants;
import com.rcg.foundation.fondify.core.typings.autorun.Autorun;
import com.rcg.foundation.fondify.core.typings.autorun.AutorunPhasesActuatorProvider;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class BeansHelper {

	public static ExecutorService executorService = null;

	/**
	 * 
	 */
	private BeansHelper() {
		throw new IllegalStateException("BeansHelper::constructor - unable to instantiate utility class!!");
	}

	public static final <T> List<T> getImplementedTypes(Class<T> cls) {
		
		return collectSubTypesOf(getRefletionsByPackages(new String[0]), cls)
			.stream()
			.filter( anyCls -> ! anyCls.isInterface() )
			.map(anyCls -> {
				try {
					return anyCls.newInstance();
				} catch (Exception e) {
					LoggerHelper.logError("AnnotationHelper::getImplementedTypes", String.format("Unable to creae instance of class: %s", ""+anyCls), null);
				}
				return (T)null;
			})
			.filter( instance -> instance != null )
			.collect(Collectors.toList());
			
	}

	public static final <T> Optional<T> getImplementedType(Class<T> cls) {
		
		return collectSubTypesOf(getRefletionsByPackages(new String[0]), cls)
			.stream()
			.filter( anyCls -> ! anyCls.isInterface() )
			.map(anyCls -> {
				try {
					return anyCls.newInstance();
				} catch (Exception e) {
					LoggerHelper.logError("AnnotationHelper::getImplementedType", String.format("Unable to creae instance of class: %s", ""+anyCls), null);
				}
				return (T)null;
			})
			.filter( instance -> instance != null )
			.findFirst();
			
	}

	/**
	 * Create a Reflections bases on input packages
	 * 
	 * @param packages
	 * @return
	 */
	public static final ConfigurationBuilder getRefletionsByPackages(String[] packages) {
		List<String> list = new ArrayList<>(0);
		list.addAll(Arrays.asList(packages));
		return getRefletionsByPackages(list);
	}

	/**
	 * Collect all subTypes of provided interface or class one.
	 * 
	 * @param <T>
	 * @param builder    Base package scanner builder
	 * @param superClass implemented class or interface
	 * @return list if sub types of provided one
	 */
	public static final <T> List<Class<? extends T>> collectSubTypesOf(ConfigurationBuilder builder,
			Class<T> superClass) {
		List<Class<? extends T>> classes = new ArrayList<Class<? extends T>>(0);
		Reflections r = new Reflections(builder.addScanners(new SubTypesScanner()));
		classes.addAll(r.getSubTypesOf(superClass));
		return classes;
	}

	/**
	 * Collect all subTypes of provided interface or class one.
	 * 
	 * @param <T>
	 * @param builder    Base package scanner builder
	 * @param superClass implemented class or interface
	 * @return list if sub types of provided one
	 */
	public static final List<Class<?>> collectSubTypesOf(ConfigurationBuilder builder, List<Class<?>> superClassList) {
		List<Class<?>> classes = new ArrayList<>(0);
		Reflections r = new Reflections(builder.addScanners(new SubTypesScanner()));
		superClassList.forEach(superClass -> {
			classes.addAll(r.getSubTypesOf(superClass));
		});
		return classes;
	}

	/**
	 * Create a Reflections bases on input packages
	 * 
	 * @param packages
	 * @return
	 */
	public static final ConfigurationBuilder getRefletionsByPackages(Collection<String> packages) {
		ConfigurationBuilder config = new ConfigurationBuilder();
		if (packages != null && packages.size() > 0) {
			List<java.net.URL> listOfClassPathRefs = new ArrayList<java.net.URL>(0);
			listOfClassPathRefs.addAll(
					packages
					.stream()
					.filter(pkg -> pkg != null && !pkg.isEmpty())
					.map(pkg -> {
						ArrayList<java.net.URL> classpathUrls = new ArrayList<java.net.URL>(0);
						try {
							classpathUrls.addAll( 
									ClasspathHelper.forPackage(pkg)
							);
						} catch (Exception e) {
							
						}
						return classpathUrls;
					})
					.flatMap(List::stream)
					.distinct()
					.collect(Collectors.toList())
			);
			if ( listOfClassPathRefs.size() > 0 ) {
				config.addUrls(
						listOfClassPathRefs
				);
			} else {
				LoggerHelper.logWarn("ScannerHelper::getRefletionsByPackages", 
						String.format("Unable to discover classpath packages: %s, then loading full classpath urls", Arrays.toString(packages.toArray())), 
						null);
				config.addUrls(ClasspathHelper.forJavaClassPath());
			}
	
		} else {
			config.addUrls(ClasspathHelper.forJavaClassPath());
		}
		return config;
	}
	
	public static synchronized final void executeAutorunComponents(UUID sessionId) {
		if ( executorService != null ) {
			LoggerHelper.logWarn("BeansHelper::executeAutorunComponents", 
					"Autorun execution still in progress. Skipping the autorun execution request!!",
					null);
			return;
		}
		List<Autorun> autorunComponentsList = getImplementedTypes(Autorun.class);
		int numeberOfActiveProcesses = Runtime.getRuntime().availableProcessors();
		if ( ArgumentsHelper.hasArgument(ArgumentsConstants.MAX_AUTORUN_THREADS) ) {
			String numProcsStr = ArgumentsHelper.getArgument(ArgumentsConstants.MAX_AUTORUN_THREADS);
			try {
				numeberOfActiveProcesses = Integer.parseInt(numProcsStr);
			} catch (NumberFormatException e) {
				LoggerHelper.logError("BeansHelper::executeAutorunComponents", 
								String.format("Invalid numeric value for argument %s -> Not a numer : <%s>!!", "max.autorun.threads", numProcsStr),
								e);
			}
		}
		if ( ArgumentsHelper.hasArgument(ArgumentsConstants.UNLIMITED_AUTORUN_THREADS) ) {
			if ( ArgumentsHelper.getArgument(ArgumentsConstants.UNLIMITED_AUTORUN_THREADS).equalsIgnoreCase("true") ) {
				numeberOfActiveProcesses = autorunComponentsList.size();
			}
		}
		executorService = Executors.newFixedThreadPool(numeberOfActiveProcesses);
		AutorunPhasesActuatorProvider provider = AutorunPhasesActuatorProvider.getInstance();
		autorunComponentsList
			.forEach( autorun -> {
				executorService.execute(new Runnable() {
					
					@Override
					public void run() {
						try {
							provider.actuateInitializerForAutorun(autorun, sessionId);
							autorun.run(ArgumentsHelper.getArguments());
							provider.actuateFinalizerForAutorun(autorun, sessionId);
						} catch (Exception e) {
							LoggerHelper.logError("BeansHelper::executeAutorunComponents", 
									String.format("Unable to execute autrun for element %s sue to ERRORS!!", autorun != null ? autorun.getClass().getName() : "<NULL>"), 
									e);
						}
					}
				});
			});
		// Request shutdown of completed tasks!!
		executorService.shutdown();
		// Execute thread service cleaning asynchronous thread!!
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while ( BeansHelper.executorService != null && ! BeansHelper.executorService.isShutdown() ) {
					try {
						Thread.sleep(2500l);
					} catch (InterruptedException e) {
						//NOTHING TO DO HERE
						;
					}
				}
				BeansHelper.executorService = null;
			}
		}).start();
	}

}
