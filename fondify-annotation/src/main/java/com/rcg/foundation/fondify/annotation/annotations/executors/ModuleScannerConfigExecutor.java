/**
 * 
 */
package com.rcg.foundation.fondify.annotation.annotations.executors;

import java.util.Arrays;

import com.rcg.foundation.fondify.annotation.annotations.ModuleScannerConfig;
import com.rcg.foundation.fondify.annotation.annotations.TransformCase;
import com.rcg.foundation.fondify.annotation.helpers.AnnotationHelper;
import com.rcg.foundation.fondify.core.exceptions.ProcessException;
import com.rcg.foundation.fondify.core.typings.AnnotationDeclaration;
import com.rcg.foundation.fondify.core.typings.AnnotationExecutor;
import com.rcg.foundation.fondify.core.typings.ExecutionAnswer;
import com.rcg.foundation.fondify.core.typings.ModuleMain;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ModuleScannerConfigExecutor implements AnnotationExecutor<ModuleScannerConfig> {

	String name = "ModuleScanner";
	
	/**
	 * 
	 */
	public ModuleScannerConfigExecutor() {
		super();
	}

	@Override
	public Class<? extends ModuleScannerConfig> getAnnotationClass() {
		return ModuleScannerConfig.class;
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
	public ExecutionAnswer<ModuleScannerConfig> executeAnnotation(AnnotationDeclaration t) throws ProcessException {
		name = "ModuleScanner";
		ModuleScannerConfig scanner = (ModuleScannerConfig)t.getAnnotation();
		Class<? extends ModuleMain>[] classes = scanner.mainClasses();
		name = scanner.name();
		if ( name == null || name.isEmpty() ) {
			name = t.getAnnotationClass().getName();
		}
		Class<?> clazz = t.getAnnotationClass();
		TransformCase tc = clazz.getAnnotation(TransformCase.class);
		if ( tc != null ) {
			name = AnnotationHelper.transformBeanName(name, tc);
		}
		String message = String.format("Warnings for annotation ModuleScanner in instance : %s has warnings %s", name, "" + (classes.length==0));
		ExecutionAnswer<ModuleScannerConfig> answer = new ExecutionAnswer<>(ModuleScannerConfig.class, message, classes.length==0, false);
		answer.addResult(Arrays.asList(classes));
		return answer;
	}
}
