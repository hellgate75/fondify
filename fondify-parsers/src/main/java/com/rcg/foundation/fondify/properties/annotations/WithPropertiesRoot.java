/**
 * 
 */
package com.rcg.foundation.fondify.properties.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Documented;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.rcg.foundation.fondify.core.properties.PropertyArchive;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
/**
 * Annotation that help injection of properties a component. It defines base 
 * component property path, the use the fieldName to declare recover the property
 * from the PropertyArchive.
 *  
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 * 
 * @see PropertyArchive
 *
 */
public @interface WithPropertiesRoot {
	/**
	 * Custom path, used as base path to match the component methods 
	 * root, for the identification of the declared in the {@link PropertyArchive}.
	 * 
	 * 
	 * See the following example :
	 * 
	 * {@code
	 *  @WithPropertiesRoot("com.biz.example")
	 *  class Sample {
	 *  
	 *  	private name;
	 *  
	 *  	@TransformCase(KeyCase.LOWER)
	 *  	private dateOfBird;
	 *  }
	 *  
	 *  'name' field will match the com.biz.example.name property in the property archive.

	 *  'dateOfBird' field will match the com.biz.example.dateofbird property in the property archive.
	 * }
	 * 
	 * @return <String> property name
	 */
	String name() default "";
}
