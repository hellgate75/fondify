/**
 * 
 */
package com.rcg.foundation.fondify.annotations.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.rcg.foundation.fondify.annotations.typings.ApplicationConsole;
import com.rcg.foundation.fondify.core.domain.ApplicationType;

@Documented
@Retention(RUNTIME)
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
	
	/**
	 * Define if in {@link ApplicationType.CONSOLE} application must be run
	 * the command shell, using the  {@link ApplicationConsole} class implementation
	 * in the extension package 
	 * @return (boolean) status of command shell execution request
	 */
	boolean hasCommandShell() default false;
}
