/**
 * 
 */
package com.rcg.foundation.fondify.components.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Properties;

import com.ctc.wstx.shaded.msv_core.reader.Controller;
import com.rcg.foundation.fondify.components.annotations.typings.InjectableType;
import com.rcg.foundation.fondify.components.injecables.InjectableExecutor;
import com.rcg.foundation.fondify.components.injecables.Service;
import com.rcg.foundation.fondify.core.domain.Scope;

@Documented
@Retention(RUNTIME)
@Target( { TYPE, METHOD } )
/**
 * Annotations that allow to create properties collections, services, 
 * controllers definition by Special Types or methods that make instance of the 
 * specific controller. They can be {@link Autowired} or Injected as {@link InjectableType}.
 * 
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 * 
 */
public @interface Injectable {
	
	/**
	 * Component extension
	 * @return {@link Component} linked
	 */
	Component component();
	
	/**
	 * Defines and {@link Injectable} executable type.
	 * 
	 * @see InjectableExecutor
	 * @see Controller
	 * @see Service
	 * @see Properties
	 * @return Reference {@link InjectableType} for the Injectable component
	 */
	InjectableType type() default InjectableType.CONTROLLER; 
	
	/**
	 * Scope of component, available Scopes in {@link Scope} enumeration  
	 * 
	 * @return ({@link Scope} 
	 */
	Scope scope() default Scope.INSTANCE;

}
