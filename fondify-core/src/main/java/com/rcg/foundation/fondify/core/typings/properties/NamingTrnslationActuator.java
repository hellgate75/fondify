/**
 * 
 */
package com.rcg.foundation.fondify.core.typings.properties;

/**
 * Interface that describes features for translating components or
 * properties reference from a given text format, and filling text 
 * reference with live value.
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface NamingTrnslationActuator {
	/**
	 * Translate components or properties reference into the
	 * given text, and return the string version with new
	 * value acquired from the components or the 
	 * properties registry. Any unmatched text will still as
	 * written because it can be parsed in a further time.
	 * @param text Input string within presence of components or properties text reference.
	 * @param applyDefault Flag that describes if must or not be applied default property value
	 * @return (String) translated text.
	 */
	String translateText(String text, boolean applyDefault);
	
	/**
	 * Verify id a text string contains components or properties reference
	 * that can be translated into a more fluent version.
	 * @param text Input string with potential presence of components or properties text reference.
	 * @return
	 */
	boolean canTranslateText(String text);
}
