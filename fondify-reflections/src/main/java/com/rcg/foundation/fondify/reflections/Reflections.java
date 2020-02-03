/**
 * 
 */
package com.rcg.foundation.fondify.reflections;

import static com.rcg.foundation.fondify.reflections.helpers.ClassPathHelper.compileClassPathEntries;
import static com.rcg.foundation.fondify.reflections.helpers.ClassPathHelper.createExecutableConfig;
import static com.rcg.foundation.fondify.reflections.helpers.ClassPathHelper.loadClassPathEntries;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.rcg.foundation.fondify.reflections.typings.ClassPathConfig;
import com.rcg.foundation.fondify.reflections.typings.ClassPathConfigBuilder;
import com.rcg.foundation.fondify.reflections.typings.JavaClassEntity;
import com.rcg.foundation.fondify.reflections.typings.MatchDescriptor;
import com.rcg.foundation.fondify.utils.helpers.ArgumentsHelper;
import com.rcg.foundation.fondify.utils.helpers.GenericHelper;
import com.rcg.foundation.fondify.utils.helpers.LoggerHelper;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class Reflections {

	/**
	 * This Library contains list of string that defines exclusion from
	 * JVM Class-Path entries. 
	 */
	public static List<String> SYSTEM_LIBRARIES_EXCLUSIONS = new ArrayList<>(0); 

	/**
	 * This Library contains list of string that defines forced inclusion of
	 * JVM Class-Path entries. 
	 */
	public static List<String> SYSTEM_LIBRARIES_FORCED_INCLUSIONS = new ArrayList<>(0); 

	/**
	 * This Library contains list of string that defines forced inclusion of
	 * JVM Class-Path entries packages.
	 */
	public static List<String> SYSTEM_PACKAGES_FORCED_INCLUSIONS = new ArrayList<>(0); 
	
	private static final Map<String, List<JavaClassEntity>> SAVED_MAP_ENTRIES = new ConcurrentHashMap<String, List<JavaClassEntity>>(0);
	private static ClassPathConfig savedMapConfiguration = null;
	private static Reflections instance = null;
	
	private boolean sessionEnabled = false;
	private boolean loadingSession = false;
	private final Map<String, List<JavaClassEntity>> entriesMap = new ConcurrentHashMap<String, List<JavaClassEntity>>(0);
	private ClassPathConfig localConfiguration = null;
	
	private List<String> availablePackageNames = new ArrayList<String>(0);
	
	/**
	 * Default protected constructor
	 */
	private Reflections(ClassPathConfig configuration) {
		sessionEnabled = configuration.enablePersistenceOfData();
		while ( sessionEnabled && loadingSession ) {
			GenericHelper.sleepThread(1200);
		}
		if ( sessionEnabled && configuration != null ) {
			configuration = savedMapConfiguration;
		} else if ( sessionEnabled ) {
			try {
				loadingSession = true;
				SAVED_MAP_ENTRIES.putAll(
					compileClassPathEntries(
						loadClassPathEntries(
								createExecutableConfig(configuration)
						),
					configuration
					)
				);
				if ( SAVED_MAP_ENTRIES.size() > 0 ) {
					savedMapConfiguration = configuration;
				}
			} catch (Exception e) {
				LoggerHelper.logError("Reflections::contructor", "Error occured during load of persistent Reflections version", e);
			} finally {
				loadingSession = false;
			}
		} else if ( ! sessionEnabled ) {
			localConfiguration = configuration;
			entriesMap.putAll(
					compileClassPathEntries(
							loadClassPathEntries(
									createExecutableConfig(configuration)
							),
						configuration
						)
			);
		}
		seekForPackages();
	}
	
	private void seekForPackages() {
		availablePackageNames.addAll(
			getCurrentMapInUse()
				.values()
				.stream()
				.flatMap(List::stream)
				.map(jce -> jce.getPackageName())
				.distinct()
				.collect(Collectors.toList())
		);
	}
	
	
	
	/**
	 * Returns the {@link ClassPathConfig} used with for filtering 
	 * the current {@link Reflections}. 
	 * @return the localConfiguration Current {@link ClassPathConfig} configuration
	 */
	public ClassPathConfig getClassPathConfiguration() {
		return localConfiguration;
	}

	/**
	 * Returns all available package names in the current {@link Reflections} instance.
	 * @return the availablePackageNames List of available package neames 
	 */
	public List<String> getAvailablePackageNames() {
		return availablePackageNames;
	}

	private Map<String, List<JavaClassEntity>> getCurrentMapInUse() {
		return sessionEnabled ? SAVED_MAP_ENTRIES : entriesMap;  
	}
	
	/**
	 * Retrieves all sub types Classes Results of given super-type class
	 * @param cls Super-type class
	 * @return (List&lt;{@link MatchDescriptor}&gt;) list of matches for the given criteria
	 */
	public List<MatchDescriptor> getSubTypesOf(Class<?> cls) {
		List<MatchDescriptor> matchList = new ArrayList<MatchDescriptor>(0);
		if ( cls == null ) {
			return matchList;
		}
		matchList.addAll(
		getCurrentMapInUse()
			.values()
			.stream()
			.flatMap(List::stream)
			.filter( jce -> jce.getImplementingTypes()
								.stream()
								.filter( scls -> cls.isAssignableFrom(scls) )
								.count() > 0)
			.map( jce -> jce.getClassDescriptor(null) )
			.collect(Collectors.toList())
		);
		return matchList;
	}
	
	/**
	 * Retrieves all sub types Classes Results of given super-type classes array
	 * @param clsArray  Array of super-type classes
	 * @return (List&lt;{@link MatchDescriptor}&gt;) list of matches for the given criteria
	 */
	public List<MatchDescriptor> getSubTypesOf(Class<?>... clsArray) {
		List<MatchDescriptor> matchList = new ArrayList<MatchDescriptor>(0);
		if ( clsArray == null || clsArray.length == 0 || clsArray[0] == null ) {
			return matchList;
		}
		List<Class<?>> clsList = Arrays.asList(clsArray);
		matchList.addAll(
		getCurrentMapInUse()
			.values()
			.stream()
			.flatMap(List::stream)
			.filter( jce -> jce.getImplementingTypes()
								.stream()
								.filter( scls -> clsList
													.stream()
													.filter( cls -> cls != null && cls.isAssignableFrom(scls) )
													.count() > 0)
								.count() > 0)
			.map( jce -> jce.getClassDescriptor(null) )
			.collect(Collectors.toList())
		);
		return matchList;
	}

	
	/**
	 * Retrieves all sub types Classes Results of given super-type classes list
	 * @param clsList  Collection of super-type classes
	 * @return (List&lt;{@link MatchDescriptor}&gt;) list of matches for the given criteria
	 */
	public List<MatchDescriptor> getSubTypesOf(Collection<Class<?>> clsList) {
		List<MatchDescriptor> matchList = new ArrayList<MatchDescriptor>(0);
		if ( clsList == null ) {
			return matchList;
		}
		matchList.addAll(
		getCurrentMapInUse()
			.values()
			.stream()
			.flatMap(List::stream)
			.filter( jce -> jce.getImplementingTypes()
								.stream()
								.filter( scls -> clsList
													.stream()
													.filter( cls -> cls != null && cls.isAssignableFrom(scls) )
													.count() > 0)
								.count() > 0)
			.map( jce -> jce.getClassDescriptor(null) )
			.collect(Collectors.toList())
		);
		return matchList;
	}

	/**
	 * Retrieves all Classes Results annotated with given annotation like class
	 * @param cls Annotation like class
	 * @return (List&lt;{@link MatchDescriptor}&gt;) list of matches for the given criteria
	 */
	public List<MatchDescriptor> getTypesAnnotatedWith(Class<? extends Annotation> cls) {
		List<MatchDescriptor> matchList = new ArrayList<MatchDescriptor>(0);
		if ( cls == null ) {
			return matchList;
		}
		matchList.addAll(
			getCurrentMapInUse()
				.values()
				.stream()
				.flatMap(List::stream)
				.filter(jce -> jce.seekClassAnnotationsClasses()
								.stream()
								.filter( annotationClass -> cls.isAssignableFrom( annotationClass ) )
								.count() > 0)
				.map( jce -> jce.getClassDescriptor(cls) )
				.collect(Collectors.toList())
		);
		
		return matchList;
	}

	/**
	 * Retrieves all Classes Results annotated with given annotation like classes array
	 * @param annotationClassArray Annotation like classes array
	 * @return (List&lt;{@link MatchDescriptor}&gt;) list of matches for the given criteria
	 */
	public List<MatchDescriptor> getTypesAnnotatedWith(@SuppressWarnings("unchecked") Class<? extends Annotation>... annotationClassArray) {
		List<MatchDescriptor> matchList = new ArrayList<MatchDescriptor>(0);
		if ( annotationClassArray == null || annotationClassArray.length == 0 || annotationClassArray[0] == null ) {
			return matchList;
		}
		List<Class<? extends Annotation>> annotationClassList = Arrays.asList(annotationClassArray);
		matchList.addAll(
			getCurrentMapInUse()
				.values()
				.stream()
				.flatMap(List::stream)
				.map( jce -> jce.getClassDescriptForAnnotations(annotationClassList) )
				.flatMap(Collection::stream)
				.collect(Collectors.toList())
		);
		return matchList;
	}
	
	/**
	 * Retrieves all Classes Results annotated with given annotation like classes list
	 * @param annotationClassCollection Collection of Annotation like classes
	 * @return (List&lt;{@link MatchDescriptor}&gt;) list of matches for the given criteria
	 */
	public List<MatchDescriptor> getTypesAnnotatedWith(Collection<Class<? extends Annotation>> annotationClassCollection) {
		List<MatchDescriptor> matchList = new ArrayList<MatchDescriptor>(0);
		if ( annotationClassCollection == null  ) {
			return matchList;
		}
		matchList.addAll(
			getCurrentMapInUse()
				.values()
				.stream()
				.flatMap(List::stream)
				.map( jce -> jce.getClassDescriptForAnnotations(annotationClassCollection) )
				.flatMap(Collection::stream)
				.collect(Collectors.toList())
		);
		return matchList;
	}
	
	/**
	 * Retrieves all Methods Results annotated with given annotation like class
	 * @param cls Annotation like class
	 * @return (List&lt;{@link MatchDescriptor}&gt;) list of matches for the given criteria
	 */
	public List<MatchDescriptor> getMethodsAnnotatedWith(Class<? extends Annotation> cls) {
		List<MatchDescriptor> matchList = new ArrayList<MatchDescriptor>(0);
		if ( cls == null ) {
			return matchList;
		}
		matchList.addAll(
			getCurrentMapInUse()
				.values()
				.stream()
				.flatMap(List::stream)
				.map(jce -> { 
					if (jce.seekMethodsAnnotationClasses()
					.stream()
					.filter( annotationClass -> cls.isAssignableFrom( annotationClass ) )
					.count() > 0) {
						return jce.getMethodsAnnotatedWith(cls);
					} else {
						return new ArrayList<MatchDescriptor>(0);
					}
				})
				.flatMap( Collection::stream )
				.collect(Collectors.toList())
		);
		
		return matchList;
	}

	/**
	 * Retrieves all Methods Results annotated with given annotation like classes array
	 * @param annotationClassArray Annotation like classes array
	 * @return (List&lt;{@link MatchDescriptor}&gt;) list of matches for the given criteria
	 */
	public List<MatchDescriptor> getMethodsAnnotatedWith(@SuppressWarnings("unchecked") Class<? extends Annotation>... annotationClassArray) {
		List<MatchDescriptor> matchList = new ArrayList<MatchDescriptor>(0);
		if ( annotationClassArray == null || annotationClassArray.length == 0 || annotationClassArray[0] == null ) {
			return matchList;
		}
		List<Class<? extends Annotation>> annotationClassList = Arrays.asList(annotationClassArray);
		matchList.addAll(
			getCurrentMapInUse()
				.values()
				.stream()
				.flatMap(List::stream)
				.map(jce -> { 
					return jce.seekMethodsAnnotationClasses()
					.stream()
					.filter( annotationClass -> isAnnotationClassPresentInList(annotationClass, annotationClassList) )
					.map(annotationClass -> jce.getMethodsAnnotatedWith(annotationClass))
					.flatMap(Collection::stream)
					.collect(Collectors.toList());
				})
				.flatMap( List::stream )
				.collect(Collectors.toList())
		);
		
		return matchList;
	}

	
	/**
	 * Retrieves all Methods Results annotated with given annotation like classes list
	 * @param annotationClassCollection Collection of Annotation like classes
	 * @return (List&lt;{@link MatchDescriptor}&gt;) list of matches for the given criteria
	 */
	public List<MatchDescriptor> getMethodsAnnotatedWith(Collection<Class<? extends Annotation>> annotationClassCollection) {
		List<MatchDescriptor> matchList = new ArrayList<MatchDescriptor>(0);
		if ( annotationClassCollection == null  ) {
			return matchList;
		}
		matchList.addAll(
			getCurrentMapInUse()
				.values()
				.stream()
				.flatMap(List::stream)
				.map(jce -> { 
					return jce.seekMethodsAnnotationClasses()
					.stream()
					.filter( annotationClass -> isAnnotationClassPresentInList(annotationClass, annotationClassCollection) )
					.map(annotationClass -> jce.getMethodsAnnotatedWith(annotationClass))
					.flatMap(Collection::stream)
					.collect(Collectors.toList());
				})
				.flatMap( List::stream )
				.collect(Collectors.toList())
		);
		
		return matchList;
	}

	/**
	 * Retrieves all Fields Results annotated with given annotation like class
	 * @param cls Annotation like class
	 * @return (List&lt;{@link MatchDescriptor}&gt;) list of matches for the given criteria
	 */
	public List<MatchDescriptor> getFieldsAnnotatedWith(Class<? extends Annotation> cls) {
		List<MatchDescriptor> matchList = new ArrayList<MatchDescriptor>(0);
		if ( cls == null ) {
			return matchList;
		}
		matchList.addAll(
			getCurrentMapInUse()
				.values()
				.stream()
				.flatMap(List::stream)
				.map(jce -> { 
					if (jce.seekFieldsAnnotationClasses()
					.stream()
					.filter( annotationClass -> cls.isAssignableFrom( annotationClass ) )
					.count() > 0) {
						return jce.getFieldsAnnotatedWith(cls);
					} else {
						return new ArrayList<MatchDescriptor>(0);
					}
				})
				.flatMap( Collection::stream )
				.collect(Collectors.toList())
		);
		
		return matchList;
	}
	

	/**
	 * Retrieves all Fields Results annotated with given annotation like classes array
	 * @param annotationClassArray Annotation like classes array
	 * @return (List&lt;{@link MatchDescriptor}&gt;) list of matches for the given criteria
	 */
	public List<MatchDescriptor> getFieldsAnnotatedWith(@SuppressWarnings("unchecked") Class<? extends Annotation>... annotationClassArray) {
		List<MatchDescriptor> matchList = new ArrayList<MatchDescriptor>(0);
		if ( annotationClassArray == null || annotationClassArray.length == 0 || annotationClassArray[0] == null ) {
			return matchList;
		}
		List<Class<? extends Annotation>> annotationClassList = Arrays.asList(annotationClassArray);
		matchList.addAll(
			getCurrentMapInUse()
				.values()
				.stream()
				.flatMap(List::stream)
				.map(jce -> { 
					return jce.seekFieldsAnnotationClasses()
					.stream()
					.filter( annotationClass -> isAnnotationClassPresentInList(annotationClass, annotationClassList) )
					.map(annotationClass -> jce.getFieldsAnnotatedWith(annotationClass))
					.flatMap(Collection::stream)
					.collect(Collectors.toList());
				})
				.flatMap( List::stream )
				.collect(Collectors.toList())
		);
		
		return matchList;
	}

	
	/**
	 * Retrieves all Fields Results annotated with given annotation like classes list
	 * @param annotationClassCollection Collection of Annotation like classes
	 * @return (List&lt;{@link MatchDescriptor}&gt;) list of matches for the given criteria
	 */
	public List<MatchDescriptor> getFieldsAnnotatedWith(Collection<Class<? extends Annotation>> annotationClassCollection) {
		List<MatchDescriptor> matchList = new ArrayList<MatchDescriptor>(0);
		if ( annotationClassCollection == null ) {
			return matchList;
		}
		matchList.addAll(
			getCurrentMapInUse()
				.values()
				.stream()
				.flatMap(List::stream)
				.map(jce -> { 
					return jce.seekFieldsAnnotationClasses()
					.stream()
					.filter( annotationClass -> isAnnotationClassPresentInList(annotationClass, annotationClassCollection) )
					.map(annotationClass -> jce.getFieldsAnnotatedWith(annotationClass))
					.flatMap(Collection::stream)
					.collect(Collectors.toList());
				})
				.flatMap( List::stream )
				.collect(Collectors.toList())
		);
		
		return matchList;
	}
	

	/**
	 * Retrieves all Methods Parameters Results annotated with given annotation like class
	 * @param cls Annotation like class
	 * @return (List&lt;{@link MatchDescriptor}&gt;) list of matches for the given criteria
	 */
	public List<MatchDescriptor> getMethodParametersAnnotatedWith(Class<? extends Annotation> cls) {
		List<MatchDescriptor> matchList = new ArrayList<MatchDescriptor>(0);
		if ( cls == null ) {
			return matchList;
		}
		matchList.addAll(
			getCurrentMapInUse()
				.values()
				.stream()
				.flatMap(List::stream)
				.map(jce -> { 
					if (jce.seekMethodParametersAnnotationClasses()
					.stream()
					.filter( annotationClass -> cls.isAssignableFrom( annotationClass ) )
					.count() > 0) {
						return jce.getMethodParametersAnnotatedWith(cls);
					} else {
						return new ArrayList<MatchDescriptor>(0);
					}
				})
				.flatMap( Collection::stream )
				.collect(Collectors.toList())
		);
		
		return matchList;
	}
	

	/**
	 * Retrieves all Methods Parameters Results annotated with given annotation like classes array
	 * @param annotationClassArray Annotation like classes array
	 * @return (List&lt;{@link MatchDescriptor}&gt;) list of matches for the given criteria
	 */
	public List<MatchDescriptor> getMethodParametersAnnotatedWith(@SuppressWarnings("unchecked") Class<? extends Annotation>... annotationClassArray) {
		List<MatchDescriptor> matchList = new ArrayList<MatchDescriptor>(0);
		if ( annotationClassArray == null || annotationClassArray.length == 0 || annotationClassArray[0] == null ) {
			return matchList;
		}
		List<Class<? extends Annotation>> annotationClassList = Arrays.asList(annotationClassArray);
		matchList.addAll(
			getCurrentMapInUse()
				.values()
				.stream()
				.flatMap(List::stream)
				.map(jce -> { 
					return jce.seekMethodParametersAnnotationClasses()
					.stream()
					.filter( annotationClass -> isAnnotationClassPresentInList(annotationClass, annotationClassList) )
					.map(annotationClass -> jce.getMethodParametersAnnotatedWith(annotationClass))
					.flatMap(Collection::stream)
					.collect(Collectors.toList());
				})
				.flatMap( List::stream )
				.collect(Collectors.toList())
		);
		
		return matchList;
	}

	
	/**
	 * Retrieves all Methods Parameters Results annotated with given annotation like classes list
	 * @param annotationClassCollection Collection of Annotation like classes
	 * @return (List&lt;{@link MatchDescriptor}&gt;) list of matches for the given criteria
	 */
	public List<MatchDescriptor> getMethodParametersAnnotatedWith(Collection<Class<? extends Annotation>> annotationClassCollection) {
		List<MatchDescriptor> matchList = new ArrayList<MatchDescriptor>(0);
		if ( annotationClassCollection == null  ) {
			return matchList;
		}
		matchList.addAll(
			getCurrentMapInUse()
				.values()
				.stream()
				.flatMap(List::stream)
				.map(jce -> { 
					return jce.seekMethodParametersAnnotationClasses()
					.stream()
					.filter( annotationClass -> isAnnotationClassPresentInList(annotationClass, annotationClassCollection) )
					.map(annotationClass -> jce.getMethodParametersAnnotatedWith(annotationClass))
					.flatMap(Collection::stream)
					.collect(Collectors.toList());
				})
				.flatMap( List::stream )
				.collect(Collectors.toList())
		);
		
		return matchList;
	}
	
	private static final boolean isAnnotationClassPresentInList(Class<? extends Annotation> annotationClass, List<Class<? extends Annotation>> annotationClassList) {
		return isAnnotationClassPresentInList(annotationClass, annotationClassList);
	}
	
	private static final boolean isAnnotationClassPresentInList(Class<? extends Annotation> annotationClass, Collection<Class<? extends Annotation>> annotationClassCollection) {
		return annotationClassCollection
				.stream()
				.filter(cls -> cls.isAssignableFrom( annotationClass ))
				.count() > 0;
	}
	
	/**
	 * Creates a new {@link Reflection} element, accordingly to the given configuration, present in the parameter of type {@link ClassPathConfig} 
	 * @param configuration Required {@link Reflection} class-path seek {@link ClassPathConfig} configuration
	 * @return New {@link Reflection} instance
	 * @throws NullPointerException In case it's passed any null {@link ClassPathConfig} instance
	 */
	public static final Reflections newReflections(ClassPathConfig configuration) throws NullPointerException {
		if ( configuration == null ) {
			throw new NullPointerException("ClassPathConfig element cannot be null requiring a new reflections reader");
		}
		return new Reflections(configuration);
	}

	/**
	 * Creates a new {@link Reflection} element, , accordingly to the given configuration parameters, present in the parameter of type {@link ClassPathConfigBuilder}
	 * @param builder Required {@link Reflection} class-path seek {@link ClassPathConfigBuilder} configuration builder, containing required parameters
	 * @return New {@link Reflection} instance
	 * @throws NullPointerException In case it's passed any null {@link ClassPathConfig} instance
	 */
	public static final Reflections newReflections(ClassPathConfigBuilder builder) throws NullPointerException {
		if ( builder == null ) {
			throw new NullPointerException("ClassPathConfigBuilder element cannot be null requiring a new reflections reader");
		}
		return new Reflections(builder.build());
	}

	/**
	 * Retrieve a singleton version of the Reflections, any configuration will be intented no-sessiom 
	 * enabled and in case of mismatch the value will be changed. After the first call any further 
	 * configuration will be ignored.
	 * @param configuration Required singleton {@link Reflection} class-path seek {@link ClassPathConfig} configuration
	 * @return Singleton {@link Reflection} reference
	 * @throws NullPointerException In case it's passed any null {@link ClassPathConfig} instance at the first call
	 */
	public static final synchronized Reflections getSigletonInstance(ClassPathConfig configuration) throws NullPointerException {
		if ( instance == null ) {
			if ( configuration == null ) {
				throw new NullPointerException("ClassPathConfig element cannot be null requiring a static reflections reader for first time");
			}
			if ( configuration.enablePersistenceOfData() ) {
				// Copy package / class-path inclusions/exclusions and
				// disable persistence of data storage. A Singleton is a 
				if ( ArgumentsHelper.traceAllLevels || ArgumentsHelper.traceReflectionsLevel )
					LoggerHelper.logTrace("Reflections::getSigletonInstance", "Correcting a persistence enable ClassPath Configuration to non persisnt!!");
				final ClassPathConfigBuilder builder = ClassPathConfigBuilder.start();
				builder.disablePersistenceOfData();
				configuration.getClassPathExclusionList()
							.forEach(eclusion -> builder.excludeClassPathEntryByName(eclusion));
				configuration.getClassPathInclusionList()
							.forEach(eclusion -> builder.includeClassPathEntryByName(eclusion));
				configuration.getPackageExclusionList()
							.forEach(eclusion -> builder.excludePackageByName(eclusion));
				configuration.getPackageInclusionList()
							.forEach(eclusion -> builder.includePackageByName(eclusion));
				configuration = builder.build();
			}
			instance =  new Reflections(configuration);
		}
		return instance;
	}

	/**
	 * It will reset the Persistence of the Reflection Scanning responses,
	 * from latest scan. It will cause a full JVM entries reload on the next
	 * session request. It's useful if you add dynamically elements to the JVM
	 * Class-Path at runtime.
	 */
	public static final void resetPersistenceStorage() {
		SAVED_MAP_ENTRIES.clear();
		savedMapConfiguration = null;
		if ( ArgumentsHelper.traceAllLevels || ArgumentsHelper.traceReflectionsLevel )
			LoggerHelper.logTrace("Reflections::resetPersistenceStorage", "Persistent Reflections data just clean!!");
	}

	/**
	 * It will reset the Persistence of the Reflection Scanning responses,
	 * as described in method {@link Reflections#resetPersistenceStorage()}.
	 * Then it will reload immediately the fresh Class-Path Java Entities from the 
	 * JVM Entries (accordingly to the {@link ClassPathConfig} specifications.
	 * @param config The {@link ClassPathConfig} JVM and Java Entries filtering configuration element.
	 * @return The new just loaded {@link Reflections} version. 
	 */
	public static final Reflections resetPersistenceStorageAndReload(ClassPathConfig config) {
		if ( ! config.enablePersistenceOfData() ) {
			if ( ArgumentsHelper.traceAllLevels || ArgumentsHelper.traceReflectionsLevel )
				LoggerHelper.logTrace("Reflections::resetPersistenceStorageAndReload", "Correcting a no-persistence enable ClassPath Configuration to persisnt!!");
			final ClassPathConfigBuilder builder = ClassPathConfigBuilder.start();
			config.getClassPathExclusionList()
						.forEach(eclusion -> builder.excludeClassPathEntryByName(eclusion));
			config.getClassPathInclusionList()
						.forEach(eclusion -> builder.includeClassPathEntryByName(eclusion));
			config.getPackageExclusionList()
						.forEach(eclusion -> builder.excludePackageByName(eclusion));
			config.getPackageInclusionList()
						.forEach(eclusion -> builder.includePackageByName(eclusion));
			config = builder.build();
			
		}
		resetPersistenceStorage();
		if ( ArgumentsHelper.traceAllLevels || ArgumentsHelper.traceReflectionsLevel )
			LoggerHelper.logTrace("Reflections::resetPersistenceStorageAndReload", "Persistent Reflections load in progress!!");
		return new Reflections(config);
	}
}
