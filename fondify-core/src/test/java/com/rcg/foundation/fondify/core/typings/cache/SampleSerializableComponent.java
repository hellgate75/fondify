/**
 * 
 */
package com.rcg.foundation.fondify.core.typings.cache;

import java.sql.Date;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class SampleSerializableComponent extends SerializableComponent {

	/**
	 * Unique Serial version Id
	 */
	private static final long serialVersionUID = -1949120182731744183L;

	private Boolean booleanField = Boolean.FALSE;
	
	private String stringField = "";
	
	private Date dateField = new Date(System.nanoTime());
	
	/**
	 * Default Constructor
	 */
	public SampleSerializableComponent() {
		super();
	}

	/**
	 * All fields Constructor
	 * @param booleanField Boolean field value
	 * @param stringField String field value
	 * @param dateField Date field value
	 */
	public SampleSerializableComponent(Boolean booleanField, String stringField, Date dateField) {
		super();
		this.booleanField = booleanField;
		this.stringField = stringField;
		this.dateField = dateField;
	}

	/**
	 * @return the booleanField
	 */
	public Boolean getBooleanField() {
		return booleanField;
	}

	/**
	 * @return the stringField
	 */
	public String getStringField() {
		return stringField;
	}

	/**
	 * @return the dateField
	 */
	public Date getDateField() {
		return dateField;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((booleanField == null) ? 0 : booleanField.hashCode());
		result = prime * result + ((dateField == null) ? 0 : dateField.hashCode());
		result = prime * result + ((stringField == null) ? 0 : stringField.hashCode());
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
		SampleSerializableComponent other = (SampleSerializableComponent) obj;
		if (booleanField == null) {
			if (other.booleanField != null)
				return false;
		} else if (!booleanField.equals(other.booleanField))
			return false;
		if (dateField == null) {
			if (other.dateField != null)
				return false;
		} else if (dateField.getTime() != other.dateField.getTime())
			return false;
		if (stringField == null) {
			if (other.stringField != null)
				return false;
		} else if (!stringField.equals(other.stringField))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SampleSerializableComponent [booleanField=" + booleanField + ", stringField=" + stringField
				+ ", dateField=" + dateField + "]";
	}
	
}
