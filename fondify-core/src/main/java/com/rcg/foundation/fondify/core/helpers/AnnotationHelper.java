/**
 * 
 */
package com.rcg.foundation.fondify.core.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.rcg.foundation.fondify.core.functions.Matcher;
import com.rcg.foundation.fondify.core.functions.Processor;
import com.rcg.foundation.fondify.core.functions.SimpleEntryPredicate;
import com.rcg.foundation.fondify.core.functions.SimplePredicate;
import com.rcg.foundation.fondify.core.typings.BeanDefinition;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class AnnotationHelper {

	/**
	 * 
	 */
	protected AnnotationHelper() {
		throw new IllegalStateException("Unable to instatiatio helper class");
	}

	public static Map<Field, List<Annotation>> selectFieldsAnnotations(Class<?> type,
			SimplePredicate<Field, Map<Field, List<Annotation>>> predicate) {
//	public static final Map<Field, List<Annotation>> selectFieldsAnnotations(Class<?> type, SimplePredicate<Field, Map<Field, List<Annotation>>> predicate) {
		Map<Field, List<Annotation>> fields = new ConcurrentHashMap<>(0);
		Arrays.asList(type.getDeclaredFields())
			.stream()
			.filter( field -> field.getAnnotations().length > 0 )
			.forEach( field -> predicate.test(field, fields));
		return fields;
	}
	
	public static final Map<Method, List<Annotation>> selectMethodsAnnotations(Class<?> type, Matcher<Class<? extends Annotation>> matcher) {
		Map<Method, List<Annotation>> methods = new ConcurrentHashMap<>(0);
		Arrays.asList(type.getDeclaredMethods())
			.stream()
			.filter( method -> method.getAnnotations().length > 0 )
			.forEach( method -> {
				List<Annotation> annotations = new ArrayList<>(0);
				annotations.addAll(Arrays.asList(method.getAnnotations())
						.stream()
						.filter(ann -> matcher.match( ann.getClass() ) )
						.collect(Collectors.toList()));
				methods.put(method, annotations);
			});
		return methods;
	}
	
	public static final List<Annotation> selectMethodAnnotations(Method method) {
		List<Annotation> annotations = new ArrayList<>(0);
		Arrays.asList(method.getAnnotations())
			.forEach( annotations::add );
		return annotations;
	}
	
	public static final Map<Parameter, List<Annotation>> selectMethodParametersAnnotations(Method method, Matcher<Class<? extends Annotation>> matcher) {
		Map<Parameter, List<Annotation>> parameters = new ConcurrentHashMap<>(0);
		Arrays.asList(method.getParameters())
			.stream()
			.filter( parameter -> parameter.getAnnotations().length > 0 )
			.forEach( parameter -> {
				List<Annotation> annotations = new ArrayList<>(0);
				annotations.addAll(Arrays.asList(parameter.getAnnotations())
						.stream()
						.filter(ann -> matcher.match( ann.getClass() ) )
						.collect(Collectors.toList()));
			} );
		return parameters;
		
	}
	
	public static final void processFieldsAnnotations(Class<?> elementClass, BeanDefinition definition, Predicate<Annotation> filter, SimpleEntryPredicate<Field, List<Annotation>, BeanDefinition> entryFilter, SimplePredicate<Field, Map<Field, List<Annotation>>> predicate) {
		Map<Field, List<Annotation>> fieldsMap = selectFieldsAnnotations(elementClass, predicate);
		fieldsMap
			.entrySet()
			.stream()
			.filter( entry -> entry.getValue().stream()
									.filter( filter )
									.count() > 0
			)
			.map( entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), 
										entry.getValue()
										.stream()
										.filter( filter )
										.collect(Collectors.toList())
										) )
			.filter(entry -> entry.getValue().size() > 0)
			.forEach( entry -> entryFilter.apply(entry, definition) );
	}

	
	public static final void processFieldsPropertyAnnotations(Class<?> elementClass, BeanDefinition definition, Predicate<Annotation> filter, SimpleEntryPredicate<Field, List<Annotation>, BeanDefinition> entryFilter, SimplePredicate<Field, Map<Field, List<Annotation>>> predicate) {
		Map<Field, List<Annotation>> fieldsMap = selectFieldsAnnotations(elementClass, predicate);
		fieldsMap
		.entrySet()
		.stream()
		.filter( entry -> {
			return entry.getValue()
					.stream()
					.filter( filter )
					.count() > 0;
				} )
		.map( entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()
				.stream()
				.filter( filter )
				.collect(Collectors.toList()))
		)
		.filter( entry -> entry.getValue().size() > 0 )
		.forEach( entry -> entryFilter.apply(entry, definition));
	}

	public static final void processMethodInitializationFinalizationAnnotations(String beanName, Class<?> elementClass, Matcher<Class<? extends Annotation>> matcher, Processor<Map<Method, List<Annotation>>> processor) {
		Map<Method, List<Annotation>> methodsAnns = selectMethodsAnnotations(elementClass, matcher);
		processor.process(methodsAnns);
	}
	
	public static final void processMethodAnnotations(Class<?> elementClass, BeanDefinition definition, Matcher<Class<? extends Annotation>> matcher, Processor<Map<Method, List<Annotation>>> processor) {
		Map<Method, List<Annotation>> methodsAnns = selectMethodsAnnotations(elementClass, matcher);
		processor.process(methodsAnns);
	}
}
