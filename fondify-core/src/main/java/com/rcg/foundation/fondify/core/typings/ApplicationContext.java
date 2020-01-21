/**
 * 
 */
package com.rcg.foundation.fondify.core.typings;

import java.util.List;
import java.util.Properties;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface ApplicationContext {
	
	Properties getApplicationProperties();
	
	String getApplicationProperty(String name);
	
	List<KeyPair<String, Object>> getSessionObjects();
	
	Object getSessionObject(String key);
	
	void registerSessionObject(String key, Object value);
	
	Properties getSessionProperties();
	
	String getSessionProperty(String name);
	
	
	public static class KeyPair<K extends Comparable<K>, V> implements Comparable<KeyPair<K, V>>{
		K key;
		V value;
		
		/**
		 * Key Pair Constructor
		 * @param key
		 * @param value
		 */
		protected KeyPair(K key, V value) {
			super();
			this.key = key;
			this.value = value;
		}

		/**
		 * Get Key Pair Key
		 * @return the Key Pair Key
		 */
		public K getKey() {
			return key;
		}

		/**
		 * Get Key Pair Value
		 * @return the Key Pair Value
		 */
		public V getValue() {
			return value;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			@SuppressWarnings("unchecked")
			KeyPair<K, V> other = (KeyPair<K, V>) obj;
			if (key == null) {
				if (other.key != null)
					return false;
			}
			return true;
		}

		public int compareTo(KeyPair<K, V> o) {
			if ( o == null || o.getKey() == null ) {
				return -1;
			}
			return this.key.compareTo(o.getKey());
		}
	}
}
