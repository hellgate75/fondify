/**
 * 
 */
package com.rcg.foundation.fondify.components.loaders.typings;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
@XmlRootElement(name="bean")
@JsonRootName("bean")
public class Bean {

	/**
	 * Public Default Constructor
	 */
	public Bean() {
		super();
	}

}
