/**
 * 
 */
package com.rcg.foundation.fondify.components.loaders;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;

import com.rcg.foundation.fondify.annotations.typings.ConfigFileLoader;
import com.rcg.foundation.fondify.components.loaders.typings.Bean;
import com.rcg.foundation.fondify.components.loaders.typings.Beans;
import com.rcg.foundation.fondify.core.exceptions.IOException;
import com.rcg.foundation.fondify.properties.typings.JSONParser;
import com.rcg.foundation.fondify.properties.typings.XMLParser;
import com.rcg.foundation.fondify.properties.typings.YAMLParser;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class BeanLoader implements ConfigFileLoader {

	private String lastPath = null;

	private String lastFileToken = null;
	
	/**
	 * 
	 */
	public BeanLoader() {
		super();
	}

	@Override
	public boolean matchConfigFile(String filePath) {
		boolean hasSlash = filePath.contains("/");
		String[] tokens = new String[0];
		if ( hasSlash ) {
			tokens = filePath.split("/");
		} else {
			tokens = filePath.split(File.separator);
		}
		if ( tokens.length < 2 ) {
			return false;
		}
		lastPath = !tokens[tokens.length - 1].contains(".") ?  tokens[tokens.length - 1] : tokens[tokens.length - 2];
		lastFileToken = !tokens[tokens.length - 1].contains(".") ? null : tokens[tokens.length - 1];
		return lastPath!=null && lastPath.toLowerCase().contains("bean");
	}

	@Override
	public void loadConfigFile(String filePath) throws IOException {
		if ( lastPath == null && lastFileToken == null ) {
			throw new IOException("Invalid path / file path or unchecked loader!!");
		}
		File f = new File(filePath);
		if ( ! f.exists() ) {
			throw new IOException(String.format("Path or File doesn't exist!!", filePath));
		}
		loadFileOrFolder(f);
	}
	
	private static final void loadFileOrFolder(File f) throws IOException {
		if ( f.isDirectory() ) {
			try {
				Arrays.asList(f.listFiles()).forEach( file -> {
					try {
						BeanLoader.loadFileOrFolder(file);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				} );
			} catch (Exception e) {
				throw new IOException(e);
			}
		} else {
			//File
			String fileExtension = null;
			String absolutePath = f.getAbsolutePath(); 
			String fileName = absolutePath;
			if ( absolutePath.contains(".") && absolutePath.indexOf(".") < absolutePath.length() - 1 ) {
				int index = absolutePath.indexOf(".");
				fileExtension = absolutePath.toLowerCase().substring(index + 1);
				fileName.substring(0, index);
			}
			if ( fileName.contains("/") ) {
				String[] arr = fileName.split("/"); 
				fileName = arr[arr.length-1];
			} else {
				String[] arr = fileName.split(File.separator); 
				fileName = arr[arr.length-1];
			}
			if ( fileExtension == null ) {
				throw new IOException(String.format("File doesn't contain any extension!!", absolutePath));
			}
			boolean isPlural=fileName.toLowerCase().endsWith("s");
			try {
				String fileContant = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
				if ( fileExtension.equals("json") ) {
					if ( isPlural ) {
						JSONParser<Beans> parser = JSONParser.newParser();
						evaluate(parser.parseText(fileContant, Beans.class));
					} else {
						JSONParser<Bean> parser = JSONParser.newParser();
						evaluate(parser.parseText(fileContant, Bean.class));
					}
				} else if ( fileExtension.equals("xml") ) {
					if ( isPlural ) {
						XMLParser<Beans> parser = XMLParser.newParser();
						evaluate(parser.parseText(fileContant, Beans.class));
					} else {
						XMLParser<Bean> parser = XMLParser.newParser();
						evaluate(parser.parseText(fileContant, Bean.class));
					}
				} else if ( fileExtension.equals("yaml") ) {
					if ( isPlural ) {
						YAMLParser<Beans> parser = YAMLParser.newParser();
						evaluate(parser.parseText(fileContant, Beans.class));
					} else {
						YAMLParser<Bean> parser = YAMLParser.newParser();
						evaluate(parser.parseText(fileContant, Bean.class));
					}
				} else {
					throw new IOException(String.format("Unavailable parser for file %s with extension :%s ", absolutePath, fileExtension));
					
				}
			} catch (java.io.IOException e) {
				throw new IOException("Errors during reading of file: " + f.getAbsolutePath(), e);
			}
		}
	}

	private static final void evaluate(Beans beans) throws IOException {
		if ( beans == null ) {
			throw new IOException("Found null Beans element");
		}
		try {
			beans.elements.forEach( bean -> {
				try {
					evaluate(bean);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	private static final void evaluate(Bean bean) throws IOException {
		if ( bean == null ) {
			throw new IOException("Found null Bean element");
		}
		//Evaluate and register new Bean from loaded data
		//TODO Develop Bean element and bean registration procedure
	}
}
