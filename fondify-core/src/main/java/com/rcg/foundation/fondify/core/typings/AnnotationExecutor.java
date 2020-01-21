/**
 * 
 */
package com.rcg.foundation.fondify.core.typings;

import java.lang.annotation.Annotation;

import com.rcg.foundation.fondify.core.exceptions.ProcessException;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface AnnotationExecutor<T extends Annotation> {
	/**
	 * @return
	 */
	Class<? extends T> getAnnotationClass();
	
	/**
	 * @return
	 */
	boolean containsResults();

	/**
	 * @return
	 */
	String getComponentName();
	
	/**
	 * @param t
	 * @return
	 * @throws ProcessException
	 */
	ExecutionAnswer<T> executeAnnotation(AnnotationDeclaration t) throws ProcessException;
}
