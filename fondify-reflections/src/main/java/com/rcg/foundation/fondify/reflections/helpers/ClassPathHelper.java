/**
 * 
 */
package com.rcg.foundation.fondify.reflections.helpers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.rcg.foundation.fondify.reflections.typings.ClassPathConfig;
import com.rcg.foundation.fondify.reflections.typings.ClassPathExecutableConfig;
import com.rcg.foundation.fondify.reflections.typings.JavaClassEntity;
import com.rcg.foundation.fondify.reflections.typings.JavaEntry;
import com.rcg.foundation.fondify.reflections.typings.KeyValuePair;
import com.rcg.foundation.fondify.reflections.typings.MatchDescriptor;
import com.rcg.foundation.fondify.reflections.typings.MatchLevel;
import com.strobel.decompiler.Decompiler;
import com.strobel.decompiler.PlainTextOutput;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class ClassPathHelper {

	/**
	 * 
	 */
	private ClassPathHelper() {
		throw new IllegalStateException("ClassPathHelper::constructor -> Unabe to make instance of Utility Class");
	}
	
	public static boolean decompileClasses = false;

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
			String className = filePathToClassName(name, basePath.length() + 1);
			String content = null;
			byte[] bytes = new byte[0];
			try {
				bytes = readStreamContent(new FileInputStream(file));
			} catch (Exception e) {
				throw new RuntimeException(String.format("Error reading data for file: %s, in class path entry: %s", name, basePath), e);
			}
			content = decompileByteCode(bytes);
			int readLen = content!= null ? content.length(): 0;
			JavaEntry javaEntry = new JavaEntry(basePath, name, className, readLen);
			javaEntry.setContent(content);
			list.add(javaEntry);
		}
		return list;
	}
	
	protected static final String decompileByteCode(byte[] bytes) {
		if ( ! decompileClasses ) {
			return "";
		}
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		final PrintWriter writer = new PrintWriter(oStream);
		String fileName = "";
		File f = null;
		try {
			f = File.createTempFile(UUID.randomUUID().toString(), ".class");
			fileName = f.getAbsolutePath();
			f.setWritable(true);
			f.setReadable(true);
			FileUtils.writeByteArrayToFile(f, bytes);
		    Decompiler.decompile(
		        fileName,
		        new PlainTextOutput(writer)
		    );
		}
		catch ( Exception e) {
			throw new RuntimeException("Error decompiling temporary java class file: <" + fileName + ">", e);
		}
		finally {
		    writer.flush();
		    if ( f != null ) {
		    	f.delete();
		    }
		}
		return oStream.toString();
	}
	
	protected static final byte[] readStreamContent(InputStream inputStream) throws Exception {
		byte[] content = new byte[0];
		try {
			content = IOUtils.readFully(inputStream, inputStream.available());
		} catch ( Exception ex ) {
			throw new RuntimeException("Error reading data from stream!!", ex);
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
				javaClassList.addAll(
						listFolderEntries(f, f.getAbsolutePath())
				);
			} else {
				if ( pathEntry.endsWith(".jar") ) {
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
									throw new RuntimeException(String.format("Error reading data for file: %s, in class path entry: %s", name, pathEntry), e);
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
					throw new IllegalStateException("UNKNOWN ENTRY TYPE IN PATH : " + pathEntry);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(String.format("Unable to load resources from classpath entry: %s, sur to ERRORS!!", pathEntry), e);
		}
		return javaClassList;
	}

	protected static final List<String> listJvmEntries() {
		List<String> entriesList = new ArrayList<>(0);
    	String classPath=System.getProperty("java.class.path");
    	entriesList.addAll(
    			Arrays.asList(classPath.split(File.pathSeparator))
    	);
		return entriesList;
	}
	
	private static final List<String> filterClassPathEntries(List<String> entries, List<String> inclusions, List<String> exclusions) {
		if ( inclusions.size() > 0 ) {
			return entries
					.stream()
					.filter(entry -> inclusions
										.stream()
										.filter(cpNameFilter -> entry.contains(cpNameFilter))
										.count() > 0)
					.collect(Collectors.toList());
		}
		if ( exclusions.size() > 0 ) {
			return entries
					.stream()
					.filter(entry -> exclusions
										.stream()
										.filter(cpNameFilter -> entry.contains(cpNameFilter))
										.count() == 0)
					.collect(Collectors.toList());
		}
		return entries;
		
	}

	private static final List<String> filterPackagesEntries(List<String> entries, List<String> inclusions, List<String> exclusions) {
		if ( inclusions.size() > 0 ) {
			return entries
					.stream()
					.filter(entry -> inclusions
										.stream()
										.filter(pkgFilter -> entry.startsWith(pkgFilter))
										.count() > 0)
					.collect(Collectors.toList());
		}
		if ( exclusions.size() > 0 ) {
			return entries
					.stream()
					.filter(entry -> exclusions
										.stream()
										.filter(pkgFilter -> entry.startsWith(pkgFilter))
										.count() == 0)
					.collect(Collectors.toList());
		}
		return entries;
		
	}
	
	public static final ClassPathExecutableConfig createExecutableConfig(ClassPathConfig config) {
		List<String> jvmClassPathEntries = listJvmEntries();
		if ( config == null ) {
			return new ClassPathExecutableConfig(jvmClassPathEntries, null, null, false);
		}
		List<String> filteredClassPathEntries = new ArrayList<>(0);
		filteredClassPathEntries.addAll(
				filterClassPathEntries(jvmClassPathEntries, config.getClassPathInclusionList(), config.getClassPathExclusionList())
		);
		return new ClassPathExecutableConfig(filteredClassPathEntries, config.getPackageInclusionList(), config.getPackageExclusionList(), config.enableSessionData());
	}

	public static final Map<String, List<JavaEntry>> loadClassPathEntries(ClassPathExecutableConfig config) {
    	Map<String, List<JavaEntry>> classPathEntriesMap = new ConcurrentHashMap<String, List<JavaEntry>>(0);
    	if ( config == null || config.getJvmClassPathEntitiesList().size() == 0 ) {
    		return classPathEntriesMap;
    	}
        try {
        	List<String> pathEntries = new ArrayList<String>(0);
        	pathEntries.addAll(
        			config.getJvmClassPathEntitiesList()
        	);
        	classPathEntriesMap.putAll(
	        	pathEntries
	        	.stream()
	        	.collect(Collectors.toMap( entry -> entry , entry -> pathEntryJavaClasses(entry) ))
	        	.entrySet()
	        	.stream()
	        	.map(entry ->  { 
	        		List<String> packages = entry.getValue()
	        				.stream()
	        				.map(javaEntry -> javaEntry.getPackageName())
	        				.distinct()
	        				.collect(Collectors.toList());
	        		List<String> selectedPackages = filterPackagesEntries(packages, config.getPackageInclusionList(), config.getPackageExclusionList());

	        		if ( selectedPackages.size() == 0 ) {
	        			return new KeyValuePair<String, List<JavaEntry>>(entry.getKey(), new ArrayList<JavaEntry>(0));
	        		}

	        		return new KeyValuePair<String, List<JavaEntry>>(entry.getKey(), entry.getValue()
	        																.stream()
	        																.filter( javaEntry -> selectedPackages.contains(javaEntry.getPackageName()) )
	        																.collect(Collectors.toList()) );
	        	})
	        	.filter( entry -> entry.getValue().size() > 0)
	        	.collect(Collectors.toMap( entry -> entry.getKey(), entry->entry.getValue() ))
        	);
        } catch (Exception e) {
			throw new RuntimeException("Error during decompose and load entries from classpath : " + System.getProperty("java.class.path"), e);
        }
        return classPathEntriesMap;
	}
	
	public static final Map<String, List<JavaClassEntity>> compileClassPathEntries(Map<String, List<JavaEntry>> entries) {
		Map<String, List<JavaClassEntity>> compiledEntriesMap = new HashMap<>(0);
		compiledEntriesMap.putAll(
		entries.entrySet()
				.stream()
				.map(entry -> new KeyValuePair<String, List<JavaClassEntity>>(entry.getKey(), entry.getValue()
															.stream()
															.map( JavaEntry::getClassEntity )
															.collect(Collectors.toList())))
	        	.collect(Collectors.toMap( entry -> entry.getKey(), entry->entry.getValue() ))
	    );
		return compiledEntriesMap;
	}

	public static final List<Class<?>> getImplementingTypes(Class<?> baseClass) {
		List<Class<?>> implementingTypes = new ArrayList<>();
		Type superType = baseClass.getGenericSuperclass();
		if ( superType != null ) {
			implementingTypes.add(
			com.google.common.reflect.TypeToken.of(superType).getRawType()
			);
		}
		implementingTypes.addAll(
			Arrays.asList(baseClass.getGenericInterfaces())
				.stream()
				.map(type -> com.google.common.reflect.TypeToken.of(type).getRawType() )
				.collect(Collectors.toList())
		);
		return implementingTypes;
	}

	public static final List<Annotation> getMatchDescriptorAnnotations(MatchDescriptor descriptor) {
		List<Annotation> annotations = new ArrayList<>();
		if (descriptor.getMatchLevel() == MatchLevel.TYPE  ) {
			annotations.addAll(
					Arrays.asList(descriptor.getMatchClass().getDeclaredAnnotations())		
			);
		} else if (descriptor.getMatchLevel() == MatchLevel.METHOD  ) {
				annotations.addAll(
						Arrays.asList(descriptor.getMatchMethod().getDeclaredAnnotations())		
				);
		} else if (descriptor.getMatchLevel() == MatchLevel.FIELD  ) {
			annotations.addAll(
					Arrays.asList(descriptor.getMatchField().getDeclaredAnnotations())		
			);
		} else if (descriptor.getMatchLevel() == MatchLevel.PARAMETER  ) {
			annotations.addAll(
					Arrays.asList(descriptor.getMatchParameter().getDeclaredAnnotations())		
			);
		}
		return annotations;
	}
	
}
