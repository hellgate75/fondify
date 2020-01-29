/**
 * 
 */
package com.rcg.foundation.fondify.core.listeners.typings;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class InjectableExecutionResponse {
	private boolean success = false;
	private boolean warnings = false;
	private String message;
	/**
	 * @param success
	 * @param warnings
	 * @param message
	 */
	public InjectableExecutionResponse(boolean success, boolean warnings, String message) {
		super();
		this.success = success;
		this.warnings = warnings;
		this.message = message;
	}
	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}
	/**
	 * @return the warnings
	 */
	public boolean isWarnings() {
		return warnings;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + (success ? 1231 : 1237);
		result = prime * result + (warnings ? 1231 : 1237);
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
		InjectableExecutionResponse other = (InjectableExecutionResponse) obj;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (success != other.success)
			return false;
		if (warnings != other.warnings)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "InjectableExecutionResponse [success=" + success + ", warnings=" + warnings + ", message=" + message
				+ "]";
	}

}
