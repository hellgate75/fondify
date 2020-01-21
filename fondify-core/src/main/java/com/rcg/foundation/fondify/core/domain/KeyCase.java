/**
 * 
 */
package com.rcg.foundation.fondify.core.domain;

/**
 * It represents components, elements and other registry stored 
 * artifacts name registration, and it's used to define a name
 * case transformation, in oder to simplify and unlink registry names 
 * for the java code names/conventions form the registry names.
 * Registry components names are case sensitive.
 * 
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public enum KeyCase {
	/**
	 * No case transformation is required
	 * 
	 * 
	 * e.g.:
	 * 
	 * MyMethodName -> MyMethodName 
	 */
	NO_CHANGE,
	/**
	 * Capital letters transformation is required
	 * 
	 * 
	 * e.g.:
	 * 
	 * MyMethodName ->MYMETHODNAME
	 */
	CAPITAL,
	/**
	 * Lower case letters transformation is required
	 * 
	 * 
	 * e.g.:
	 * 
	 * MyMethodName -> mymethodname
	 */
	LOWER,
	/**
	 * Initial Capital letter transformation is required
	 * 
	 * 
	 * e.g.:
	 * 
	 * MyMethodName -> Mymethodname
	 */
	INIT_CAP,
	/**
	 * Snake case letters transformation is required
	 * 
	 * 
	 * e.g.:
	 * 
	 * MyMethodName -> MyMeThOdNaMe
	 */
	SNAKE,
	/**
	 * Camel case letters transformation is required
	 * 
	 * 
	 * e.g.:
	 * 
	 * MyMethodName -> myMethodName
	 */
	CAMEL
}
