/**
 * 
 */
package com.rcg.foundation.fondify.core.typings;

import java.util.List;

import com.rcg.foundation.fondify.core.exceptions.MappingException;

/**
 * Protorype for a Data or element mapper, it's used to transform an element and cast it 
 * into another element type or same elements with some alterations
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 */
public interface Mapper<S, D> {
	
	/**
	 * Initialization method used to setUp the Mapper element at any moment before use
	 * in the session. It's used to pass the {@link Session} to the mapper
	 * @param context <ApplicationContext> context used by the mapper.
	 */
	void init(Session context);

	/**
	 * Maps and cast a source element of type <S> in an element of type <D>
	 * @param source <S> Source element
	 * @return <D> Destination element, mapped for the source element of type <S> 
	 * @throw MappingException Is thrown during any error that can occurs during mapping/revert operations
	 * @throw NullPointerException Is thrown in case any element in the mapping/revert operation is null
	 */
	D map(S source) throws MappingException, NullPointerException;
	
	/**
	 * Maps and cast a source elements list of type <S> in an element list of type <D>
	 * @param listOfSources <List<S>> Source elements list
	 * @return <List<D>> Lit of destination elements, mapped for the list of source elements, all of type <S> 
	 * @return
	 * @throw MappingException Is thrown during any error that can occurs during mapping/revert operations
	 * @throw NullPointerException Is thrown in case any element in the mapping/revert operation is null
	 */
	List<D> map(List<S> listOfSources) throws MappingException, NullPointerException;
	
	/**
	 * Revert a destination element (of type <D>) to the original source or most close representation of the type <S>
	 * @param destination <D> Destination element as input of the reverse operation 
	 * @return <S> Source element as output of the mapping revert operation
	 * @throw MappingException Is thrown during any error that can occurs during mapping/revert operations
	 * @throw NullPointerException Is thrown in case any element in the mapping/revert operation is null
	 */
	S revert(D destination) throws MappingException, NullPointerException;
	
	/**
	 * Revert a destination elements list (of type <D>) to the original list of sources or most close representation of the type <S>
	 * @param listOfDestinations <List<D>> List of Destination elements as input of the reverse operation 
	 * @return <S> List of Source elements as output of the mapping revert operation of the list of Destination elements
	 * @throw MappingException Is thrown during any error that can occurs during mapping/revert operations
	 * @throw NullPointerException Is thrown in case any element in the mapping/revert operation is null
	 */
	List<S> revert(List<D> listOfDestinations) throws MappingException, NullPointerException;
}
