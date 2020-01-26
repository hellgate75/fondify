/**
 * 
 */
package com.rcg.foundation.fondify.core.domain;

import java.util.List;

import com.rcg.foundation.fondify.core.exceptions.MappingException;
import com.rcg.foundation.fondify.core.typings.Mapper;
import com.rcg.foundation.fondify.core.typings.lifecycle.Session;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ByPassMapper implements Mapper<Object, Object> {

	public void init(Session context) {
	}

	@Override
	public Object map(Object source) throws MappingException, NullPointerException {
		return source;
	}

	@Override
	public List<Object> map(List<Object> listOfSources) throws MappingException, NullPointerException {
		return listOfSources;
	}

	@Override
	public Object revert(Object destination) throws MappingException, NullPointerException {
		return destination;
	}

	@Override
	public List<Object> revert(List<Object> listOfDestinations) throws MappingException, NullPointerException {
		return listOfDestinations;
	}

}
