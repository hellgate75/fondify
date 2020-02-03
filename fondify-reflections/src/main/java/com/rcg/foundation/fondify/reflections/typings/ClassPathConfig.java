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
	private List<ClassLoader> classLoadersList = new ArrayList<ClassLoader>();
	private boolean enablePersistenceOfData = true;

	/**
	 * Default protected Constructor (For full JVM Class-Path scanning purposes)
	 */
	protected ClassPathConfig() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * Full parameters protected constructor
	 * @param packageInclusionList List of packages included in the Class-Path scanning
	 * @param packageExclusionList List of packages excluded in the Class-Path scanning
	 * @param classPathInclusionList List of java libraries/folders included in the Class-Path scanning
	 * @param classPathExclusionList List of java libraries/folders excluded in the Class-Path scanning
	 * @param classLoadersList List of provided ClassLoaders
	 * @param enablePersistenceOfData Keep in-memory the loaded and compiled Java Entries from the Class-Path
	 */
	protected ClassPathConfig(List<String> packageInclusionList, List<String> packageExclusionList,
			List<String> classPathInclusionList, List<String> classPathExclusionList, 
			boolean enablePersistenceOfData, List<ClassLoader> classLoadersList) {
		super();
		if ( packageInclusionList != null )
			this.packageInclusionList.addAll( packageInclusionList );
		if ( packageExclusionList != null )
			this.packageExclusionList.addAll( packageExclusionList );
		if ( classPathInclusionList != null )
			this.classPathInclusionList.addAll( classPathInclusionList );
		if ( classPathExclusionList != null )
			this.classPathExclusionList.addAll( classPathExclusionList );
		this.enablePersistenceOfData = enablePersistenceOfData;
		this.classLoadersList = classLoadersList;
	}
	/**
	 * Returns the list of packages or packages root included in the
	 * JVM Class-Path Scanning
	 * @return the packageInclusionList is the list of included packages
	 */
	public List<String> getPackageInclusionList() {
		return packageInclusionList;
	}
	/**
	 * Returns the list of packages or packages root excluded in the
	 * JVM Class-Path Scanning
	 * @return the packageExclusionList is the list of excluded packages
	 */
	public List<String> getPackageExclusionList() {
		return packageExclusionList;
	}
	/**
	 * Returns the list of partial or exact names of java libraries or 
	 * folders included in the JVM Class-Path Scanning
	 * @return the classPathInclusionList is the list of partial/exact Class-Path entries
	 */
	public List<String> getClassPathInclusionList() {
		return classPathInclusionList;
	}
	/**
	 * Returns the list of partial or exact names of java libraries or 
	 * folders excluded in the JVM Class-Path Scanning
	 * @return the classPathExclusion is the list of partial/exact Class-Path entries
	 */
	public List<String> getClassPathExclusionList() {
		return classPathExclusionList;
	}
	
	/**
	 * Return custom list of {@link ClassLoader}s required for the
	 * system scanning
	 * @return the classLoadersList List of required {@link ClassLoader} 
	 */
	public List<ClassLoader> getClassLoadersList() {
		return classLoadersList;
	}
	/**
	 * Returns the state of persistence of JVM Java Entries
	 * discovered during the Class-Path Scan or recovery of
	 * previous scan. By default value is true, giving the
	 * application huge performances in case of multiple scans.  
	 * @return the enablePersistenceOfData is the state of the persistence of scanning results data
	 */
	public boolean enablePersistenceOfData() {
		return enablePersistenceOfData;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((classPathExclusionList == null) ? 0 : classPathExclusionList.hashCode());
		result = prime * result + ((classPathInclusionList == null) ? 0 : classPathInclusionList.hashCode());
		result = prime * result + (enablePersistenceOfData ? 1231 : 1237);
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
		if (enablePersistenceOfData != other.enablePersistenceOfData)
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
				+ ", classPathExclusionList=" + classPathExclusionList + ", enableSessionData=" + enablePersistenceOfData
				+ "]";
	}

}
