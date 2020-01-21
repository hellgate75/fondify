/**
 * 
 */
package com.rcg.foundation.fondify.core.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
@DependsOn({Application.class, Configuration.class})
/**
 * Annotation that activate custom component scans into specific packages.
 * It uses {@link StreamIOConfiguration} packages merged to eventually declared
 * ones in this annotation. If no packages are specified it mean that the components
 * scan will be extended to any package and class imported and / or linked at the
 * final STREAM IO Application  
 * It works in presence of {@link StreamIOApplication} or {@link StreamIOConfiguration} annotations.
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public @interface ComponentsScan {
	/**
	 * Array of strings that declares packages to be scanned. By default scan is 
	 * on all imported artifacts.
	 * 
	 * Path work as the initial point, and format should be as follow:
	 * 
	 * {@code
	 * @StreamIOConfiguration(packages={"com.my.package.to.configuration.items.*"})
	 * }
	 * 
	 * Application will scan only in the package com.my.package.to.configuration.items and
	 * all sub packages, for configuration items
	 * 
	 * @return (String[]) list of packages to be scanned
	 */
	String[] value() default {};

}
