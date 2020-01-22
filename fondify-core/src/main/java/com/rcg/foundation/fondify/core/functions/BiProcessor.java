/**
 * 
 */
package com.rcg.foundation.fondify.core.functions;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
@FunctionalInterface
public interface BiProcessor<T, J> {

	void process(T item, J item2, Object... arguments);

}
