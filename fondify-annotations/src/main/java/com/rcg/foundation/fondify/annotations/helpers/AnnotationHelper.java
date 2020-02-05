/**
 * 
 */
package com.rcg.foundation.fondify.annotations.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.rcg.foundation.fondify.annotations.annotations.DependsOn;
import com.rcg.foundation.fondify.annotations.annotations.TransformCase;
import com.rcg.foundation.fondify.annotations.typings.BeanDefinition;
import com.rcg.foundation.fondify.core.domain.KeyCase;
import com.rcg.foundation.fondify.core.functions.Matcher;
import com.rcg.foundation.fondify.core.functions.Processor;
import com.rcg.foundation.fondify.core.functions.SimpleEntryPredicate;
import com.rcg.foundation.fondify.core.functions.SimplePredicate;
import com.rcg.foundation.fondify.core.typings.fields.FieldValueActuatorProvider;
import com.rcg.foundation.fondify.core.typings.methods.MethodReturnObjectValueActuatorProvider;
import com.rcg.foundation.fondify.core.typings.parameters.ParameterValueActuatorProvider;
import com.rcg.foundation.fondify.utils.helpers.LoggerHelper;

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
	
	@SuppressWarnings("unchecked")
	public static final boolean validateAnnotation(Class<?> owner, Annotation ann) {
		if ( owner != null && ann.getClass().getDeclaredAnnotation(DependsOn.class) != null ) {
			DependsOn dependsOn = ann.getClass().getDeclaredAnnotation(DependsOn.class);
			Class<?>[] superAnnotations = dependsOn.value(); 
			if ( superAnnotations != null && superAnnotations.length > 0 ) {
				boolean match = Arrays.asList(superAnnotations)
					.stream()
					.map(annCls -> (Class<Annotation>)annCls)
					.filter(annCls -> {
						return owner.getDeclaredAnnotation(annCls) != null;
					})
					.count() > 0;
				if ( ! match ) {
					LoggerHelper.logWarn("AnnotationHelper::validateAnnotation", 
										String.format("Validation failed for annotation type: %s (value: %s), owned by Class type: %s, it doesn't contain one of required annotations: %s",
												ann.getClass().getName(), 
												ann.toString(),
												owner.getName(), 
												Arrays.toString(superAnnotations)), 
										null);
				}
				return match;
			}
			return true;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public static final boolean validateAnnotation(Method owner, Annotation ann) {
		if ( owner != null && ann.getClass().getDeclaredAnnotation(DependsOn.class) != null ) {
			DependsOn dependsOn = ann.getClass().getDeclaredAnnotation(DependsOn.class);
			Class<?>[] superAnnotations = dependsOn.value(); 
			if ( superAnnotations != null && superAnnotations.length > 0 ) {
				boolean match = Arrays.asList(superAnnotations)
					.stream()
					.map(annCls -> (Class<Annotation>)annCls)
					.filter(annCls -> {
						return owner.getDeclaredAnnotation(annCls) != null;
					})
					.count() > 0;
				if ( ! match ) {
					LoggerHelper.logWarn("AnnotationHelper::validateAnnotation", 
										String.format("Validation failed for Method named: %s (value: %s), owned by Class type: %s, it doesn't contain one of required annotations: %s",
												ann.getClass().getName(), 
												ann.toString(),
												owner.getName(), 
												Arrays.toString(superAnnotations)), 
										null);
				}
				return match;
			}
			return true;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public static final boolean validateAnnotation(Field owner, Annotation ann) {
		if ( owner != null && ann.getClass().getDeclaredAnnotation(DependsOn.class) != null ) {
			DependsOn dependsOn = ann.getClass().getDeclaredAnnotation(DependsOn.class);
			Class<?>[] superAnnotations = dependsOn.value(); 
			if ( superAnnotations != null && superAnnotations.length > 0 ) {
				boolean match = Arrays.asList(superAnnotations)
					.stream()
					.map(annCls -> (Class<Annotation>)annCls)
					.filter(annCls -> {
						return owner.getDeclaredAnnotation(annCls) != null;
					})
					.count() > 0;
				if ( ! match ) {
					LoggerHelper.logWarn("AnnotationHelper::validateAnnotation", 
										String.format("Validation failed for Field named: %s (value: %s), owned by Class type: %s, it doesn't contain one of required annotations: %s",
												ann.getClass().getName(), 
												ann.toString(),
												owner.getName(), 
												Arrays.toString(superAnnotations)), 
										null);
				}
				return match;
			}
			return true;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public static final boolean validateAnnotation(Parameter owner, Annotation ann) {
		if ( owner != null && ann.getClass().getDeclaredAnnotation(DependsOn.class) != null ) {
			DependsOn dependsOn = ann.getClass().getDeclaredAnnotation(DependsOn.class);
			Class<?>[] superAnnotations = dependsOn.value(); 
			if ( superAnnotations != null && superAnnotations.length > 0 ) {
				boolean match = Arrays.asList(superAnnotations)
					.stream()
					.map(annCls -> (Class<Annotation>)annCls)
					.filter(annCls -> {
						return owner.getDeclaredAnnotation(annCls) != null;
					})
					.count() > 0;
				if ( ! match ) {
					LoggerHelper.logWarn("AnnotationHelper::validateAnnotation", 
										String.format("Validation failed for Parameter named: %s (value: %s), owned by Class type: %s, it doesn't contain one of required annotations: %s",
												ann.getClass().getName(), 
												ann.toString(),
												owner.getName(), 
												Arrays.toString(superAnnotations)), 
										null);
				}
				return match;
			}
			return true;
		}
		return true;
	}

	public static Map<Field, List<Annotation>> selectFieldsAnnotations(Class<?> type,
			SimplePredicate<Field, Map<Field, List<Annotation>>> predicate) {
		Map<Field, List<Annotation>> fields = new ConcurrentHashMap<>(0);
		Arrays.asList(type.getDeclaredFields())
			.stream()
			.filter( field -> field.getDeclaredAnnotations().length > 0 )
			.forEach( field -> predicate.test(field, fields));
		return fields;
	}
	
	public static final Map<Method, List<Annotation>> selectMethodsAnnotations(Class<?> type, Matcher<Class<? extends Annotation>> matcher) {
		Map<Method, List<Annotation>> methods = new ConcurrentHashMap<>(0);
		Arrays.asList(type.getDeclaredMethods())
			.stream()
			.filter( method -> method.getDeclaredAnnotations().length > 0 )
			.forEach( method -> {
				List<Annotation> annotations = new ArrayList<>(0);
				annotations.addAll(Arrays.asList(method.getDeclaredAnnotations())
						.stream()
						.filter(ann -> matcher.match( ann.getClass() ) )
						.collect(Collectors.toList()));
				methods.put(method, annotations);
			});
		return methods;
	}
	
	public static final List<Annotation> selectMethodAnnotations(Method method) {
		List<Annotation> annotations = new ArrayList<>(0);
		Arrays.asList(method.getDeclaredAnnotations())
			.forEach( annotations::add );
		return annotations;
	}
	
	public static final Map<Parameter, List<Annotation>> selectMethodParametersAnnotations(Method method, Matcher<Class<? extends Annotation>> matcher) {
		Map<Parameter, List<Annotation>> parameters = new ConcurrentHashMap<>(0);
		Arrays.asList(method.getParameters())
			.stream()
			.filter( parameter -> parameter.getDeclaredAnnotations().length > 0 )
			.forEach( parameter -> {
				List<Annotation> annotations = new ArrayList<>(0);
				annotations.addAll(Arrays.asList(parameter.getDeclaredAnnotations())
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

	public final static String transformBeanName(String name, TransformCase caseTransformer) {
		
		if ( name != null && !name.isEmpty() && 
				caseTransformer!= null && 
				caseTransformer.value() != null ) {
			KeyCase caseType = caseTransformer.value();
			AtomicInteger counter = new AtomicInteger(0);
			switch (caseType) {
				case NO_CHANGE:
					return name;
				case CAPITAL:
					return name.toUpperCase();
				case LOWER:
					return name.toLowerCase();
				case INIT_CAP:
					if ( name.length() == 1 )
						return name.toUpperCase();
					return (""+name.charAt(0)).substring(0, 1).toUpperCase() + name.toLowerCase().substring(1, name.length());
				case SNAKE:
					return Arrays.asList(name.split(""))
						.stream()
						.map( s -> {
							if ( s.trim().length() > 0 ) {
								boolean isOdd = counter.incrementAndGet() % 2 == 1;
								if ( Character.isAlphabetic(s.charAt(0)) ) {
									if ( isOdd ) {
										if ( Character.isLowerCase(s.charAt(0)) ) {
											return s.toUpperCase();
										}
									} else {
										if ( Character.isUpperCase(s.charAt(0)) ) {
											return s.toLowerCase();
										}
									}
								}
							} else {
								return "";
							}
							return s;
						} )
						.reduce("", (p, n) -> p += n);
				case CAMEL:
					AtomicBoolean firstTaken = new AtomicBoolean(false);
					AtomicBoolean newWord = new AtomicBoolean(false);
					return Arrays.asList(name.split(""))
							.stream()
							.map( s -> {
								if ( s.trim().length() > 0 ) {
									if ( Character.isAlphabetic(s.charAt(0)) ) {
										if ( ! firstTaken.get() ) {
											if ( Character.isUpperCase(s.charAt(0)) ) {
												return s.toLowerCase();
											}
											firstTaken.set(true);
										} else {
											
										}
										if ( newWord.get() ) {
											if ( Character.isLowerCase(s.charAt(0)) ) {
												return s.toUpperCase();
											}
											newWord.set(false);
										} else {
											if ( Character.isUpperCase(s.charAt(0)) ) {
												return s.toLowerCase();
											}
										}
									}
								} else {
									newWord.set(true);
									return "";
								}
								return s;
							} )
							.reduce("", (p, n) -> p += n);
			}
		}
		return name;
	}
	
	public static final Object valueOfInjectedField(Field field) {
		if ( field != null ) {
			try {
				Optional<Object> valueOpt = FieldValueActuatorProvider.getInstance().tranlateFieldValue(field);
				if ( valueOpt.isPresent() ) {
					return valueOpt.get();
				}
			} catch (Exception e) {
				LoggerHelper.logError("AnnotationHelper::valueOfInjectedField", 
						String.format("Errors recovering injected value of %s field", field != null ? field.getName() : "<NULL>"), 
						e);
			}
		}
		return null;
	}
	
	
	public static final Object valueOfInjectedParameter(Parameter parameter) {
		if ( parameter != null ) {
			try {
				Optional<Object> valueOpt = ParameterValueActuatorProvider.getInstance().tranlateParameterValue(parameter);
				if ( valueOpt.isPresent() ) {
					return valueOpt.get();
				}
			} catch (Exception e) {
				LoggerHelper.logError("AnnotationHelper::valueOfInjectedParameter", 
						String.format("Errors recovering injected value of %s parameter", parameter != null ? parameter.getName() : "<NULL>"), 
						e);
			}
		}
		return null;
	}

	public static final Object valueOfInjectedMethodReturnObject(Method method) {
		if ( method != null ) {
			try {
				Optional<Object> valueOpt = MethodReturnObjectValueActuatorProvider.getInstance().tranlateMethodReturnObjectValue(method);
				if ( valueOpt.isPresent() ) {
					return valueOpt.get();
				}
			} catch (Exception e) {
				LoggerHelper.logError("AnnotationHelper::valueOfInjectedMethodReturnObject", 
						String.format("Errors recovering injected value of %s method return object", method != null ? method.getName() : "<NULL>"), 
						e);
			}
		}
		return null;
	}

}
