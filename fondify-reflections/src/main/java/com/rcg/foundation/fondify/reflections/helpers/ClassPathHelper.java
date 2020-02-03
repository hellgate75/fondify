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
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.rcg.foundation.fondify.reflections.Reflections;
import com.rcg.foundation.fondify.reflections.typings.ClassPathConfig;
import com.rcg.foundation.fondify.reflections.typings.ClassPathExecutableConfig;
import com.rcg.foundation.fondify.reflections.typings.JavaClassEntity;
import com.rcg.foundation.fondify.reflections.typings.JavaEntry;
import com.rcg.foundation.fondify.reflections.typings.KeyValuePair;
import com.rcg.foundation.fondify.reflections.typings.MatchDescriptor;
import com.rcg.foundation.fondify.reflections.typings.MatchLevel;
import com.rcg.foundation.fondify.utils.helpers.ArgumentsHelper;
import com.rcg.foundation.fondify.utils.helpers.LoggerHelper;
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
			if ( decompileClasses ) {
				try {
					bytes = readStreamContent(new FileInputStream(file));
				} catch (Exception e) {
					throw new RuntimeException(String.format("Error reading data for file: %s, in class path entry: %s", name, basePath), e);
				}
					content = decompileByteCode(bytes);
			}
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
		String separator = File.separator;
		if ( separator.equals("/") )
			separator = "\\"+separator;
		else if ( separator.equals("\\") )
			separator = "\\\\";
		if ( jump > 0 ) {
			String newPath = filePath.substring(jump);
			return newPath.replaceAll("\\/", ".").replaceAll(separator, ".").substring(0, newPath.length() - 6);
		}
		return filePath.replaceAll("\\/", ".").replaceAll(separator, ".").substring(0, filePath.length() - 6);
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
								if ( decompileClasses ) {
									try {
										bytes = readStreamContent(jarFile.getInputStream(entry));
									} catch (Exception e) {
										throw new RuntimeException(String.format("Error reading data for file: %s, in class path entry: %s", name, pathEntry), e);
									}
									content = decompileByteCode(bytes);
								}
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
		if ( ArgumentsHelper.traceAllLevels || ArgumentsHelper.traceReflectionsLevel ) {
			LoggerHelper.logTrace("ClassPathHelper::listJvmEntries", "Discovered JVM Class-Path Entrries : " + entriesList.size());
			entriesList.forEach(jvmEntry -> {
				LoggerHelper.logTrace("ClassPathHelper::listJvmEntries", "Discovered JVM Class-Path Entrry with path : " + jvmEntry);
			});
		}
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
					.filter( entry -> entry != null && ! entry.isEmpty() )
					.collect(Collectors.toList());
		}
		if ( exclusions.size() > 0 ) {
			return entries
					.stream()
					.filter(entry -> exclusions
										.stream()
										.filter(cpNameFilter -> entry.contains(cpNameFilter))
										.count() == 0)
					.filter( entry -> entry != null && ! entry.isEmpty() )
					.collect(Collectors.toList());
		}
		return entries.stream().filter( entry -> entry != null && ! entry.isEmpty() ).collect(Collectors.toList());
		
	}

	private static final List<String> filterPackagesEntries(List<String> entries, List<String> inclusions, List<String> exclusions) {
		List<String> results = new ArrayList<>(0);
		boolean anySelection = false;

		if ( inclusions.size() > 0 ) {
			results.addAll(
					entries
					.stream()
					.filter(entry -> inclusions
										.stream()
										.filter(pkgFilter -> entry.startsWith(pkgFilter))
										.count() > 0)
					.filter( entry -> entry != null && ! entry.isEmpty() )
					.collect(Collectors.toList())
			);
			anySelection = true;
		}
		if ( exclusions.size() > 0 ) {
			results.addAll(
				entries
					.stream()
					.filter(entry -> exclusions
										.stream()
										.filter(pkgFilter -> entry.startsWith(pkgFilter))
										.count() == 0)
					.filter( entry -> entry != null && ! entry.isEmpty() )
					.collect(Collectors.toList())
			);
			anySelection = true;
		}
		if ( ! anySelection ) {
			results.addAll(entries);
		} else {
			List<String> systemSelectedItems = new ArrayList<>();
			systemSelectedItems.addAll(results);
			if ( ArgumentsHelper.traceAllLevels || ArgumentsHelper.traceReflectionsLevel ) {
				LoggerHelper.logTrace("ClassPathHelper::createExecutableConfig", String.format("Adding Default System JVM Packages: %s", Arrays.asList(Reflections.SYSTEM_PACKAGES_FORCED_INCLUSIONS.toArray())));
			}
			if ( Reflections.SYSTEM_PACKAGES_FORCED_INCLUSIONS.size() > 0 ) {
				systemSelectedItems.addAll(
						entries
						.stream()
						.filter(entry -> Reflections.SYSTEM_PACKAGES_FORCED_INCLUSIONS
											.stream()
											.filter(pkgFilter -> entry.startsWith(pkgFilter))
											.count() > 0)
						.filter( entry -> entry != null && ! entry.isEmpty() )
						.collect(Collectors.toList())
				);
			}
			if ( ArgumentsHelper.traceAllLevels || ArgumentsHelper.traceReflectionsLevel ) {
				LoggerHelper.logTrace("ClassPathHelper::createExecutableConfig", "Filtered JVM Packages: " + systemSelectedItems.size());
			}
			results.clear();
			results.addAll(systemSelectedItems
							.stream()
							.distinct()
							.collect(Collectors.toList()));
		}
		return results;
		
	}
	
	public static final ClassPathExecutableConfig createExecutableConfig(ClassPathConfig config) {
		List<String> jvmClassPathEntries = listJvmEntries();
		if ( config == null ) {
			return new ClassPathExecutableConfig(jvmClassPathEntries, new ArrayList<String>(), new ArrayList<String>(), false);
		}
		List<String> filteredClassPathEntries = new ArrayList<>(0);
		filteredClassPathEntries.addAll(
				filterClassPathEntries(jvmClassPathEntries, config.getClassPathInclusionList(), config.getClassPathExclusionList())
		);
		
		if ( Reflections.SYSTEM_LIBRARIES_EXCLUSIONS.size() > 0 ) {
			List<String> systemFilteredClassPathEntries = new ArrayList<>(0);
			systemFilteredClassPathEntries.addAll(
					filterClassPathEntries(filteredClassPathEntries, new ArrayList<String>(0), Reflections.SYSTEM_LIBRARIES_EXCLUSIONS)
			);
			filteredClassPathEntries.clear();
			filteredClassPathEntries.addAll(systemFilteredClassPathEntries);
		}
		if ( Reflections.SYSTEM_LIBRARIES_FORCED_INCLUSIONS.size() > 0 ) {
			List<String> systemFilteredClassPathEntries = new ArrayList<>(0);
			systemFilteredClassPathEntries.addAll(filteredClassPathEntries);
			systemFilteredClassPathEntries.addAll(
					filterClassPathEntries(filteredClassPathEntries, Reflections.SYSTEM_LIBRARIES_FORCED_INCLUSIONS, new ArrayList<String>(0))
			);
			filteredClassPathEntries.clear();
			filteredClassPathEntries.addAll(
					systemFilteredClassPathEntries
						.stream()
						.distinct()
						.collect(Collectors.toList())
			);
			
		}
		if ( ArgumentsHelper.traceAllLevels || ArgumentsHelper.traceReflectionsLevel ) {
			LoggerHelper.logTrace("ClassPathHelper::createExecutableConfig", "Filtered JVM Class-Path Enttries : " + filteredClassPathEntries.size());
			filteredClassPathEntries.forEach(javaDescriptorEntry -> LoggerHelper.logTrace("ClassPathHelper::createExecutableConfig", String.format("Loaded JVM Class-Path Entry : %s", javaDescriptorEntry)) );
		}
		return new ClassPathExecutableConfig(filteredClassPathEntries, config.getPackageInclusionList(), config.getPackageExclusionList(), config.enablePersistenceOfData());
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
        	if ( ArgumentsHelper.traceAllLevels || ArgumentsHelper.traceReflectionsLevel )
        		pathEntries.forEach(entry -> LoggerHelper.logTrace("ClassPathHelper::loadClassPathEntries", String.format("Using Class-Path Entry: %s", entry)));
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
	        				.filter(pkgName -> pkgName != null && !pkgName.isEmpty())
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
		if ( ArgumentsHelper.traceAllLevels || ArgumentsHelper.traceReflectionsLevel ) {
			LoggerHelper.logTrace("ClassPathHelper::loadClassPathEntries", "Filtered JVM Class-Path Java Entry Descriptors : " + classPathEntriesMap.values().stream().flatMap(List::stream).count());
			classPathEntriesMap.values().stream().flatMap(List::stream).forEach(javaDescriptorEntry -> LoggerHelper.logTrace("ClassPathHelper::loadClassPathEntries", String.format("Loaded JVM Class-Path Java Entry Descriptor with class : %s from orgin: %s in file : %s", javaDescriptorEntry.getClassName(),javaDescriptorEntry.getOrigin(), javaDescriptorEntry.getFileName())) );
		}
        return classPathEntriesMap;
	}
	
	public static final Map<String, List<JavaClassEntity>> compileClassPathEntries(Map<String, List<JavaEntry>> entries, ClassPathConfig config) {
		Map<String, List<JavaClassEntity>> compiledEntriesMap = new HashMap<>(0);
		List<ClassLoader> requiredClassLoaders = new ArrayList<>(0);
		if ( config.getClassLoadersList().size() == 0 ) {
			if ( ArgumentsHelper.traceAllLevels || ArgumentsHelper.traceReflectionsLevel ) {
				LoggerHelper.logTrace("ClassPathHelper::compileClassPathEntries", "Used default ClassLoaders");
			}
			requiredClassLoaders.addAll(getDefaultClassLoadersList(config));
		} else {
			requiredClassLoaders.addAll(config.getClassLoadersList());
			if ( ArgumentsHelper.traceAllLevels || ArgumentsHelper.traceReflectionsLevel ) {
				LoggerHelper.logTrace("ClassPathHelper::compileClassPathEntries", "Used custom / required ClassLoaders : " + requiredClassLoaders.size());
			}
		}
		ClassLoader[] classLoaders = new ClassLoader[requiredClassLoaders.size()];
		classLoaders = requiredClassLoaders.toArray(classLoaders);
		final ClassLoader[] currentClassLoders = classLoaders;
		compiledEntriesMap.putAll(
		entries.entrySet()
				.stream()
				.map(entry -> new KeyValuePair<String, List<JavaClassEntity>>(entry.getKey(), entry.getValue()
															.stream()
															.map( javaEntity -> {
																try {
																	return javaEntity.getClassEntity(currentClassLoders);
																} catch (Exception e) {
//																	LoggerHelper.logError("", String.format("Error during initialization of java entity class: %s as follow:", javaEntity != null ? javaEntity.getClassName() : "<NULL>"), e);
																}
																return null;
															} )
															.filter( jce -> jce != null )
															.collect(Collectors.toList())))
	        	.collect(Collectors.toMap( entry -> entry.getKey(), entry->entry.getValue() ))
	    );
		if ( ArgumentsHelper.traceAllLevels || ArgumentsHelper.traceReflectionsLevel ) {
			LoggerHelper.logTrace("ClassPathHelper::compileClassPathEntries", "Loaded JVM Class-Path Entries : " + compiledEntriesMap.size());
			compiledEntriesMap.keySet().forEach(classPathEntry -> LoggerHelper.logTrace("ClassPathHelper::compileClassPathEntries", String.format("Loaded JVM Class-Path Entry at : %s", classPathEntry)) );
			LoggerHelper.logTrace("ClassPathHelper::compileClassPathEntries", "Total Compiled Java Data Entities : " + compiledEntriesMap.values().stream().flatMap(List::stream).count());
			compiledEntriesMap.values().stream().flatMap(List::stream).forEach(entity -> LoggerHelper.logTrace("ClassPathHelper::compileClassPathEntries", String.format("Compiled Java Entity: %s -> super types: %s ", entity.getBaseClass().getName(), Arrays.toString(entity.getImplementingTypes().toArray()))));
		}
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
	
	protected static final Collection<ClassLoader> getDefaultClassLoadersList(ClassPathConfig config) {
		List<ClassLoader> defaultClassLoaders = new ArrayList<>(0);
		List<URL> classPathEntriesList = listCurrentClassPathUrls(config);
		URL[] urls = new URL[classPathEntriesList.size()];
		defaultClassLoaders.add(URLClassLoader.newInstance(urls));
		defaultClassLoaders.add(ClassLoader.getSystemClassLoader());
		return defaultClassLoaders;
	}
	
	protected static final List<URL> listCurrentClassPathUrls(ClassPathConfig config) {
		List<URL> filteredClassPathEntries = new ArrayList<>(0);
		filteredClassPathEntries.addAll(
				filterClassPathEntries(listJvmEntries(), config.getClassPathInclusionList(), config.getClassPathExclusionList())
				.stream()
				.map( classPathEntry -> {
					try {
						if ( classPathEntry.startsWith("http://") || classPathEntry.startsWith("https://") || classPathEntry.startsWith("ftp://") || 
								classPathEntry.startsWith("tcp://") || classPathEntry.startsWith("udp://")) {
							return new URL(classPathEntry);
							
						} else {
							return new URL("file://" + classPathEntry);
						}
					} catch (Exception e) {
						LoggerHelper.logError("ClassPathHelper::listCurrentClassPathUrls", String.format("Error during initialization of URL from path: %s", classPathEntry), e);
					}
					return null; 
				} )
				.collect(Collectors.toList())
		);
		return filteredClassPathEntries;
	}
	
	private static final Throwable reduceThrowables(List<Throwable> throwbles) {
		if ( throwbles == null || throwbles.size() == 0 ) {
			return null;
		}
		Throwable t = null;
		Throwable p = null;
		Iterator<Throwable> throwblesIter = throwbles.iterator();
		while ( throwblesIter.hasNext() ) {
			if ( t == null ) {
				t = throwblesIter.next();
				p = t;
			} else {
				if ( Exception.class.isAssignableFrom(t.getClass()) ) {
					((Exception)p).initCause(throwblesIter.next());
					p = p.getCause();
				} else if ( Exception.class.isAssignableFrom(t.getClass()) ) {
					((Error)p).initCause(throwblesIter.next());
					p = p.getCause();
				} else {
					throwblesIter.next();
				}
			}
		}
		return t;
	}
	
	@SuppressWarnings({ "unchecked" })
	public static final <T> Class<T> loadClassFromName(String className, List<ClassLoader> classLoaders) throws Exception, Error {
		if ( className == null || className.isEmpty() ) {
			return null;
		}
		
		Iterator<ClassLoader> loadersIterator = classLoaders.listIterator();
		Class<T> retCls = null;
		List<String> errorsMessagesList = new ArrayList<>(0);
		List<Throwable> errorsList = new ArrayList<>(0);
		while ( retCls == null && loadersIterator.hasNext() ) {
			try {
				retCls = (Class<T>) loadersIterator.next().loadClass(className);
			} catch (Exception e) {
//				e.printStackTrace();
				errorsList.add(e);
				errorsMessagesList.add(e.getClass().getName() + " -> " + e.getMessage());
			} catch (Error e) {
//				e.printStackTrace();
				errorsList.add(e);
				errorsMessagesList.add(e.getClass().getName() + " -> " + e.getMessage());
			}
		}
		
		if( retCls == null ) {
			throw new IllegalStateException(errorsMessagesList
												.stream()
												.reduce("", 
														(p, n) -> p += (p.isEmpty() ? "" : "\n") + n ),
												reduceThrowables(errorsList));
		}
		
		return retCls;
		
	}
	
}
