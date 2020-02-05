/**
 * 
 */
package com.rcg.foundation.fondify.annotations.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.strobel.decompiler.languages.java.ast.Annotation;

@Documented
@Retention(RUNTIME)
@Target({ ANNOTATION_TYPE, TYPE})
/**
 * Defines elements dependency on multiple classes and it means that a specific element
 * depends on specific other ones.
 * It can be applied to Type or Annotation
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public @interface DependsOn {
	/**
	 * Array of classes of elements from with the marked element depends on.
	 * Meaning changes from the application. 
	 * 
	 * In a Java Type it means that one specific 
	 * Element cannot be instantiated before other ones, so engine must instantiate
	 * dependencies on request of the marked element. 
	 * 
	 * In Java annotation it means that an annotation can be validated only if in presence
	 * of some other annotation.
	 * @return
	 */
	Class<?>[] value() default {};
}
