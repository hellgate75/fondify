/**
 * 
 */
package com.rcg.foundation.fondify.sample.components;

import java.util.concurrent.atomic.AtomicLong;

import com.rcg.foundation.fondify.components.annotations.Component;
import com.rcg.foundation.fondify.components.annotations.Injectable;
import com.rcg.foundation.fondify.components.injecables.Service;
import com.rcg.foundation.fondify.core.domain.Scope;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
@Injectable(component = @Component("myInjectableElement"), scope = Scope.APPLICATION)
public class MyInjectable implements Service<Integer, Long> {

	AtomicLong counter = new AtomicLong(0L);
	/**
	 * 
	 */
	public MyInjectable() {
		super();
	}
	
	@Override
	public Long doAction(Integer request) {
		long next = counter.addAndGet(request);
		LoggerHelper.logInfo("MyInjectable::doAction", 
				String.format("Current value", ""+next));
		return next;
	}

}
