/**
 * 
 */
package com.rcg.foundation.fondify.properties.typings;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.rcg.foundation.fondify.core.exceptions.IOException;
import com.rcg.foundation.fondify.utils.helpers.LoggerHelper;

/**
 * YAML Format Parser implementation
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class YAMLParser<T> implements Parser<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7424817417470449537L;

	private YAMLMapper mapper = new YAMLMapper(new YAMLFactory());
	
	/**
	 * Default Constructor
	 */
	protected YAMLParser() {
		super();
	}

	@Override
	public T loadData(String fileName, Class<? extends T> clazz) throws IOException {
		try (FileInputStream stream = new FileInputStream(fileName)) {
			return mapper.readValue(stream, clazz);
		} catch (Exception ex) {
			String message = String.format("Unable to load data from file: %s", fileName);
			LoggerHelper.logError("YAMLParser::loadData<string>", message, ex);
			throw new IOException(message, ex);
		}
	}

	@Override
	public T loadData(InputStream stream, Class<? extends T> clazz) throws IOException {
		try {
			return mapper.readValue(stream, clazz);
		} catch (Exception ex) {
			String message = "Unable to load data from input stream";
			LoggerHelper.logError("YAMLParser::loadData<inputstream>", message, ex);
			throw new IOException(message, ex);
		}
	}

	@Override
	public T parseText(String text, Class<? extends T> clazz) {
		try {
			return mapper.readValue(text, clazz);
		} catch (Exception ex) {
			String message = String.format("Unable to load data from given body: %s", text);
			LoggerHelper.logError("YAMLParser::parseText<string body>", message, ex);
			throw new RuntimeException(message, ex);
		}
	}

	@Override
	public void saveData(String fileName, T t) throws IOException {
		try (FileOutputStream stream = new FileOutputStream(fileName)) {
			mapper.writeValue(stream, t);
		} catch (Exception ex) {
			String message = String.format("Unable to save data to file: %s", fileName);
			LoggerHelper.logError("YAMLParser::saveData<string, type instance>", message, ex);
			throw new IOException(message, ex);
		}
	}

	@Override
	public void saveData(OutputStream stream, T t) throws IOException {
		try {
			mapper.writeValue(stream, t);
		} catch (Exception ex) {
			String message = "Unable to save data to output stream";
			LoggerHelper.logError("YAMLParser::saveData<outputstream, type instance>", message, ex);
			throw new IOException(message, ex);
		}
	}

	@Override
	public String toText(T t) {
		try {
			return mapper.writeValueAsString(t);
		} catch (Exception ex) {
			String message = String.format("Unable to convert data from given element: %s", ""+t);
			LoggerHelper.logError("YAMLParser::toText<type instance>", message, ex);
			throw new RuntimeException(message, ex);
		}
	}

	@Override
	public ParserType getType() {
		return ParserType.YAML;
	}

	/**
	 * Create new parser instance
	 * @param <T>
	 * @return
	 */
	public static final <T> YAMLParser<T> newParser() {
		return new YAMLParser<>(); 
	}

}
