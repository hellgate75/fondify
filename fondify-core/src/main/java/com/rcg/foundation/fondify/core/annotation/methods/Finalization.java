/**
 * 
 */
package com.rcg.foundation.fondify.core.annotation.methods;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(METHOD)
/**
 * Execute Annotated Component or Injectable methods when JVM destroy
 * the bean
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public @interface Finalization {

	/**
	 * Order in finalization call
	 * @return <int> order number
	 */
	int order() default 0;

}
