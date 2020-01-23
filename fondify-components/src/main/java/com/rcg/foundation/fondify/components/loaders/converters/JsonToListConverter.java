/**
 * 
 */
package com.rcg.foundation.fondify.components.loaders.converters;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.util.StdConverter;
import com.hazelcast.internal.json.Json;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class JsonToListConverter<T> extends StdConverter<String, List<T>> {

	@SuppressWarnings("unchecked")
	@Override
	public List<T> convert(String value) {
		return Json.parse(value).asArray().values()
			.stream()
			.map( jsonVal -> (T) jsonVal.asObject() )
			.collect(Collectors.toList());
	}

}
