/**
 * 
 */
package com.rcg.foundation.fondify.components.annotations.typings;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public enum ElementType {
	/**
	 * Component type reference
	 */
	COMPONENT,
	/**
	 * Anything non-component Injectable type reference
	 */
	INJECTABLE,
	/**
	 * Any property value reference
	 */
	PROPERTY_VALUE,
	/**
	 * Any system (Stream I/O) value reference
	 */
	SYSTEM
}
