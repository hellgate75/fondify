/**
 * 
 */
package com.rcg.foundation.fondify.core.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class BeansHelper {

	/**
	 * 
	 */
	private BeansHelper() {
		throw new IllegalStateException("BeansHelper::constructor - unable to instantiate utility class!!");
	}

	public static final <T> List<T> getImplementedTypes(Class<T> cls) {
		
		return collectSubTypesOf(getRefletionsByPackages(new String[0]), cls)
			.stream()
			.filter( anyCls -> ! anyCls.isInterface() )
			.map(anyCls -> {
				try {
					return anyCls.newInstance();
				} catch (Exception e) {
					LoggerHelper.logError("AnnotationHelper::getImplementedTypes", String.format("Unable to creae instance of class: %s", ""+anyCls), null);
				}
				return (T)null;
			})
			.filter( instance -> instance != null )
			.collect(Collectors.toList());
			
	}

	public static final <T> Optional<T> getImplementedType(Class<T> cls) {
		
		return collectSubTypesOf(getRefletionsByPackages(new String[0]), cls)
			.stream()
			.filter( anyCls -> ! anyCls.isInterface() )
			.map(anyCls -> {
				try {
					return anyCls.newInstance();
				} catch (Exception e) {
					LoggerHelper.logError("AnnotationHelper::getImplementedType", String.format("Unable to creae instance of class: %s", ""+anyCls), null);
				}
				return (T)null;
			})
			.filter( instance -> instance != null )
			.findFirst();
			
	}

	/**
	 * Create a Reflections bases on input packages
	 * 
	 * @param packages
	 * @return
	 */
	public static final ConfigurationBuilder getRefletionsByPackages(String[] packages) {
		List<String> list = new ArrayList<>(0);
		list.addAll(Arrays.asList(packages));
		return getRefletionsByPackages(list);
	}

	/**
	 * Collect all subTypes of provided interface or class one.
	 * 
	 * @param <T>
	 * @param builder    Base package scanner builder
	 * @param superClass implemented class or interface
	 * @return list if sub types of provided one
	 */
	public static final <T> List<Class<? extends T>> collectSubTypesOf(ConfigurationBuilder builder,
			Class<T> superClass) {
		List<Class<? extends T>> classes = new ArrayList<Class<? extends T>>(0);
		Reflections r = new Reflections(builder.addScanners(new SubTypesScanner()));
		classes.addAll(r.getSubTypesOf(superClass));
		return classes;
	}

	/**
	 * Collect all subTypes of provided interface or class one.
	 * 
	 * @param <T>
	 * @param builder    Base package scanner builder
	 * @param superClass implemented class or interface
	 * @return list if sub types of provided one
	 */
	public static final List<Class<?>> collectSubTypesOf(ConfigurationBuilder builder, List<Class<?>> superClassList) {
		List<Class<?>> classes = new ArrayList<>(0);
		Reflections r = new Reflections(builder.addScanners(new SubTypesScanner()));
		superClassList.forEach(superClass -> {
			classes.addAll(r.getSubTypesOf(superClass));
		});
		return classes;
	}

	/**
	 * Create a Reflections bases on input packages
	 * 
	 * @param packages
	 * @return
	 */
	public static final ConfigurationBuilder getRefletionsByPackages(Collection<String> packages) {
		ConfigurationBuilder config = new ConfigurationBuilder();
		if (packages != null && packages.size() > 0) {
			List<java.net.URL> listOfClassPathRefs = new ArrayList<java.net.URL>(0);
			listOfClassPathRefs.addAll(
					packages
					.stream()
					.filter(pkg -> pkg != null && !pkg.isEmpty())
					.map(pkg -> {
						ArrayList<java.net.URL> classpathUrls = new ArrayList<java.net.URL>(0);
						try {
							classpathUrls.addAll( 
									ClasspathHelper.forPackage(pkg)
							);
						} catch (Exception e) {
							
						}
						return classpathUrls;
					})
					.flatMap(List::stream)
					.distinct()
					.collect(Collectors.toList())
			);
			if ( listOfClassPathRefs.size() > 0 ) {
				config.addUrls(
						listOfClassPathRefs
				);
			} else {
				LoggerHelper.logWarn("ScannerHelper::getRefletionsByPackages", 
						String.format("Unable to discover classpath packages: %s, then loading full classpath urls", Arrays.toString(packages.toArray())), 
						null);
				config.addUrls(ClasspathHelper.forJavaClassPath());
			}
	
		} else {
			config.addUrls(ClasspathHelper.forJavaClassPath());
		}
		return config;
	}

}
