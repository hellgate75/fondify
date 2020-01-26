/**
 * 
 */
package com.rcg.foundation.fondify.annotations.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URLClassLoader;
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

import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.MethodParameterScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.rcg.foundation.fondify.annotations.annotations.Application;
import com.rcg.foundation.fondify.annotations.annotations.ComponentsScan;
import com.rcg.foundation.fondify.annotations.annotations.Configuration;
import com.rcg.foundation.fondify.annotations.annotations.DependsOn;
import com.rcg.foundation.fondify.annotations.annotations.ModuleScannerConfig;
import com.rcg.foundation.fondify.annotations.contants.AnnotationConstants;
import com.rcg.foundation.fondify.core.exceptions.ScannerException;
import com.rcg.foundation.fondify.core.functions.Matcher;
import com.rcg.foundation.fondify.core.functions.Processor;
import com.rcg.foundation.fondify.core.helpers.ArgumentsHelper;
import com.rcg.foundation.fondify.core.helpers.BeansHelper;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;
import com.rcg.foundation.fondify.core.registry.ComponentsRegistry;
import com.rcg.foundation.fondify.core.registry.typings.ComponentRegistryItem;
import com.rcg.foundation.fondify.core.typings.AnnotationDeclaration;
import com.rcg.foundation.fondify.core.typings.AnnotationEngineInitializer;
import com.rcg.foundation.fondify.core.typings.AnnotationExecutor;
import com.rcg.foundation.fondify.core.typings.AnnotationTypesCollector;
import com.rcg.foundation.fondify.core.typings.ModuleMain;
import com.rcg.foundation.fondify.core.typings.ModuleScanner;

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

	/**
	 * Create a Reflections bases on input packages
	 * 
	 * @param packages
	 * @return
	 */
	public static final ConfigurationBuilder getRefletionsByPackages(String[] packages) {
		return BeansHelper.getRefletionsByPackages(packages);
	}

	public static final boolean isApplicationClass(Class<?> mainClass) {
		if (mainClass == null) {
			return false;
		}
		return mainClass.getAnnotation(Application.class) != null;
	}

	public static final List<? extends Annotation> getApplicationClassAnnotations(Class<?> mainClass) {
		List<Annotation> list = new ArrayList<>(0);
		if (mainClass == null) {
			return list;
		}
		list.addAll(Arrays.asList(mainClass.getAnnotations()));
		return list;
	}
	
	protected static final boolean isRunningFromJar() {
		return (""+ScannerHelper.class.getResource("ScannerHelper.class")).toLowerCase().startsWith("jar");
	}
	
	private static URLClassLoader classLoader = null;
	
	protected static final ClassLoader getLocalClassLoader() {
		if (classLoader != null)
			return classLoader;
		Collection<java.net.URL> coll = ClasspathHelper.forJavaClassPath();
		java.net.URL[] urls = new java.net.URL[coll.size()]; 
		urls = coll.toArray(urls);
		classLoader =  URLClassLoader.newInstance(urls);
		return classLoader;
	}

	/**
	 * Create a Reflections bases on input packages
	 * 
	 * @param packages
	 * @return
	 */
	public static final ConfigurationBuilder getRefletionsByPackages(Collection<String> packages) {
		return BeansHelper.getRefletionsByPackages(packages);
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
		ConfigurationBuilder config = new ConfigurationBuilder().addUrls(ClasspathHelper.forJavaClassPath())
				.addScanners(new SubTypesScanner());
		List<Class<? extends ModuleScanner>> moduleScannerTypes = collectSubTypesOf(config, ModuleScanner.class);
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
	 * Collect all subTypes of provided interface or class one.
	 * 
	 * @param <T>
	 * @param builder    Base package scanner builder
	 * @param superClass implemented class or interface
	 * @return list if sub types of provided one
	 */
	public static final <T> List<Class<? extends T>> collectSubTypesOf(ConfigurationBuilder builder,
			Class<T> superClass) {
		return BeansHelper.collectSubTypesOf(builder, superClass);
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
		return BeansHelper.collectSubTypesOf(builder, superClassList);
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
			ConfigurationBuilder builder, List<Class<Annotation>> annotations) {
		Map<Class<Annotation>, List<AnnotationDeclaration>> map = new HashMap<>(0);
		annotations.forEach(annotation -> {
			// try in types
			Reflections r = new Reflections(builder.addScanners(new SubTypesScanner()));
			List<AnnotationDeclaration> annotationsDeclarationsList = new ArrayList<>(0);
			Set<Class<?>> annTypes = r.getTypesAnnotatedWith(annotation);
			annTypes.forEach(clz -> {
				AnnotationDeclaration declaration = new AnnotationDeclaration(annotation, clz, null, null, null);
				if (validateAnnotationDeclaration(declaration))
					annotationsDeclarationsList.add(declaration);
			});
			r = new Reflections(builder.addScanners(new FieldAnnotationsScanner()));
			Set<Field> fields = r.getFieldsAnnotatedWith(annotation);
			fields.forEach(fld -> {
				AnnotationDeclaration declaration = new AnnotationDeclaration(annotation, fld.getDeclaringClass(), fld,
						null, null);
				if (validateAnnotationDeclaration(declaration))
					annotationsDeclarationsList.add(declaration);
			});
			r = new Reflections(builder.addScanners(new MethodAnnotationsScanner()));
			Set<Method> methods = r.getMethodsAnnotatedWith(annotation);
			methods.forEach(m -> {
				AnnotationDeclaration declaration = new AnnotationDeclaration(annotation, m.getDeclaringClass(), null,
						m, null);
				if (validateAnnotationDeclaration(declaration))
					annotationsDeclarationsList.add(declaration);
			});
			r = new Reflections(builder.addScanners(new MethodParameterScanner()));
			Set<Method> methodsOfParameters = r.getMethodsWithAnyParamAnnotated(annotation);
			methodsOfParameters.forEach(m -> {
				List<Parameter> params = Arrays.asList(m.getParameters()).stream()
						.filter(p -> p.getAnnotationsByType(annotation).length > 0).collect(Collectors.toList());
				params.forEach(p -> {
					AnnotationDeclaration declaration = new AnnotationDeclaration(annotation, m.getDeclaringClass(),
							null, m, p);
					if (validateAnnotationDeclaration(declaration))
						annotationsDeclarationsList.add(declaration);
				});
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
			DependsOn dependsOn = annotation.annotationType().getAnnotation(DependsOn.class);
			Class<?>[] annotationClasses = dependsOn.value();
			List<Class<? extends Annotation>> requiredAnnotations = new ArrayList<>(0);
			for (Class<?> clz : annotationClasses) {
				if (Annotation.class.isAssignableFrom(clz)) {
					requiredAnnotations.add((Class<Annotation>) clz);
				}
			}
			List<Class<? extends Annotation>> presentAnnotations = Arrays
					.asList(annotation.annotationType().getAnnotations()).stream().map(a -> a.annotationType())
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

	public static final List<String> collectScanningPackagesFilter(String basePackage) {
		ConfigurationBuilder builder = getRefletionsByPackages(new String[] {});
		List<Class<? extends Annotation>> annotations = new ArrayList<>(0);
		annotations.add(ComponentsScan.class);
		annotations.add(Configuration.class);
		@SuppressWarnings("unchecked")
		Map<Class<Annotation>, List<AnnotationDeclaration>> scanMap = collectAnnotations(builder,
				annotations
				.stream()
				.map(ann -> (Class<Annotation>)ann)
				.collect(Collectors.toList()));
		return scanMap.entrySet().stream().filter(i -> getPackageName(i.getKey()).contains(basePackage))
				.map(a -> a.getValue()).flatMap(List::stream).map(ad -> {
					if (ComponentsScan.class.isAssignableFrom(ad.getAnnotation().getClass()))
						return (ComponentsScan) ad.getAnnotation();
					else
						return (Configuration) ad.getAnnotation();
				}).map(a -> {
					if (ComponentsScan.class.isAssignableFrom(a.getClass()))
						return Arrays.asList(((ComponentsScan) a).value());
					else
						return Arrays.asList(((Configuration) a).packages());
				}).flatMap(List::stream).distinct().collect(Collectors.toList());
	}

	private static boolean scanningPackagesComplete = false;
	private static List<String> scanningPackages = new ArrayList<>(0);

	public static final List<String> collectScanningPackages() {
		if (scanningPackagesComplete)
			return scanningPackages;
		ConfigurationBuilder builder = getRefletionsByPackages(new String[] {});
		List<Class<? extends Annotation>> annotations = new ArrayList<>(0);
		annotations.add(ComponentsScan.class);
		annotations.add(Configuration.class);
		@SuppressWarnings("unchecked")
		Map<Class<Annotation>, List<AnnotationDeclaration>> scanMap = collectAnnotations(builder,
				annotations
				.stream()
				.map(ann -> (Class<Annotation>)ann)
				.collect(Collectors.toList()));
		scanningPackages.addAll(scanMap.entrySet().stream().map(a -> a.getValue()).flatMap(List::stream)
				.map(AnnotationDeclaration::getAnnotation).map(a -> {
					if (ComponentsScan.class.isAssignableFrom(a.getClass()))
						return Arrays.asList(((ComponentsScan) a).value());
					else
						return Arrays.asList(((Configuration) a).packages());
				})
				.flatMap(List::stream)
				.distinct()
				.map(packageName -> {
					if ( packageName.contains(".*") ) {
						packageName = packageName.substring(0, packageName.indexOf(".*"));
					}
					return packageName;
				})
				.collect(Collectors.toList()));
		scanningPackagesComplete = true;
		return scanningPackages;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final void addAnnotationExecutorsInPackage(String packageName,
			Processor<AnnotationExecutor<? extends Annotation>> executorStoreProcessor) {
		Reflections reflections = new Reflections(packageName, new SubTypesScanner());
		Set<Class<? extends AnnotationExecutor>> classes = reflections.getSubTypesOf(AnnotationExecutor.class);
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

	private static final Pattern textPattern = Pattern.compile("^(.*)(?=.*[A-Z])(?=.*).+$");

	/**
	 * @param clazz
	 * @return
	 */
	public final static String getPackageName(Class<?> clazz) {
		String tmp = clazz.getCanonicalName();
		while (tmp.contains(".") && textPattern.matcher(tmp).matches()) {
			tmp = tmp.substring(0, tmp.lastIndexOf("."));
		}
		return tmp;

	}

	/**
	 * Execute the Scanner Annota
	 * 
	 * @param classes
	 */
	protected static final void executeModuleMainClasses(Class<? extends ModuleMain>[] classes) {
		if (classes != null && classes.length > 0) {
			Arrays.asList(classes).forEach(cls -> {
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
	public static final void executeScannerMainClasses() {
		if (executorService != null) {
			LoggerHelper.logWarn("ScannerHelper::executeScannerMainClasses", "Main Class Execution in progress...",
					null);
		}
		Set<ModuleScanner> scanners = moduleRegistry.getEntitiesMap().values().stream().filter(scanner -> {
			if (scanner.getClass().getAnnotation(ModuleScannerConfig.class) == null)
				return false;
			com.rcg.foundation.fondify.annotations.annotations.ModuleScannerConfig scannerAnn = scanner.getClass()
					.getAnnotation(ModuleScannerConfig.class);
			if (scannerAnn.mainClasses() == null || scannerAnn.mainClasses().length == 0)
				return false;
			return true;
		}).collect(Collectors.toSet());
		long mainClassesCount = scanners.stream().map(scanner -> {
			return Arrays.asList(scanner.getClass().getAnnotation(ModuleScannerConfig.class).mainClasses());
		}).flatMap(List::stream).count();
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		scanners.stream().forEach(scc -> {
			try {
				com.rcg.foundation.fondify.annotations.annotations.ModuleScannerConfig scannerAnn = scc.getClass()
						.getAnnotation(com.rcg.foundation.fondify.annotations.annotations.ModuleScannerConfig.class);
				if (ArgumentsHelper.debug) {
					LoggerHelper.logTrace("ScannerHelper::executeScannerMainClasses",
							String.format("Executing main classes for Module Scanner : %s", scc.getClass().getName()));
				}
				executeModuleMainClasses(scannerAnn.mainClasses());
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
		ConfigurationBuilder config = new ConfigurationBuilder().addUrls(ClasspathHelper.forJavaClassPath())
				.addScanners(new SubTypesScanner());
		Reflections r = new Reflections(config);
		List<Class<? extends ModuleScanner>> moduleScannerTypes = collectSubTypesOf(config, ModuleScanner.class);
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
		Set<Class<?>> annScanTypes = r
				.getTypesAnnotatedWith(com.rcg.foundation.fondify.annotations.annotations.ModulesScan.class);
		annScanTypes.forEach(cls -> {
			try {
				com.rcg.foundation.fondify.annotations.annotations.ModulesScan ann = (com.rcg.foundation.fondify.annotations.annotations.ModulesScan) cls
						.getAnnotation(com.rcg.foundation.fondify.annotations.annotations.ModulesScan.class);
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
	public static final void scanBaseElementsAndStoreData() {
		ScannerHelper
				.collectSubTypesOf(ScannerHelper.getRefletionsByPackages(new String[0]), Arrays.asList(new Class<?>[] {
						AnnotationEngineInitializer.class, AnnotationExecutor.class, AnnotationTypesCollector.class }))
				.forEach(elementClass -> {
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

	@SuppressWarnings("unchecked")
	public static final void executeProvidedBaseAnnotationExecutors() {
		ConfigurationBuilder builder = ScannerHelper.getRefletionsByPackages(ScannerHelper.collectScanningPackages());
		annotationsDeclarationMaps.putAll(ScannerHelper.collectAnnotations(builder, baseAnnotationTypes
																						.stream()
																						.map(ann -> (Class<Annotation>)ann)
																						.collect(Collectors.toList())));
		LoggerHelper.logTrace("ScannerHelper::executeProvidedBaseAnnotationExecutors", String
				.format("Found follwoing number of annotations %s declaration in the classpath", ""+annotationsDeclarationMaps.size()));
		annotationsDeclarationMaps.entrySet().forEach(entry -> {
			String className = entry.getKey().getName();
			AnnotationExecutor<? extends Annotation> executor = ComponentsRegistry.getInstance()
					.get(AnnotationConstants.REGISTRY_CLASS_ANNOTATION_EXECUTORS, className);
			if (entry.getValue().size() > 0) {
				LoggerHelper.logTrace("ScannerHelper::executeProvidedBaseAnnotationExecutors", String
						.format("Praparing execution for annotation %s from executor: %s", className, "" + executor));
				if (executor != null) {
					LoggerHelper.logTrace("ScannerHelper::executeProvidedBaseAnnotationExecutors",
							String.format("Executing annotation %s from executor: %s on %s annotations!!", className,
									"" + executor, "" + entry.getValue().size()));
					entry.getValue().forEach(ad -> {
						try {
							executor.executeAnnotation(ad);
						} catch (Exception e) {
							LoggerHelper.logError("ScannerHelper::executeProvidedBaseAnnotationExecutors",
									String.format(
											"Unable to execute annotation executor for annotation class %s : Error executing executor : %s",
											className, executor.getClass().getName()),
									e);
						}
					});
				} else {
					LoggerHelper.logWarn("ScannerHelper::executeProvidedBaseAnnotationExecutors", String.format(
							"Unable to execute annotation executor for annotation class %s : EXECUTOR NOT FOUND",
							className), null);
				}
			} else {
				LoggerHelper.logWarn("ScannerHelper::executeProvidedBaseAnnotationExecutors",
						String.format("Cannot eecute annotation %s from executor: %s NO ANNOTATIONS FOUND!!", className,
								"" + executor),
						null);

			}
		});
	}
}
