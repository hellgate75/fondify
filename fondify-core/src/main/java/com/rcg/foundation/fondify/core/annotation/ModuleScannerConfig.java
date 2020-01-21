/**
 * 
 */
package com.rcg.foundation.fondify.core.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.rcg.foundation.fondify.core.typings.ModuleMain;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
/**
 * Annotations that identify a Module Scanner, used to scan for 
 * features. It allows to run one or more {@link ModuleMain} classes
 * reusing the discovered annotations in order to give life to the module
 * capabilities. 
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public @interface ModuleScannerConfig {
	/**
	 * Sets the module name
	 * @return module name
	 */
	String name();
	/**
	 * Sets the classes related to module post scan, main execution
	 * @return <Class<? extends ModuleMain>[]> module main classes array
	 */
	Class<? extends ModuleMain>[] mainClasses() default {};
}
