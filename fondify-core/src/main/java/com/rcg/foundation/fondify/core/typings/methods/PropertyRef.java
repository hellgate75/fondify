/**
 * 
 */
package com.rcg.foundation.fondify.core.typings.methods;

import java.lang.annotation.Annotation;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class PropertyRef {

	private String typeRef;

	private String elemRef;

	private String propertyDescr;
	
	private boolean typeProperty;
	
	private boolean valueProperty;

	
	private Annotation valueAnnotation = null;

	/**
	 * 
	 */
	public PropertyRef() {
		super();
	}

	/**
	 * @param typeRef
	 * @param elemRef
	 * @param propertyDescr
	 * @param typeProperty
	 * @param valueProperty
	 */
	public PropertyRef(String typeRef, String elemRef, String propertyDescr, boolean typeProperty,
			boolean valueProperty) {
		super();
		this.typeRef = typeRef;
		this.elemRef = elemRef;
		this.propertyDescr = propertyDescr;
		this.typeProperty = typeProperty;
		this.valueProperty = valueProperty;
	}

	/**
	 * @return the typeRef
	 */
	public String getTypeRef() {
		return typeRef;
	}

	/**
	 * @param typeRef the typeRef to set
	 */
	public void setTypeRef(String typeRef) {
		this.typeRef = typeRef;
	}

	/**
	 * @return the elemRef
	 */
	public String getElemRef() {
		return elemRef;
	}

	/**
	 * @param elemRef the elemRef to set
	 */
	public void setElemRef(String elemRef) {
		this.elemRef = elemRef;
	}

	/**
	 * @return the propertyDescr
	 */
	public String getPropertyDescr() {
		return propertyDescr;
	}

	/**
	 * @param propertyDescr the propertyDescr to set
	 */
	public void setPropertyDescr(String propertyDescr) {
		this.propertyDescr = propertyDescr;
	}

	/**
	 * @return the isTypeProperty
	 */
	public boolean isTypeProperty() {
		return typeProperty;
	}

	/**
	 * @param typeProperty the isTypeProperty to set
	 */
	public void setTypeProperty(boolean typeProperty) {
		this.typeProperty = typeProperty;
	}

	/**
	 * @return the isMethodProperty
	 */
	public boolean isValueProperty() {
		return valueProperty;
	}

	/**
	 * @param valueProperty the isMethodProperty to set
	 */
	public void setValueProperty(boolean valueProperty) {
		this.valueProperty = valueProperty;
	}

	/**
	 * @return the valueAnnotation
	 */
	public Annotation getValueAnnotation() {
		return valueAnnotation;
	}

	/**
	 * @param valueAnnotation the valueAnnotation to set
	 */
	public void setValueAnnotation(Annotation valueAnnotation) {
		this.valueAnnotation = valueAnnotation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((elemRef == null) ? 0 : elemRef.hashCode());
		result = prime * result + (valueProperty ? 1231 : 1237);
		result = prime * result + (typeProperty ? 1231 : 1237);
		result = prime * result + ((propertyDescr == null) ? 0 : propertyDescr.hashCode());
		result = prime * result + ((typeRef == null) ? 0 : typeRef.hashCode());
		result = prime * result + ((valueAnnotation == null) ? 0 : valueAnnotation.hashCode());
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
		PropertyRef other = (PropertyRef) obj;
		if (elemRef == null) {
			if (other.elemRef != null)
				return false;
		} else if (!elemRef.equals(other.elemRef))
			return false;
		if (valueProperty != other.valueProperty)
			return false;
		if (typeProperty != other.typeProperty)
			return false;
		if (propertyDescr == null) {
			if (other.propertyDescr != null)
				return false;
		} else if (!propertyDescr.equals(other.propertyDescr))
			return false;
		if (typeRef == null) {
			if (other.typeRef != null)
				return false;
		} else if (!typeRef.equals(other.typeRef))
			return false;
		if (valueAnnotation == null) {
			if (other.valueAnnotation != null)
				return false;
		} else if (!valueAnnotation.equals(other.valueAnnotation))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PropertyRef [typeRef=" + typeRef + ", elemRef=" + elemRef + ", propertyDescr=" + propertyDescr
				+ ", typeProperty=" + typeProperty + ", valueProperty=" + valueProperty + ", valueAnnotation="
				+ valueAnnotation + "]";
	}
	
}
