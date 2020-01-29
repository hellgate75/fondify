/**
 * 
 */
package com.rcg.foundation.fondify.core.typings.autorun;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface AutorunFinalizerActuator {
	
	/**
	 * Finalize {@link Autorun} extension element 
	 * @param autorun
	 * @throws Exception
	 */
	void finalizeAutorun(Autorun autorun) throws Exception;

	/**
	 * Retrieve the class of the Autorun extension class for which feature is provided
	 * @return
	 */
	Class<?> getFinalizerSuperClass();
}
