/**
 * 
 */
package com.rcg.foundation.fondify.annotation.typings;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.rcg.foundation.fondify.annotation.annotations.methods.Finalization;
import com.rcg.foundation.fondify.annotation.annotations.methods.Initialization;
import com.rcg.foundation.fondify.core.functions.Transformer;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;
import com.rcg.foundation.fondify.core.typings.methods.ParameterRef;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class MethodExecutor implements Comparable<MethodExecutor> {
	String beanName;
	private Method method = null;
	private Initialization initializationAnnotation = null;
	private Finalization finalizationAnnotation = null;
	List<ParameterRef> parameters = new ArrayList<>(0);
	
	/**
	 * @param beanName
	 * @param method
	 * @param initializationAnnotation
	 * @param finalizationAnnotation
	 */
	public MethodExecutor(String beanName, Method method, Initialization initializationAnnotation, Finalization finalizationAnnotation) {
		super();
		this.beanName = beanName;
		this.method = method;
		method.setAccessible(true);
		this.initializationAnnotation = initializationAnnotation;
		this.finalizationAnnotation = finalizationAnnotation;
	}

	/**
	 * @param beanName
	 * @param method
	 * @param initializationAnnotation
	 * @param finalizationAnnotation
	 * @param fieldsRef
	 */
	public MethodExecutor(String beanName, Method method, Initialization initializationAnnotation, Finalization finalizationAnnotation,
			Collection<ParameterRef> fieldsRef) {
		this(beanName, method, initializationAnnotation, finalizationAnnotation);
		this.parameters.addAll(fieldsRef);
	}

	
	
	/**
	 * @return the beanName
	 */
	public String getBeanName() {
		return beanName;
	}

	/**
	 * @return the fieldsRef
	 */
	public List<ParameterRef> getParameters() {
		return parameters;
	}

	/**
	 * @param parameters the parameters collection to add
	 */
	public void addAllParameters(Collection<ParameterRef> parameters) {
		this.parameters.addAll(parameters);
	}

	/**
	 * @param parameter the parameter to add
	 */
	public void addParameter(ParameterRef parameter) {
		this.parameters.add(parameter);
	}

	/**
	 * @return the method
	 */
	public Method getMethod() {
		return method;
	}

	/**
	 * @return the initializationAnnotation
	 */
	public Initialization getInitializationAnnotation() {
		return initializationAnnotation;
	}

	/**
	 * @return the finalizationAnnotation
	 */
	public Finalization getFinalizationAnnotation() {
		return finalizationAnnotation;
	}

	@Override
	public int compareTo(MethodExecutor other) {
		if ( initializationAnnotation != null && 
			 other.initializationAnnotation != null) {
			return initializationAnnotation.order() - 
					other.initializationAnnotation.order();
		} else if ( finalizationAnnotation != null && 
			other.finalizationAnnotation != null) {
			return finalizationAnnotation.order() - 
					other.finalizationAnnotation.order();
		} 
		return 0;
	}

	public Object execute(Object instance, Transformer<Annotation, String> valueExtractor, 
						Transformer<Annotation, Object> autowiredTransformer, Transformer<Annotation, Object> injectTransformer) throws Exception {
		
		try {
			Object[] args = new Object[method.getParameterCount()];
			AtomicInteger counter = new AtomicInteger(0);
			Arrays.asList(method.getParameters()).forEach( param -> {
				int index = counter.get();
				List<ParameterRef> list = parameters
					.stream()
					.filter( fr -> fr.getParameterRef().equals(param.getName()) )
					.collect(Collectors.toList());
				if ( list.size() == 0 ) {
					throw new RuntimeException(String.format("Unable to find injection of parameter name: %s", param.getName()));
				}
				args[index] = list.get(0).execute(valueExtractor, autowiredTransformer, injectTransformer);
				counter.incrementAndGet();
			} );
			return method.invoke(instance, args);
		} catch (Exception e) {
			String message = "Unable to eecute method as follow: " + this;
			LoggerHelper.logError("MethodExecutor::execute", message, e);
			throw new Exception(message, e);
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result + ((finalizationAnnotation == null) ? 0 : finalizationAnnotation.hashCode());
		result = prime * result + ((initializationAnnotation == null) ? 0 : initializationAnnotation.hashCode());
		result = prime * result + ((method == null) ? 0 : method.hashCode());
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
		MethodExecutor other = (MethodExecutor) obj;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		if (finalizationAnnotation == null) {
			if (other.finalizationAnnotation != null)
				return false;
		} else if (!finalizationAnnotation.equals(other.finalizationAnnotation))
			return false;
		if (initializationAnnotation == null) {
			if (other.initializationAnnotation != null)
				return false;
		} else if (!initializationAnnotation.equals(other.initializationAnnotation))
			return false;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MethodExecutor [method=" + method + ", initializationAnnotation=" + initializationAnnotation
				+ ", finalizationAnnotation=" + finalizationAnnotation + ", fieldsRef=" + parameters + "]";
	}
	
}
