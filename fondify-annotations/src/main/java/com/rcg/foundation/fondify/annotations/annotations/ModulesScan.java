/**
 * 
 */
package com.rcg.foundation.fondify.annotations.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.rcg.foundation.fondify.core.typings.ModuleScanner;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
@DependsOn({Application.class, Configuration.class})
/**
 * Annotation that activate custom modules scan (system modules are scanned by default, if there is
 * no stop module scan option or if the classes of the module configurations are not reported in the exclusions).
 * It has allows declaration of inclusions and exclusions of module scanners.
 * A global flag will report if Module scan is disabled for both system and custom modules.
 * 
 * It works in presence of {@link StreamIOApplication} or {@link StreamIOConfiguration} annotations.
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public @interface ModulesScan {
	/**
	 * Array of {@link ModuleScanner} that declares modules and the way they are scanned. By default scan is 
	 * on all imported artifacts for any system module, custom modules will not be scanned if not required.
	 * 
	 * @return (Class<? extends ModuleScanner>[]) list of module scanner to be imported and used
	 */
	Class<? extends ModuleScanner>[] includes() default {};
	/**
	 * If [@code true} disables permanently the Module scan for both system and custom modules.
	 * 
	 * @return (boolean) flag for Module scan disable
	 */
	boolean value() default false;
	/**
	 * Array of {@link ModuleScanner} that declares modules will not be scanned. If not present in this list, 
	 * all other system modules or custom modules will be scanned.
	 * 
	 * @return (Class<? extends ModuleScanner>[]) list of module scanner not to be imported and used
	 */
	Class<? extends ModuleScanner>[] excludes() default {};

}
