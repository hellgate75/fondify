/**
 * 
 */
package com.rcg.foundation.fondify.core.typings.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class CacheChangeEvent {
	
	private Map<String, Map<String, Object>> changeMap = new HashMap<String, Map<String,Object>>(0);

	private CacheEventType eventType = CacheEventType.NO_EVENT;

	/**
	 * @param changeMap
	 * @param eventType
	 */
	public CacheChangeEvent(Map<String, Map<String, Object>> changeMap, CacheEventType eventType) {
		super();
		this.changeMap = changeMap;
		this.eventType = eventType;
	}

	/**
	 * @return the changeMap
	 */
	public Map<String, Map<String, Object>> getChangeMap() {
		return changeMap;
	}

	/**
	 * @param changeMap the changeMap to set
	 */
	public void setChangeMap(Map<String, Map<String, Object>> changeMap) {
		this.changeMap = changeMap;
	}

	/**
	 * @return the eventType
	 */
	public CacheEventType getEventType() {
		return eventType;
	}

	/**
	 * @param eventType the eventType to set
	 */
	public void setEventType(CacheEventType eventType) {
		this.eventType = eventType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((changeMap == null) ? 0 : changeMap.hashCode());
		result = prime * result + ((eventType == null) ? 0 : eventType.hashCode());
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
		CacheChangeEvent other = (CacheChangeEvent) obj;
		if (changeMap == null) {
			if (other.changeMap != null)
				return false;
		} else if (!changeMap.equals(other.changeMap))
			return false;
		if (eventType != other.eventType)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CacheChangeEvent [changeMap=" + changeMap + ", eventType=" + eventType + "]";
	}
	

}
