/**
 * 
 */
package com.rcg.foundation.fondify.core.functions;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
@FunctionalInterface
public interface Processor<T> {

	void process(T item, Object... arguments);

}
