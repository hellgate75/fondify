/**
 * 
 */
package com.rcg.foundation.fondify.components.loaders.typings;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.rcg.foundation.fondify.components.loaders.converters.JsonToListConverter;
import com.rcg.foundation.fondify.components.loaders.converters.ListToJsonConverter;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
@XmlRootElement(name="beans")
@JsonRootName("beans")
public class Beans {

	@XmlElement(name = "elements")
	@JsonProperty("elements")
	@JsonSerialize(converter = ListToJsonConverter.class)
	@JsonDeserialize(converter = JsonToListConverter.class  )
	public List<Bean> elements = new ArrayList<Bean>(0);
	
	/**
	 * 
	 */
	public Beans() {
		super();
	}

	/**
	 * @return the elements
	 */
	public List<Bean> getElements() {
		return elements;
	}

	/**
	 * @param elements the elements to set
	 */
	public void setElements(List<Bean> elements) {
		this.elements.addAll(elements);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((elements == null) ? 0 : elements.hashCode());
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
		Beans other = (Beans) obj;
		if (elements == null) {
			if (other.elements != null)
				return false;
		} else if (!elements.equals(other.elements))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Beans [elements size=" + elements.size() + "]";
	}

	
}
