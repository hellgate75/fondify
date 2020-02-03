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

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class Reflections {

	public static List<String> SYSTEM_LIBRARIES_EXCLUSIONS = new ArrayList<>(0); 
		static {
			SYSTEM_LIBRARIES_EXCLUSIONS.addAll(
					Arrays.asList("asm-tree", "j2objc-annotations", "aether-",
							"jackson-", "maven-", "slf4j-", "log4j-", "plexus-", "hazelcast-", "commons-beanutils-", 
							"guava-","common-codec-", "stax2-api-", "jakarta.", "commons-digester-", "woodstox-core-",
							"jdom2", "asm-", "commons-collections-", "commons-logging-", "commons-lang3-",
							"snakeyaml-", "sisu-", "commons-validator-", "rror_prone_annotations-",
							"procyon-", "jdependency-", "jsr305-", "animal-sniffer-", "commons-io-", "commons-codec-",
							"powermock-", "mockito-", "junit-")
			);
		}
	
	private static final Map<String, List<JavaClassEntity>> SAVED_MAP_ENTRIES = new ConcurrentHashMap<String, List<JavaClassEntity>>(0);
	private static boolean loadedMap = false;
	private static Reflections instance = null;
	
	private boolean sessionEnabled = false;
	private final Map<String, List<JavaClassEntity>> entriesMap = new ConcurrentHashMap<String, List<JavaClassEntity>>(0);
	
	/**
	 * Default protected constructor
	 */
	private Reflections(ClassPathConfig configuration) {
		sessionEnabled = configuration.enablePersistenceOfData();
		if ( sessionEnabled && ! loadedMap ) {
			SAVED_MAP_ENTRIES.putAll(
				compileClassPathEntries(
					loadClassPathEntries(
							createExecutableConfig(configuration)
					),
				configuration
				)
			);
			loadedMap = SAVED_MAP_ENTRIES.size() > 0;
		} else if ( ! sessionEnabled ) {
			entriesMap.putAll(
					compileClassPathEntries(
							loadClassPathEntries(
									createExecutableConfig(configuration)
							),
						configuration
						)
			);
		}
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

}
