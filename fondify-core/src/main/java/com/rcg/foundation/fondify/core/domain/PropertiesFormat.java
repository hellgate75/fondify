/**
 * 
 */
package com.rcg.foundation.fondify.core.domain;

import java.util.Properties;

import com.rcg.foundation.fondify.core.properties.PropertyManager;


/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 * @see PropertyManager
 */
public enum PropertiesFormat {
	/**
	 * Format that matches with .properties file and loaded in the 
	 * {@link Properties} java artifact
	 */
	PROPERTIES,
	/**
	 * Yaml file format, it will reflext properties as a map
	 */
	YAML,
	/**
	 * Spring Cloud Config Server JSON REST format (optional use)
	 */
	SPRING_CLOUD
}
