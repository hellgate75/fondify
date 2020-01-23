/**
 * 
 */
package com.rcg.foundation.fondify.components.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import com.rcg.foundation.fondify.annotations.annotations.TransformCase;
import com.rcg.foundation.fondify.annotations.annotations.methods.Finalization;
import com.rcg.foundation.fondify.annotations.annotations.methods.Initialization;
import com.rcg.foundation.fondify.annotations.contants.AnnotationConstants;
import com.rcg.foundation.fondify.annotations.typings.BeanDefinition;
import com.rcg.foundation.fondify.annotations.typings.MethodExecutor;
import com.rcg.foundation.fondify.components.annotations.Autowired;
import com.rcg.foundation.fondify.components.annotations.Component;
import com.rcg.foundation.fondify.components.annotations.Inject;
import com.rcg.foundation.fondify.components.annotations.Injectable;
import com.rcg.foundation.fondify.components.typings.ComponentRef;
import com.rcg.foundation.fondify.core.exceptions.ScannerException;
import com.rcg.foundation.fondify.core.functions.Matcher;
import com.rcg.foundation.fondify.core.functions.Processor;
import com.rcg.foundation.fondify.core.functions.SimpleEntryPredicate;
import com.rcg.foundation.fondify.core.functions.SimplePredicate;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;
import com.rcg.foundation.fondify.core.registry.ComponentsRegistry;
import com.rcg.foundation.fondify.core.typings.AnnotationExecutor;
import com.rcg.foundation.fondify.core.typings.methods.ParameterRef;
import com.rcg.foundation.fondify.core.typings.methods.PropertyRef;
import com.rcg.foundation.fondify.properties.annotations.PropertiesSet;
import com.rcg.foundation.fondify.properties.annotations.Value;
import com.rcg.foundation.fondify.properties.annotations.WithPropertiesRoot;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class AnnotationHelper extends com.rcg.foundation.fondify.annotations.helpers.AnnotationHelper {

	/**
	 * 
	 */
	private AnnotationHelper() {
		throw new IllegalStateException("Unable to instatiatio helper class");
	}

	public final static <T extends Annotation> void addExecutorInRegistry(AnnotationExecutor<T> exec) {
		if ( exec != null )
		ComponentsRegistry.getInstance().add(AnnotationConstants.REGISTRY_CLASS_ANNOTATION_EXECUTORS, exec.getAnnotationClass().getName(), exec);
	}
	
	public static final boolean filterBean(Class<? extends Annotation> type) {
		return type.isAssignableFrom(Component.class) ||
			   type.isAssignableFrom(Injectable.class) ||
			   type.isAssignableFrom(PropertiesSet.class) ||
			   type.isAssignableFrom(WithPropertiesRoot.class);
	}
	
	public static final String getClassBeanName(Class<?> beanClass, String proposed) {
		String name = proposed != null && ! proposed.isEmpty() ? proposed :  beanClass.getSimpleName();
		Component compAnn = beanClass.getAnnotation(Component.class); 
		if ( compAnn != null && compAnn.value() != null && ! compAnn.value().isEmpty() ) {
			proposed = compAnn.value();
		}
		Injectable injAnn = beanClass.getAnnotation(Injectable.class); 
		if ( injAnn != null && injAnn.component().value() != null && ! injAnn.component().value().isEmpty() ) {
			proposed = injAnn.component().value();
		}
		TransformCase caseTransformer = beanClass.getAnnotation(TransformCase.class);
		if ( caseTransformer != null ) {
			name = com.rcg.foundation.fondify.annotations.helpers.AnnotationHelper.transformBeanName(name, caseTransformer);
		}
		return name;
	}
	
	public static final String getClassMethodBeanName(Method m, String proposed) {
		String name = proposed != null && ! proposed.isEmpty() ? proposed :  m.getName();
		Component compAnn = m.getAnnotation(Component.class); 
		if ( compAnn != null && compAnn.value() != null && ! compAnn.value().isEmpty() ) {
			proposed = compAnn.value();
		}
		Injectable injAnn = m.getAnnotation(Injectable.class); 
		if ( injAnn != null && injAnn.component().value() != null && ! injAnn.component().value().isEmpty() ) {
			proposed = injAnn.component().value();
		}
		TransformCase caseTransformer = m.getAnnotation(TransformCase.class);
		if ( caseTransformer != null ) {
			name = com.rcg.foundation.fondify.annotations.helpers.AnnotationHelper.transformBeanName(name, caseTransformer);
		}
		return name;
	}
	
	public static final String getClassFieldBeanName(Field f, String proposed) {
		String name = proposed != null && ! proposed.isEmpty() ? proposed :  f.getName();
		Component compAnn = f.getAnnotation(Component.class); 
		if ( compAnn != null && compAnn.value() != null && ! compAnn.value().isEmpty() ) {
			proposed = compAnn.value();
		}
		Injectable injAnn = f.getAnnotation(Injectable.class); 
		if ( injAnn != null && injAnn.component().value() != null && ! injAnn.component().value().isEmpty() ) {
			proposed = injAnn.component().value();
		}
		TransformCase caseTransformer = f.getAnnotation(TransformCase.class);
		if ( caseTransformer != null ) {
			name = com.rcg.foundation.fondify.annotations.helpers.AnnotationHelper.transformBeanName(name, caseTransformer);
		}
		return name;
	}
	
	public static final String getMethodParameterBeanName(Parameter p, String proposed) {
		String name = proposed != null && ! proposed.isEmpty() ? proposed :  p.getName();
		Component compAnn = p.getAnnotation(Component.class); 
		if ( compAnn != null && compAnn.value() != null && ! compAnn.value().isEmpty() ) {
			proposed = compAnn.value();
		}
		Injectable injAnn = p.getAnnotation(Injectable.class); 
		if ( injAnn != null && injAnn.component().value() != null && ! injAnn.component().value().isEmpty() ) {
			proposed = injAnn.component().value();
		}
		TransformCase caseTransformer = p.getAnnotation(TransformCase.class);
		if ( caseTransformer != null ) {
			name = com.rcg.foundation.fondify.annotations.helpers.AnnotationHelper.transformBeanName(name, caseTransformer);
		}
		return name;
	}
	
	public static final boolean filterBeanField(Class<? extends Annotation> type) {
		return type.isAssignableFrom(Autowired.class) ||
			   type.isAssignableFrom(Inject.class) ||
			   type.isAssignableFrom(Value.class);
	}
	
	public static final boolean filterBeanMethod(Class<? extends Annotation> type) {
		return type.isAssignableFrom(Injectable.class);
	}
	
	public static final boolean filterBeanMethodInitializationFinalization(Class<? extends Annotation> type) {
		return type.isAssignableFrom(Initialization.class) ||
				type.isAssignableFrom(Finalization.class);
	}
	
	
	public static final boolean filterBeanMethodParameter(Class<? extends Annotation> type) {
		return type.isAssignableFrom(Inject.class) ||
			   type.isAssignableFrom(Autowired.class) ||
			   type.isAssignableFrom(Value.class);
	}
	
	public static final <T> Map<Field, List<Annotation>> selectFieldsAnnotations(Class<T> type) {
		SimplePredicate<Field, Map<Field, List<Annotation>>> predicate = (field, fields) -> {
			List<Annotation> annotations = new ArrayList<>(0); 
			annotations.addAll(Arrays.asList(field.getAnnotations())
					.stream()
					.filter(ann -> filterBeanField( ann.getClass() ) )
					.collect(Collectors.toList()));
			fields.put(field, annotations);
			return true;
		};
		return com.rcg.foundation.fondify.annotations.helpers.AnnotationHelper.selectFieldsAnnotations(type, predicate);
	}
	
	public static final <T> Map<Method, List<Annotation>> selectMethodsAnnotations(Class<T> type) {
		Matcher<Class<? extends Annotation>> matcher = cls -> filterBeanMethod( cls );
		return com.rcg.foundation.fondify.annotations.helpers.AnnotationHelper.selectMethodsAnnotations(type, matcher);
	}
	
	public static final Map<Parameter, List<Annotation>> selectMethodParametersAnnotations(Method method) {
		Matcher<Class<? extends Annotation>> matcher = cls -> filterBeanMethodParameter( cls );
		return com.rcg.foundation.fondify.annotations.helpers.AnnotationHelper.selectMethodParametersAnnotations(method, matcher);
	}
	
	public static final boolean filterComponentMethodAnnotation(Annotation ann) {
		return Component.class.isAssignableFrom(ann.getClass()) ||
				Injectable.class.isAssignableFrom(ann.getClass());
	}
	
	public static final boolean filterComponentFieldAnnotation(Annotation ann) {
		return Autowired.class.isAssignableFrom(ann.getClass()) ||
				Inject.class.isAssignableFrom(ann.getClass());
	}
	
	public static final void processFieldsAnnotations(Class<?> elementClass, BeanDefinition currentDefinition, String typeRef, Predicate<Annotation> filter) {
		SimpleEntryPredicate<Field, List<Annotation>, BeanDefinition> entryFilter = (entry, definition) -> {
			Field field = entry.getKey();
			entry.getValue()
			.stream()
			.map( ann -> {
				String name = field.getType().getClass().getName();
				if ( Autowired.class.isAssignableFrom(ann.getClass()) ) {
					Autowired autowired = (Autowired) ann;
					name = name.substring(0,1).toLowerCase() + name.substring(1);
					if ( autowired.name() != null && 
							! autowired.name().isEmpty()) {
						name = autowired.name();
					}
				} else {
					Inject inject = (Inject)ann;
					name = name.substring(0,1).toLowerCase() + name.substring(1);
					if ( inject.name() != null && 
							! inject.name().isEmpty()) {
						name = inject.name();
					}
				}
				TransformCase fieldCaseTransformer = field.getAnnotation(TransformCase.class);
				if ( fieldCaseTransformer != null ) {
					name = AnnotationHelper.transformBeanName(name, fieldCaseTransformer);
				}
				ComponentRef ref = new ComponentRef(typeRef, field.getName(), name, true, false, false);
				if ( Autowired.class.isAssignableFrom(ann.getClass()) ) {
					ref.setAutowiredAnnotation((Autowired) ann);
				} else {
					ref.setInjectAnnotation((Inject) ann);
				}
				return ref;
			} )
			.forEach( definition::addComponentsReference );
		};
		SimplePredicate<Field, Map<Field, List<Annotation>>> predicate = (field, fields) -> {
			List<Annotation> annotations = new ArrayList<>(0); 
			annotations.addAll(Arrays.asList(field.getAnnotations())
					.stream()
					.filter(ann -> filterBeanField( ann.getClass() ) )
					.collect(Collectors.toList()));
			fields.put(field, annotations);
			return true;
		};
		com.rcg.foundation.fondify.annotations.helpers.AnnotationHelper.processFieldsAnnotations(elementClass, currentDefinition, filter, entryFilter, predicate);
	}

	
	public static final void processFieldsPropertyAnnotations(Class<?> elementClass, BeanDefinition currentDefinition, String typeRef, Predicate<Annotation> filter) {
		SimplePredicate<Field, Map<Field, List<Annotation>>> predicate = (field, fields) -> {
			List<Annotation> annotations = new ArrayList<>(0); 
			annotations.addAll(Arrays.asList(field.getAnnotations())
					.stream()
					.filter(ann -> filterBeanField( ann.getClass() ) )
					.collect(Collectors.toList()));
			fields.put(field, annotations);
			return true;
		};
		SimpleEntryPredicate<Field, List<Annotation>, BeanDefinition> entryFilter = (entry, definition) -> {
			Field field = entry.getKey();
			entry.getValue()
			.stream()
			.map( ann -> {
				
				String propertyDescr = ((Value)ann).value();
				
				TransformCase caseTransformer = field.getAnnotation(TransformCase.class);
				if ( caseTransformer != null ) {
					propertyDescr = AnnotationHelper.transformBeanName(propertyDescr, caseTransformer);
				}
				PropertyRef ref = new PropertyRef(typeRef, field.getName(), propertyDescr, true, false);
				ref.setValueAnnotation((Value)ann);
				return ref;
			} )
			.forEach( definition::addPropertiesReference );
		};
		com.rcg.foundation.fondify.annotations.helpers.AnnotationHelper.processFieldsPropertyAnnotations(elementClass, currentDefinition, filter, entryFilter, predicate);
	}

	public static final void processMethodInitializationFinalizationAnnotations(String beanName, Class<?> elementClass, BeanDefinition definition, Predicate<Annotation> filter) {
		Matcher<Class<? extends Annotation>> matcher = cls -> filterBeanMethodInitializationFinalization( cls );
		Processor<Map<Method, List<Annotation>>> processor = (methodsAnns, objs) -> methodsAnns
				.entrySet()
				.stream()
				.filter( entry -> {
					return entry.getValue()
					.stream()
					.filter( filter )
					.count() > 0;
				})
				.map( entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()
																			.stream()
																			.filter( filter )
																			.collect(Collectors.toList()))
				)
				.filter( entry -> entry.getValue().size() > 0 )
				.forEach( entry -> {
					Method m = entry.getKey();
					List<Annotation> anns = entry.getValue();
					if ( anns.size() == 0 ) {
						return;
					} else if ( anns.size() > 1 ) {
						String messageX = String.format("Too many annotations (%s > 1) for method %s in component class %s, annotations are : %s",
										  ""+anns.size(),
										  m.getName(),
										  elementClass.getName(),
										  Arrays.toString( anns.stream().map( ann -> ann.getClass().getName() ).collect(Collectors.toList()).toArray() ));
						LoggerHelper.logError("ComponentExecutor::executeAnnotation", messageX, null);
						throw new ScannerException(messageX);
					}
					Annotation ann = anns.get(0);
					Initialization initializationAnnotation = null;
					Finalization finalizationAnnotation = null;
					if ( Initialization.class.isAssignableFrom(ann.getClass()) ) {
						initializationAnnotation = m.getAnnotation(Initialization.class);
					}
					if ( Finalization.class.isAssignableFrom(ann.getClass()) ) {
						finalizationAnnotation = m.getAnnotation(Finalization.class);
					}
					MethodExecutor executor = new MethodExecutor(beanName, m, initializationAnnotation, finalizationAnnotation);
					final String methodName = beanName;
					Arrays.asList(m.getParameters())
						.stream()
						.map(parameter -> new AbstractMap.SimpleEntry<Parameter, List<Annotation>>(parameter, Arrays.asList(parameter.getAnnotations())))
						.filter( fieldEntry -> fieldEntry.getValue().size() > 0 )
						.filter( fieldEntry -> fieldEntry.getValue()
												.stream()
												.filter(annMethod -> { return Autowired.class.isAssignableFrom(annMethod.getClass()) ||
																	Inject.class.isAssignableFrom(annMethod.getClass()) ||
																	Value.class.isAssignableFrom(annMethod.getClass());} )
												.count() > 0
								)
						.map( fieldEntry -> new AbstractMap.SimpleEntry<Parameter, List<Annotation>>( fieldEntry.getKey(), fieldEntry.getValue()
																																	  .stream()
																																	  .filter(annMethod -> { return Autowired.class.isAssignableFrom(annMethod.getClass()) ||
																																				Inject.class.isAssignableFrom(annMethod.getClass()) ||
																																				Value.class.isAssignableFrom(annMethod.getClass()); })
																																	  .collect(Collectors.toList())
																									)
						)
						.filter( fieldEntry -> fieldEntry.getValue().size() > 0 )
						.forEach(fieldEntry -> {
							Parameter parameter = fieldEntry.getKey();
							List<Annotation> localAnns = fieldEntry.getValue();
							if ( localAnns.size() == 0 ) {
								return;
							} else if ( localAnns.size() > 1 ) {
								String messageX = String.format("Too many annotations (%s > 1) for parmeter %s of method %s in component class %s, annotations are : %s",
												  ""+anns.size(),
												  parameter.getName(),
												  m.getName(),
												  elementClass.getName(),
												  Arrays.toString( anns.stream().map( pAnn -> pAnn.getClass().getName() ).collect(Collectors.toList()).toArray() ));
								LoggerHelper.logError("StreamIOConfigurationExecutor::executeAnnotation", messageX, null);
								throw new ScannerException(messageX);
							}
							String localBeanName = parameter.getName();
							Annotation localAnn = localAnns.get(0);
							if ( Autowired.class.isAssignableFrom(localAnn.getClass()) ) {
								Autowired autowired = (Autowired) localAnn;
								localBeanName = localBeanName.substring(0,1).toLowerCase() + localBeanName.substring(1);
								if ( autowired.name() != null && 
										! autowired.name().isEmpty()) {
									localBeanName = autowired.name();
								}
							} else if (Inject.class.isAssignableFrom(localAnn.getClass())) {
								Inject inject = (Inject)localAnn;
								localBeanName = localBeanName.substring(0,1).toLowerCase() + localBeanName.substring(1);
								if ( inject.name() != null && 
										! inject.name().isEmpty()) {
									localBeanName = inject.name();
								}
							}
							TransformCase localFieldCaseTransformer = parameter.getAnnotation(TransformCase.class);
							if ( localFieldCaseTransformer != null ) {
								localBeanName = AnnotationHelper.transformBeanName(localBeanName, localFieldCaseTransformer);
							}
							Autowired autowiredAnnotation=null; 
							Inject injectAnnotation=null; 
							Value valueAnnotation=null;
							if ( Autowired.class.isAssignableFrom(localAnn.getClass()) ) {
								autowiredAnnotation = (Autowired) localAnn;
							} else if (Inject.class.isAssignableFrom(localAnn.getClass())) {
								injectAnnotation = (Inject) localAnn;
							} else {
								valueAnnotation = (Value) localAnn;
							}
							ParameterRef param = new ParameterRef(beanName, methodName, localBeanName, parameter.getName(), autowiredAnnotation, injectAnnotation, valueAnnotation);
							executor.addParameter(param);
						});
					if ( initializationAnnotation != null ) {
						definition.addInitializationMethod(executor);
					}
					if ( finalizationAnnotation != null ) {
						definition.addFinalizationMethod(executor);
					}
				});
		com.rcg.foundation.fondify.annotations.helpers.AnnotationHelper.processMethodInitializationFinalizationAnnotations(beanName, elementClass, matcher, processor);
	}
	
	public static final void processMethodAnnotations(Class<?> elementClass, BeanDefinition definition, String beanName, Predicate<Annotation> filter) {
		Map<Method, List<Annotation>> methodsAnns = AnnotationHelper.selectMethodsAnnotations(elementClass);
		methodsAnns
		.entrySet()
		.stream()
		.filter( entry -> {
			return entry.getValue()
			.stream()
			.filter( filter )
			.count() > 0;
		})
		.map( entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()
																	.stream()
																	.filter( filter )
																	.collect(Collectors.toList()))
		)
		.filter( entry -> entry.getValue().size() > 0 )
		.forEach( entry -> {
			Method m = entry.getKey();
			List<Annotation> anns = entry.getValue();
			if ( anns.size() == 0 ) {
				return;
			} else if ( anns.size() > 1 ) {
				String messageX = String.format("Too many annotations (%s > 1) for method %s in component class %s, annotations are : %s",
								  ""+anns.size(),
								  m.getName(),
								  elementClass.getName(),
								  Arrays.toString( anns.stream().map( ann -> ann.getClass().getName() ).collect(Collectors.toList()).toArray() ));
				LoggerHelper.logError("StreamIOConfigurationExecutor::executeAnnotation", messageX, null);
				throw new ScannerException(messageX);
			}
			Annotation ann = anns.get(0);
			String typeRef = beanName;
			String name = m.getName();
			if ( Component.class.isAssignableFrom(ann.getClass()) ) {
				Component component = (Component) ann;
				name = name.substring(0,1).toLowerCase() + name.substring(1);
				if ( component.value() != null && 
						! component.value().isEmpty()) {
					name = component.value();
				}
			} else {
				Inject inject = (Inject)ann;
				name = name.substring(0,1).toLowerCase() + name.substring(1);
				if ( inject.name() != null && 
						! inject.name().isEmpty()) {
					name = inject.name();
				}
			}
			TransformCase fieldCaseTransformer = m.getAnnotation(TransformCase.class);
			if ( fieldCaseTransformer != null ) {
				name = AnnotationHelper.transformBeanName(name, fieldCaseTransformer);
			}
			ComponentRef ref = new ComponentRef(typeRef, m.getName(), name, true, false, false);
			if ( Component.class.isAssignableFrom(ann.getClass()) ) {
				ref.setComponentAnnotation((Component) ann);
			} else {
				ref.setInjectAnnotation((Inject) ann);
			}
			final String methodName = name;
			Arrays.asList(m.getParameters())
				.stream()
				.map(parameter -> new AbstractMap.SimpleEntry<Parameter, List<Annotation>>(parameter, Arrays.asList(parameter.getAnnotations())))
				.filter( fieldEntry -> fieldEntry.getValue().size() > 0 )
				.filter( fieldEntry -> fieldEntry.getValue()
										.stream()
										.filter(annMethod -> { return Autowired.class.isAssignableFrom(annMethod.getClass()) ||
															Inject.class.isAssignableFrom(annMethod.getClass()) ||
															Value.class.isAssignableFrom(annMethod.getClass());} )
										.count() > 0
						)
				.map( fieldEntry -> new AbstractMap.SimpleEntry<Parameter, List<Annotation>>( fieldEntry.getKey(), fieldEntry.getValue()
																															  .stream()
																															  .filter(annMethod -> { return Autowired.class.isAssignableFrom(annMethod.getClass()) ||
																																		Inject.class.isAssignableFrom(annMethod.getClass()) ||
																																		Value.class.isAssignableFrom(annMethod.getClass()); })
																															  .collect(Collectors.toList())
																							)
				)
				.filter( fieldEntry -> fieldEntry.getValue().size() > 0 )
				.forEach(fieldEntry -> {
					Parameter parameter = fieldEntry.getKey();
					List<Annotation> localAnns = fieldEntry.getValue();
					if ( localAnns.size() == 0 ) {
						return;
					} else if ( localAnns.size() > 1 ) {
						String messageX = String.format("Too many annotations (%s > 1) for parmeter %s of method %s in component class %s, annotations are : %s",
										  ""+anns.size(),
										  parameter.getName(),
										  m.getName(),
										  elementClass.getName(),
										  Arrays.toString( anns.stream().map( pAnn -> pAnn.getClass().getName() ).collect(Collectors.toList()).toArray() ));
						LoggerHelper.logError("StreamIOConfigurationExecutor::executeAnnotation", messageX, null);
						throw new ScannerException(messageX);
					}
					String localBeanName = parameter.getName();
					Annotation localAnn = localAnns.get(0);
					if ( Autowired.class.isAssignableFrom(localAnn.getClass()) ) {
						Autowired autowired = (Autowired) localAnn;
						localBeanName = localBeanName.substring(0,1).toLowerCase() + localBeanName.substring(1);
						if ( autowired.name() != null && 
								! autowired.name().isEmpty()) {
							localBeanName = autowired.name();
						}
					} else if (Inject.class.isAssignableFrom(localAnn.getClass())) {
						Inject inject = (Inject)localAnn;
						localBeanName = localBeanName.substring(0,1).toLowerCase() + localBeanName.substring(1);
						if ( inject.name() != null && 
								! inject.name().isEmpty()) {
							localBeanName = inject.name();
						}
					}
					TransformCase localFieldCaseTransformer = parameter.getAnnotation(TransformCase.class);
					if ( localFieldCaseTransformer != null ) {
						localBeanName = AnnotationHelper.transformBeanName(localBeanName, localFieldCaseTransformer);
					}
					Autowired autowiredAnnotation=null; 
					Inject injectAnnotation=null; 
					Value valueAnnotation=null;
					if ( Autowired.class.isAssignableFrom(localAnn.getClass()) ) {
						autowiredAnnotation = (Autowired) localAnn;
					} else if (Inject.class.isAssignableFrom(localAnn.getClass())) {
						injectAnnotation = (Inject) localAnn;
					} else {
						valueAnnotation = (Value) localAnn;
					}
					ParameterRef param = new ParameterRef(typeRef, methodName, localBeanName, parameter.getName(), autowiredAnnotation, injectAnnotation, valueAnnotation);
					ref.addParameter(param);
				});
			definition.addComponentsReference(ref);
		});
	}
	
	public static final List<ParameterRef> getParametersRefFor(Method m, Class<?> elementClass, String typeRef) {
		List<Annotation> anns = Arrays.asList(m.getAnnotations());
		String proposed = m.getName();
		Component compAnn = m.getAnnotation(Component.class); 
		if ( compAnn != null ) {
			proposed = compAnn.value();
		}
		Injectable injAnn = m.getAnnotation(Injectable.class); 
		if ( injAnn != null ) {
			proposed = injAnn.component().value();
		}
		
		final String methodName = getClassMethodBeanName(m, proposed);
		
		return Arrays.asList(m.getParameters())
			.stream()
			.map(parameter -> new AbstractMap.SimpleEntry<Parameter, List<Annotation>>(parameter, Arrays.asList(parameter.getAnnotations())))
			.filter( fieldEntry -> fieldEntry.getValue().size() > 0 )
			.filter( fieldEntry -> fieldEntry.getValue()
									.stream()
									.filter(annMethod -> { return Autowired.class.isAssignableFrom(annMethod.getClass()) ||
														Inject.class.isAssignableFrom(annMethod.getClass()) ||
														Value.class.isAssignableFrom(annMethod.getClass());} )
									.count() > 0
					)
			.map( fieldEntry -> new AbstractMap.SimpleEntry<Parameter, List<Annotation>>( fieldEntry.getKey(), fieldEntry.getValue()
																														  .stream()
																														  .filter(annMethod -> { return Autowired.class.isAssignableFrom(annMethod.getClass()) ||
																																	Inject.class.isAssignableFrom(annMethod.getClass()) ||
																																	Value.class.isAssignableFrom(annMethod.getClass()); })
																														  .collect(Collectors.toList())
																						)
			)
			.filter( fieldEntry -> fieldEntry.getValue().size() > 0 )
			.map(fieldEntry -> {
				Parameter parameter = fieldEntry.getKey();
				List<Annotation> localAnns = fieldEntry.getValue();
				if ( localAnns.size() == 0 ) {
					return null;
				} else if ( localAnns.size() > 1 ) {
					String messageX = String.format("Too many annotations (%s > 1) for parmeter %s of method %s in component class %s, annotations are : %s",
									  ""+anns.size(),
									  parameter.getName(),
									  m.getName(),
									  elementClass.getName(),
									  Arrays.toString( anns.stream().map( pAnn -> pAnn.getClass().getName() ).collect(Collectors.toList()).toArray() ));
					LoggerHelper.logError("StreamIOConfigurationExecutor::executeAnnotation", messageX, null);
					throw new ScannerException(messageX);
				}
				String localBeanName = parameter.getName();
				Annotation localAnn = localAnns.get(0);
				if ( Autowired.class.isAssignableFrom(localAnn.getClass()) ) {
					Autowired autowired = (Autowired) localAnn;
					localBeanName = localBeanName.substring(0,1).toLowerCase() + localBeanName.substring(1);
					if ( autowired.name() != null && 
							! autowired.name().isEmpty()) {
						localBeanName = autowired.name();
					}
				} else if (Inject.class.isAssignableFrom(localAnn.getClass())) {
					Inject inject = (Inject)localAnn;
					localBeanName = localBeanName.substring(0,1).toLowerCase() + localBeanName.substring(1);
					if ( inject.name() != null && 
							! inject.name().isEmpty()) {
						localBeanName = inject.name();
					}
				}
				TransformCase localFieldCaseTransformer = parameter.getAnnotation(TransformCase.class);
				if ( localFieldCaseTransformer != null ) {
					localBeanName = AnnotationHelper.transformBeanName(localBeanName, localFieldCaseTransformer);
				}
				Autowired autowiredAnnotation=null; 
				Inject injectAnnotation=null; 
				Value valueAnnotation=null;
				if ( Autowired.class.isAssignableFrom(localAnn.getClass()) ) {
					autowiredAnnotation = (Autowired) localAnn;
				} else if (Inject.class.isAssignableFrom(localAnn.getClass())) {
					injectAnnotation = (Inject) localAnn;
				} else {
					valueAnnotation = (Value) localAnn;
				}
				return new ParameterRef(typeRef, methodName, localBeanName, parameter.getName(), autowiredAnnotation, injectAnnotation, valueAnnotation);
			})
			.filter(parameterRef -> parameterRef != null)
			.collect(Collectors.toList());
		}
}
