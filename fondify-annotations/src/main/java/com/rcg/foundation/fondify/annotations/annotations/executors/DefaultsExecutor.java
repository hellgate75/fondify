/**
 * 
 */
package com.rcg.foundation.fondify.annotations.annotations.executors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.rcg.foundation.fondify.annotations.annotations.Defaults;
import com.rcg.foundation.fondify.core.exceptions.ProcessException;
import com.rcg.foundation.fondify.core.typings.AnnotationDeclaration;
import com.rcg.foundation.fondify.core.typings.AnnotationExecutor;
import com.rcg.foundation.fondify.core.typings.ExecutionAnswer;
import com.rcg.foundation.fondify.utils.helpers.LoggerHelper;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class DefaultsExecutor implements AnnotationExecutor<Defaults> {

	
	/**
	 * 
	 */
	public DefaultsExecutor() {
		super();
	}

	@Override
	public Class<? extends Defaults> getAnnotationClass() {
		return Defaults.class;
	}

	@Override
	public boolean containsResults() {
		return true;
	}

	@Override
	public String getComponentName() {
		return "DefaultInfo";
	}

	@Override
	public ExecutionAnswer<Defaults> executeAnnotation(AnnotationDeclaration t) throws ProcessException {
		LoggerHelper.logTrace("DefaultsExecutor::executeAnnotation(Defaults)", "Executing annotation in TRCG Annotation Engine Annotations Module");
		Defaults defaults = (Defaults) t.getAnnotation();
		List<String> jobFolders = Collections.synchronizedList(new ArrayList<>(0));
		jobFolders.addAll(
				Arrays.asList(
						defaults.jobsFolders()
				)
		);
		List<String> baseFolders = Collections.synchronizedList(new ArrayList<>(0));
		baseFolders.addAll(
				Arrays.asList(
						defaults.configFolders()
				)
		);
		String message = String.format("Defaults warnings -> config folders: %s, jobs folder: %s ", ""+ baseFolders.isEmpty(), "" + jobFolders.isEmpty());
		ExecutionAnswer<Defaults> answer = new ExecutionAnswer<>(Defaults.class, message, baseFolders.isEmpty() || jobFolders.isEmpty(), false);
		ConfigDefaults config = new ConfigDefaults(baseFolders, jobFolders);
		answer.addResult(config);
		return answer;
	}
	
	static class ConfigDefaults implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 162979096878676458L;
		
		private List<String> configFoldersList = new ArrayList<>();
		private List<String> jobFoldersList = new ArrayList<>();
		/**
		 * @param configFoldersList
		 * @param jobFoldersList
		 */
		protected ConfigDefaults(List<String> configFoldersList, List<String> jobFoldersList) {
			super();
			this.configFoldersList.addAll(configFoldersList);
			this.jobFoldersList.addAll(jobFoldersList);
		}
		/**
		 * @return the configFoldersList
		 */
		public List<String> getConfigFoldersList() {
			return configFoldersList;
		}
		/**
		 * @return the jobFoldersList
		 */
		public List<String> getJobFoldersList() {
			return jobFoldersList;
		}
		
		
	}
}
