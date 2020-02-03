/**
 * 
 */
package com.rcg.foundation.fondify.annotations.helpers;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import com.rcg.foundation.fondify.annotations.typings.ConfigFileLoader;
import com.rcg.foundation.fondify.core.registry.ComponentsRegistry;
import com.rcg.foundation.fondify.utils.helpers.LoggerHelper;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class ScriptsHelper {

	/**
	 * 
	 */
	private ScriptsHelper() {
		throw new IllegalStateException("ScriptsHelper::constructor - unable to instantiate utility class!!");
	}

	public static final void loadScriptFileOrFolder(String fileOrFolder, String registryKey) {
		File file = new File(fileOrFolder);
		if ( file.isDirectory() ) {
			// config folder
			Arrays.asList(file.list()).forEach(f->loadScriptFileOrFolder(f, registryKey));
			
		} else {
			// config file
			boolean containsSlash = fileOrFolder.contains("/");
			String lastFolderName = "";
			String fileName = "";
			String[] fileTokens = new String[0];
			if (containsSlash) {
				fileOrFolder.split("/");
			} else {
				fileOrFolder.split(File.separator);
			}
			if ( fileTokens.length >= 2 ) {
				fileName = fileTokens[fileTokens.length-1];
				int index = 2;
				while ( lastFolderName.isEmpty() && index <= fileTokens.length ) {
					lastFolderName = fileTokens[fileTokens.length-index];
					while (lastFolderName.contains(":")) {
						lastFolderName.replace(":", "");
					}
					index++;
				}
			} else {
				String message = String.format("Unable to load Configuration File : %s (file name: %s), because file containing folder is not provided...", fileOrFolder, fileName);
				LoggerHelper.logWarn("ApplicatrionHelper::loadScriptFileOrFolder", message, null);
			}
			Collection<ConfigFileLoader> loaders = ComponentsRegistry.getInstance().getAll(registryKey);
			final String loalFileName = fileName;
			loaders
				.stream()
				.filter( loader -> loader.matchConfigFile(fileOrFolder) )
				.forEach(loader ->  { 
					try {
						loader.loadConfigFile(fileOrFolder);
					} catch (Exception e) {
						String message = String.format("Unable to load Configuration File : %s (file name: %s), with loader: %s", fileOrFolder, loalFileName, loader.getClass().getName());
						LoggerHelper.logError("ApplicatrionHelper::loadScriptFileOrFolder", message, e);
					} 
				});
		}
	}

}
