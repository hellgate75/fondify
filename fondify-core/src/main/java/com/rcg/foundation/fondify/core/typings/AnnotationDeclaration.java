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
	private Class<?> annotationClass;
	private Method annotationMethod;
	private Parameter annotationParameter;
	private Field annotationField;
	/**
	 * @param annotationClass
	 * @param annotationMethod
	 * @param annotationParameter
	 * @param annotationField
	 */
	public AnnotationDeclaration(Class<? extends Annotation> annotationDeclarationClass, Class<?> annotationClass, Field annotationField, Method annotationMethod,
			Parameter annotationParameter) {
		super();
		this.annotationClass = annotationClass;
		this.annotationMethod = annotationMethod;
		this.annotationParameter = annotationParameter;
		this.annotationField = annotationField;
		if (annotationParameter != null) {
			List<Annotation> aList = Arrays.asList(annotationParameter.getAnnotations()).stream()
					.filter( an -> annotationDeclarationClass.isAssignableFrom(an.getClass()) )
					.collect(Collectors.toList());
			if ( aList.size() > 0 ) {
				this.annotation = aList.get(0);
			}
		} else if (annotationMethod != null) {
			List<Annotation> aList = Arrays.asList(annotationMethod.getAnnotations()).stream()
					.filter( an -> annotationDeclarationClass.isAssignableFrom(an.getClass()) )
					.collect(Collectors.toList());
			if ( aList.size() > 0 ) {
				this.annotation = aList.get(0);
			}
		} else if (annotationField != null) {
			List<Annotation> aList = Arrays.asList(annotationField.getAnnotations()).stream()
					.filter( an -> annotationDeclarationClass.isAssignableFrom(an.getClass()) )
					.collect(Collectors.toList());
			if ( aList.size() > 0 ) {
				this.annotation = aList.get(0);
			}
		} else if (annotationClass != null) {
			List<Annotation> aList = Arrays.asList(annotationClass.getAnnotations()).stream()
					.filter( an -> annotationDeclarationClass.isAssignableFrom(an.getClass()) )
					.collect(Collectors.toList());
			if ( aList.size() > 0 ) {
				this.annotation = aList.get(0);
			}
		}
	}
	/**
	 * @return the annotation
	 */
	public Annotation getAnnotation() {
		return annotation;
	}
	/**
	 * @return the annotationClass
	 */
	public Class<?> getAnnotationClass() {
		return annotationClass;
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
		if (annotationClass == null) {
			return AnnotationDeclarationType.NONE;
		}
		if ( annotationField == null && annotationMethod == null &&
				annotationParameter == null ) {
				return AnnotationDeclarationType.TYPE;
		}
		if ( annotationField != null && annotationMethod == null &&
			annotationParameter == null ) {
			return AnnotationDeclarationType.FIELD;
		}
		if ( annotationMethod != null &&
				annotationParameter == null ) {
				return AnnotationDeclarationType.METHOD;
		}
		if ( annotationParameter != null ) {
				return AnnotationDeclarationType.PARAMETER;
		}
		return AnnotationDeclarationType.NONE;
	}
}