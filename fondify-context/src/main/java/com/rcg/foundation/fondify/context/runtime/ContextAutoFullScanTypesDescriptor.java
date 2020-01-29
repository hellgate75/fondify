/**
 * 
 */
package com.rcg.foundation.fondify.context.runtime;

import java.util.Arrays;
import java.util.Collection;

import com.rcg.foundation.fondify.annotations.lifecycle.SessionContext;
import com.rcg.foundation.fondify.core.typings.autorun.AutoFullScanTypesDescriptor;
import com.rcg.foundation.fondify.core.typings.lifecycle.Session;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ContextAutoFullScanTypesDescriptor implements AutoFullScanTypesDescriptor {

	/**
	 * 
	 */
	public ContextAutoFullScanTypesDescriptor() {
		super();
	}

	@Override
	public Collection<Class<?>> getGenericInterfacesList() {
		return Arrays.asList(new Class<?>[] {SessionContext.class, Session.class});
	}

}
