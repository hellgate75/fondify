/**
 * 
 */
package com.rcg.foundation.fondify.core.functions;

import java.util.Objects;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface SimplePredicate<T, R> {
	
	boolean test(T item, R reference);
	
	default SimplePredicate<T, R> and(SimplePredicate<T, R> other) {
        Objects.requireNonNull(other);
        return (t, r) -> test(t, r) && other.test(t, r);
    }
}
