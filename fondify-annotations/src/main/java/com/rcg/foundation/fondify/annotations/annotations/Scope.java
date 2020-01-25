/**
 * 
 */
package com.rcg.foundation.fondify.annotations.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
/**
 * Annotation that defines element scope.
 * 
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public @interface Scope {
	/**
	 * Define scope of element
	 * @return {@link com.rcg.foundation.fondify.core.domain.Scope}
	 */
	com.rcg.foundation.fondify.core.domain.Scope value() default com.rcg.foundation.fondify.core.domain.Scope.APPLICATION;
	
}
