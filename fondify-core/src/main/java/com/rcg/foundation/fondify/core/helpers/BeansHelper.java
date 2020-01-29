/**
 * 
 */
package com.rcg.foundation.fondify.core.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.rcg.foundation.fondify.core.constants.ArgumentsConstants;
import com.rcg.foundation.fondify.core.exceptions.InitializationException;
import com.rcg.foundation.fondify.core.typings.autorun.AutoFullScanTypesDescriptor;
import com.rcg.foundation.fondify.core.typings.autorun.Autorun;
import com.rcg.foundation.fondify.core.typings.autorun.AutorunPhasesActuatorProvider;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class BeansHelper {

	public static ExecutorService executorService = null;
	
	private static final Map<Class<?>, List<Class<?>>> globalTypesClasses = new ConcurrentHashMap<>(0);
	
	private static final Queue<Class<?>> globalTypesInterfaces = new ConcurrentLinkedQueue<>();

	/**
	 * 
	 */
	private BeansHelper() {
		throw new IllegalStateException("BeansHelper::constructor - unable to instantiate utility class!!");
	}
	
	protected static final void addNewInterfaceImplementation(Class<?> iface) {
		if ( iface != null ) {
			globalTypesInterfaces.add(iface);
		} else {
			throw new InitializationException("Class is null, so cannot add given class object.");
		}
	}

	public static final Collection<Class<?>> getImplementatedSubtypesOf(Class<?> iface) {
		if ( iface != null ) {
			return globalTypesClasses.getOrDefault(iface, new ArrayList<>(0));
		} else {
			throw new InitializationException("Class is null, so cannot process discovery for subtypes.");
		}
	}


	public static final Optional<Class<?>> getImplementatedSubtypeOf(Class<?> iface) {
		if ( iface != null ) {
			return globalTypesClasses.getOrDefault(iface, new ArrayList<>(0)).stream().findFirst();
		} else {
			throw new InitializationException("Class is null, so cannot process discovery for subtypes.");
		}
	}
	
	public static final void loadAllInterfacesImplementations() {
		globalTypesInterfaces.addAll(
			collectSubTypesOf(getRefletionsByPackages(new String[0]), Arrays.asList(new Class<?>[] {AutoFullScanTypesDescriptor.class}))
			.parallelStream()
			.map( descriptorClass -> {
				try {
					AutoFullScanTypesDescriptor descriptor = (AutoFullScanTypesDescriptor) descriptorClass.newInstance();
					return descriptor.getGenericInterfacesList();
				} catch (Exception e) {
					LoggerHelper.logError("BeansHelper::loadAllInterfacesImplementations", String.format("Unable to make instance of class: %s type of %s", descriptorClass!= null ? descriptorClass.getName() : "<NULL>", AutoFullScanTypesDescriptor.class.getName()), e);
				}
				return new ArrayList<Class<?>>(0);
			})
			.flatMap(Collection::stream)
			.distinct()
			.collect(Collectors.toList())
		);
		List<Class<?>> listOfElementClasses = collectSubTypesOf(getRefletionsByPackages(new String[0]), globalTypesInterfaces);
		globalTypesInterfaces
		.parallelStream()
		.forEach( iface -> {
			List<Class<?>> classList = listOfElementClasses
					.stream()
					.filter( cls -> { 
						return ! cls.isInterface() && iface.isAssignableFrom(cls);
					})
					.collect(Collectors.toList());
			globalTypesClasses.put( iface,  classList );
			LoggerHelper.logTrace("BeansHelper::loadAllInterfacesImplementations", "Size for <" + iface.getName() + "> = " + classList.size());
		});
	}

	@SuppressWarnings("unchecked")
	public static final <T> List<T> getImplementedTypes(Class<T> cls) {
		
		return globalTypesClasses
			.entrySet()
			.stream()
			.filter( entry -> entry.getKey() == cls )
			.map(entry -> entry.getValue())
			.flatMap(List::stream)
			.filter( anyCls -> ! anyCls.isInterface() )
			.map(anyCls -> {
				try {
					return (T)anyCls.newInstance();
				} catch (Exception e) {
					LoggerHelper.logError("AnnotationHelper::getImplementedTypes", String.format("Unable to creae instance of class: %s", ""+anyCls), null);
				}
				return (T)null;
			})
			.filter( instance -> instance != null )
			.collect(Collectors.toList());
			
	}

	@SuppressWarnings("unchecked")
	public static final <T> Optional<T> getImplementedType(Class<T> cls) {
		
		return globalTypesClasses
			.entrySet()
			.stream()
			.filter( entry -> entry.getKey() == cls )
			.map(entry -> entry.getValue())
			.flatMap(List::stream)
			.filter( anyCls -> ! anyCls.isInterface() )
			.map(anyCls -> {
				try {
					return (T)anyCls.newInstance();
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
	protected static final <T> List<Class<? extends T>> collectSubTypesOf(ConfigurationBuilder builder,
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
	protected static final List<Class<?>> collectSubTypesOf(ConfigurationBuilder builder, Collection<Class<?>> superClassList) {
		List<Class<?>> classes = new ArrayList<>(0);
		Reflections r = new Reflections(builder.addScanners(new SubTypesScanner()));
		superClassList
		.parallelStream()
		.forEach(superClass -> {
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
	
	public static synchronized final void executeAutorunComponents(String threadName) {
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
		if ( numeberOfActiveProcesses == 0 ) {
			numeberOfActiveProcesses = 1;
		}
		LoggerHelper.logTrace("BeansHelper::executeAutorunComponents", "Executing Autorun with number of threads: " + numeberOfActiveProcesses);
		executorService = Executors.newFixedThreadPool(numeberOfActiveProcesses);
		AutorunPhasesActuatorProvider provider = AutorunPhasesActuatorProvider.getInstance();
		autorunComponentsList
			.forEach( autorun -> {
				executorService.execute(new Runnable() {
					
					@Override
					public void run() {
						try {
							GenericHelper.fixCurrentThreadStandardName(threadName);
							provider.actuateInitializerForAutorun(autorun);
							autorun.run(ArgumentsHelper.getArguments());
							provider.actuateFinalizerForAutorun(autorun);
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
					GenericHelper.sleepThread(2500L);
				}
				BeansHelper.executorService = null;
			}
		}).start();
	}

	@SuppressWarnings("unchecked")
	public static final <T> T getParameterAnnotation(Parameter parameter, Class<T> classT) {
		Optional<T> tOpt = Arrays.asList(parameter.getDeclaredAnnotations())
				.stream()
				.filter( ann -> classT.isAssignableFrom(ann.getClass()))
				.map( ann -> (T) ann )
				.findFirst();
		if ( tOpt.isPresent() ) {
			return tOpt.get();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static final <T> T getFieldAnnotation(Field field, Class<T> classT) {
		Optional<T> tOpt = Arrays.asList(field.getDeclaredAnnotations())
				.stream()
				.filter( ann -> classT.isAssignableFrom((Class<Annotation>)ann.getClass()) )
				.map( ann -> (T) ann )
				.findFirst();
		if ( tOpt.isPresent() ) {
			return tOpt.get();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static final <T> T getMethodAnnotation(Method method, Class<T> classT) {
		Optional<T> tOpt = Arrays.asList(method.getDeclaredAnnotations())
				.stream()
				.filter( ann -> classT.isAssignableFrom((Class<Annotation>)ann.getClass()) )
				.map( ann -> (T) ann )
				.findFirst();
		if ( tOpt.isPresent() ) {
			return tOpt.get();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static final <T> T getClassAnnotation(Class<?> classX, Class<T> classT) {
		Optional<T> tOpt = Arrays.asList(classX.getDeclaredAnnotations())
				.stream()
				.filter( ann -> classT.isAssignableFrom((Class<Annotation>)ann.getClass()) )
				.map( ann -> (T) ann )
				.findFirst();
		if ( tOpt.isPresent() ) {
			return tOpt.get();
		}
		return null;
	}
	
	public static final boolean isProxyClass(Class<?> cls) {
		return Proxy.isProxyClass(cls);
	}
	
	public static final boolean isProxyObject(Object obj) {
		return Proxy.isProxyClass(obj.getClass());
	}
	
	public static final List<Class<?>> getProxyClasses(Class<?> cls) {
		List<Class<?>> list = new ArrayList<>(0);
		if ( isProxyClass(cls) ) {
			list.addAll(
					Arrays.asList(cls.getInterfaces())
			);
			
		}
		return list;
	}

}
