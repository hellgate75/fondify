/**
 * 
 */
package com.rcg.foundation.fondify.reflections.typings;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper used for creating a {@link ClassPathConfig} configuration
 * for filtering class-path / packages scanning.
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class ClassPathConfigBuilder {
	
	public static final ClassPathConfig ALL_JVM_SCAN_CONFIGURATION = new ClassPathConfig();
	
	private List<String> packageInclusionList = new ArrayList<String>();
	private List<String> packageExclusionList = new ArrayList<String>();
	private List<String> classPathInclusionList = new ArrayList<String>();
	private List<String> classPathExclusionList = new ArrayList<String>();
	private boolean enableSessionData = true;

	/**
	 * Default protected constructor for only internal use.
	 */
	protected ClassPathConfigBuilder() {
		super();
	}
	
	/**
	 * Start new ClassPath Configuration Builder used for configuring 
	 * Package / ClassPath filter scan, in order to reduce computation 
	 * and machine time, in case of filtering of packages or class-path 
	 * entries performances increase based on the current libraries volume.
	 * @return ({@link ClassPathConfigBuilder}) Builder for fluent interface
	 */
	public static final ClassPathConfigBuilder start() {
		return new ClassPathConfigBuilder();
	}
	
	private static final String fixPackageName(String name) {
		if (name.endsWith(".*") )
			name = name.substring(0, name.length()-2);
		if ( ! name.equals(name.toLowerCase()) ) {
			String newName = "";
			for(String token: name.split(".")) {
				if ( token.equals(token.toLowerCase()) ) {
					newName = (newName.isEmpty() ? "" : ".") + token;
				} else {
					break;
				}
			}
			name = newName;
		}
		return name;
	}
	
	/**
	 * Add include package entry, it will filter only passed data from
	 * package list for filtering. Any partial package name will be evaluated as
	 * token for starts with that name on all packages entries.
	 * Any exclude package entry will be deleted.
	 * @param name name of package
	 * @return ({@link ClassPathConfigBuilder}) Builder for fluent interface
	 */
	public ClassPathConfigBuilder includePackageByName(String name) {
		if ( name != null && ! name.isEmpty() ) {
			name = fixPackageName(name);
			if ( ! packageInclusionList.contains(name) ) {
				packageExclusionList.clear();
				packageInclusionList.add(name);
			}
		}
		return this;
	}
	
	/**
	 * Add exclude package entry, it will filter all package but passed that
	 * package list matching to start with this name. Any partial package name 
	 * will be evaluated as token for starts with that name on all packages 
	 * entries.
	 * Any include package entry will be deleted.
	 * @param name name of package
	 * @return ({@link ClassPathConfigBuilder}) Builder for fluent interface
	 */
	public ClassPathConfigBuilder excludePackageByName(String name) {
		if ( name != null && ! name.isEmpty() ) {
			name = fixPackageName(name);
			if ( ! packageExclusionList.contains(name) ) {
				packageInclusionList.clear();
				packageExclusionList.add(name);
			}
		}
		return this;
	}
	
	/**
	 * Add exclude class path entry, it will filter all sources but data from
	 * class-path matches. Any partial resource name will be evaluated as
	 * token for partial or total match on the class-path entries.
	 * Any include class-path entry will be deleted.
	 * @param name name of JVM resource
	 * @return ({@link ClassPathConfigBuilder}) Builder for fluent interface
	 */
	public ClassPathConfigBuilder excludeClassPathEntryByName(String name) {
		if ( name != null && ! name.isEmpty() ) {
			if ( ! classPathExclusionList.contains(name) ) {
				classPathInclusionList.clear();
				classPathExclusionList.add(name);
			}
		}
		return this;
	}
	
	/**
	 * Add include class path entry, it will filter only passed data from
	 * class-path for filtering. Any partial resource name will be evaluated as
	 * token for partial or total match on the class-path entries.
	 * Any exclude class-path entry will be deleted.
	 * @param name name of JVM resource
	 * @return ({@link ClassPathConfigBuilder}) Builder for fluent interface
	 */
	public ClassPathConfigBuilder includeClassPathEntryByName(String name) {
		if ( name != null && ! name.isEmpty() ) {
			if ( ! classPathInclusionList.contains(name) ) {
				classPathExclusionList.clear();
				classPathInclusionList.add(name);
			}
		}
		return this;
	}
	
	/**
	 * Disable in-memory save of scanned entities for future scanning operations,
	 * so any further scanning operation will require a full java entries scan.
	 * @return ({@link ClassPathConfigBuilder}) Builder for fluent interface
	 */
	public ClassPathConfigBuilder disableSessionData() {
		this.enableSessionData = false;
		return this;
	}
	
	/**
	 * Build passed parameters and return {@link ClassPathConfig} element.
	 * If no inclusion or exclusion will be settled up, scanning will return
	 * entire list of classes present in the class-path entries, with meaningful
	 * decrease of performances.
	 * @return {@link ClassPathConfig} Configuration used to filter reflection classes scan
	 */
	public ClassPathConfig build() {
		return new ClassPathConfig(packageInclusionList, packageExclusionList, 
									classPathInclusionList, classPathExclusionList, 
									enableSessionData);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((classPathExclusionList == null) ? 0 : classPathExclusionList.hashCode());
		result = prime * result + ((classPathInclusionList == null) ? 0 : classPathInclusionList.hashCode());
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
		ClassPathConfigBuilder other = (ClassPathConfigBuilder) obj;
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
		return "ClassPathConfigBuilder [packageInclusionList=" + packageInclusionList + ", packageExclusionList="
				+ packageExclusionList + ", classPathInclusionList=" + classPathInclusionList
				+ ", classPathExclusionList=" + classPathExclusionList + "]";
	}
	
	

}
