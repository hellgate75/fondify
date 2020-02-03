/**
 * 
 */
package com.rcg.foundation.fondify.reflections.typings;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.rcg.foundation.fondify.reflections.helpers.ClassPathHelper;


/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class JavaClassEntity {
	private String origin;
	private String packageName;
	private Class<?> baseClass;
	/**
	 * 
	 */
	public JavaClassEntity(JavaEntry entry, ClassLoader... classLoaders) {
		try {
			this.origin = entry.getOrigin();
			this.baseClass = ClassPathHelper.loadClassFromName(entry.getClassName(), Arrays.asList(classLoaders));
			this.packageName = baseClass.getPackage().getName();
		} catch (Exception e) {
//			LoggerHelper.logError("", String.format("Exception occurred during JavaClassEntity intialization: error for class: %s, described as follow:", entry != null ? entry.getClassName() : "<NULL>"), e);
			throw new RuntimeException(String.format("Exception occurred during initializing class : %s", entry != null ? entry.getClassName() : "<NULL>)"), e);
		} catch (Error e) {
//			LoggerHelper.logError("", String.format("Error occurred during JavaClassEntity intialization: error for class: %s, described as follow:", entry != null ? entry.getClassName() : "<NULL>"), e);
			throw new RuntimeException(String.format("MAIN ERROR occurred during initializing class : %s", entry != null ? entry.getClassName() : "<NULL>)"), e);
		}
	}
	
	/**
	 * @return the origin
	 */
	public String getOrigin() {
		return origin;
	}
	/**
	 * @return the packageName
	 */
	public String getPackageName() {
		return packageName;
	}
	
	/**
	 * @return the baseClass
	 */
	public Class<?> getBaseClass() {
		return baseClass;
	}

	/**
	 * @return the baseClass
	 */
	public Collection<Class<?>> getImplementingTypes() {
		List<Class<?>> implementingTypes = new ArrayList<>();
		try {
			Type superType = this.baseClass.getGenericSuperclass();
			if ( superType != null ) {
				implementingTypes.add(
				com.google.common.reflect.TypeToken.of(superType).getRawType()
				);
			}
		} catch (Exception ex) {
		} catch (Error er) {
		}
		try {
			implementingTypes.addAll(
				Arrays.asList(this.baseClass.getGenericInterfaces())
					.stream()
					.map(type -> com.google.common.reflect.TypeToken.of(type).getRawType() )
					.collect(Collectors.toList())
			);
		} catch (Exception ex) {
		} catch (Error er) {
		}
		return implementingTypes;
	}
	

	/**
	 * @return the classAnnotations
	 */
	public Collection<Class<? extends Annotation>> seekClassAnnotationsClasses() {
		List<Class<? extends Annotation>> annotations = new ArrayList<>(0);
		annotations.addAll(
			Arrays.asList(this.baseClass.getDeclaredAnnotations())
			.stream()
			.map( ann -> (Class<? extends Annotation>)ann.getClass() )
			.distinct()
			.collect(Collectors.toList())
			
		);
		return annotations;
	}

	public Collection<Class<? extends Annotation>> seekMethodsAnnotationClasses() {
		List<Class<? extends Annotation>> annotations = new ArrayList<>(0);
		annotations.addAll(
			Arrays.asList(this.baseClass.getDeclaredMethods())
				.stream()
				.map(method -> Arrays.asList(method.getDeclaredAnnotations()))
				.flatMap(List::stream)
				.map( ann -> (Class<? extends Annotation>)ann.getClass() )
				.distinct()
				.collect(Collectors.toList())
		);
		return annotations;
	}


	public Collection<Class<? extends Annotation>> seekMethodParametersAnnotationClasses() {
		List<Class<? extends Annotation>> annotations = new ArrayList<>(0);
		annotations.addAll(
			Arrays.asList(this.baseClass.getDeclaredMethods())
				.stream()
				.map(method -> Arrays.asList(method.getParameters()))
				.flatMap(List::stream)
				.map(parameter -> Arrays.asList(parameter.getDeclaredAnnotations()))
				.flatMap(List::stream)
				.map( ann -> (Class<? extends Annotation>)ann.getClass() )
				.distinct()
				.collect(Collectors.toList())
		);
		return annotations;
	}
	
	public Collection<Class<? extends Annotation>> seekFieldsAnnotationClasses() {
		List<Class<? extends Annotation>> annotations = new ArrayList<>(0);
		annotations.addAll(
			Arrays.asList(this.baseClass.getDeclaredFields())
				.stream()
				.map(method -> Arrays.asList(method.getDeclaredAnnotations()))
				.flatMap(List::stream)
				.map( ann -> (Class<? extends Annotation>)ann.getClass() )
				.distinct()
				.collect(Collectors.toList())
		);
		return annotations;
	}
	
	public MatchDescriptor getClassDescriptor(Class<? extends Annotation> annotationClass) {
		return new MatchDescriptor(this.baseClass, null, null, null, annotationClass);
	}

	public Collection<MatchDescriptor> getClassDescriptForAnnotations(List<Class<? extends Annotation>> annotationClassList) {
		return getClassDescriptForAnnotations(annotationClassList);
	}
	
	public Collection<MatchDescriptor> getClassDescriptForAnnotations(Collection<Class<? extends Annotation>> annotationClassList) {
		List<MatchDescriptor> descriptorsList = new ArrayList<>(0);
		descriptorsList.addAll(
			annotationClassList
				.stream()
				.filter( cls -> cls != null )
				.filter(cls -> this.baseClass.getDeclaredAnnotation(cls) != null)
				.map(cls -> new MatchDescriptor(this.baseClass, null, null, null, cls))
				.collect(Collectors.toList())
		);
		return descriptorsList;
	}

	public Collection<MatchDescriptor> getMethodsAnnotatedWith(Class<? extends Annotation> annotationClass) {
		List<MatchDescriptor> methodsList = new ArrayList<>(0);

		methodsList.addAll(
			Arrays.asList(this.baseClass.getDeclaredMethods())
			.stream()
			.map( method -> new MatchDescriptor(this.baseClass, null, method, null, annotationClass) )
			.filter( descriptor -> descriptor.getMatchMethod().getDeclaredAnnotation(annotationClass) != null )
			.collect(Collectors.toList())
		);
		return methodsList;
	}
	
	public Collection<MatchDescriptor> getFieldsAnnotatedWith(Class<? extends Annotation> annotationClass) {
		List<MatchDescriptor> fieldsList = new ArrayList<>(0);
		fieldsList.addAll(
				Arrays.asList(this.baseClass.getDeclaredFields())
				.stream()
				.map( field -> new MatchDescriptor(this.baseClass, field, null, null, annotationClass) )
				.filter( descriptor -> descriptor.getMatchField().getDeclaredAnnotation(annotationClass) != null )
				.collect(Collectors.toList())
		);
		return fieldsList;
	}

	
	public Collection<MatchDescriptor> getMethodParametersAnnotatedWith(Class<? extends Annotation> annotationClass) {
		List<MatchDescriptor> parametersList = new ArrayList<>(0);
		parametersList.addAll(
				Arrays.asList(this.baseClass.getDeclaredMethods())
				.stream()
				.collect( Collectors.toMap(method -> method, method -> Arrays.asList(method.getParameters())) )
				.entrySet()
				.parallelStream()
				.map( entry -> entry.getValue()
									.parallelStream()
									.map(parameter -> new MatchDescriptor(this.baseClass, null, entry.getKey(), parameter, annotationClass) )
									.collect(Collectors.toList())
				)
				.flatMap(List::stream)
				.collect(Collectors.toList())
		);
		return parametersList;
	}
	
}
