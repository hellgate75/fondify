/**
 * 
 */
package com.rcg.foundation.fondify.core.typings;

import java.util.Collection;
import java.util.List;

/**
 * Interface that defines main features of a Module and it identifies 
 * Internal annotations, giving life to new feature and its capabilities
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface ModuleScanner {
	
	/**
	 * Scan packages for annotations
	 * @param packages Array of package names or null/empty for full scan executions
	 */
	void scanPackages(Collection<String> packages);
	
	/**
	 * Gets all retrieved annotations
	 * @return <List<AnnotationDeclaration>> list of module related annotations declaration
	 */
	List<AnnotationDeclaration> annotations();
}
