/**
 * 
 */
package com.rcg.foundation.fondify.annotations.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.rcg.foundation.fondify.annotations.annotations.Application;
import com.rcg.foundation.fondify.annotations.annotations.ComponentsScan;
import com.rcg.foundation.fondify.annotations.annotations.Configuration;
import com.rcg.foundation.fondify.annotations.annotations.DependsOn;
import com.rcg.foundation.fondify.annotations.annotations.FastBoot;
import com.rcg.foundation.fondify.annotations.annotations.ModuleScannerConfig;
import com.rcg.foundation.fondify.annotations.contants.AnnotationConstants;
import com.rcg.foundation.fondify.annotations.typings.BeanDefinition;
import com.rcg.foundation.fondify.annotations.typings.MethodExecutor;
import com.rcg.foundation.fondify.core.exceptions.MappingException;
import com.rcg.foundation.fondify.core.exceptions.ProcessException;
import com.rcg.foundation.fondify.core.exceptions.ScannerException;
import com.rcg.foundation.fondify.core.functions.Matcher;
import com.rcg.foundation.fondify.core.functions.Processor;
import com.rcg.foundation.fondify.core.helpers.BeansHelper;
import com.rcg.foundation.fondify.core.registry.ComponentsRegistry;
import com.rcg.foundation.fondify.core.registry.typings.ComponentRegistryItem;
import com.rcg.foundation.fondify.core.typings.AnnotationDeclaration;
import com.rcg.foundation.fondify.core.typings.AnnotationEngineInitializer;
import com.rcg.foundation.fondify.core.typings.AnnotationExecutor;
import com.rcg.foundation.fondify.core.typings.AnnotationTypesCollector;
import com.rcg.foundation.fondify.core.typings.ExecutionAnswer;
import com.rcg.foundation.fondify.core.typings.ModuleMain;
import com.rcg.foundation.fondify.core.typings.ModuleScanner;
import com.rcg.foundation.fondify.core.typings.registry.AnnotationBeanActuatorProvider;
import com.rcg.foundation.fondify.reflections.Reflections;
import com.rcg.foundation.fondify.reflections.typings.ClassPathConfigBuilder;
import com.rcg.foundation.fondify.reflections.typings.MatchDescriptor;
import com.rcg.foundation.fondify.utils.helpers.ArgumentsHelper;
import com.rcg.foundation.fondify.utils.helpers.GenericHelper;
import com.rcg.foundation.fondify.utils.helpers.LoggerHelper;

