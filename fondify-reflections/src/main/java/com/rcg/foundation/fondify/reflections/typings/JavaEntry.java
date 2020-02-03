/**
 * 
 */
package com.rcg.foundation.fondify.reflections.typings;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class JavaEntry {
	private String origin;
	private String fileName;
	private String className;
	private String packageName;
	private String content = null;
	private long fileSize;

	/**
	 * @param origin
	 * @param fileName
	 * @param fileSize
	 */
	public JavaEntry(String origin, String fileName, String className, long fileSize) {
		super();
		this.origin = origin;
		this.fileName = fileName;
		this.className = className;
		this.packageName = className;
		while (this.packageName!= null && ! this.packageName.isEmpty() &&
				! this.packageName.equals(this.packageName.toLowerCase()) &&
				this.packageName.contains(".")) {
			this.packageName = this.packageName.substring(0, this.packageName.lastIndexOf("."));
		}
		this.fileSize = fileSize;
	}

	/**
	 * @return the origin
	 */
	public String getOrigin() {
		return origin;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @return the packageName
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * @return the fileSize
	 */
	public long getFileSize() {
		return fileSize;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + (int) (fileSize ^ (fileSize >>> 32));
		result = prime * result + ((origin == null) ? 0 : origin.hashCode());
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
		JavaEntry other = (JavaEntry) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (fileSize != other.fileSize)
			return false;
		if (origin == null) {
			if (other.origin != null)
				return false;
		} else if (!origin.equals(other.origin))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "JavaEntry [origin=" + origin + ", fileName=" + fileName + ", className=" + className + ", fileSize=" + fileSize + 
				", content=" + content + "]";
	}

	public JavaClassEntity getClassEntity(ClassLoader... classLoaders) {
		return new JavaClassEntity(this, classLoaders);
	}

}
