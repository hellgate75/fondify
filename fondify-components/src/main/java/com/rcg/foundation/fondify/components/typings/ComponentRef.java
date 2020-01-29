/**
 * 
 */
package com.rcg.foundation.fondify.components.typings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.rcg.foundation.fondify.components.annotations.Autowired;
import com.rcg.foundation.fondify.components.annotations.Component;
import com.rcg.foundation.fondify.components.annotations.Injectable;
import com.rcg.foundation.fondify.core.typings.fields.ComponentReference;
import com.rcg.foundation.fondify.core.typings.methods.ParameterRef;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ComponentRef implements Serializable, ComponentReference {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4153568683866925537L;

	private String typeRef;

	private String elemRef;

	private String elemDescriptor;
	
	private boolean isType;
	
	private boolean isInField;
	
	private boolean isInjection;
	
	private Component componentAnnotation = null;

	
	private Autowired autowiredAnnotation = null;

	private Injectable injectAnnotation = null;
	
	private List<ParameterRef> parameters = new ArrayList<ParameterRef>(0);

	/**
	 * 
	 */
	public ComponentRef() {
		super();
	}

	/**
	 * @param typeRef
	 * @param elemRef
	 * @param elemDescriptor
	 * @param isType
	 * @param isInField
	 * @param isInjection
	 */
	public ComponentRef(String typeRef, String elemRef, String elemDescriptor, boolean isType, boolean isInField,
			boolean isInjection) {
		super();
		this.typeRef = typeRef;
		this.elemRef = elemRef;
		this.elemDescriptor = elemDescriptor;
		this.isType = isType;
		this.isInField = isInField;
		this.isInjection = isInjection;
	}

	/**
	 * @return the parameters
	 */
	public List<ParameterRef> getParameters() {
		return parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(List<ParameterRef> parameters) {
		this.parameters = parameters;
	}

	/**
	 * @param parameters the parameters collection to add
	 */
	public void addAllParameters(Collection<ParameterRef> parameters) {
		this.parameters.addAll(parameters);
	}
	
	/**
	 * @param parameters the parameter to add
	 */
	public void addParameter(ParameterRef parameter) {
		this.parameters.add(parameter);
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
	 * @return the elemDescriptor
	 */
	public String getElemDescriptor() {
		return elemDescriptor;
	}

	/**
	 * @param elemDescriptor the elemDescriptor to set
	 */
	public void setElemDescriptor(String elemDescriptor) {
		this.elemDescriptor = elemDescriptor;
	}

	/**
	 * @return the isType
	 */
	public boolean isType() {
		return isType;
	}

	/**
	 * @param isType the isType to set
	 */
	public void setType(boolean isType) {
		this.isType = isType;
	}

	/**
	 * @return the isInField
	 */
	public boolean isInField() {
		return isInField;
	}

	/**
	 * @param isInField the isInField to set
	 */
	public void setInField(boolean isInField) {
		this.isInField = isInField;
	}

	/**
	 * @return the isInjection
	 */
	public boolean isInjection() {
		return isInjection;
	}

	/**
	 * @param isInjection the isInjection to set
	 */
	public void setInjection(boolean isInjection) {
		this.isInjection = isInjection;
	}

	/**
	 * @return the componentAnnotation
	 */
	public Component getComponentAnnotation() {
		return componentAnnotation;
	}

	/**
	 * @param componentAnnotation the componentAnnotation to set
	 */
	public void setComponentAnnotation(Component componentAnnotation) {
		this.componentAnnotation = componentAnnotation;
	}

	/**
	 * @return the injectAnnotation
	 */
	public Injectable getInjectAnnotation() {
		return injectAnnotation;
	}

	/**
	 * @param injectAnnotation the injectAnnotation to set
	 */
	public void setInjectAnnotation(Injectable injectAnnotation) {
		this.injectAnnotation = injectAnnotation;
	}

	/**
	 * @return the autowiredAnnotation
	 */
	public Autowired getAutowiredAnnotation() {
		return autowiredAnnotation;
	}

	/**
	 * @param autowiredAnnotation the autowiredAnnotation to set
	 */
	public void setAutowiredAnnotation(Autowired autowiredAnnotation) {
		this.autowiredAnnotation = autowiredAnnotation;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((autowiredAnnotation == null) ? 0 : autowiredAnnotation.hashCode());
		result = prime * result + ((componentAnnotation == null) ? 0 : componentAnnotation.hashCode());
		result = prime * result + ((elemDescriptor == null) ? 0 : elemDescriptor.hashCode());
		result = prime * result + ((elemRef == null) ? 0 : elemRef.hashCode());
		result = prime * result + ((injectAnnotation == null) ? 0 : injectAnnotation.hashCode());
		result = prime * result + (isInField ? 1231 : 1237);
		result = prime * result + (isInjection ? 1231 : 1237);
		result = prime * result + (isType ? 1231 : 1237);
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result + ((typeRef == null) ? 0 : typeRef.hashCode());
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
		ComponentRef other = (ComponentRef) obj;
		if (autowiredAnnotation == null) {
			if (other.autowiredAnnotation != null)
				return false;
		} else if (!autowiredAnnotation.equals(other.autowiredAnnotation))
			return false;
		if (componentAnnotation == null) {
			if (other.componentAnnotation != null)
				return false;
		} else if (!componentAnnotation.equals(other.componentAnnotation))
			return false;
		if (elemDescriptor == null) {
			if (other.elemDescriptor != null)
				return false;
		} else if (!elemDescriptor.equals(other.elemDescriptor))
			return false;
		if (elemRef == null) {
			if (other.elemRef != null)
				return false;
		} else if (!elemRef.equals(other.elemRef))
			return false;
		if (injectAnnotation == null) {
			if (other.injectAnnotation != null)
				return false;
		} else if (!injectAnnotation.equals(other.injectAnnotation))
			return false;
		if (isInField != other.isInField)
			return false;
		if (isInjection != other.isInjection)
			return false;
		if (isType != other.isType)
			return false;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (parameters.size() != other.parameters.size() ||
					!parameters.equals(other.parameters))
			return false;
		if (typeRef == null) {
			if (other.typeRef != null)
				return false;
		} else if (!typeRef.equals(other.typeRef))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ComponentRef [typeRef=" + typeRef + ", elemRef=" + elemRef + ", elemDescriptor=" + elemDescriptor
				+ ", isType=" + isType + ", isInField=" + isInField + ", isInjection=" + isInjection
				+ ", componentAnnotation=" + componentAnnotation + ", injectAnnotation=" + injectAnnotation
				+ ", autowiredAnnotation=" + autowiredAnnotation + ", parameters size=" + parameters.size() + "]";
	}
	
}
