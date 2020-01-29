/**
 * 
 */
package com.rcg.foundation.fondify.components.runtime;

import java.util.Arrays;
import java.util.Collection;

import com.rcg.foundation.fondify.annotations.lifecycle.ApplicationManagerProvider;
import com.rcg.foundation.fondify.core.typings.autorun.AutoFullScanTypesDescriptor;
import com.rcg.foundation.fondify.core.typings.lifecycle.ComponentManagerProvider;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ComponentsAutoFullScanTypesDescriptor implements AutoFullScanTypesDescriptor {

	/**
	 * 
	 */
	public ComponentsAutoFullScanTypesDescriptor() {
		super();
	}

	@Override
	public Collection<Class<?>> getGenericInterfacesList() {
		return Arrays.asList(new Class<?>[] {
			ApplicationManagerProvider.class,
			ComponentManagerProvider.class
		});
	}

}
