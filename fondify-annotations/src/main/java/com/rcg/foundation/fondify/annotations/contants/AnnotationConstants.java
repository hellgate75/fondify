/**
 * 
 */
package com.rcg.foundation.fondify.annotations.contants;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class AnnotationConstants {

	/**
	 * 
	 */
	private AnnotationConstants() {
		throw new UnsupportedOperationException("Cannot give instants to a Constants class");
	}
	
	public static final String REGISTRY_CLASS_CONFIG_LOADERS="ConfigurationLoaders";
	
	public static final String REGISTRY_CLASS_ANNOTATION_EXECUTORS="AnnotationExecutors";

	public static final String REGISTRY_COMPONENT_BEAN_DEFINITIONS="ComponentBeanDefinitions";

	public static final String REGISTRY_COMPONENT_REFERENCES="ComponentReferences";

	public static final String REGISTRY_INJECTABLE_REFERENCES="InjectablesReferences";

	public static final String REGISTRY_INJECTABLE_BEAN_DEFINITIONS="InjectableBeanDefinitions";

	public static final String REGISTRY_INJECTABLE_METHODD_DEFINITIONS="InjectablesMethodsDefinitions";

	public static final String REGISTRY_BEAN_INSTANCE_APPLICATION_SCOPE="BeanInstanceApplicatioScope";

	public static final String REGISTRY_BEAN_SYSTEM_SCOPE="BeanInstanceSystemScope";

	public static final String REGISTRY_ANNOTATION_DESCRIPTORS="AnnotationDescriptorsList";
	
}
