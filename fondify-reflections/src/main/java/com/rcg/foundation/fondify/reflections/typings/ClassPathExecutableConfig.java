/**
 * 
 */
package com.rcg.foundation.fondify.reflections.typings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ClassPathExecutableConfig {

	private List<String> jvmClassPathEntitiesList = new ArrayList<>();
	private List<String> packageInclusionList = new ArrayList<String>();
	private List<String> packageExclusionList = new ArrayList<String>();
	private boolean enableSessionData = true;
	/**
	 * @param jvmClassPathEntitiesList
	 * @param packageInclusionList
	 * @param packageExclusionList
	 * @param enableSessionData
	 */
	public ClassPathExecutableConfig(List<String> jvmClassPathEntitiesList, List<String> packageInclusionList,
			List<String> packageExclusionList, boolean enableSessionData) {
		super();
		if ( jvmClassPathEntitiesList != null )
			this.jvmClassPathEntitiesList.addAll(jvmClassPathEntitiesList);
		if ( packageInclusionList != null )
			this.packageInclusionList.addAll(packageInclusionList);
		if ( packageExclusionList != null )
			this.packageExclusionList.addAll(packageExclusionList);
		this.enableSessionData = enableSessionData;
	}
	/**
	 * @return the jvmClassPathEntitiesList
	 */
	public List<String> getJvmClassPathEntitiesList() {
		return jvmClassPathEntitiesList;
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
	 * @return the enableSessionData
	 */
	public boolean isEnableSessionData() {
		return enableSessionData;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (enableSessionData ? 1231 : 1237);
		result = prime * result + ((jvmClassPathEntitiesList == null) ? 0 : jvmClassPathEntitiesList.hashCode());
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
		ClassPathExecutableConfig other = (ClassPathExecutableConfig) obj;
		if (enableSessionData != other.enableSessionData)
			return false;
		if (jvmClassPathEntitiesList == null) {
			if (other.jvmClassPathEntitiesList != null)
				return false;
		} else if (!jvmClassPathEntitiesList.equals(other.jvmClassPathEntitiesList))
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
		return "ClassPathExecutableConfig [jvmClassPathEntitiesList=" + jvmClassPathEntitiesList
				+ ", packageInclusionList=" + packageInclusionList + ", packageExclusionList=" + packageExclusionList
				+ ", enableSessionData=" + enableSessionData + "]";
	}
	
}
