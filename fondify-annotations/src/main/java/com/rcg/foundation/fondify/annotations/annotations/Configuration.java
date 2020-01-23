/**
 * 
 */
package com.rcg.foundation.fondify.annotations.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
/**
 * Annotation that defines type that contains beans or beans scanning options
 * 
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public @interface Configuration {
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
	String[] packages() default {};
	
	/**
	 * Array of strings that declares files that contains configuration items,
	 * available format JSON, XML and YAML.
	 * Script paths can contain JVM execution properties with following notation:
	 * 
	 * {@code
	 * @StreamIOConfiguration(initScripts={"/my/path/to/configuration/folder/${environment}-${path}/myfile.yml"})
	 * }
	 * 
	 * This notation will merge into the place-holders 'environment' and 'path' the relevant JVM execution parameters 
	 * named 'environment' and 'path' -> e.g.: {@code -Denvironment=D -Dpath=5 } in this case file full final path will be:
	 * 
	 * {@code
	 * /my/path/to/configuration/folder/D-5/myfile.yml
	 * }
	 * 
	 * @return (String[]) list of Stream IO configuration files
	 */
	String[] initScripts() default {};
}
