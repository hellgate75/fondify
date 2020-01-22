/**
 * 
 */
package com.rcg.foundation.fondify.properties.typings;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import com.rcg.foundation.fondify.core.exceptions.IOException;

/**
 * Data parser interface
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface Parser<T> extends Serializable {

	/**
	 * Load element from the file path
	 * @param fileName file path
	 * @param clazz Element Class
	 * @return Parsed Element
	 * @throws IOException Any I/O operation exception
	 */
	T loadData(String fileName, Class<? extends T> clazz) throws IOException;
	
	/**
	 * Load element from the input stream
	 * @param stream Input Stream
	 * @param clazz Element Class
	 * @return Parsed Element
	 * @throws IOException Any I/O operation exception
	 */
	T loadData(InputStream stream, Class<? extends T> clazz) throws IOException;
	
	/**
	 * Load element from the string
	 * @param text Encoded element representing string
	 * @param clazz Element Class
	 * @return Parsed Element
	 */
	T parseText(String text, Class<? extends T> clazz);

	/**
	 * Save encoded text representing the Element to file
	 * @param fileName file path
	 * @param t Element to encode in I/O stream
	 * @throws IOException Any I/O operation exception
	 */
	void saveData(String fileName, T t) throws IOException;
	
	/**
	 * Save encoded text representing the Element into stream
	 * @param stream Output Stream
	 * @param t Element to encode in I/O stream
	 * @throws IOException Any I/O operation exception
	 */
	void saveData(OutputStream stream, T t) throws IOException;
	
	/**
	 * Spool encoded text representing the Element into output string
	 * @param t Element to encode in output string
	 * @return Encoded element representing string
	 */
	String toText(T t);
	
	/**
	 * Retrieve parser type of the current instance
	 * @return parser type
	 */
	ParserType getType();
}
