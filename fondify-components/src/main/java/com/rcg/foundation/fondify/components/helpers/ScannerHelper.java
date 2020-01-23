/**
 * 
 */
package com.rcg.foundation.fondify.components.helpers;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import com.rcg.foundation.fondify.annotations.contants.AnnotationConstants;
import com.rcg.foundation.fondify.core.exceptions.ScannerException;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;
import com.rcg.foundation.fondify.core.typings.AnnotationExecutor;

/**
 * Utility class that provides features for helping with Java artifacts Scan and
 * {@link Annotation} list recovery from the Java Class Path resources (any
 * folders/jars in the entire class-path artifacts or only in a subset of
 * packages present in one or more folders/jars!!).
 * 
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ScannerHelper extends com.rcg.foundation.fondify.annotations.helpers.ScannerHelper {

	static {
		com.rcg.foundation.fondify.annotations.helpers.ScannerHelper.DEFAULT_SCANNERS.add("com.rcg.streams.streamio.core.module.CoreModuleScanner"); 
		com.rcg.foundation.fondify.annotations.helpers.ScannerHelper.DEFAULT_SCANNERS.add("com.rcg.streams.streamio.data.DataModuleScanner");
		com.rcg.foundation.fondify.annotations.helpers.ScannerHelper.DEFAULT_SCANNERS.add("com.rcg.streams.streamio.jobs.JobsModuleScanner");
		com.rcg.foundation.fondify.annotations.helpers.ScannerHelper.DEFAULT_SCANNERS.add("com.rcg.streams.streamio.web.WebModuleScanner");
		com.rcg.foundation.fondify.annotations.helpers.ScannerHelper.annotationDescriptorsRegistryKey = AnnotationConstants.REGISTRY_ANNOTATION_DESCRIPTORS;
	}
	
	/**
	 * Denied access constructor
	 */
	private ScannerHelper() {
		throw new IllegalStateException("ScannerHelper::constructor - unable to instantiate utility class!!");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final void addAnnotationExecutorsInPackage(String packageName) {
		Reflections reflections = new Reflections(packageName, new SubTypesScanner());
		Set<Class<? extends AnnotationExecutor>> classes = reflections.getSubTypesOf(AnnotationExecutor.class);
		classes.forEach(cls -> {
			try {
				AnnotationHelper.addExecutorInRegistry(cls.newInstance());
			} catch (Exception ex) {
				String message = String.format("Unable to create insatnce of executor class : " + cls.getName());
				LoggerHelper.logError("ScannerHelper::addAnnotationExecutorsInPackage", message, ex);
				throw new ScannerException(message, ex);
			}
		});
	}
}
