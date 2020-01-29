/**
 * 
 */
package com.rcg.foundation.fondify.properties.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.rcg.foundation.fondify.annotations.annotations.Application;
import com.rcg.foundation.fondify.annotations.annotations.Configuration;
import com.rcg.foundation.fondify.annotations.annotations.DependsOn;
import com.rcg.foundation.fondify.core.domain.PropertiesFormat;

@Documented
@Retention(RUNTIME)
@Target({FIELD})
@DependsOn({Application.class, Configuration.class})
/**
 * Annotation that activate custom properties file load from different sources.
 * It allows load from file, url, classpath or spring cloud config server and loads 
 * configurations in Properties, Yaml and Spring Cloud REST JSON answer protocols.  
 * It works in presence of {@link StreamIOApplication} or {@link StreamIOConfiguration} annotations, 
 * and it's accumulable in multiple presences with different files. Redeclare a duplicate source
 * import will slow down execution without any benefit. No duplicates check will be performed,
 * due to performance reasons.
 * This annotation can be applied to a field and it will save all properties in the field value when it
 * is made of type Map<Object, Object> or of type {@link java.util.Properties}.
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public @interface PropertiesSet {
	/**
	 * Array of strings that declares files/files path to loaded. It requires a protocol
	 * for special calls (url, file, classpath, spring-config-server), while no protocol will mean file reference. You can find an example as follow:
	 * 
	 * {@code
	 * 
	 * @PropertiesSet({"file:/etc/services/myservice/${myenv}/${myservernumber}/config.properties"})
	 * 
	 * This will load a properties file from the give file url.
	 * 
	 * 
	 * @PropertiesSet(value={"classpath:properties.yaml"}, format=PropertiesFormat.YAML)
	 * 
	 * This will load a properties file in yaml format from the classpath.
	 * 
	 * 
	 * @PropertiesSet({spring-config-server:http://localhost:8888"}) OR
	 * 
	 * @PropertiesSet({spring-config-server:http://${myurlvariable}:${myportvariable}"})
	 * 
	 * This will load a properties file in Spring Cloud Config Server format from the web url.
	 * }
	 * 
	 * 
	 * Base type represent the (.properties) file
	 * 
	 * Yaml type represent the typical yaml output format.
	 * 
	 * Spring Config Server will load JSON config from url and profiles and if present label reference in
	 * Spring Cloud Config server format
	 * 
	 * 
	 * Default system variables will be parsed during the file url recovery
	 * 
	 * @return (String[]) list of packages to be scanned
	 */
	String[] value() default {};
	
	/**
	 * Properties file format, available formats: Properties, Yaml, Spring Cloud Config Server 
	 * 
	 * @return ({@link PropertiesFormat} 
	 */
	PropertiesFormat format() default PropertiesFormat.PROPERTIES;

}
