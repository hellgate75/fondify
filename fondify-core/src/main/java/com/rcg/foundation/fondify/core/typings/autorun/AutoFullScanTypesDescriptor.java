/**
 * 
 */
package com.rcg.foundation.fondify.core.typings.autorun;

import java.util.Collection;

import com.rcg.foundation.fondify.core.helpers.BeansHelper;

/**
 * Any implementation declares the auto load procedure input data
 * composed by super-classes or interfaces of elements to be loaded.
 * In requirements it's excluded reporting of sub-interfaces.
 * 
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 * @See {@link BeansHelper#getImplementatedSubtypesOf(Class)}
 */
public interface AutoFullScanTypesDescriptor {
	/**
	 * Retrieve the list of super-classes / interfaces for which 
	 * it's required a full package scan to recover sub-types of
	 * given classes or interface classes.
	 * It's used for collection their implementations. It happens at 
	 * the annotation engine start-up that this interface implementations
	 * are loaded from a full package scan and data is collected and accessible 
	 * from the interface. Sub-Type are accessible from the common bean artifact,
	 * i.e. the {@link BeansHelper} utility class using {@link BeansHelper#getImplementatedSubtypesOf(Class)}
	 * static method by wanted super-type or interface.  
	 * @return
	 */
	Collection<Class<?>> getGenericInterfacesList();
}
