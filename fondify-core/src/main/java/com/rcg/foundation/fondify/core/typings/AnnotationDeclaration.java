package com.rcg.foundation.fondify.core.typings;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AnnotationDeclaration {
	private Annotation annotation = null;
	private Class<?> annotatedClass;
	private Class<? extends Annotation> annotationDeclarationClass = null;
	private Method annotationMethod;
	private Parameter annotationParameter;
	private Field annotationField;
	private AnnotationDeclarationType declarationType = AnnotationDeclarationType.NONE; 
	/**
	 * @param annotatedClass
	 * @param annotationMethod
	 * @param annotationParameter
	 * @param annotationField
	 */
	public AnnotationDeclaration(Class<? extends Annotation> annotationDeclarationClass, Class<?> annotatedClass, Field annotationField, Method annotationMethod,
			Parameter annotationParameter) {
		super();
		this.annotationDeclarationClass = annotationDeclarationClass;
		this.annotatedClass = annotatedClass;
		this.annotationMethod = annotationMethod;
		this.annotationParameter = annotationParameter;
		this.annotationField = annotationField;
		if (annotationParameter != null) {
			List<Annotation> aList = Arrays.asList(annotationParameter.getDeclaredAnnotations()).stream()
					.filter( an -> annotationDeclarationClass.isAssignableFrom(an.getClass()) )
					.collect(Collectors.toList());
			if ( aList.size() > 0 ) {
				this.annotation = aList.get(0);
			}
			declarationType = AnnotationDeclarationType.PARAMETER;
		} else if (annotationMethod != null) {
			List<Annotation> aList = Arrays.asList(annotationMethod.getDeclaredAnnotations()).stream()
					.filter( an -> annotationDeclarationClass.isAssignableFrom(an.getClass()) )
					.collect(Collectors.toList());
			if ( aList.size() > 0 ) {
				this.annotation = aList.get(0);
			}
			declarationType = AnnotationDeclarationType.METHOD;
		} else if (annotationField != null) {
			List<Annotation> aList = Arrays.asList(annotationField.getDeclaredAnnotations()).stream()
					.filter( an -> annotationDeclarationClass.isAssignableFrom(an.getClass()) )
					.collect(Collectors.toList());
			if ( aList.size() > 0 ) {
				this.annotation = aList.get(0);
			}
			declarationType = AnnotationDeclarationType.FIELD;
		} else if (annotatedClass != null) {
			List<Annotation> aList = Arrays.asList(annotatedClass.getDeclaredAnnotations()).stream()
					.filter( an -> annotationDeclarationClass.isAssignableFrom(an.getClass()) )
					.collect(Collectors.toList());
			if ( aList.size() > 0 ) {
				this.annotation = aList.get(0);
			}
			declarationType = AnnotationDeclarationType.TYPE;
		}
	}
	/**
	 * @return the annotation
	 */
	public Annotation getAnnotation() {
		return annotation;
	}
	
	
	/**
	 * @return the annotationDeclarationClass
	 */
	public Class<? extends Annotation> getAnnotationDeclarationClass() {
		return annotationDeclarationClass;
	}
	/**
	 * @return the annotatedClass
	 */
	public Class<?> getAnnotatedClass() {
		return annotatedClass;
	}
	/**
	 * @return the annotationMethod
	 */
	public Method getAnnotationMethod() {
		return annotationMethod;
	}
	/**
	 * @return the annotationParameter
	 */
	public Parameter getAnnotationParameter() {
		return annotationParameter;
	}
	/**
	 * @return the annotationField
	 */
	public Field getAnnotationField() {
		return annotationField;
	}
	
	public AnnotationDeclarationType getAnnotationDeclarationType() {
		return declarationType;
	}
}