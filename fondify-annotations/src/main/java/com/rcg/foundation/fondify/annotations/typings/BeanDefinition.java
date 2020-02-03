/**
 * 
 */
package com.rcg.foundation.fondify.annotations.typings;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.rcg.foundation.fondify.annotations.lifecycle.SessionContext;
import com.rcg.foundation.fondify.core.domain.Scope;
import com.rcg.foundation.fondify.core.functions.Transformer;
import com.rcg.foundation.fondify.core.properties.PropertyArchive;
import com.rcg.foundation.fondify.core.typings.AnnotationDeclaration;
import com.rcg.foundation.fondify.core.typings.fields.ComponentReference;
import com.rcg.foundation.fondify.core.typings.lifecycle.Session;
import com.rcg.foundation.fondify.core.typings.methods.PropertyRef;
import com.rcg.foundation.fondify.utils.helpers.LoggerHelper;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class BeanDefinition {

	private AnnotationDeclaration declaration;

	private Scope scope = Scope.APPLICATION;
	
	private List<ComponentReference> componentsReference = new ArrayList<>(0);
	
	private List<PropertyRef> propertiesReference = new ArrayList<>(0);
	
	private List<MethodExecutor> initializationMethods = new ArrayList<>(0);
	
	private List<MethodExecutor> finalizationMethods = new ArrayList<>(0);
	
	/**
	 * 
	 */
	public BeanDefinition(AnnotationDeclaration declaration) {
		this.declaration = declaration;
	}

	/**
	 * @return the componentsReference
	 */
	public List<ComponentReference> getComponentsReference() {
		return componentsReference;
	}

	/**
	 * Add all {@link ComponentRef} elements to the Bean definition  
	 * @param componentsReference the componentsReference to set
	 */
	public void addAllComponentsReferences(Collection<ComponentReference> componentsReference) {
		this.componentsReference.addAll(componentsReference);
	}

	/**
	 * Add single {@link ComponentRef} element to the Bean definition  
	 * @param componentsReference the componentsReference to set
	 */
	public void addComponentsReference(ComponentReference componentsReference) {
		this.componentsReference.add(componentsReference);
	}

	/**
	 * @return the propertiesReference
	 */
	public List<PropertyRef> getPropertiesReference() {
		return propertiesReference;
	}

	/**
	 * Add single {@link PropertyRef} element to the Bean definition  
	 * @param propertiesReference the propertiesReference to set
	 */
	public void addPropertiesReference(PropertyRef propertiesReference) {
		this.propertiesReference.add(propertiesReference);
	}

	/**
	 * Add all {@link PropertyRef} elements to the Bean definition  
	 * @param propertiesReference the propertiesReference to set
	 */
	public void addAllPropertiesReferences(Collection<PropertyRef> propertiesReference) {
		this.propertiesReference.addAll(propertiesReference);
	}

	/**
	 * @return the initializationMethods
	 */
	public List<MethodExecutor> getInitializationMethods() {
		return initializationMethods;
	}

	/**
	 * @param initializationMethods the initializationMethods to set
	 */
	public void addAllInitializationMethods(Collection<MethodExecutor> initializationMethods) {
		this.initializationMethods.addAll(initializationMethods);
		Collections.sort(this.initializationMethods, (a, b) ->  a.getInitializationAnnotation().order() - b.getInitializationAnnotation().order()  );
	}

	/**
	 * @param initializationMethods the initializationMethods to set
	 */
	public void addInitializationMethod(MethodExecutor initializationMethod) {
		this.initializationMethods.add(initializationMethod);
		Collections.sort(this.initializationMethods, (a, b) ->  a.getInitializationAnnotation().order() - b.getInitializationAnnotation().order()  );
	}

	/**
	 * @return the finalizationMethods
	 */
	public List<MethodExecutor> getFinalizationMethods() {
		return finalizationMethods;
	}

	/**
	 * @param finalizationMethods the finalizationMethods to set
	 */
	public void addAllFinalizationMethods(Collection<MethodExecutor> finalizationMethods) {
		this.finalizationMethods.addAll(finalizationMethods);
		Collections.sort(this.finalizationMethods, (a, b) ->  a.getFinalizationAnnotation().order() - b.getFinalizationAnnotation().order()  );
	}

	/**
	 * @param finalizationMethods the finalizationMethods to set
	 */
	public void addFinalizationMethod(MethodExecutor finalizationMethod) {
		this.finalizationMethods.add(finalizationMethod);
		Collections.sort(this.finalizationMethods, (a, b) ->  a.getFinalizationAnnotation().order() - b.getFinalizationAnnotation().order()  );
	}

	/**
	 * @return the scope
	 */
	public Scope getScope() {
		return scope;
	}

	/**
	 * @param scope the scope to set
	 */
	public void setScope(Scope scope) {
		this.scope = scope;
	}

	/**
	 * @return the declaration
	 */
	public AnnotationDeclaration getDeclaration() {
		return declaration;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((declaration == null) ? 0 : declaration.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BeanDefinition other = (BeanDefinition) obj;
		if (declaration == null) {
			if (other.declaration != null)
				return false;
		} else if (!declaration.equals(other.declaration))
			return false;
		return true;
	}
	
	public Object execute(Object defaultValue, Transformer<String, Object> nametoBeanTranformer) {
		Object instance = defaultValue;
		if ( instance == null ) {
			try {
				instance = this.declaration.getAnnotatedClass().newInstance();
			} catch (Exception ex) {
				String message = String.format("Unable to make instance of AnnotationDeclaration element: %s", this.declaration.toString());
				LoggerHelper.logError("BeanDefinition::execute", 
										message,
										ex);
				throw new IllegalStateException(message, ex);
			}
		}
		if ( instance == null ) {
			String message = String.format("Unavailable instance of AnnotationDeclaration element: %s", this.declaration.toString());
			LoggerHelper.logError("BeanDefinition::execute", 
									message,
									null);
			throw new IllegalStateException(message);
		}
		final Object instanceFinal = instance;
		propertiesReference.forEach(property ->{
			String ref = property.getTypeRef();
			try {
				Field f = instanceFinal.getClass().getDeclaredField(ref);
				if ( f != null ) {
					
				}
				String element = property.getPropertyDescr();
				Object value = null;
				if ( property.isValueProperty() ) {
					value = PropertyArchive.getInstance().getProperty(element);
				} else if (ref.equals("arguments") &&
						Properties.class.isAssignableFrom(f.getType())) {
					value = nametoBeanTranformer.tranform("?arguments?");
				}  else if (ref.equals("applicationContext") ||
						Session.class.isAssignableFrom(f.getType())) {
					value = nametoBeanTranformer.tranform("?applicationContext?");
				} else if (ref.equals("sessionContext") ||
						SessionContext.class.isAssignableFrom(f.getType())) {
					value = nametoBeanTranformer.tranform("?sessionContext?");
				} else if (ref.equals("session") ||
						Session.class.isAssignableFrom(f.getType())) {
					value = nametoBeanTranformer.tranform("?session?");
				} else {
					value = nametoBeanTranformer.tranform(element);
				}
				f.set(instanceFinal, value);
			} catch (Exception ex) {
				String message = String.format("Unable to populate field %s of element: %s", 
										ref,
										instanceFinal.getClass().getName());
				LoggerHelper.logError("BeanDefinition::execute", 
										message,
										ex);
				throw new IllegalArgumentException(message, ex);
			}
		});
		return instanceFinal;
	}

	@Override
	public String toString() {
		return "BeanDefinition [declaration=" + declaration + ", scope=" + scope + ", componentsReference="
				+ componentsReference + ", propertiesReference=" + propertiesReference + "]";
	}

}
