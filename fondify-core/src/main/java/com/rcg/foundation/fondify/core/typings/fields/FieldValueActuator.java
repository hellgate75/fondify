/**
 * 
 */
package com.rcg.foundation.fondify.core.typings.fields;

import java.lang.reflect.Field;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface FieldValueActuator {
	/**
	 * Retrive value of a field in presence of a specific Annotation type
	 * @param field
	 * @return
	 */
	Object valueOfField(Field field);
	
	/**
	 * Checks if the field contains the annotation related to the current translator
	 * @param field
	 * @return
	 */
	boolean isAppliableTo(Field field);
}
