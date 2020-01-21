/**
 * 
 */
package com.rcg.foundation.fondify.core.typings;

import java.util.Collection;

import com.rcg.foundation.fondify.core.exceptions.ModuleException;

/**
 * Defines Module execution capabilities as Main class executor.
 * 
 * Any of this implementation will be threaded as a Singleton.
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface ModuleMain {
	
	/**
	 * Execute Module main using module related annotations
	 * @param annotations declaration list of module related annotations
	 * @throws ModuleException Arisen during any module load / abort / enquire operation.
	 */
	void execute(Collection<AnnotationDeclaration> annotations) throws ModuleException;
	
	/**
	 * Aborts module execution
	 * @throws ModuleException Arisen during any module load / abort / enquire operation.
	 */
	void abort() throws ModuleException;
	
	/**
	 * Get state of module if it's running.
	 * @return <boolean> Running state
	 * @throws ModuleException Arisen during any module load / abort / enquire operation.
	 */
	boolean isRunning() throws ModuleException;
}
