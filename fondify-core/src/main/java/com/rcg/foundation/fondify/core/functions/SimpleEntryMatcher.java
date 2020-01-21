/**
 * 
 */
package com.rcg.foundation.fondify.core.functions;

import java.util.AbstractMap.SimpleEntry;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface SimpleEntryMatcher<K, V> {
	boolean match(SimpleEntry<K, V> entry);
}
