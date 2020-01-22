/**
 * 
 */
package com.rcg.foundation.fondify.properties.properties;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
@XmlRootElement(name = "response")
public class SCSResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8280531613628954343L;

	@JsonProperty("name")
	@XmlElement(name = "name")
	private String name;
	
	@JsonProperty("profiles")
	@XmlElement(name = "profiles")
	private String[] profiles;
	
	@JsonProperty("label")
	@XmlElement(name = "label")
	private String label;
	
	@JsonProperty("version")
	@XmlElement(name = "version")
	private String version;
	
	@JsonProperty("state")
	@XmlElement(name = "state")
	private String state;
	
	@JsonProperty("propertySources")
	@XmlElement(name = "propertySources")
	private PropertyDetails[] propertySources = new PropertyDetails[0];
	
	/**
	 * 
	 */
	public SCSResponse() {
		super();
	}

	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return the profiles
	 */
	public String[] getProfiles() {
		return profiles;
	}


	/**
	 * @param profiles the profiles to set
	 */
	public void setProfiles(String[] profiles) {
		this.profiles = profiles;
	}


	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}


	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}


	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}


	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}


	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}


	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}


	/**
	 * @return the propertySources
	 */
	public PropertyDetails[] getPropertySources() {
		return propertySources;
	}


	/**
	 * @param propertySources the propertySources to set
	 */
	public void setPropertySources(PropertyDetails[] propertySources) {
		this.propertySources = propertySources;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + Arrays.hashCode(profiles);
		result = prime * result + Arrays.hashCode(propertySources);
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		SCSResponse other = (SCSResponse) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (!Arrays.equals(profiles, other.profiles))
			return false;
		if (!Arrays.equals(propertySources, other.propertySources))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "SCSResponse [name=" + name + ", profiles=" + Arrays.toString(profiles) + ", label=" + label
				+ ", version=" + version + ", state=" + state + ", propertySources=" + Arrays.toString(propertySources)
				+ "]";
	}


	static class PropertyDetails implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7479194180107838285L;

		@JsonProperty("name")
		@XmlElement(name = "name")
		private String name;
		
		@JsonProperty("source")
		@XmlElement(name = "source")
		private Map<String, Object> source = new HashMap<>();

		/**
		 * 
		 */
		public PropertyDetails() {
			super();
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the source
		 */
		public Map<String, Object> getSource() {
			return source;
		}

		/**
		 * @param source the source to set
		 */
		public void setSource(Map<String, Object> source) {
			this.source = source;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((source == null) ? 0 : source.hashCode());
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
			PropertyDetails other = (PropertyDetails) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (source == null) {
				if (other.source != null)
					return false;
			} else if (!source.equals(other.source))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "PropertyDetails [name=" + name + ", source=" + source + "]";
		}
		
		
	}
}
