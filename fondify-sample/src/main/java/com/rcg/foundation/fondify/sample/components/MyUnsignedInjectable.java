/**
 * 
 */
package com.rcg.foundation.fondify.sample.components;

import com.rcg.foundation.fondify.components.annotations.Autowired;
import com.rcg.foundation.fondify.components.injecables.Service;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;

/**
 * Manual injectable element, with autowired autiomated 
 * injectable service 
 * 
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class MyUnsignedInjectable implements Service<Integer, Long> {

	@Autowired(name = "myInjectableElement")
	private MyInjectable myInjectableElement;

	/**
	 * 
	 */
	public MyUnsignedInjectable() {
		super();
	}
	
	@Override
	public Long doAction(Integer request) {
		long next = myInjectableElement.doAction(request);
		LoggerHelper.logInfo("MyUnsignedInjectable::doAction", 
				String.format("Current value", ""+next));
		return next;
	}

}
