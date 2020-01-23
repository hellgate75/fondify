/**
 * 
 */
package com.rcg.foundation.fondify.components.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.rcg.foundation.fondify.components.annotations.typings.ElementType;

@Documented
@Retention(RUNTIME)
@Target({ FIELD, PARAMETER })
/**
 * Annotation that help injection of elements: Entities, Streams, Jobs, Steps, Executors,
 * Matchers, Properties (Values): all injected in fields, methods/constructors parameters of other annotated 
 * elements or in a configuration artifact for the specific element scope or life-cycle (for Context, Session 
 * or Application scopes) 
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public @interface Inject {
	/**
	 * Custom name, different from field or parameter one on which element has been declared
	 * for the specific type.
	 * @return
	 */
	String name() default "";
	
	/**
	 * Define specific binding type. Specific type belongs to specific registry entry / type
	 * @return
	 */
	ElementType type() default  ElementType.COMPONENT;
}
