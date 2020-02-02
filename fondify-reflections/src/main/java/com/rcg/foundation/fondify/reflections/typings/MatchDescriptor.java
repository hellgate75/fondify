/**
 * 
 */
package com.rcg.foundation.fondify.reflections.typings;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class MatchDescriptor {

	private Class<?> matchClass;
	private Field matchField;
	private Method matchMethod;
	private Parameter matchParameter;
	private Class<? extends Annotation> searchAnnotationClass;
	private MatchLevel matchLevel;
	/**
	 * @param matchClass
	 * @param matchField
	 * @param matchMethod
	 * @param matchParameter
	 */
	protected MatchDescriptor(Class<?> matchClass, Field matchField, Method matchMethod, Parameter matchParameter, Class<? extends Annotation> searchAnnotationClass) {
		super();
		this.matchClass = matchClass;
		this.matchField = matchField;
		this.matchMethod = matchMethod;
		this.matchParameter = matchParameter;
		this.searchAnnotationClass = searchAnnotationClass;
		if ( matchParameter != null ) {
			matchLevel = MatchLevel.PARAMETER;
		} else if ( matchMethod != null ) {
			matchLevel = MatchLevel.METHOD;
		} else if ( matchField != null ) {
			matchLevel = MatchLevel.FIELD;
		} else if ( matchClass != null ) {
			matchLevel = MatchLevel.TYPE;
		}
	}
	
	/**
	 * @return the matchClass
	 */
	public Class<?> getMatchClass() {
		return matchClass;
	}
	/**
	 * @return the matchField
	 */
	public Field getMatchField() {
		return matchField;
	}
	/**
	 * @return the matchMethod
	 */
	public Method getMatchMethod() {
		return matchMethod;
	}
	/**
	 * @return the matchParameter
	 */
	public Parameter getMatchParameter() {
		return matchParameter;
	}
	/**
	 * @return the searchAnnotationClass
	 */
	public Class<? extends Annotation> getSearchAnnotationClass() {
		return searchAnnotationClass;
	}
	/**
	 * @return the matchLevel
	 */
	public MatchLevel getMatchLevel() {
		return matchLevel;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((matchClass == null) ? 0 : matchClass.hashCode());
		result = prime * result + ((matchField == null) ? 0 : matchField.hashCode());
		result = prime * result + ((matchLevel == null) ? 0 : matchLevel.hashCode());
		result = prime * result + ((matchMethod == null) ? 0 : matchMethod.hashCode());
		result = prime * result + ((matchParameter == null) ? 0 : matchParameter.hashCode());
		result = prime * result + ((searchAnnotationClass == null) ? 0 : searchAnnotationClass.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MatchDescriptor other = (MatchDescriptor) obj;
		if (matchClass == null) {
			if (other.matchClass != null)
				return false;
		} else if (!matchClass.equals(other.matchClass))
			return false;
		if (matchField == null) {
			if (other.matchField != null)
				return false;
		} else if (!matchField.equals(other.matchField))
			return false;
		if (matchLevel != other.matchLevel)
			return false;
		if (matchMethod == null) {
			if (other.matchMethod != null)
				return false;
		} else if (!matchMethod.equals(other.matchMethod))
			return false;
		if (matchParameter == null) {
			if (other.matchParameter != null)
				return false;
		} else if (!matchParameter.equals(other.matchParameter))
			return false;
		if (searchAnnotationClass == null) {
			if (other.searchAnnotationClass != null)
				return false;
		} else if (!searchAnnotationClass.equals(other.searchAnnotationClass))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MatchDescriptor [matchClass=" + matchClass + ", matchField=" + matchField + ", matchMethod="
				+ matchMethod + ", matchParameter=" + matchParameter + ", searchAnnotationClass="
				+ searchAnnotationClass + ", matchLevel=" + matchLevel + "]";
	}

	
	
}
