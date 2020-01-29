/**
 * 
 */
package com.rcg.foundation.fondify.sample.components;

import java.util.ArrayList;
import java.util.List;

import com.rcg.foundation.fondify.components.annotations.Autowired;
import com.rcg.foundation.fondify.components.annotations.Inject;
import com.rcg.foundation.fondify.components.injecables.Executable;
import com.rcg.foundation.fondify.core.exceptions.ProcessException;
import com.rcg.foundation.fondify.core.helpers.GenericHelper;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;
import com.rcg.foundation.fondify.core.listeners.AsyncExecutionListener;
import com.rcg.foundation.fondify.core.listeners.typings.InjectableExecutionResponse;

/**
 * Manual injectable element, with autowired autiomated 
 * injectable service 
 * 
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class MyUnsignedInjectable implements Executable<Integer, Long> {

	@Autowired(name = "myInjectableElement")
	private MyInjectable myInjectableElement;
	
	private List<AsyncExecutionListener<Object, InjectableExecutionResponse>> listenersList = new ArrayList<>(0);

	/**
	 * 
	 */
	public MyUnsignedInjectable() {
		super();
	}
	
	@Override
	public Long doAction(@Inject(name = "numericIncrement") Integer request) {
		try {
			long next = myInjectableElement.doAction(request);
			LoggerHelper.logInfo("MyUnsignedInjectable::doAction", 
					String.format("Current value: %s", ""+next));
			listenersList.forEach(listener -> listener.reportSuccess(request, new InjectableExecutionResponse(true, false, "")));
			return next;
		} catch (Exception e) {
			listenersList.forEach(listener -> listener.reportFailure(request, new InjectableExecutionResponse(false, false, GenericHelper.convertStackTrace(e.getStackTrace()))));
			throw new ProcessException("Unable to complete service task due to ERRORS", e);
		}
	}

	@Override
	public void add(AsyncExecutionListener<Object, InjectableExecutionResponse> listener) {
		if ( listener != null && ! listenersList.contains(listener) )
			listenersList.add(listener);
	}

}
