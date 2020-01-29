/**
 * 
 */
package com.rcg.foundation.fondify.sample.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.rcg.foundation.fondify.annotations.annotations.TransformCase;
import com.rcg.foundation.fondify.components.annotations.Component;
import com.rcg.foundation.fondify.components.annotations.Inject;
import com.rcg.foundation.fondify.components.annotations.Injectable;
import com.rcg.foundation.fondify.components.injecables.Service;
import com.rcg.foundation.fondify.core.domain.KeyCase;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;
import com.rcg.foundation.fondify.core.listeners.AsyncExecutionListener;
import com.rcg.foundation.fondify.core.listeners.typings.InjectableExecutionResponse;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
@TransformCase(KeyCase.LOWER)
@Injectable(component = @Component(""))
public class PrintArgumentsPerLineInjectable implements Service<Properties> {
	List<AsyncExecutionListener<Object, InjectableExecutionResponse>> listenersList = new ArrayList<>(0);
	@Override
	public void doService(@Inject(name = "arguments") Properties arguments) {
		try {
			LoggerHelper.logTrace("PrintArgumentsPerLineInjectable::doService", "Initializing service...");
			arguments.entrySet().forEach( entry -> {
				LoggerHelper.logInfo("PrintArgumentsPerLineInjectable::doService", String.format("Argument -> %s=%s", ""+entry.getKey(), ""+entry.getValue()));
			});
			listenersList.forEach(listener -> listener.reportSuccess(arguments, new InjectableExecutionResponse(true, false, "")));
		} catch (Exception e) {
			LoggerHelper.logError("PrintArgumentsPerLineInjectable::doService", "Unable to execute " + this.getClass().getName() + " sue to ERRORS!!", e);
		}
		LoggerHelper.logTrace("PrintArgumentsPerLineInjectable::doService", "Service action provided!!");
	}

	@Override
	public void add(AsyncExecutionListener<Object, InjectableExecutionResponse> listener) {
		if ( listener != null && ! listenersList.contains(listener) )
			listenersList.add(listener);
	}

	
}
