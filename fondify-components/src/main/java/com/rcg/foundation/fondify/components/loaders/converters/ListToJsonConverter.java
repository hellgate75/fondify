/**
 * 
 */
package com.rcg.foundation.fondify.components.loaders.converters;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ListToJsonConverter<T> extends StdConverter<List<T>, String> {

	@Override
	public String convert(List<T> value) {
		try {
			return new ObjectMapper().writeValueAsString(value.toArray());
		} catch (Exception e) {
			LoggerHelper.logError("ListToJsonConverter::convert", "Unable to convert list to json", e);
			return null;
		}
	}

}
