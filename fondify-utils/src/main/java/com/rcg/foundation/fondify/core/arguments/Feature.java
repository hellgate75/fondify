/**
 * 
 */
package com.rcg.foundation.fondify.core.arguments;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class Feature {
	private String name;
	private FeatureMatchFunc matchFunction;
	private FeatureApplyFunc applyFunction;
	/**
	 * @param name
	 * @param matchFunction
	 * @param applyFunction
	 */
	public Feature(String name, FeatureMatchFunc matchFunction, FeatureApplyFunc applyFunction) {
		super();
		this.name = name;
		this.matchFunction = matchFunction;
		this.applyFunction = applyFunction;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the matchFunction
	 */
	public FeatureMatchFunc getMatchFunction() {
		return matchFunction;
	}
	/**
	 * @return the applyFunction
	 */
	public FeatureApplyFunc getApplyFunction() {
		return applyFunction;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Feature other = (Feature) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Feature [name=" + name + "]";
	}

}
