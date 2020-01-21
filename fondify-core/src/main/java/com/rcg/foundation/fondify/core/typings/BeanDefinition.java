/**
 * 
 */
package com.rcg.foundation.fondify.core.typings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.rcg.foundation.fondify.core.domain.Scope;
import com.rcg.foundation.fondify.core.typings.fields.ComponentReference;
import com.rcg.foundation.fondify.core.typings.methods.PropertyRef;

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

	@Override
	public String toString() {
		return "BeanDefinition [declaration=" + declaration + ", scope=" + scope + ", componentsReference="
				+ componentsReference + ", propertiesReference=" + propertiesReference + "]";
	}

}
