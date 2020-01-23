/**
 * 
 */
package com.rcg.foundation.fondify.core.typings;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Interface implementation is used to collect Annotation Types, in order to 
 * collect and load annotations into the artifacts. Only declared annotations
 * in the implemting classes will be considered.
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface AnnotationTypesCollector {

	List<Class<? extends Annotation>> listAnnotationTypes();
}
