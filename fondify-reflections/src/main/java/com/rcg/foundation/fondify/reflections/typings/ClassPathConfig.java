/**
 * 
 */
package com.rcg.foundation.fondify.reflections.typings;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for class-path scan filtering, defining rules for 
 * recovering classes present in any of the class-path entries (jars and folders).
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class ClassPathConfig {
	private List<String> packageInclusionList = new ArrayList<String>();
	private List<String> packageExclusionList = new ArrayList<String>();
	private List<String> classPathInclusionList = new ArrayList<String>();
	private List<String> classPathExclusionList = new ArrayList<String>();
	private boolean enableSessionData = true;

	/**
	 * Default Constructor
	 */
	protected ClassPathConfig() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param packageInclusionList
	 * @param packageExclusionList
	 * @param classPathInclusionList
	 * @param classPathExclusionList
	 * @param enableSessionData
	 */
	protected ClassPathConfig(List<String> packageInclusionList, List<String> packageExclusionList,
			List<String> classPathInclusionList, List<String> classPathExclusionList, boolean enableSessionData) {
		super();
		if ( packageInclusionList != null )
			this.packageInclusionList.addAll( packageInclusionList );
		if ( packageExclusionList != null )
			this.packageExclusionList.addAll( packageExclusionList );
		if ( classPathInclusionList != null )
			this.classPathInclusionList.addAll( classPathInclusionList );
		if ( classPathExclusionList != null )
			this.classPathExclusionList.addAll( classPathExclusionList );
		this.enableSessionData = enableSessionData;
	}
	/**
	 * @return the packageInclusionList
	 */
	public List<String> getPackageInclusionList() {
		return packageInclusionList;
	}
	/**
	 * @return the packageExclusionList
	 */
	public List<String> getPackageExclusionList() {
		return packageExclusionList;
	}
	/**
	 * @return the classPathInclusionList
	 */
	public List<String> getClassPathInclusionList() {
		return classPathInclusionList;
	}
	/**
	 * @return the classPathExclusionList
	 */
	public List<String> getClassPathExclusionList() {
		return classPathExclusionList;
	}
	
	/**
	 * @return the enableSessionData
	 */
	public boolean enableSessionData() {
		return enableSessionData;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((classPathExclusionList == null) ? 0 : classPathExclusionList.hashCode());
		result = prime * result + ((classPathInclusionList == null) ? 0 : classPathInclusionList.hashCode());
		result = prime * result + (enableSessionData ? 1231 : 1237);
		result = prime * result + ((packageExclusionList == null) ? 0 : packageExclusionList.hashCode());
		result = prime * result + ((packageInclusionList == null) ? 0 : packageInclusionList.hashCode());
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
		ClassPathConfig other = (ClassPathConfig) obj;
		if (classPathExclusionList == null) {
			if (other.classPathExclusionList != null)
				return false;
		} else if (!classPathExclusionList.equals(other.classPathExclusionList))
			return false;
		if (classPathInclusionList == null) {
			if (other.classPathInclusionList != null)
				return false;
		} else if (!classPathInclusionList.equals(other.classPathInclusionList))
			return false;
		if (enableSessionData != other.enableSessionData)
			return false;
		if (packageExclusionList == null) {
			if (other.packageExclusionList != null)
				return false;
		} else if (!packageExclusionList.equals(other.packageExclusionList))
			return false;
		if (packageInclusionList == null) {
			if (other.packageInclusionList != null)
				return false;
		} else if (!packageInclusionList.equals(other.packageInclusionList))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "ClassPathConfig [packageInclusionList=" + packageInclusionList + ", packageExclusionList="
				+ packageExclusionList + ", classPathInclusionList=" + classPathInclusionList
				+ ", classPathExclusionList=" + classPathExclusionList + ", enableSessionData=" + enableSessionData
				+ "]";
	}

}
