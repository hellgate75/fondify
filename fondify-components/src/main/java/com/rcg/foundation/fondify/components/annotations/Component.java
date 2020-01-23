/**
 * 
 */
package com.rcg.foundation.fondify.components.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.rcg.foundation.fondify.core.domain.Scope;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
/**
 * Annotation that define what is custom component, that can define features, beans, streams, and any other FIELD and METHOD element.
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public @interface Component {
	/**
	 * Custom name that identify in a unique way the compinent, elsewise the calss name in Java
	 * notation will be use. Please see following sample:
	 * 
	 * {@code
	 * 
	 * @Component(value="MyComponentName")
	 * 
	 * This will save the element instance, and it will define in the Factory the component declaration,
	 * in orer to be instances and collected with name 'MyComponentName'.
	 * 
	 * @return (String) name of component in the registry. Overwrite will be allowed
	 */
	String value() default "";
	
	/**
	 * Scope of component, available Scopes in {@link Scope} enumeration  
	 * 
	 * @return ({@link Scope} 
	 */
	Scope scope() default Scope.INSTANCE;

}
