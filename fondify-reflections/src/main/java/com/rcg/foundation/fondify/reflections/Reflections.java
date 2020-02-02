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

	private static final Map<String, List<JavaClassEntity>> SAVED_MAP_ENTRIES = new ConcurrentHashMap<String, List<JavaClassEntity>>(0);
	private static boolean loadedMap = false;
	private static Reflections instance = null;
	
	private boolean sessionEnabled = false;
	private final Map<String, List<JavaClassEntity>> entriesMap = new ConcurrentHashMap<String, List<JavaClassEntity>>(0);
	
	/**
	 * Default protected constructor
	 */
	private Reflections(ClassPathConfig configuration) {
		sessionEnabled = configuration.enableSessionData();
		if ( sessionEnabled && ! loadedMap ) {
			SAVED_MAP_ENTRIES.putAll(
				compileClassPathEntries(
					loadClassPathEntries(
							createExecutableConfig(configuration)
					)
				)
			);
			loadedMap = SAVED_MAP_ENTRIES.size() > 0;
		} else if ( ! sessionEnabled ) {
			entriesMap.putAll(
					compileClassPathEntries(
							loadClassPathEntries(
									createExecutableConfig(configuration)
							)
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
	public List<MatchDescriptor> getSubtypesOf(Class<?> cls) {
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
	public List<MatchDescriptor> getSubtypesOf(Class<?>... clsArray) {
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
	 * @param clsList  List of super-type classes
	 * @return (List&lt;{@link MatchDescriptor}&gt;) list of matches for the given criteria
	 */
	public List<MatchDescriptor> getSubtypesOf(List<Class<?>> clsList) {
		List<MatchDescriptor> matchList = new ArrayList<MatchDescriptor>(0);
		if ( clsList == null || clsList.size() == 0 || clsList.get(0) == null ) {
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
	public List<MatchDescriptor> getClassesAnnotatedWith(Class<? extends Annotation> cls) {
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
	public List<MatchDescriptor> getClassesAnnotatedWith(@SuppressWarnings("unchecked") Class<? extends Annotation>... annotationClassArray) {
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
				.flatMap(List::stream)
				.collect(Collectors.toList())
		);
		return matchList;
	}
	
	/**
	 * Retrieves all Classes Results annotated with given annotation like classes list
	 * @param annotationClassList Annotation like classes list
	 * @return (List&lt;{@link MatchDescriptor}&gt;) list of matches for the given criteria
	 */
	public List<MatchDescriptor> getClassesAnnotatedWith(List<Class<? extends Annotation>> annotationClassList) {
		List<MatchDescriptor> matchList = new ArrayList<MatchDescriptor>(0);
		if ( annotationClassList == null || annotationClassList.size() == 0 || 
				annotationClassList.get(0) == null ) {
			return matchList;
		}
		matchList.addAll(
			getCurrentMapInUse()
				.values()
				.stream()
				.flatMap(List::stream)
				.map( jce -> jce.getClassDescriptForAnnotations(annotationClassList) )
				.flatMap(List::stream)
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
				.flatMap( List::stream )
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
					.flatMap(List::stream)
					.collect(Collectors.toList());
				})
				.flatMap( List::stream )
				.collect(Collectors.toList())
		);
		
		return matchList;
	}

	
	/**
	 * Retrieves all Methods Results annotated with given annotation like classes list
	 * @param annotationClassList Annotation like classes list
	 * @return (List&lt;{@link MatchDescriptor}&gt;) list of matches for the given criteria
	 */
	public List<MatchDescriptor> getMethodsAnnotatedWith(List<Class<? extends Annotation>> annotationClassList) {
		List<MatchDescriptor> matchList = new ArrayList<MatchDescriptor>(0);
		if ( annotationClassList == null || annotationClassList.size() == 0 || 
				annotationClassList.get(0) == null ) {
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
					.filter( annotationClass -> isAnnotationClassPresentInList(annotationClass, annotationClassList) )
					.map(annotationClass -> jce.getMethodsAnnotatedWith(annotationClass))
					.flatMap(List::stream)
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
				.flatMap( List::stream )
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
					.flatMap(List::stream)
					.collect(Collectors.toList());
				})
				.flatMap( List::stream )
				.collect(Collectors.toList())
		);
		
		return matchList;
	}

	
	/**
	 * Retrieves all Fields Results annotated with given annotation like classes list
	 * @param annotationClassList Annotation like classes list
	 * @return (List&lt;{@link MatchDescriptor}&gt;) list of matches for the given criteria
	 */
	public List<MatchDescriptor> getFieldsAnnotatedWith(List<Class<? extends Annotation>> annotationClassList) {
		List<MatchDescriptor> matchList = new ArrayList<MatchDescriptor>(0);
		if ( annotationClassList == null || annotationClassList.size() == 0 || 
				annotationClassList.get(0) == null ) {
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
					.filter( annotationClass -> isAnnotationClassPresentInList(annotationClass, annotationClassList) )
					.map(annotationClass -> jce.getFieldsAnnotatedWith(annotationClass))
					.flatMap(List::stream)
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
				.flatMap( List::stream )
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
					.flatMap(List::stream)
					.collect(Collectors.toList());
				})
				.flatMap( List::stream )
				.collect(Collectors.toList())
		);
		
		return matchList;
	}

	
	/**
	 * Retrieves all Methods Parameters Results annotated with given annotation like classes list
	 * @param annotationClassList Annotation like classes list
	 * @return (List&lt;{@link MatchDescriptor}&gt;) list of matches for the given criteria
	 */
	public List<MatchDescriptor> getMethodParametersAnnotatedWith(List<Class<? extends Annotation>> annotationClassList) {
		List<MatchDescriptor> matchList = new ArrayList<MatchDescriptor>(0);
		if ( annotationClassList == null || annotationClassList.size() == 0 || 
				annotationClassList.get(0) == null ) {
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
					.filter( annotationClass -> isAnnotationClassPresentInList(annotationClass, annotationClassList) )
					.map(annotationClass -> jce.getMethodParametersAnnotatedWith(annotationClass))
					.flatMap(List::stream)
					.collect(Collectors.toList());
				})
				.flatMap( List::stream )
				.collect(Collectors.toList())
		);
		
		return matchList;
	}
	
	private static final boolean isAnnotationClassPresentInList(Class<? extends Annotation> annotationClass, List<Class<? extends Annotation>> annotationClassList) {
		return annotationClassList
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
			instance =  new Reflections(configuration);
		}
		return instance;
	}

}
