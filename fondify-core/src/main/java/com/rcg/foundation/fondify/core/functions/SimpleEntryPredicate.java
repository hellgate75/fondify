/**
 * 
 */
package com.rcg.foundation.fondify.core.functions;

import java.util.AbstractMap.SimpleEntry;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface SimpleEntryPredicate<K, V, T> {
	void apply(SimpleEntry<K, V> entry, T reference);
}
