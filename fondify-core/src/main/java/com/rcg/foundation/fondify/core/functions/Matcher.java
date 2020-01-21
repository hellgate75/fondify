/**
 * 
 */
package com.rcg.foundation.fondify.core.functions;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
@FunctionalInterface
public interface Matcher<T> {
	boolean match(T matchElement);
}