/**
 * Utility class that provides features for helping with Java artifacts Scan and
 * {@link Annotation} list recovery from the Java Class Path resources (any
 * folders/jars in the entire class-path artifacts or only in a subset of
 * packages present in one or more folders/jars!!).
 * 
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ScannerHelper {

	/**
	 * Registry for {@link ModuleScanner} entities, used to start up and track
	 * different modules implemented and added to the JVM class path.
	 */
	public static ComponentRegistryItem<ModuleScanner> moduleRegistry = new ComponentRegistryItem<>();
	private static ExecutorService executorService = null;

	public static String annotationDescriptorsRegistryKey = "AnnotationDescriptorsKey";
	public static final List<Class<? extends Annotation>> baseAnnotationTypes = new ArrayList<>(0);
	public static final Map<Class<Annotation>, List<AnnotationDeclaration>> annotationsDeclarationMaps = new ConcurrentHashMap<>(
			0);

	/**
	 * Denied access constructor
	 */
	protected ScannerHelper() {
		throw new IllegalStateException("ScannerHelper::constructor - unable to instantiate utility class!!");
	}
	
	public static final List<String> providedBaseAnnotationNames() {
		return annotationsDeclarationMaps
				.values()
				.stream()
				.flatMap(List::stream)
				.map(entry -> entry.getAnnotationDeclarationClass().getName())
				.collect(Collectors.toList());
	}

	public static final boolean isApplicationClass(Class<?> mainClass) {
		if (mainClass == null) {
			return false;
		}
		return BeansHelper.getClassAnnotation(mainClass, Application.class) != null;
	}

	public static final List<? extends Annotation> getApplicationClassAnnotations(Class<?> mainClass) {
		List<Annotation> list = new ArrayList<>(0);
		if (mainClass == null) {
			return list;
		}
		list.addAll(Arrays.asList(mainClass.getDeclaredAnnotations()));
		return list;
	}
	
	protected static final boolean isRunningFromJar() {
		return (""+ScannerHelper.class.getResource("ScannerHelper.class")).toLowerCase().startsWith("jar");
	}

	protected static List<String> DEFAULT_SCANNERS = new ArrayList<>(0);

	/**
	 * Execute the Scanner Annota
	 * 
	 * @param classes
	 */
	protected static final void executeModuleMainClasses(Class<? extends ModuleMain>[] classes,
			Collection<AnnotationDeclaration> annotations) {
		if (classes != null && classes.length > 0) {
			Arrays.asList(classes).forEach(cls -> {
				try {
					ModuleMain main = cls.newInstance();
					if (ArgumentsHelper.debug) {
						LoggerHelper.logTrace("ScannerHelper::executeModuleMainClasses",
								String.format("Executing main class : %s", main.getClass().getName()));
					}
					executorService.execute(new Runnable() {

						@Override
						public void run() {
							try {
								LoggerHelper.logTrace("ScannerHelper::executeModuleMainClasses [thread]",
										String.format("Executing main class %s ...", main.getClass().getName()));
								main.execute(annotations);

								LoggerHelper.logTrace("ScannerHelper::executeModuleMainClasses [thread]",
										String.format("Main class %s initialized : state %s!!",
												main.getClass().getName(), main.isRunning() ? "RUNNING" : "COMPLETE"));
								// In case the main class is a daemon we plan a shutdown hook to
								// Shutdown/Abort the class execution.
								if (main.isRunning()) {
									Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

										@Override
										public void run() {
											try {
												if (main.isRunning()) {
													main.abort();
												}
											} catch (Exception ex) {
												String message = String.format(
														"Error in Shutdown/Abort of Module Main Class : %s",
														main.getClass().getName());
												LoggerHelper.logError(
														"ScannerHelper::executeModuleMainClasses::shutdownHook",
														message, ex);
											}
										}
									}));
								}
							} catch (Exception e) {
								String message = String.format("Unable to execute ModuleScanner main classes : %s",
										cls.getCanonicalName());
								LoggerHelper.logError("ScannerHelper::executeModuleMainClasses", message, e);
								throw new ScannerException(message, e);
							}
						}
					});
				} catch (Exception e) {
					String message = String.format("Unable to execute ModuleScanner main classes : %s",
							cls.getCanonicalName());
					LoggerHelper.logError("ScannerHelper::executeModuleMainClasses [thread]", message, e);
					throw new ScannerException(message, e);
				}
			});
		}
	}

	/**
	 * 
	 */
	public static final void executeScannerMainClasses(Set<Class<? extends ModuleMain>[]> scanners,
			Collection<AnnotationDeclaration> annotations) {
		if (executorService != null) {
			LoggerHelper.logWarn("ScannerHelper::executeScannerMainClasses", "Main Class Execution in progress...",
					null);
			return;
		}
		long mainClassesCount = scanners.stream().map(entry -> Arrays.asList(entry)).flatMap(List::stream).count();
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		scanners.stream().forEach(scc -> {
			try {
				executeModuleMainClasses(scc, annotations);
			} catch (Exception e) {
				String message = String.format("Unable to execute ModuleScanner main classes related to : %s",
						scc.getClass().getName());
				LoggerHelper.logError("ScannerHelper::executeScannerMainClasses", message, e);
				throw new ScannerException(message, e);
			}
		});
		executorService.shutdown();
		executorService = null;
		if (mainClassesCount > 0)
			Runtime.getRuntime().runFinalization();
	}

	/**
	 * Collect {@link ModuleScanner}s in some or all JVM packages, based on internal
	 * list of parameters and on a full scan on all packages, in which the
	 * ModuleMain, part of the parameters in the
	 * {@link com.rcg.foundation.fondify.annotation.annotations.streams.streamio.core.annotations.ModuleScannerConfig}
	 * annotations recovering the declared list of ModuleMain implementations,
	 * declared for the module.
	 */
	public static final void collectModuleScanners(Matcher<Class<?>> annotationMatcher,
			Class<? extends Annotation> moduleAnnotationClass, List<Class<? extends ModuleScanner>> inlcudeClasses,
			List<Class<? extends ModuleScanner>> excludeClasses) {
		final List<ModuleScanner> list = new ArrayList<>(0);
		List<ModuleScanner> excludeList = new ArrayList<>(0);
		DEFAULT_SCANNERS.forEach(scc -> {
			try {
				list.add((ModuleScanner) Class.forName(scc).newInstance());
			} catch (Exception e) {
				String message = String.format("Unable to load class for DEFAULT ModuleScanner with class : %s", scc);
				LoggerHelper.logError("ScannerHelper::collectModuleScanners", message, e);
			}
		});
		
		List<Class<? extends ModuleScanner>> moduleScannerTypes = BeansHelper.collectSubTypesOf(ClassPathConfigBuilder.start().disablePersistenceOfData(), ModuleScanner.class);
		LoggerHelper.logTrace("ScannerHelper::collectModuleScanners", "Collecting for Module Scanner classes ...");
		LoggerHelper.logTrace("ScannerHelper::collectModuleScanners",
				String.format("Collecting %s modules ...", "" + moduleScannerTypes.size()));
		List<Class<? extends ModuleScanner>> validModuleScannerTypes = moduleScannerTypes.stream().filter(cls -> {
			if (annotationMatcher.match(cls)) {
				if (DEFAULT_SCANNERS.contains(cls.getName()))
					return false;
				return true;
			}
			LoggerHelper.logError("ScannerHelper::collectModuleScanners",
					String.format(
							"Uable to add class %s, bacause it's missing of ModuleScanner configuration annotation"),
					null);
			return false;
		}).collect(Collectors.toList());
		LoggerHelper.logTrace("ScannerHelper::collectModuleScanners",
				String.format("Collection: passed %s modules ...", "" + validModuleScannerTypes.size()));
		validModuleScannerTypes.forEach(cls -> {
			try {
				if (list.stream().filter(mdl -> mdl.getClass() == cls).count() == 0) {
					list.add((ModuleScanner) cls.newInstance());
					LoggerHelper.logTrace("ScannerHelper::collectModuleScanners",
							"Found scanner : " + cls.getCanonicalName());
				} else {
					LoggerHelper.logTrace("ScannerHelper::collectModuleScanners",
							String.format("Scanner class instance already present in registry: %s", cls.getName()));
				}
			} catch (InstantiationException | IllegalAccessException e) {
				String message = String.format(
						"Class Instantiation Error :: Unable to instantiate ModuleScanner object for class : %s",
						cls.getCanonicalName());
				LoggerHelper.logError("ScannerHelper::collectModuleScanners", message, e);
				throw new ScannerException(message);
			} catch (Exception e) {
				String message = String.format(
						"Generic Error :: Unable to instantiate ModuleScanner object for class : %s",
						cls.getCanonicalName());
				LoggerHelper.logError("ScannerHelper::collectModuleScanners", message, e);
				throw new ScannerException(message);
			}
		});
		LoggerHelper.logTrace("ScannerHelper::collectModuleScanners", "Scanning for Module Scanner classes complete!!");
		LoggerHelper.logTrace("ScannerHelper::collectModuleScanners", "Scanning for Module Scan classes ...");
		inlcudeClasses.stream().filter(cls -> {
			String name = cls.getCanonicalName();
			return !DEFAULT_SCANNERS.contains(name);
		}).forEach(mdlCls -> {
			try {
				if (!DEFAULT_SCANNERS.contains(mdlCls.getCanonicalName())
						&& list.stream().filter(mdl -> mdl.getClass() == mdlCls).count() == 0)
					list.add(mdlCls.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				String message = String.format(
						"Class Instantiation Error :: Unable to instantiate ModulesScan (inclusion) object for class : %s",
						mdlCls.getCanonicalName());
				LoggerHelper.logError("ScannerHelper::collectModuleScanners", message, e);
				throw new ScannerException(message);
			} catch (Exception e) {
				String message = String.format(
						"Generic Error :: Unable to instantiate (inclusion) ModulesScan object for class : %s",
						mdlCls.getCanonicalName());
				LoggerHelper.logError("ScannerHelper::collectModuleScanners", message, e);
				throw new ScannerException(message, e);
			}
		});
		excludeClasses.stream().filter(cls -> {
			String name = cls.getCanonicalName();
			return !DEFAULT_SCANNERS.contains(name);
		}).forEach(mdlCls -> {
			try {
				if (excludeList.stream().filter(mdl -> mdl.getClass() == mdlCls).count() == 0)
					excludeList.add(mdlCls.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				String message = String.format(
						"Class Instantiation Error :: Unable to instantiate ModulesScan (exclusion) object for class : %s",
						mdlCls.getCanonicalName());
				LoggerHelper.logError("ScannerHelper::collectModuleScanners", message, e);
				throw new ScannerException(message);
			} catch (Exception e) {
				String message = String.format(
						"Generic Error :: Unable to instantiate ModulesScan (exclusion) object for class : %s",
						mdlCls.getCanonicalName());
				LoggerHelper.logError("ScannerHelper::collectModuleScanners", message, e);
				throw new ScannerException(message, e);
			}
		});
		LoggerHelper.logTrace("ScannerHelper::collectModuleScanners", "Scanning for Module Scan classes complete!!");
		LoggerHelper.logTrace("ScannerHelper::collectModuleScanners",
				"Collecting exclusion class list and filter allowed scanners...");
		List<Class<?>> exclusionClasses = excludeList.stream().map(excl -> excl.getClass())
				.collect(Collectors.toList());
		list.stream().filter(incl -> !exclusionClasses.contains(incl.getClass()))
				.forEach(ms -> moduleRegistry.registerEntity(ms.getClass().getName(), ms));
		LoggerHelper.logTrace("ScannerHelper::collectModuleScanners",
				"Collection of exclusion class list and filtering of allowed scanners complete!!");

	}


	/**
	 * Collect {@link Annotation}s in some or all JVM packages, based on a list of
	 * {@link Annotation} classes
	 * 
	 * @param builder
	 * @param annotations
	 * @return
	 */
	public static final Map<Class<Annotation>, List<AnnotationDeclaration>> collectAnnotations(
			ClassPathConfigBuilder builder, List<Class<Annotation>> annotations) {
		Map<Class<Annotation>, List<AnnotationDeclaration>> map = new HashMap<>(0);

		Reflections refl = null; 
		
		try {
			refl = Reflections.getSigletonInstance(builder.build());
		} catch (NullPointerException e) {
			String message = "Error during recovery of Reflections singleton instance!!";
			LoggerHelper.logError("ScannerHelper::collectAnnotations", message, e);
			throw new ProcessException(message, e);
		}

		final Reflections r = refl; 

		annotations.forEach(annotation -> {
			// try in types
			List<AnnotationDeclaration> annotationsDeclarationsList = new ArrayList<>(0);
			
			r.getTypesAnnotatedWith(annotation)
				.stream()
				.map( jce -> jce.getMatchClass() )
				.forEach(clz -> {
					AnnotationDeclaration declaration = new AnnotationDeclaration(annotation, clz, null, null, null);
					if (validateAnnotationDeclaration(declaration))
						annotationsDeclarationsList.add(declaration);
				});
			r.getFieldsAnnotatedWith(annotation)
				.stream()
				.map( jce -> jce.getMatchField() )
				.forEach(fld -> {
					AnnotationDeclaration declaration = new AnnotationDeclaration(annotation, fld.getDeclaringClass(), fld,
							null, null);
					if (validateAnnotationDeclaration(declaration))
						annotationsDeclarationsList.add(declaration);
				});
			r.getMethodsAnnotatedWith(annotation)
				.stream()
				.map( jce -> jce.getMatchMethod() )
				.forEach(m -> {
					AnnotationDeclaration declaration = new AnnotationDeclaration(annotation, m.getDeclaringClass(), null,
							m, null);
					if (validateAnnotationDeclaration(declaration))
						annotationsDeclarationsList.add(declaration);
				});
			r.getMethodParametersAnnotatedWith(annotation)
				.forEach(md -> {
					Parameter p = md.getMatchParameter();
					Method m = md.getMatchMethod();
					if ( p.getAnnotationsByType(annotation) != null ) {
						AnnotationDeclaration declaration = new AnnotationDeclaration(annotation, m.getDeclaringClass(),
								null, m, p);
						if (validateAnnotationDeclaration(declaration))
							annotationsDeclarationsList.add(declaration);
						
					}
				});
			map.put(annotation, annotationsDeclarationsList);
		});
		return map;
	}

	/**
	 * @param declaration
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected static final boolean validateAnnotationDeclaration(AnnotationDeclaration declaration) {
		if (declaration == null || declaration.getAnnotation() == null) {
			return false;
		}
		Annotation annotation = declaration.getAnnotation();
		if (annotation.annotationType().isAnnotationPresent(DependsOn.class)) {
			// Annotation has dependencies!!
			DependsOn dependsOn = BeansHelper.getClassAnnotation(annotation.annotationType(), DependsOn.class);
			Class<?>[] annotationClasses = dependsOn.value();
			List<Class<? extends Annotation>> requiredAnnotations = new ArrayList<>(0);
			for (Class<?> clz : annotationClasses) {
				if (Annotation.class.isAssignableFrom(clz)) {
					requiredAnnotations.add((Class<Annotation>) clz);
				}
			}
			List<Class<? extends Annotation>> presentAnnotations = Arrays
					.asList(annotation.annotationType().getDeclaredAnnotations()).stream().map(a -> a.annotationType())
					.collect(Collectors.toList());
			long missingAnnotations = requiredAnnotations.stream().filter(ra -> !presentAnnotations.contains(ra))
					.count();
			return missingAnnotations == 0;
		}

		return true;
	}

	public static final Optional<ModuleScanner> getModuleByPackage(String basePackage) {
		List<ModuleScanner> moduleScanners = moduleRegistry.getEntitiesMap().values().stream()
				.collect(Collectors.toList());
		return moduleScanners.stream().filter(ms -> getPackageName(ms.getClass()).contains(basePackage)).findFirst();
	}
	
	private static final List<Annotation> discoverAnnotationsInPackage(Class<?> referenceClass, List<Class<? extends Annotation>> typeAnnotationClasses) {
		List<Annotation> annotations = new ArrayList<Annotation>(0);
		
		typeAnnotationClasses.forEach( annCls -> {
			if ( referenceClass.getDeclaredAnnotation(annCls) != null ) { 
				annotations.addAll(Arrays.asList(referenceClass.getDeclaredAnnotationsByType(annCls)));
			}
		});
		return annotations;
	}
	
	private static final List<Annotation> loadBaseAnnotations(Class<?> applicationClass) {
		if ( ArgumentsHelper.traceLow ) {
			LoggerHelper.logTrace("ScannerHelper::initializeScanningPackagesFilter", "Loading  Configuration and ComponentsScan annotations ...");
		}
		List<Annotation> results = new ArrayList<>(0);
		if ( applicationClass != null && applicationClass.getDeclaredAnnotation(FastBoot.class) != null ) {
			if ( applicationClass.getDeclaredAnnotation(Application.class) == null ) {
				LoggerHelper.logWarn("ScannerHelper::loadBaseAnnotations", "Found FastBoot in Main Class but no Application annotation is present. Fast Boot process will be skipped.", null);
			} else {
				FastBoot fastBoot = applicationClass.getAnnotation(FastBoot.class);
				Class<?>[] importPaths = fastBoot.value();
				if ( importPaths == null || importPaths.length == 0 ) {
					LoggerHelper.logWarn("ScannerHelper::loadBaseAnnotations", "Found FastBoot in Main Class but IMPORT PATH is not present, no package declared. Fast Boot process will be skipped.", null);
				} else {
					List<Class<? extends Annotation>> annotations = new ArrayList<>(0);
					annotations.add(ComponentsScan.class);
					annotations.add(Configuration.class);
					results.addAll(
							Arrays.asList(importPaths)
							.stream()
							.map( referenceClass -> discoverAnnotationsInPackage(referenceClass, annotations) )
							.flatMap(List::stream)
							.collect(Collectors.toList())
					);
					if ( ArgumentsHelper.traceLow ) {
						LoggerHelper.logTrace("ScannerHelper::initializeScanningPackagesFilter", "Found (from application definition) Configuration and ComponentsScan annotations: " + results.size());
					}
					return results;
				}
				
			}
			
		}
		List<Class<? extends Annotation>> annotations = new ArrayList<>(0);
		annotations.add(ComponentsScan.class);
		annotations.add(Configuration.class);
		
		Reflections r = Reflections.newReflections(ClassPathConfigBuilder.start().disablePersistenceOfData().build());
		List<MatchDescriptor> typesList = r.getTypesAnnotatedWith(annotations);
		results.addAll(
				typesList
				.stream()
				.map( jce -> jce.getMatchClass() )
				.distinct()
				.map( referenceClass -> discoverAnnotationsInPackage(referenceClass, annotations) )
				.flatMap(List::stream)
				.collect(Collectors.toList())
		);
		if ( ArgumentsHelper.traceLow ) {
			LoggerHelper.logTrace("ScannerHelper::initializeScanningPackagesFilter", "Found (from JVM ClassPath Scan) ->  Configuration and ComponentsScan annotations: " + results.size());
		}
		return results;
	}
	
	public static ClassPathConfigBuilder DEFAULT_APP_BUILDER = null; 

	public static final void initializeScanningPackagesFilter(Class<?> applicationClass) {
		ClassPathConfigBuilder builder = ClassPathConfigBuilder.start();
		if ( ArgumentsHelper.traceLow ) {
			LoggerHelper.logTrace("ScannerHelper::initializeScanningPackagesFilter", "Initializing default scanner configuration...");
		}
		loadBaseAnnotations(applicationClass)
			.forEach(ann -> {
				if (  Configuration.class.isAssignableFrom(ann.getClass()) ) {
					Configuration configurationAnnotation = (Configuration)ann;
					Arrays.asList(configurationAnnotation.includeJvmEntries())
						.forEach(entry -> builder.includeClassPathEntryByName(entry));
					Arrays.asList(configurationAnnotation.excludesJvmEntries())
					.forEach(entry -> builder.excludeClassPathEntryByName(entry));
				} else if ( ComponentsScan.class.isAssignableFrom(ann.getClass()) ) {
					ComponentsScan compScanAnnotation = (ComponentsScan)ann;
					Arrays.asList(compScanAnnotation.includes())
					.forEach(entry -> builder.includePackageByName(entry));
					Arrays.asList(compScanAnnotation.excludes())
					.forEach(entry -> builder.excludePackageByName(entry));
					
				}
				
			});
		DEFAULT_APP_BUILDER = builder;
		BeansHelper.DEFAULT_BUILDER = DEFAULT_APP_BUILDER;
		if ( ArgumentsHelper.traceLow ) {
			LoggerHelper.logTrace("ScannerHelper::initializeScanningPackagesFilter", String.format("Loaded new default classpath scanner configuration: %s", builder.toString()));
		}
	}

	public static final ClassPathConfigBuilder collectDefaultClassPathConfigBuilder() {
		if ( DEFAULT_APP_BUILDER != null ) {
			return DEFAULT_APP_BUILDER;
		}
		return ClassPathConfigBuilder.start();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final void addAnnotationExecutorsInPackage(String packageName,
			Processor<AnnotationExecutor<? extends Annotation>> executorStoreProcessor) {
		Reflections reflections = Reflections.newReflections(ClassPathConfigBuilder.start().includePackageByName(packageName).disablePersistenceOfData());
		Set<Class<? extends AnnotationExecutor>> classes = reflections.getSubTypesOf(AnnotationExecutor.class)
																		.stream()
																		.map(jce -> (Class<? extends AnnotationExecutor>)jce.getMatchClass())
																		.collect(Collectors.toSet());
		classes.forEach(cls -> {
			try {
				executorStoreProcessor.process(cls.newInstance());
			} catch (Exception ex) {
				String message = String.format("Unable to create insatnce of executor class : " + cls.getName());
				LoggerHelper.logError("ScannerHelper::addAnnotationExecutorsInPackage", message, ex);
				throw new ScannerException(message, ex);
			}
		});
	}

	protected static final Pattern textPattern = Pattern.compile("^(.*)(?=.*[A-Z])(?=.*).+$");

	/**
	 * @param clazz
	 * @return
	 */
	public final static String getPackageName(Class<?> clazz) {
		return clazz == null ? null : clazz.getPackage().getName();

	}

	/**
	 * Execute the Modules using Main Classes
	 * 
	 * @param classes
	 */
	protected static final void executeModuleMainClasses(Class<? extends ModuleMain>[] classes, String threadName) {
		if (classes != null && classes.length > 0) {
			Arrays.asList(classes).forEach(cls -> {
				GenericHelper.fixCurrentThreadStandardName(threadName);
				try {
					ModuleMain main = cls.newInstance();
					Collection<AnnotationDeclaration> annotations = ComponentsRegistry.getInstance()
							.getAll(annotationDescriptorsRegistryKey);
					if (ArgumentsHelper.debug) {
						LoggerHelper.logTrace("ScannerHelper::executeModuleMainClasses",
								String.format("Executing main class : %s", main.getClass().getName()));
					}
					executorService.execute(new Runnable() {

						@Override
						public void run() {
							try {
								LoggerHelper.logTrace("ScannerHelper::executeModuleMainClasses [thread]",
										String.format("Executing main class %s ...", main.getClass().getName()));
								main.execute(annotations);

								LoggerHelper.logTrace("ScannerHelper::executeModuleMainClasses [thread]",
										String.format("Main class %s initialized : state %s!!",
												main.getClass().getName(), main.isRunning() ? "RUNNING" : "COMPLETE"));
								// In case the main class is a daemon we plan a shutdown hook to
								// Shutdown/Abort the class execution.
								if (main.isRunning()) {
									Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

										@Override
										public void run() {
											try {
												if (main.isRunning()) {
													main.abort();
												}
											} catch (Exception ex) {
												String message = String.format(
														"Error in Shutdown/Abort of Module Main Class : %s",
														main.getClass().getName());
												LoggerHelper.logError(
														"ScannerHelper::executeModuleMainClasses::shutdownHook",
														message, ex);
											}
										}
									}));
								}
							} catch (Exception e) {
								String message = String.format("Unable to execute ModuleScanner main classes : %s",
										cls.getCanonicalName());
								LoggerHelper.logError("ScannerHelper::executeModuleMainClasses", message, e);
								throw new ScannerException(message, e);
							}
						}
					});
				} catch (Exception e) {
					String message = String.format("Unable to execute ModuleScanner main classes : %s",
							cls.getCanonicalName());
					LoggerHelper.logError("ScannerHelper::executeModuleMainClasses [thread]", message, e);
					throw new ScannerException(message, e);
				}
			});
		}
	}

	/**
	 * 
	 */
	public static final void executeScannerMainClasses(String threadName) {
		if (executorService != null) {
			LoggerHelper.logWarn("ScannerHelper::executeScannerMainClasses", "Main Class Execution in progress...",
					null);
		}
		Set<ModuleScanner> scanners = moduleRegistry.getEntitiesMap().values().stream().filter(scanner -> {
			ModuleScannerConfig scannerAnn = BeansHelper.getClassAnnotation(scanner.getClass(), ModuleScannerConfig.class);
			if (scannerAnn == null)
				return false;
			if (scannerAnn.mainClasses() == null || scannerAnn.mainClasses().length == 0)
				return false;
			return true;
		}).collect(Collectors.toSet());
		long mainClassesCount = scanners.stream().map(scanner -> {
			return Arrays.asList(BeansHelper.getClassAnnotation(scanner.getClass(), ModuleScannerConfig.class).mainClasses());
		}).flatMap(List::stream).count();
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		scanners.stream().forEach(scc -> {
			try {
				GenericHelper.fixCurrentThreadStandardName(threadName);
				ModuleScannerConfig scannerAnn = BeansHelper.getClassAnnotation(scc.getClass(), ModuleScannerConfig.class);
				if (ArgumentsHelper.debug) {
					LoggerHelper.logTrace("ScannerHelper::executeScannerMainClasses",
							String.format("Executing main classes for Module Scanner : %s", scc.getClass().getName()));
				}
				executeModuleMainClasses(scannerAnn.mainClasses(), threadName);
			} catch (Exception e) {
				String message = String.format("Unable to execute ModuleScanner main classes related to : %s",
						scc.getClass().getName());
				LoggerHelper.logError("ScannerHelper::executeScannerMainClasses", message, e);
				throw new ScannerException(message, e);
			}
		});
		executorService.shutdown();
		executorService = null;
		if (mainClassesCount > 0)
			Runtime.getRuntime().runFinalization();
	}

	/**
	 * Collect {@link ModuleScanner}s in some or all JVM packages, based on internal
	 * list of parameters and on a full scan on all packages, in which the
	 * ModuleMain, part of the parameters in the
	 * {@link com.rcg.foundation.fondify.annotations.annotations.ModuleScannerConfig}
	 * annotations recovering the declared list of ModuleMain implementations,
	 * declared for the module.
	 */
	public static final void collectModuleScanners() {
		final List<ModuleScanner> list = new ArrayList<>(0);
		List<ModuleScanner> excludeList = new ArrayList<>(0);
		DEFAULT_SCANNERS.forEach(scc -> {
			try {
				list.add((ModuleScanner) Class.forName(scc).newInstance());
			} catch (Exception e) {
				String message = String.format("Unable to load class for DEFAULT ModuleScanner with class : %s", scc);
				LoggerHelper.logError("ScannerHelper::collectModuleScanners", message, e);
			}
		});
		List<Class<? extends ModuleScanner>> moduleScannerTypes = BeansHelper.collectSubTypesOf(ClassPathConfigBuilder.start().disablePersistenceOfData(), ModuleScanner.class);
		LoggerHelper.logTrace("ScannerHelper::collectModuleScanners", "Collecting for Module Scanner classes ...");
		LoggerHelper.logTrace("ScannerHelper::collectModuleScanners",
				String.format("Collecting %s modules ...", "" + moduleScannerTypes.size()));
		List<Class<? extends ModuleScanner>> validModuleScannerTypes = moduleScannerTypes.stream().filter(cls -> {
			if (cls.getAnnotation(
					com.rcg.foundation.fondify.annotations.annotations.ModuleScannerConfig.class) != null) {
				if (DEFAULT_SCANNERS.contains(cls.getName()))
					return false;
				return true;
			}
			LoggerHelper.logError("ScannerHelper::collectModuleScanners",
					String.format(
							"Uable to add class %s, bacause it's missing of ModuleScanner configuration annotation"),
					null);
			return false;
		}).collect(Collectors.toList());
		LoggerHelper.logTrace("ScannerHelper::collectModuleScanners",
				String.format("Collection: passed %s modules ...", "" + validModuleScannerTypes.size()));
		validModuleScannerTypes.forEach(cls -> {
			try {
				if (list.stream().filter(mdl -> mdl.getClass() == cls).count() == 0) {
					list.add((ModuleScanner) cls.newInstance());
					LoggerHelper.logTrace("ScannerHelper::collectModuleScanners",
							"Found scanner : " + cls.getCanonicalName());
				} else {
					LoggerHelper.logTrace("ScannerHelper::collectModuleScanners",
							String.format("Scanner class instance already present in registry: %s", cls.getName()));
				}
			} catch (InstantiationException | IllegalAccessException e) {
				String message = String.format(
						"Class Instantiation Error :: Unable to instantiate ModuleScanner object for class : %s",
						cls.getCanonicalName());
				LoggerHelper.logError("ScannerHelper::collectModuleScanners", message, e);
				throw new ScannerException(message);
			} catch (Exception e) {
				String message = String.format(
						"Generic Error :: Unable to instantiate ModuleScanner object for class : %s",
						cls.getCanonicalName());
				LoggerHelper.logError("ScannerHelper::collectModuleScanners", message, e);
				throw new ScannerException(message);
			}
		});
		LoggerHelper.logTrace("ScannerHelper::collectModuleScanners", "Scanning for Module Scanner classes complete!!");
		LoggerHelper.logTrace("ScannerHelper::collectModuleScanners", "Scanning for Module Scan classes ...");
		Reflections r = null;
		
		try {
			r = Reflections.getSigletonInstance(ClassPathConfigBuilder.start().disablePersistenceOfData().build());
		} catch (Exception e1) {
			String message = "Errors revocering Reflections singleton instance!!";
			LoggerHelper.logError("ScannerHelper::collectModuleScanner", message, e1);;
			throw new ProcessException(message, e1);
		}
		
		Set<Class<?>> annScanTypes = r
				.getTypesAnnotatedWith(com.rcg.foundation.fondify.annotations.annotations.ModulesScan.class)
				.stream()
				.map(jce -> jce.getMatchClass())
				.collect(Collectors.toSet());
		annScanTypes.forEach(cls -> {
			try {
				com.rcg.foundation.fondify.annotations.annotations.ModulesScan ann = (com.rcg.foundation.fondify.annotations.annotations.ModulesScan) 
						BeansHelper.getClassAnnotation(cls, com.rcg.foundation.fondify.annotations.annotations.ModulesScan.class);
				if (ann.value()) {
					Arrays.asList(ann.includes()).stream().filter(clsX -> {
						String name = clsX.getCanonicalName();
						return !DEFAULT_SCANNERS.contains(name);
					}).forEach(mdlCls -> {
						try {
							if (!DEFAULT_SCANNERS.contains(mdlCls.getCanonicalName())
									&& list.stream().filter(mdl -> mdl.getClass() == mdlCls).count() == 0)
								list.add(mdlCls.newInstance());
						} catch (InstantiationException | IllegalAccessException e) {
							String message = String.format(
									"Class Instantiation Error :: Unable to instantiate ModulesScan (inclusion) object for class : %s",
									mdlCls.getCanonicalName());
							LoggerHelper.logError("ScannerHelper::collectModuleScanners", message, e);
							throw new ScannerException(message);
						} catch (Exception e) {
							String message = String.format(
									"Generic Error :: Unable to instantiate (inclusion) ModulesScan object for class : %s",
									mdlCls.getCanonicalName());
							LoggerHelper.logError("ScannerHelper::collectModuleScanners", message, e);
							throw new ScannerException(message, e);
						}
					});
					Arrays.asList(ann.excludes()).stream().filter(clsX -> {
						String name = clsX.getCanonicalName();
						return !DEFAULT_SCANNERS.contains(name);
					}).forEach(mdlCls -> {
						try {
							if (excludeList.stream().filter(mdl -> mdl.getClass() == mdlCls).count() == 0)
								excludeList.add(mdlCls.newInstance());
						} catch (InstantiationException | IllegalAccessException e) {
							String message = String.format(
									"Class Instantiation Error :: Unable to instantiate ModulesScan (exclusion) object for class : %s",
									mdlCls.getCanonicalName());
							LoggerHelper.logError("ScannerHelper::collectModuleScanners", message, e);
							throw new ScannerException(message);
						} catch (Exception e) {
							String message = String.format(
									"Generic Error :: Unable to instantiate ModulesScan (exclusion) object for class : %s",
									mdlCls.getCanonicalName());
							LoggerHelper.logError("ScannerHelper::collectModuleScanners", message, e);
							throw new ScannerException(message, e);
						}
					});
				}
			} catch (Exception e) {
				String message = String.format(
						"Generic Error :: Unable to instantiate ModulesScan object for class : %s",
						cls.getCanonicalName());
				LoggerHelper.logError("ScannerHelper::collectModuleScanners", message, e);
				throw new ScannerException(message, e);
			}
		});
		LoggerHelper.logTrace("ScannerHelper::collectModuleScanners", "Scanning for Module Scan classes complete!!");
		LoggerHelper.logTrace("ScannerHelper::collectModuleScanners",
				"Collecting exclusion class list and filter allowed scanners...");
		List<Class<?>> exclusionClasses = excludeList.stream().map(excl -> excl.getClass())
				.collect(Collectors.toList());
		list.stream().filter(incl -> !exclusionClasses.contains(incl.getClass()))
				.forEach(ms -> moduleRegistry.registerEntity(ms.getClass().getName(), ms));
		LoggerHelper.logTrace("ScannerHelper::collectModuleScanners",
				"Collection of exclusion class list and filtering of allowed scanners complete!!");

	}

	/**
	 * @return
	 */
	public static final List<Class<? extends Annotation>> getBaseAnnotationTypes() {
		return baseAnnotationTypes;
	}

	@SuppressWarnings("unchecked")
	public static final void scanBaseElementsAndStoreData(String threadName) {
		BeansHelper
				.collectSubTypesOf(collectDefaultClassPathConfigBuilder(), Arrays.asList(new Class<?>[] {
						AnnotationEngineInitializer.class, AnnotationExecutor.class, AnnotationTypesCollector.class }))
				.forEach(elementClass -> {
					GenericHelper.fixCurrentThreadStandardName(threadName);
					if (AnnotationEngineInitializer.class.isAssignableFrom(elementClass)) {
						try {
							LoggerHelper.logTrace("ScannerHelper::scanBaseElementsAndStoreData",
									"Running annotation engine initializer : " + elementClass);
							((Class<AnnotationEngineInitializer>) elementClass).newInstance().initialize();
						} catch (Exception e) {
							LoggerHelper.logError("ScannerHelper::scanBaseElementsAndStoreData",
									"Errors during execution of annotation engine initializer : " + elementClass, e);
						}
					} else if (AnnotationExecutor.class.isAssignableFrom(elementClass)) {
						try {
							LoggerHelper.logTrace("ScannerHelper::scanBaseElementsAndStoreData",
									"Found Annotation executor: " + elementClass);
							AnnotationExecutor<? extends Annotation> executor = (AnnotationExecutor<? extends Annotation>) elementClass
									.newInstance();
							String className = executor.getAnnotationClass().getName();
							LoggerHelper.logTrace("ScannerHelper::scanBaseElementsAndStoreData",
									"Annotation Executor Reference Class: " + className);
							ComponentsRegistry.getInstance()
									.add(AnnotationConstants.REGISTRY_CLASS_ANNOTATION_EXECUTORS, className, executor);
						} catch (Exception ex) {
							LoggerHelper.logError("ScannerHelper::scanBaseElementsAndStoreData",
									String.format("Errors during collection of executor", "" + elementClass), ex);
						}
					} else if (AnnotationTypesCollector.class.isAssignableFrom(elementClass)) {
						try {
							LoggerHelper.logTrace("ScannerHelper::scanBaseElementsAndStoreData",
									"Found annotation engine Annotation Collector : " + elementClass);
							baseAnnotationTypes.addAll(((Class<AnnotationTypesCollector>) elementClass).newInstance()
									.listAnnotationTypes());
						} catch (Exception e) {
							LoggerHelper.logError("ScannerHelper::scanBaseElementsAndStoreData",
									"Errors during execution of annotation engine initializer : " + elementClass, e);
						}
					} else {
						LoggerHelper.logWarn("ScannerHelper::scanBaseElementsAndStoreData",
								String.format("Unable to associate base Annotation Engine Type: %s", "" + elementClass),
								null);
					}
				});
	}
	
	public static final boolean isInjectable(Object obj) {
		if ( obj == null )
			return false;
		Class<?> cls = isBeanDefinition(obj) ? ((BeanDefinition)obj).getDeclaration().getAnnotatedClass() : 
			(isMethodExecutor(obj) ? ((MethodExecutor)obj).getTargetClass() :  null);
		return cls != null && 
				com.rcg.foundation.fondify.core.typings.Injectable.class.isAssignableFrom(cls);
	}
	
	public static final boolean isBeanDefinition(Object obj) {
		return obj != null && 
				BeanDefinition.class.isAssignableFrom(obj.getClass());
	}
	
	public static final boolean isMethodExecutor(Object obj) {
		return obj != null && 
				MethodExecutor.class.isAssignableFrom(obj.getClass());
	}
	
	@SuppressWarnings("unchecked")
	public static final void executeAnnotationExecutor(AnnotationDeclaration ad, AnnotationExecutor<? extends Annotation> executor) {
		Class<Annotation> annCls = (Class<Annotation>)ad.getAnnotationDeclarationClass(); 
		String annName = annCls.getName();
			if ( executor !=  null ) {
				LoggerHelper.logTrace("ScannerHelperHelper::executeAnnotationExecutor [stream cycle]", 
						String.format("Executing AnnotationExecutor for annotation name: %s of type: %s!!", annName, executor.getClass().getCanonicalName()));
				try {
					ExecutionAnswer<? extends Annotation> answer = executor.executeAnnotation(ad);
					if (answer.isErrors()) {
						LoggerHelper.logWarn("ScannerHelperHelper::executeAnnotationExecutor  [stream cycle]", String.format("Errors executing annotation : %s, message is :%s ", annName, answer.getMessage()), null);
					} else {
						if (answer.isWarnings()) {
							LoggerHelper.logWarn("ScannerHelperHelper::executeAnnotationExecutor  [stream cycle]", String.format("Warning executing annotation : %s, message is :%s ", annName, answer.getMessage()), null);
						}
						if ( executor.containsResults() ) {
							List<Object> test = new ArrayList<>(answer.getResults());
							List<Object> results = new ArrayList<>(test);
							Object obj = test.iterator().next();
							boolean isComponent = isBeanDefinition(obj) || isMethodExecutor(obj);
							if ( isComponent ) {
								String regName = "";
								if ( isMethodExecutor(obj) ) {
									LoggerHelper.logTrace("ScannerHelperHelper::executeAnnotationExecutor", 
											String.format("Injectable method executor bean name : %s and bean's type %s!!", executor.getComponentName(), obj!= null ? obj.getClass().getName():"<NULL>") );
									regName = AnnotationConstants.REGISTRY_INJECTABLE_METHODD_DEFINITIONS;
								} else {
									LoggerHelper.logTrace("ScannerHelperHelper::executeAnnotationExecutor", 
											String.format("Component type bean name : %s and bean's type %s!!", executor.getComponentName(), obj!= null ? obj.getClass().getName():"<NULL>") );
									regName = AnnotationConstants.REGISTRY_COMPONENT_BEAN_DEFINITIONS;
								}

								if ( results.size() == 1 ) {
									ComponentsRegistry.getInstance().add(regName, executor.getComponentName(), results.get(0));
								} else if ( results.size() > 1 ) {
									ComponentsRegistry.getInstance().add(regName, executor.getComponentName(), results);
								}
							} else {
								//NO COMPONENT TYPE
								LoggerHelper.logWarn("ScannerHelperHelper::executeAnnotationExecutor", 
										String.format("Undefined type for annotations' executor of type : %s and bean's type", annName, obj!= null ? obj.getClass().getName():"<NULL>"), null);
								if ( obj != null ) {
									LoggerHelper.logWarn("ScannerHelperHelper::executeAnnotationExecutor", 
											String.format("Trying registration of custom bean : %s ", obj.getClass().getName()), null);
									AnnotationBeanActuatorProvider.getInstance().registerCustomBean(obj);
								}
							}
						}
					}
					
				} catch (ProcessException e) {
					String message = String.format("Unable to run executor due to ERRORS for annotation type : %s", annName);
					LoggerHelper.logError("ScannerHelperHelper::executeAnnotationExecutor [stream cycle]", message, e);
					throw new MappingException(message, e);
				}
			} else {
				LoggerHelper.logWarn("ScannerHelperHelper::executeAnnotationExecutor", 
						String.format("Unable to find annotations' executor of type : %s", annName), null);
			}
	}

	@SuppressWarnings("unchecked")
	public static final void executeProvidedBaseAnnotationExecutors(String threadName) {
		ClassPathConfigBuilder builder = collectDefaultClassPathConfigBuilder();
		annotationsDeclarationMaps.putAll(collectAnnotations(builder, baseAnnotationTypes
																						.stream()
																						.map( baseCls -> (Class<Annotation>) baseCls )
																						.collect(Collectors.toList())));

		LoggerHelper.logTrace("ScannerHelper::executeProvidedBaseAnnotationExecutors", String
				.format("Found follwoing number of annotations %s declaration in the classpath", ""+annotationsDeclarationMaps.size()));
		annotationsDeclarationMaps.entrySet().forEach(entry -> {
			String className = entry.getKey().getName();
			entry.getValue().forEach(ad -> {
				GenericHelper.fixCurrentThreadStandardName(threadName);
				LoggerHelper.logTrace("ScannerHelper::executeProvidedBaseAnnotationExecutors [stream cycle]", 
						String.format("Scanning executors for annotation class name: %s...", className));
				AnnotationExecutor<? extends Annotation> executor = ComponentsRegistry.getInstance()
						.get(AnnotationConstants.REGISTRY_CLASS_ANNOTATION_EXECUTORS, className);
				Class<Annotation> annCls = (Class<Annotation>) ad.getAnnotationDeclarationClass();
				String annName = annCls.getName();
				LoggerHelper.logTrace("ScannerHelper::executeProvidedBaseAnnotationExecutors [stream cycle]", 
						String.format("Found annotation name: %s of executor type: %s!!", annName, executor != null ? executor.getClass().getCanonicalName() : "<NULL>"));
				ScannerHelper.executeAnnotationExecutor(ad, executor);
			});
		});
	}
}
