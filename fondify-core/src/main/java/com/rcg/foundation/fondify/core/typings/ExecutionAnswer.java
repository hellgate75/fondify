/**
 * 
 */
package com.rcg.foundation.fondify.core.typings;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ExecutionAnswer<T extends Annotation> {

	private Class<? extends T> type;

	private List<Object> results = new ArrayList<>(0);
	
	private String message = null;
	
	private boolean warnings = false;
	
	private boolean errors = false;

	/**
	 * @param type
	 */
	public ExecutionAnswer(Class<? extends T> type) {
		super();
		this.type = type;
	}

	/**
	 * @param type
	 * @param message
	 * @param warnings
	 * @param errors
	 */
	public ExecutionAnswer(Class<? extends T> type, String message, boolean warnings, boolean errors) {
		super();
		this.type = type;
		this.message = message;
		this.warnings = warnings;
		this.errors = errors;
	}

	/**
	 * @param type
	 * @param results
	 * @param message
	 * @param warnings
	 * @param errors
	 */
	public ExecutionAnswer(Class<? extends T> type, List<Object> results, String message, boolean warnings,
			boolean errors) {
		super();
		this.type = type;
		this.results.addAll(results);
		this.message = message;
		this.warnings = warnings;
		this.errors = errors;
	}

	/**
	 * @return the type
	 */
	public Class<? extends T> getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Class<? extends T> type) {
		this.type = type;
	}

	/**
	 * @return the results
	 */
	public Collection<Object> getResults() {
		return results;
	}

	/**
	 * @param results the results to set
	 */
	public void setResults(Collection<Object> results) {
		this.results.addAll(results);
	}

	/**
	 * @param result the result to add
	 */
	public void addResult(Object result) {
		this.results.add(result);
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the warnings
	 */
	public boolean isWarnings() {
		return warnings;
	}

	/**
	 * @param warnings the warnings to set
	 */
	public void setWarnings(boolean warnings) {
		this.warnings = warnings;
	}

	/**
	 * @return the errors
	 */
	public boolean isErrors() {
		return errors;
	}

	/**
	 * @param errors the errors to set
	 */
	public void setErrors(boolean errors) {
		this.errors = errors;
	}
	
	

}
