/**
 * 
 */
package com.rcg.foundation.fondify.core.typings.autorun;

import java.util.ArrayList;
import java.util.List;

import com.rcg.foundation.fondify.core.helpers.BeansHelper;
import com.rcg.foundation.fondify.utils.helpers.LoggerHelper;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class AutorunPhasesActuatorProvider {

	private static AutorunPhasesActuatorProvider instance = null;
	
	List<AutorunInitializerActuator> initializers = new ArrayList<>(0);

	List<AutorunFinalizerActuator> finalizers = new ArrayList<>(0);
	
	/**
	 * 
	 */
	private AutorunPhasesActuatorProvider() {
		super();
		initializers.addAll(
				BeansHelper.getImplementedTypes(AutorunInitializerActuator.class)
		);
		finalizers.addAll(
				BeansHelper.getImplementedTypes(AutorunFinalizerActuator.class)
		);
	}
	
	public void actuateInitializerForAutorun(Autorun autorun) {
		if ( autorun != null ) {
			initializers
				.stream()
				.filter( initializer -> initializer.getInitializerSuperClass().isAssignableFrom(autorun.getClass()) )
				.forEach( initializer -> { 
					try {
						initializer.initAutorun(autorun);
					} catch (Exception e) {
						LoggerHelper.logError("AutorunPhasesActuatorProvider::actuatePhasesForAutorunComponent", 
								String.format("Unable to actuate autrun %s with the initializer : %s due to ERRORS!!!", autorun.getClass().getName(), initializer.getClass().getName()), 
								e);
					} 
				});
		}
	}

	public void actuateFinalizerForAutorun(Autorun autorun) {
		if ( autorun != null ) {
			finalizers
				.stream()
				.filter( finalizer -> finalizer.getFinalizerSuperClass().isAssignableFrom(autorun.getClass()) )
				.forEach( finalizer -> { 
					try {
						finalizer.finalizeAutorun(autorun);
					} catch (Exception e) {
						LoggerHelper.logError("AutorunPhasesActuatorProvider::actuatePhasesForAutorunComponent", 
								String.format("Unable to actuate autrun %s with the finalizer : %s due to ERRORS!!!", autorun.getClass().getName(), finalizer.getClass().getName()), 
								e);
					} 
				});
		}
	}
	
	public static synchronized final AutorunPhasesActuatorProvider getInstance() {
		if ( instance == null ) {
			instance = new AutorunPhasesActuatorProvider();
		}
		return instance;
	}

}

