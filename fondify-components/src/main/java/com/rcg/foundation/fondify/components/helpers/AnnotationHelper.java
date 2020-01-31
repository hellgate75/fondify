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
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.rcg.foundation.fondify.annotations.annotations.TransformCase;
import com.rcg.foundation.fondify.annotations.annotations.methods.Finalization;
import com.rcg.foundation.fondify.annotations.annotations.methods.Initialization;
import com.rcg.foundation.fondify.annotations.contants.AnnotationConstants;
import com.rcg.foundation.fondify.annotations.typings.BeanDefinition;
import com.rcg.foundation.fondify.annotations.typings.MethodExecutor;
import com.rcg.foundation.fondify.components.ComponentsManagerImpl;
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
import com.rcg.foundation.fondify.core.helpers.BeansHelper;
import com.rcg.foundation.fondify.core.helpers.GenericHelper;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;
import com.rcg.foundation.fondify.core.registry.ComponentsRegistry;
import com.rcg.foundation.fondify.core.typings.AnnotationDeclaration;
import com.rcg.foundation.fondify.core.typings.AnnotationExecutor;
import com.rcg.foundation.fondify.core.typings.fields.FieldValueActuatorProvider;
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
		throw new IllegalStateException("AnnotationHelper::constructor -> Unable to instatiatio helper class");
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
		String defaultName = "Bean-" + UUID.randomUUID().toString();
		if (beanClass == null) {
			return defaultName;
		}
		defaultName = beanClass.getSimpleName();
		defaultName = "" + defaultName.toLowerCase().charAt(0) + defaultName.substring(1);
		String name = proposed != null && ! proposed.isEmpty() ? proposed :  defaultName;
		Injectable injAnn = BeansHelper.getClassAnnotation(beanClass, Injectable.class); 
		if ( injAnn != null && injAnn.component().value() != null && ! injAnn.component().value().isEmpty() ) {
			name = injAnn.component().value();
		} else {
			Component compAnn = BeansHelper.getClassAnnotation(beanClass, Component.class); 
			if ( compAnn != null && compAnn.value() != null && ! compAnn.value().isEmpty() ) {
				name = compAnn.value();
			}
		}
		TransformCase caseTransformer = BeansHelper.getClassAnnotation(beanClass, TransformCase.class);
		if ( caseTransformer != null ) {
			name = com.rcg.foundation.fondify.annotations.helpers.AnnotationHelper.transformBeanName(name, caseTransformer);
		}
		return name;
	}
	
	public static final String getClassMethodBeanName(Method m, String proposed) {
		String name = proposed != null && ! proposed.isEmpty() ? proposed :  m.getName();
		Injectable injAnn = BeansHelper.getMethodAnnotation(m, Injectable.class); 
		if ( injAnn != null && injAnn.component().value() != null && ! injAnn.component().value().isEmpty() ) {
			name = injAnn.component().value();
		} else {
			Component compAnn = BeansHelper.getMethodAnnotation(m, Component.class); 
			if ( compAnn != null && compAnn.value() != null && ! compAnn.value().isEmpty() ) {
				name = compAnn.value();
			}
		}
		TransformCase caseTransformer = BeansHelper.getMethodAnnotation(m, TransformCase.class);
		if ( caseTransformer != null ) {
			name = com.rcg.foundation.fondify.annotations.helpers.AnnotationHelper.transformBeanName(name, caseTransformer);
		}
		return name;
	}
	
	public static final String getClassFieldBeanName(Field f, String proposed) {
		String name = proposed != null && ! proposed.isEmpty() ? proposed :  f.getName();
		Value valueAnn = BeansHelper.getFieldAnnotation(f, Value.class); 
		if ( valueAnn != null && valueAnn.value() != null && ! valueAnn.value().isEmpty() ) {
			name = valueAnn.value();
		} else {
			Injectable injAnn = BeansHelper.getFieldAnnotation(f, Injectable.class); 
			if ( injAnn != null && injAnn.component().value() != null && ! injAnn.component().value().isEmpty() ) {
				name = injAnn.component().value();
			} else {
				Component compAnn = BeansHelper.getFieldAnnotation(f, Component.class); 
				if ( compAnn != null && compAnn.value() != null && ! compAnn.value().isEmpty() ) {
					name = compAnn.value();
				} else {
					Inject injsmplAnn = BeansHelper.getFieldAnnotation(f, Inject.class); 
					if ( injsmplAnn != null && injsmplAnn.name() != null && ! injsmplAnn.name().isEmpty() ) {
						name = injsmplAnn.name();
					} else {
						Autowired autowiredAnn = BeansHelper.getFieldAnnotation(f, Autowired.class); 
						if ( autowiredAnn != null && autowiredAnn.name() != null && ! autowiredAnn.name().isEmpty() ) {
							name = autowiredAnn.name();
						}
					}
				}
			}
		}
		TransformCase caseTransformer = BeansHelper.getFieldAnnotation(f, TransformCase.class);
		if ( caseTransformer != null ) {
			name = com.rcg.foundation.fondify.annotations.helpers.AnnotationHelper.transformBeanName(name, caseTransformer);
		}
		return name;
	}
	
	public static final String getMethodParameterBeanName(Parameter p, String proposed) {
		String name = proposed != null && ! proposed.isEmpty() ? proposed :  p.getName();
		Inject injAnn = BeansHelper.getParameterAnnotation(p, Inject.class);
		if ( injAnn != null && injAnn.name() != null && ! injAnn.name().isEmpty() ) {
			name = injAnn.name();
		}
		Value valueAnn = BeansHelper.getParameterAnnotation(p, Value.class);
		if ( valueAnn != null && valueAnn.value() != null && ! valueAnn.value().isEmpty() ) {
			proposed = valueAnn.value();
		}
		TransformCase caseTransformer = BeansHelper.getParameterAnnotation(p, TransformCase.class);
		if ( caseTransformer != null ) {
			name = com.rcg.foundation.fondify.annotations.helpers.AnnotationHelper.transformBeanName(name, caseTransformer);
		}
		return name;
	}
	
	public static final boolean filterBeanField(Class<? extends Annotation> type) {
		return Autowired.class.isAssignableFrom(type) ||
			   Inject.class.isAssignableFrom(type) ||
			   Value.class.isAssignableFrom(type);
	}
	
	public static final boolean filterBeanMethod(Class<? extends Annotation> type) {
		return Injectable.class.isAssignableFrom(type);
	}
	
	public static final boolean filterBeanMethodInitializationFinalization(Class<? extends Annotation> type) {
		return Initialization.class.isAssignableFrom(type) ||
				Finalization.class.isAssignableFrom(type);
	}
	
	
	public static final boolean filterBeanMethodParameter(Class<? extends Annotation> type) {
		return Inject.class.isAssignableFrom(type) ||
			   Autowired.class.isAssignableFrom(type) ||
			   Value.class.isAssignableFrom(type);
	}
	
	public static final <T> Map<Field, List<Annotation>> selectFieldsAnnotations(Class<T> type) {
		SimplePredicate<Field, Map<Field, List<Annotation>>> predicate = (field, fields) -> {
			List<Annotation> annotations = new ArrayList<>(0); 
			annotations.addAll(Arrays.asList(field.getDeclaredAnnotations())
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
	
	public static final Annotation getAnnotationViaClass(Class<?> beanClass) {
		if ( beanClass == null )
			return null;
		Optional<Annotation> annotationName = Arrays
				.asList(beanClass.getDeclaredAnnotations())
				.stream()
				.filter( ann -> {
					if ( ann == null )
						return false;
					try {
						Method m1 = ann.getClass().getDeclaredMethod("value");
						if ( m1 == null || m1.getReturnType()!=String.class) {
							m1 = ann.getClass().getDeclaredMethod("componebnt");
							if ( m1 != null && m1.getReturnType() == Component.class ) {
								return true;
							} else {
								m1 = ann.getClass().getDeclaredMethod("name");
								if ( m1 == null || m1.getReturnType()!=String.class) {
									return false;
								}
							}
						}
						return m1 != null;
					} catch (Exception e) {
						return false;
					}
				})
				.findFirst();
		if ( annotationName.isPresent() ) {
			return annotationName.get();
		}
		return null;
	}

	public static final String getBeanNameByAnnotationViaClass(Class<?> beanClass) {
		if ( beanClass == null )
			return null;
		@SuppressWarnings("unused")
		Optional<String> annotationName = Arrays
				.asList(beanClass.getDeclaredAnnotations())
				.stream()
				.map( ann -> {
					if ( ann == null )
						return "";
					try {
						Method m1 = ann.getClass().getDeclaredMethod("value");
						if ( m1 == null || m1.getReturnType()!=String.class) {
							m1 = ann.getClass().getDeclaredMethod("componebnt");
							if ( m1 != null && m1.getReturnType() == Component.class ) {
								return ((Component)m1.invoke(ann)).value();
							} else {
								m1 = ann.getClass().getDeclaredMethod("name");
								if ( m1 == null || m1.getReturnType()!=String.class) {
									return "";
								}
							}
						}
						if ( m1 != null )
							return (String)m1.invoke(ann);
					} catch (Exception e) {
						return "";
					}
					return "";
				})
				.filter(name -> name != null && ! name.isEmpty() )
				.findFirst();
		if ( annotationName.isPresent() ) {
			return getClassBeanName(beanClass, annotationName.get());
		} else {
			getClassBeanName(beanClass, GenericHelper.initCapBeanName(beanClass.getSimpleName()));
		}
		return null;
	}
	
	public static final Object scanAndProcessEntity(Object entity, Class<?> entityClass) {
		return scanAndProcessEntity(entity, entityClass, null);
	}
	
	public static final Object scanAndProcessEntity(Object entity, Class<?> entityClass, String wantedBeanName) {
		if ( entityClass == null && entity != null ) {
			entityClass = entity.getClass();
		}
		String beanName = wantedBeanName;
		if ( beanName == null || beanName.isEmpty()  ) {
			if (entityClass != null)
				beanName = entityClass == null ? null : AnnotationHelper.getClassBeanName(entityClass, GenericHelper.initCapBeanName(entityClass.getSimpleName()));
		}
		ComponentsManagerImpl componentsManager = new ComponentsManagerImpl();
		if ( entity == null ) {
			
			if ( entity == null && beanName != null && ! beanName.isEmpty() ) {
				try {
					entity = componentsManager.getInjectableOrComponentByName(beanName, null);
					return entity;
				} catch (Exception e) {
					LoggerHelper.logError("AnnotationHelper::scanAndProcessEntity", 
							String.format("Error recovering instance of bean names %s, so trying new instance...", beanName), 
							e);
				}
			}
			if ( entityClass == null ) {
				LoggerHelper.logWarn("AnnotationHelper::scanAndProcessEntity", "Null entity and class, so no processing...", null);
				return null;
			} else {
				try {
					entity = entityClass.newInstance();
				} catch (Exception e) {
					LoggerHelper.logError("AnnotationHelper::scanAndProcessEntity", 
												String.format("Error making instance of class %s, so no processing...", entityClass.getName()), 
												e);
					return null;
				}
			}
			if ( entity == null ) {
				LoggerHelper.logWarn("AnnotationHelper::scanAndProcessEntity", "Null entity instance, so no processing...", null);
				return null;
			}
		}

		if ( beanName == null ) {
			beanName = AnnotationHelper.getClassBeanName(entityClass, GenericHelper.initCapBeanName(entityClass.getSimpleName()));
		}
		
		processFieldsAnnotations(entityClass, entity);

		return entity;
	}
	
	protected static final void processFieldsAnnotations(Class<?> elementClass, Object instance) {
			Arrays.asList(elementClass.getDeclaredFields()).stream()
					.collect(Collectors.toMap((field) -> field,
							(field) -> Arrays.asList(field.getDeclaredAnnotations())))
					.entrySet()
					.stream()
					.filter(entry -> entry.getValue().stream().filter(ann -> filterBeanField(ann.getClass())).count() > 0)
					.forEach(entry -> {
						Field field = entry.getKey();
						LoggerHelper.logTrace("AnnotationHelper::processFieldsAnnotations(Class<?>, Object)", String.format("Processing Bean FIELD annotations for class: %s at field: %s", elementClass.getName(), field.getName()));
						String name = getClassFieldBeanName(field, field.getName());
						try {
							Optional<Object> value = FieldValueActuatorProvider.getInstance().tranlateFieldValue(field);
							if (value.isPresent())
								field.set(instance, value.get());
						} catch (Exception e) {
							LoggerHelper.logError("AnnotationHelper::processFieldsAnnotations(Class<?>, Object)",
									String.format("Unable to fill field %s (bean: %s), due to ERRORS!!",
											field != null ? field.getName() : "<NULL>", name),
									e);
						}
					});
	}
	
	public static final void processFieldsAnnotations(Class<?> elementClass, BeanDefinition currentDefinition, String typeRef, Predicate<Annotation> filter) {
		SimpleEntryPredicate<Field, List<Annotation>, BeanDefinition> entryFilter = (entry, definition) -> {
			Field field = entry.getKey();
			entry.getValue()
			.stream()
			.map( ann -> {
				String name = getClassFieldBeanName(field, field.getName());
				ComponentRef ref = new ComponentRef(typeRef, field.getName(), name, true, false, false);
				if ( Autowired.class.isAssignableFrom(ann.getClass()) ) {
					ref.setAutowiredAnnotation((Autowired) ann);
				} else if ( Injectable.class.isAssignableFrom(ann.getClass()) ) {
					ref.setInjectAnnotation((Injectable) ann);
				} else {
					return null;
				}
				return ref;
			} )
			.filter( cr -> cr != null )
			.forEach( definition::addComponentsReference );
		};
		SimplePredicate<Field, Map<Field, List<Annotation>>> predicate = (field, fields) -> {
			List<Annotation> annotations = new ArrayList<>(0); 
			annotations.addAll(Arrays.asList(field.getDeclaredAnnotations())
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
			annotations.addAll(Arrays.asList(field.getDeclaredAnnotations())
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
				
				TransformCase caseTransformer = BeansHelper.getFieldAnnotation(field, TransformCase.class);
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
						initializationAnnotation = BeansHelper.getMethodAnnotation(m, Initialization.class);
					}
					if ( Finalization.class.isAssignableFrom(ann.getClass()) ) {
						finalizationAnnotation = BeansHelper.getMethodAnnotation(m, Finalization.class);
					}
					Class<?> annotatedClass = m.getReturnType();
					
					AnnotationDeclaration declaration = new AnnotationDeclaration(ann.getClass(), annotatedClass, null, m, null);
					MethodExecutor executor = new MethodExecutor(declaration, beanName, m, initializationAnnotation, finalizationAnnotation);
					final String methodName = beanName;
					Arrays.asList(m.getParameters())
						.stream()
						.map(parameter -> new AbstractMap.SimpleEntry<Parameter, List<Annotation>>(parameter, Arrays.asList(parameter.getDeclaredAnnotations())))
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
							TransformCase localFieldCaseTransformer = BeansHelper.getParameterAnnotation(parameter, TransformCase.class);
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
				Injectable inject = (Injectable)ann;
				name = name.substring(0,1).toLowerCase() + name.substring(1);
				if ( inject.component() != null && 
						inject.component().value() != null && 
						! inject.component().value().isEmpty()) {
					name = inject.component().value();
				}
			}
			TransformCase fieldCaseTransformer = BeansHelper.getMethodAnnotation(m, TransformCase.class);;
			if ( fieldCaseTransformer != null ) {
				name = AnnotationHelper.transformBeanName(name, fieldCaseTransformer);
			}
			ComponentRef ref = new ComponentRef(typeRef, m.getName(), name, true, false, false);
			if ( Component.class.isAssignableFrom(ann.getClass()) ) {
				ref.setComponentAnnotation((Component) ann);
			} else {
				ref.setInjectAnnotation((Injectable) ann);
			}
			final String methodName = name;
			Arrays.asList(m.getParameters())
				.stream()
				.map(parameter -> new AbstractMap.SimpleEntry<Parameter, List<Annotation>>(parameter, Arrays.asList(parameter.getDeclaredAnnotations())))
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
					TransformCase localFieldCaseTransformer = BeansHelper.getParameterAnnotation(parameter, TransformCase.class);
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
		List<Annotation> anns = Arrays.asList(m.getDeclaredAnnotations());
		String proposed = m.getName();
		Component compAnn = BeansHelper.getMethodAnnotation(m, Component.class); 
		if ( compAnn != null ) {
			proposed = compAnn.value();
		}
		Injectable injAnn = BeansHelper.getMethodAnnotation(m, Injectable.class);
		if ( injAnn != null ) {
			proposed = injAnn.component().value();
		}
		
		final String methodName = getClassMethodBeanName(m, proposed);
		
		return Arrays.asList(m.getParameters())
			.stream()
			.map(parameter -> new AbstractMap.SimpleEntry<Parameter, List<Annotation>>(parameter, Arrays.asList(parameter.getDeclaredAnnotations())))
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
				TransformCase localFieldCaseTransformer = BeansHelper.getParameterAnnotation(parameter, TransformCase.class);
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
