/**
 * 
 */
package com.rcg.foundation.fondify.annotation.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.rcg.foundation.fondify.core.domain.ApplicationType;

@Documented
@Retention(CLASS)
@Target(TYPE)
/**
 * Annotation that defines where main Application parameters occurs.
 * 
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public @interface Application {
	/**
	 * Define scope of application
	 * @return {@link StreamIOApplicationType}
	 */
	ApplicationType scope() default ApplicationType.CONSOLE;
}
