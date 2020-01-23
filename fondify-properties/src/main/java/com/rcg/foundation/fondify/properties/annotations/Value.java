/**
 * 
 */
package com.rcg.foundation.fondify.properties.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.rcg.foundation.fondify.core.properties.PropertyArchive;

@Documented
@Retention(RUNTIME)
@Target({ FIELD, PARAMETER })
/**
 * Annotation that help injection of properties in components.
 *  
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public @interface Value {
	/**
	 * Custom name, declared in the {@link PropertyArchive}.
	 * @return <String> property name
	 */
	String value() default "";
}
