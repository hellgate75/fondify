/**
 * 
 */
package com.rcg.foundation.fondify.core.typings.methods;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import com.rcg.foundation.fondify.core.functions.Transformer;
import com.rcg.foundation.fondify.core.properties.PropertyArchive;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ParameterRef implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3024529245446395096L;


	private String typeRef;

	private String methodRef;

	private String parameterRef;

	private String elemDescriptor;
	
	private Annotation autowiredAnnotation = null;
	
	private Annotation injectAnnotation = null;

	private Annotation valueAnnotation = null;

	/**
	 * 
	 */
	public ParameterRef() {
		super();
	}

	/**
	 * @param typeRef
	 * @param methodRef
	 * @param parameterRef
	 * @param elemDescriptor
	 * @param autowiredAnnotation
	 * @param injectAnnotation
	 * @param valueAnnotation
	 */
	public ParameterRef(String typeRef, String methodRef, String parameterRef, String elemDescriptor,
			Annotation autowiredAnnotation, Annotation injectAnnotation, Annotation valueAnnotation) {
		super();
		this.typeRef = typeRef;
		this.methodRef = methodRef;
		this.parameterRef = parameterRef;
		this.elemDescriptor = elemDescriptor;
		this.autowiredAnnotation = autowiredAnnotation;
		this.injectAnnotation = injectAnnotation;
		this.valueAnnotation = valueAnnotation;
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
	 * @return the methodRef
	 */
	public String getMethodRef() {
		return methodRef;
	}

	/**
	 * @param methodRef the methodRef to set
	 */
	public void setMethodRef(String methodRef) {
		this.methodRef = methodRef;
	}

	/**
	 * @return the parameterRef
	 */
	public String getParameterRef() {
		return parameterRef;
	}

	/**
	 * @param parameterRef the parameterRef to set
	 */
	public void setParameterRef(String parameterRef) {
		this.parameterRef = parameterRef;
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
	 * @return the autowiredAnnotation
	 */
	public Annotation getAutowiredAnnotation() {
		return autowiredAnnotation;
	}

	/**
	 * @param autowiredAnnotation the autowiredAnnotation to set
	 */
	public void setAutowiredAnnotation(Annotation autowiredAnnotation) {
		this.autowiredAnnotation = autowiredAnnotation;
	}

	/**
	 * @return the injectAnnotation
	 */
	public Annotation getInjectAnnotation() {
		return injectAnnotation;
	}

	/**
	 * @param injectAnnotation the injectAnnotation to set
	 */
	public void setInjectAnnotation(Annotation injectAnnotation) {
		this.injectAnnotation = injectAnnotation;
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
		result = prime * result + ((autowiredAnnotation == null) ? 0 : autowiredAnnotation.hashCode());
		result = prime * result + ((elemDescriptor == null) ? 0 : elemDescriptor.hashCode());
		result = prime * result + ((injectAnnotation == null) ? 0 : injectAnnotation.hashCode());
		result = prime * result + ((methodRef == null) ? 0 : methodRef.hashCode());
		result = prime * result + ((parameterRef == null) ? 0 : parameterRef.hashCode());
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
		ParameterRef other = (ParameterRef) obj;
		if (autowiredAnnotation == null) {
			if (other.autowiredAnnotation != null)
				return false;
		} else if (!autowiredAnnotation.equals(other.autowiredAnnotation))
			return false;
		if (elemDescriptor == null) {
			if (other.elemDescriptor != null)
				return false;
		} else if (!elemDescriptor.equals(other.elemDescriptor))
			return false;
		if (injectAnnotation == null) {
			if (other.injectAnnotation != null)
				return false;
		} else if (!injectAnnotation.equals(other.injectAnnotation))
			return false;
		if (methodRef == null) {
			if (other.methodRef != null)
				return false;
		} else if (!methodRef.equals(other.methodRef))
			return false;
		if (parameterRef == null) {
			if (other.parameterRef != null)
				return false;
		} else if (!parameterRef.equals(other.parameterRef))
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
		return "ParameterRef [typeRef=" + typeRef + ", methodRef=" + methodRef + ", parameterRef=" + parameterRef
				+ ", elemDescriptor=" + elemDescriptor + ", autowiredAnnotation=" + autowiredAnnotation
				+ ", injectAnnotation=" + injectAnnotation + ", valueAnnotation=" + valueAnnotation + "]";
	}
	@SuppressWarnings("unchecked")
	public <T> T execute(Transformer<Annotation, String> valueExtractor,
			Transformer<Annotation, Object> autowiredTransformer,
			Transformer<Annotation, Object> injectTransformer) {
		T t = null;
		if ( autowiredAnnotation != null ) {
			String name = this.parameterRef;
			Object obj = autowiredTransformer.tranform(autowiredAnnotation, name);
			if ( obj != null )
				t = (T)obj;
//			if ( autowiredAnnotation.name() != null && ! autowiredAnnotation.name().isEmpty() )
//				name = autowiredAnnotation.name();
//			t = ComponentsRegistry.getInstance().get(AnnotationConstants.REGISTRY_COMPONENT_REFERENCES, name);
//			if ( t == null ) {
//				t = ComponentsRegistry.getInstance().get(AnnotationConstants.REGISTRY_INJECTABLE_REFERENCES, name);
//			}
		} else if ( injectAnnotation != null ) {
			String name = this.parameterRef;
			Object obj = injectTransformer.tranform(injectAnnotation, name);
			if ( obj != null )
				t = (T)obj;
//			if ( injectAnnotation.name() != null && ! injectAnnotation.name().isEmpty() )
//				name = injectAnnotation.name();
//			t = ComponentsRegistry.getInstance().get(AnnotationConstants.REGISTRY_COMPONENT_REFERENCES, name);
//			if ( t == null ) {
//				t = ComponentsRegistry.getInstance().get(AnnotationConstants.REGISTRY_INJECTABLE_REFERENCES, name);
//			}
		} else if ( valueAnnotation != null ) {
			String name = this.parameterRef;
			String tranformedValue = valueExtractor.tranform(valueAnnotation);
			if ( tranformedValue != null && ! tranformedValue.isEmpty() )
				name = tranformedValue;
		 t = (T)PropertyArchive.getInstance().getProperty(name);	
		}
		return t;
	}

}
