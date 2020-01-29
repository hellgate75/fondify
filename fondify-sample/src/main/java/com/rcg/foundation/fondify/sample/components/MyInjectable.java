/**
 * 
 */
package com.rcg.foundation.fondify.sample.components;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.rcg.foundation.fondify.components.annotations.Component;
import com.rcg.foundation.fondify.components.annotations.Inject;
import com.rcg.foundation.fondify.components.annotations.Injectable;
import com.rcg.foundation.fondify.components.injecables.Executable;
import com.rcg.foundation.fondify.core.domain.Scope;
import com.rcg.foundation.fondify.core.exceptions.ProcessException;
import com.rcg.foundation.fondify.core.helpers.GenericHelper;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;
import com.rcg.foundation.fondify.core.listeners.AsyncExecutionListener;
import com.rcg.foundation.fondify.core.listeners.typings.InjectableExecutionResponse;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
@Injectable(component = @Component("myInjectableElement"), scope = Scope.APPLICATION)
public class MyInjectable implements Executable<Integer, Long> {

	AtomicLong counter = new AtomicLong(0L);
	
	private List<AsyncExecutionListener<Object, InjectableExecutionResponse>> listenersList = new ArrayList<>(0);

	/**
	 * 
	 */
	public MyInjectable() {
		super();
	}
	
	@Override
	public Long doAction(@Inject(name = "numericIncrement") Integer request) {
		try {
			long next = counter.addAndGet(request);
			LoggerHelper.logInfo("MyInjectable::doAction", 
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
