/**
 * 
 */
package com.rcg.foundation.fondify.annotation.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.rcg.foundation.fondify.core.domain.KeyCase;

@Documented
@Retention(RUNTIME)
@Target({ TYPE, FIELD, METHOD, PARAMETER })
/**
 * Define Transformation name of any element, in the component/properties registry, in
 * as the name is stored, or the name of the reference to the registry element, in 
 * order to match case insensitive the registry reference.
 *  
 * 
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public @interface TransformCase {
	
	/**
	 * Define the case transformation for the annotated type, field, method or method parameter.
	 * 
	 * @return Wanted {@link KeyCase} transformation
	 */
	KeyCase value() default KeyCase.NO_CHANGE;
}
