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
@Target({ TYPE })
@DependsOn({Application.class, Configuration.class})
/**
 * Annotation that describes default folders, files and other system based information
 * It works in presence of {@link StreamIOApplication} or {@link StreamIOConfiguration} annotations.
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public @interface Defaults {
	/**
	 * Array of strings that declares folders that contains streams, mappers, beans and other elements configuration files,
	 * available format JSON, XML and YAML.
	 * Script paths can contain JVM execution properties with following notation:
	 * 
	 * {@code
	 * Defaults(configFolders={"/my/path/to/configuration/folder/${environment}-${path}/"})
	 * }
	 * 
	 * This notation will merge into the place-holders 'env' and 'path' the relevant JVM execution parameters 
	 * named 'environment' and 'path' -> e.g.: {@code -Denvironment=D -Dpath=5 } in this case file full final path will be:
	 * 
	 * {@code
	 * /my/path/to/configuration/folder/D-5/
	 * }
	 * 
	 * Default folder is : './config'
	 * 
	 * @return (String[]) list of Stream IO configuration folders
	 */
	String[] configFolders() default {"./config"};

	/**
	 * Array of strings that declares folders that contains jobs definition files,
	 * available format JSON, XML and YAML.
	 * Script paths can contain JVM execution properties with following notation:
	 * 
	 * {@code
	 * Defaults(jobsFolders={"/my/path/to/configuration/folder/${environment}-${path}/"})
	 * }
	 * 
	 * This notation will merge into the place-holders 'environment' and 'path' the relevant JVM execution parameters 
	 * named 'environment' and 'path' -> e.g.: {@code -Denvironment=D -Dpath=5 } in this case file full final path will be:
	 * 
	 * {@code
	 * /my/path/to/configuration/folder/D-5/
	 * }
	 * 
	 * Default folder is : './jobs'
	 * 
	 * @return (String[]) list of Stream IO configuration folders
	 */
	String[] jobsFolders() default {"./jobs"};

}
