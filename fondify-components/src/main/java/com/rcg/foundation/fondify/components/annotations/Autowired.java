/**
 * 
 */
package com.rcg.foundation.fondify.components.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target({ FIELD })
/**
 * Annotation that help injection of elements: Entities, Streams, Jobs, Steps, Executors,
 * Matchers: all injected in fields, methods/constructors parameters of other annotated 
 * elements or in a configuration artifact for the specific element scope or life-cycle (for Context, Session 
 * or Application scopes) 
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public @interface Autowired {
	/**
	 * Custom name, different from field or parameter one on which element has been declared
	 * for the specific type.
	 * @return
	 */
	String name() default "";
}
