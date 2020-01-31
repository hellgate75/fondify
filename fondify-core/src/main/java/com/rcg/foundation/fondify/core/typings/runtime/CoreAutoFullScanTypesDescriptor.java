/**
 * 
 */
package com.rcg.foundation.fondify.core.typings.runtime;

import java.util.Arrays;
import java.util.Collection;

import com.rcg.foundation.fondify.core.typings.AnnotationEngineInitializer;
import com.rcg.foundation.fondify.core.typings.AnnotationExecutor;
import com.rcg.foundation.fondify.core.typings.AnnotationTypesCollector;
import com.rcg.foundation.fondify.core.typings.ModuleScanner;
import com.rcg.foundation.fondify.core.typings.autorun.AutoFullScanTypesDescriptor;
import com.rcg.foundation.fondify.core.typings.autorun.Autorun;
import com.rcg.foundation.fondify.core.typings.autorun.AutorunFinalizerActuator;
import com.rcg.foundation.fondify.core.typings.autorun.AutorunInitializerActuator;
import com.rcg.foundation.fondify.core.typings.cache.CacheProvider;
import com.rcg.foundation.fondify.core.typings.fields.FieldValueActuator;
import com.rcg.foundation.fondify.core.typings.lifecycle.ComponentsDiscoveryManager;
import com.rcg.foundation.fondify.core.typings.parameters.ParameterValueActuator;
import com.rcg.foundation.fondify.core.typings.registry.AnnotationBeanActuator;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class CoreAutoFullScanTypesDescriptor implements AutoFullScanTypesDescriptor {

	/**
	 * 
	 */
	public CoreAutoFullScanTypesDescriptor() {
		super();
	}

	@Override
	public Collection<Class<?>> getGenericInterfacesList() {
		return Arrays.asList(new Class<?>[] {
								AnnotationBeanActuator.class,
								AnnotationEngineInitializer.class,
								AnnotationExecutor.class,
								AnnotationTypesCollector.class,
								Autorun.class,
								AutorunInitializerActuator.class,
								AutorunFinalizerActuator.class,
								CacheProvider.class,
								ComponentsDiscoveryManager.class,
								FieldValueActuator.class,
								ModuleScanner.class,
								ParameterValueActuator.class
								});
	}

}
