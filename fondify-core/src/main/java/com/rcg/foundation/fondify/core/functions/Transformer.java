/**
 * 
 */
package com.rcg.foundation.fondify.core.functions;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
@FunctionalInterface
public interface Transformer<I, O> {
	O tranform(I input, Object... arguments);
}
