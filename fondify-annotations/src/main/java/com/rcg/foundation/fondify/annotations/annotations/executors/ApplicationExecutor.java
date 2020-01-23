/**
 * 
 */
package com.rcg.foundation.fondify.annotations.annotations.executors;

import com.rcg.foundation.fondify.annotations.annotations.Application;
import com.rcg.foundation.fondify.core.domain.ApplicationType;
import com.rcg.foundation.fondify.core.exceptions.ProcessException;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;
import com.rcg.foundation.fondify.core.typings.AnnotationDeclaration;
import com.rcg.foundation.fondify.core.typings.AnnotationExecutor;
import com.rcg.foundation.fondify.core.typings.ExecutionAnswer;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ApplicationExecutor implements AnnotationExecutor<Application> {

	String name = "Application";
	
	/**
	 * 
	 */
	public ApplicationExecutor() {
		super();
	}

	@Override
	public Class<? extends Application> getAnnotationClass() {
		return Application.class;
	}

	@Override
	public boolean containsResults() {
		return true;
	}

	@Override
	public String getComponentName() {
		return name;
	}

	@Override
	public ExecutionAnswer<Application> executeAnnotation(AnnotationDeclaration t) throws ProcessException {
		LoggerHelper.logTrace("ApplicationExecutor::executeAnnotation(Application)", "Executing annotation in TRCG Annotation Engine Annotations Module");
		name = "Application";
		Application application = (Application)t.getAnnotation();
		ApplicationType type = application.scope();
		name = t.getAnnotatedClass().getName();
		String message = "";
		ExecutionAnswer<Application> answer = new ExecutionAnswer<>(Application.class, message, false, false);
		answer.addResult(type);
		return answer;
	}
}
