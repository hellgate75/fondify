/**
 * 
 */
package com.rcg.foundation.fondify.reflections;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.rcg.foundation.fondify.reflections.typings.JavaEntry;
import com.strobel.decompiler.Decompiler;
import com.strobel.decompiler.PlainTextOutput;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class TestMain {
	
	protected static final List<JavaEntry> listFolderEntries(File file, String basePath) {
		List<JavaEntry> list = new ArrayList<>(0);
		if ( file.isDirectory() ) {
			list.addAll(
				Arrays.asList(file.listFiles())
					.stream()
					.filter(fileN -> fileN.isDirectory() || fileN.getName().endsWith(".class"))
					.map(fileN -> listFolderEntries(fileN, basePath))
					.flatMap( List::stream )
					.collect(Collectors.toList())
			);
		} else {
			String name = file.getAbsolutePath();
			System.out.println("File Name: " + name + " (" + name.length() + ")");
			String className = filePathToClassName(name, basePath.length() + 1);
			System.out.println("Class Name: " + className);
			String content = null;
			byte[] bytes = new byte[0];
			try {
				bytes = readStreamContent(new FileInputStream(file));
			} catch (Exception e) {
				e.printStackTrace();
			}
			content = decompileByteCode(bytes);
			System.out.println("Content: \n" + content);
			int readLen = content!= null ? content.length(): 0;
			JavaEntry javaEntry = new JavaEntry(basePath, name, className, readLen);
			javaEntry.setContent(content);
			list.add(javaEntry);
		}
		return list;
	}
	
	protected static final String decompileByteCode(byte[] bytes) {
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		final PrintWriter writer = new PrintWriter(oStream);

		try {
			File f = File.createTempFile(UUID.randomUUID().toString(), ".class");
			f.setWritable(true);
			f.setReadable(true);
			FileUtils.writeByteArrayToFile(f, bytes);
		    Decompiler.decompile(
		        f.getAbsolutePath(),
		        new PlainTextOutput(writer)
		    );
		    f.delete();
		}
		catch ( Exception e) {
			e.printStackTrace();
		}
		finally {
		    writer.flush();
		}
		return oStream.toString();
	}
	
	protected static final byte[] readStreamContent(InputStream inputStream) throws Exception {
		byte[] content = new byte[0];
		try {
			content = IOUtils.readFully(inputStream, inputStream.available());
		} catch ( Exception ex ) {
			ex.printStackTrace();
		}
		return content;
	}
	
	protected static final String filePathToClassName(String filePath, int jump) {
		if ( jump > 0 ) {
			String newPath = filePath.substring(jump);
			return newPath.replaceAll("\\/", ".").substring(0, newPath.length() - 6);
		}
		return filePath.replaceAll("\\/", ".").substring(0, filePath.length() - 6);
	}
	
	public static final List<JavaEntry> pathEntryJavaClasses(String pathEntry) {
		List<JavaEntry> javaClassList = new ArrayList<>(0);
		
		try {
			File f = new File(pathEntry);
			if ( f.isDirectory() ) {
				System.out.println("It's a folder : " + pathEntry);
				javaClassList.addAll(
						listFolderEntries(f, f.getAbsolutePath())
				);
			} else {
				if ( pathEntry.endsWith(".jar") ) {
					System.out.println("It's a jar : " + pathEntry);
					@SuppressWarnings("resource")
					JarFile jarFile = new JarFile(f);
					javaClassList.addAll(
					jarFile
							.stream()
							.filter(  entry -> ! entry.isDirectory() && entry.getName().endsWith(".class") )
							.map(entry->{
								String name = entry.getName();
								String className=filePathToClassName(name, 0);
								String content = null;
								byte[] bytes = new byte[0];
								try {
									bytes = readStreamContent(jarFile.getInputStream(entry));
								} catch (Exception e) {
									e.printStackTrace();
								}
								content = decompileByteCode(bytes);
								int readLen = content!= null ? content.length(): 0;
								JavaEntry javaEntry = new JavaEntry(pathEntry, name, className, readLen);
								javaEntry.setContent(content);
								return javaEntry;
							})
							.collect(Collectors.toList())
						);
				} else {
					System.out.println("UNKNOWN FILE : " + pathEntry);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return javaClassList;
	}
	
	public static void main(String[] args) throws Exception {
        try {
        	Map<String, List<JavaEntry>> systemEntries = new ConcurrentHashMap<String, List<JavaEntry>>(0);
        	System.out.println(File.pathSeparator);
        	String classPath=System.getProperty("java.class.path");
        	System.out.println("Classpath=" + classPath);
        	List<String> pathEntries = new ArrayList<String>(0);
        	pathEntries.addAll(
        			Arrays.asList(classPath.split(File.pathSeparator))
        	);
        	systemEntries.putAll(
	        	pathEntries
	        	.stream()
	        	.collect(Collectors.toMap( entry -> entry , entry -> pathEntryJavaClasses(entry) ))
        	);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}