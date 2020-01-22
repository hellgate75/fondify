/**
 * 
 */
package com.rcg.foundation.fondify.annotation.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
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

import com.rcg.foundation.fondify.annotation.annotations.ComponentsScan;
import com.rcg.foundation.fondify.annotation.annotations.Configuration;
import com.rcg.foundation.fondify.annotation.annotations.DependsOn;
import com.rcg.foundation.fondify.core.exceptions.ScannerException;
import com.rcg.foundation.fondify.core.functions.Matcher;
import com.rcg.foundation.fondify.core.functions.Processor;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;
import com.rcg.foundation.fondify.core.registry.typings.ComponentRegistryItem;
import com.rcg.foundation.fondify.core.typings.AnnotationDeclaration;
import com.rcg.foundation.fondify.core.typings.AnnotationExecutor;
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

	/**
	 * Denied access constructor
	 */
	protected ScannerHelper() {
		throw new IllegalStateException("ScannerHelper::constructor - unable to instantiate utility class!!");
	}

	/**
	 * Create a Reflections bases on input packages
	 * 
	 * @param packages
	 * @return
	 */
	public static final ConfigurationBuilder getRefletionsByPackages(String[] packages) {
		ConfigurationBuilder config = new ConfigurationBuilder();
		if (packages != null && packages.length > 0) {
			Arrays.asList(packages).stream().filter(pkg -> pkg != null && !pkg.isEmpty()).forEach(pkg -> {
				config.addUrls(ClasspathHelper.forPackage(pkg, ClassLoader.getSystemClassLoader()));
			});
		} else {
			config.addUrls(ClasspathHelper.forJavaClassPath());
		}
		return config;
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
			packages.stream().filter(pkg -> pkg != null && !pkg.isEmpty()).forEach(pkg -> {
				config.addUrls(ClasspathHelper.forPackage(pkg, ClassLoader.getSystemClassLoader()));
			});
		} else {
			config.addUrls(ClasspathHelper.forJavaClassPath());
		}
		return config;
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
												main.getClass().getName(),
												main.isRunning() ? "RUNNING" : "COMPLETE"));
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

	private static ExecutorService executorService = null;

	/**
	 * 
	 */
	public static final void executeScannerMainClasses(Set<Class<? extends ModuleMain>[]> scanners, Collection<AnnotationDeclaration> annotations) {
		if ( executorService != null ) {
			LoggerHelper.logWarn("ScannerHelper::executeScannerMainClasses", 
					"Main Class Execution in progress...", null);
			return;
		}
		long mainClassesCount = scanners.stream()
				.map(entry -> Arrays.asList(entry))
				.flatMap(List::stream).count();
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
		if ( mainClassesCount > 0 )
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
	public static final void collectModuleScanners(Matcher<Class<?>> annotationMatcher, Class<? extends Annotation> moduleAnnotationClass,
			List<Class<? extends ModuleScanner>> inlcudeClasses, List<Class<? extends ModuleScanner>> excludeClasses) {
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
		List<Class<? extends ModuleScanner>> moduleScannerTypes = collectSubTypesOf(config,
				ModuleScanner.class);
		LoggerHelper.logTrace("ScannerHelper::collectModuleScanners", "Collecting for Module Scanner classes ...");
		LoggerHelper.logTrace("ScannerHelper::collectModuleScanners",
				String.format("Collecting %s modules ...", "" + moduleScannerTypes.size()));
		List<Class<? extends ModuleScanner>> validModuleScannerTypes = moduleScannerTypes.stream().filter(cls -> {
			if (annotationMatcher.match(cls) ) {
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
		inlcudeClasses
		.stream()
		.filter(cls -> {
				String name = cls.getCanonicalName();
				return !DEFAULT_SCANNERS.contains(name);
			})
		.forEach(mdlCls -> {
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
		excludeClasses
		.stream()
		.filter(cls -> {
				String name = cls.getCanonicalName();
				return !DEFAULT_SCANNERS.contains(name);
			})
		.forEach(mdlCls -> {
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
		List<Class<? extends T>> classes = new ArrayList<Class<? extends T>>(0);
		Reflections r = new Reflections(builder.addScanners(new SubTypesScanner()));
		classes.addAll(r.getSubTypesOf(superClass));
		return classes;
	}

	/**
	 * Collect {@link Annotation}s in some or all JVM packages, based on a list of
	 * {@link Annotation} classes
	 * 
	 * @param builder
	 * @param annotations
	 * @return
	 */
	public static final Map<Class<? extends Annotation>, List<AnnotationDeclaration>> collectAnnotations(
			ConfigurationBuilder builder, List<Class<? extends Annotation>> annotations) {
		Map<Class<? extends Annotation>, List<AnnotationDeclaration>> map = new HashMap<>(0);
		List<Class<? extends Annotation>> annotationsLocal = new ArrayList<>(0);
		annotationsLocal.addAll(annotations);
		annotationsLocal.forEach(annotation -> {
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
		return moduleScanners.stream()
				.filter(ms -> getPackageName(ms.getClass()).contains(basePackage)).findFirst();
	}

	public static final List<String> collectScanningPackagesFilter(String basePackage) {
		ConfigurationBuilder builder = getRefletionsByPackages(new String[] {});
		List<Class<? extends Annotation>> annotations = new ArrayList<>(0);
		annotations.add(ComponentsScan.class);
		annotations.add(Configuration.class);
		Map<Class<? extends Annotation>, List<AnnotationDeclaration>> scanMap = collectAnnotations(builder,
				annotations);
		return scanMap.entrySet().stream()
				.filter(i -> getPackageName(i.getKey()).contains(basePackage)).map(a -> a.getValue())
				.flatMap(List::stream).map(ad -> {
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

	public static final List<String> collectScanningPackages() {
		ConfigurationBuilder builder = getRefletionsByPackages(new String[] {});
		List<Class<? extends Annotation>> annotations = new ArrayList<>(0);
		annotations.add(ComponentsScan.class);
		annotations.add(Configuration.class);
		Map<Class<? extends Annotation>, List<AnnotationDeclaration>> scanMap = collectAnnotations(builder,
				annotations);
		return scanMap.entrySet().stream().map(a -> a.getValue()).flatMap(List::stream)
				.map(AnnotationDeclaration::getAnnotation).map(a -> {
					if (ComponentsScan.class.isAssignableFrom(a.getClass()))
						return Arrays.asList(((ComponentsScan) a).value());
					else
						return Arrays.asList(((Configuration) a).packages());
				}).flatMap(List::stream).distinct().collect(Collectors.toList());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final void addAnnotationExecutorsInPackage(String packageName, Processor<AnnotationExecutor<? extends Annotation>> executorStoreProcessor) {
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

	private static  final Pattern textPattern = Pattern.compile("^(.*)(?=.*[A-Z])(?=.*).+$");

	/**
	 * @param clazz
	 * @return
	 */
	public final static String getPackageName(Class<?> clazz) {
		String tmp = clazz.getCanonicalName();
		while ( tmp.contains(".") && textPattern.matcher(tmp).matches() ) {
			tmp = tmp.substring(0, tmp.lastIndexOf("."));
		}
		return tmp;
	
	}
}
