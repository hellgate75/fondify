/**
 * 
 */
package com.rcg.foundation.fondify.core.arguments;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ArgumentDescriptor {
	private String argumentName;
	private ArgumentType argumentType;
	private boolean list;
	
	/**
	 * @param argumentName
	 * @param argumentType
	 * @param list
	 */
	public ArgumentDescriptor(String argumentName, ArgumentType argumentType, boolean list) {
		super();
		this.argumentName = argumentName;
		this.argumentType = argumentType;
		this.list = list;
	}
	
	/**
	 * @return the argumentName
	 */
	public String getArgumentName() {
		return argumentName;
	}
	
	/**
	 * @param argumentName the argumentName to set
	 */
	public void setArgumentName(String argumentName) {
		this.argumentName = argumentName;
	}
	/**
	 * @return the argumentType
	 */
	public ArgumentType getArgumentType() {
		return argumentType;
	}
	
	/**
	 * @param argumentType the argumentType to set
	 */
	public void setArgumentType(ArgumentType argumentType) {
		this.argumentType = argumentType;
	}
	
	/**
	 * @return the list
	 */
	public boolean isList() {
		return list;
	}
	
	/**
	 * @param list the list to set
	 */
	public void setList(boolean list) {
		this.list = list;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((argumentName == null) ? 0 : argumentName.hashCode());
		result = prime * result + ((argumentType == null) ? 0 : argumentType.hashCode());
		result = prime * result + (list ? 1231 : 1237);
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
		ArgumentDescriptor other = (ArgumentDescriptor) obj;
		if (argumentName == null) {
			if (other.argumentName != null)
				return false;
		} else if (!argumentName.equals(other.argumentName))
			return false;
		if (argumentType != other.argumentType)
			return false;
		if (list != other.list)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "ArgumentDescriptor [argumentName=" + argumentName + ", argumentType=" + argumentType + ", list=" + list
				+ "]";
	}

}
