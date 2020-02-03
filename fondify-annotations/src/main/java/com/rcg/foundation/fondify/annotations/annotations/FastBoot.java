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
@DependsOn(Application.class)
/**
 * Annotation that defines fast boot, taking responsibility of
 * declaring packages where {@link Configuration} and {@link ComponentScan}
 * annotations resides. If any package is forgotten, that configuration will not be taken,
 * if any empty list is provided, not fast boot will be performed. So it will be
 * skipped a full JVM Class-Path scan to recover that configuration
 * names cannot be combined with variables, because use of this annotation will be
 * allowed only in start-up prior stage, and only if applied to the
 * Main Class, and in presence of the Application Annotation.
 * 
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public @interface FastBoot {
	
	/**
	 * List of classes used to extract all annotations of type {@link Configuration} and {@link ComponentScan}, where they 
	 * reside. Any mismatch or miss of configuration can cause fast boot skip, and full 
	 * scan required for collecting that annotations in the application.
	 * @return
	 */
	Class<?>[] value() default {};
}
