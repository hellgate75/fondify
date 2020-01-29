/**
 * 
 */
package com.rcg.foundation.fondify.annotations.lifecycle.runtime;

import java.util.Arrays;
import java.util.Collection;

import com.rcg.foundation.fondify.annotations.lifecycle.ApplicationManagerProvider;
import com.rcg.foundation.fondify.annotations.typings.ApplicationConsole;
import com.rcg.foundation.fondify.core.typings.autorun.AutoFullScanTypesDescriptor;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class AnnotationsAutoFullScanTypesDescriptor implements AutoFullScanTypesDescriptor {

	/**
	 * 
	 */
	public AnnotationsAutoFullScanTypesDescriptor() {
		super();
	}

	@Override
	public Collection<Class<?>> getGenericInterfacesList() {
		return Arrays.asList(new Class<?>[] {
						ApplicationConsole.class,
						ApplicationManagerProvider.class
					});
	}

}
