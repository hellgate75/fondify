/**
 * 
 */
package com.rcg.foundation.fondify.sample;


import com.rcg.foundation.fondify.annotations.AnnotationEngine;
import com.rcg.foundation.fondify.annotations.annotations.Application;
import com.rcg.foundation.fondify.annotations.annotations.Configuration;
import com.rcg.foundation.fondify.annotations.annotations.TransformCase;
import com.rcg.foundation.fondify.components.annotations.Injectable;
import com.rcg.foundation.fondify.components.annotations.Component;
import com.rcg.foundation.fondify.core.domain.ApplicationType;
import com.rcg.foundation.fondify.core.domain.KeyCase;
import com.rcg.foundation.fondify.core.helpers.GenericHelper;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;
import com.rcg.foundation.fondify.sample.components.MyUnsignedInjectable;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
@Application(scope = ApplicationType.CONSOLE, hasCommandShell = true)
@Configuration(packages = {"com.rcg.foundation.fondify.sample.*", "com.rcg.foundation.fondify.sample.components.*"})
public class SampleApplication {

	public static final void main(String[] arguments) throws Exception {
		AnnotationEngine.run(SampleApplication.class, null, null, arguments);
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			
			@Override
			public void run() {
				LoggerHelper.logInfo("SampleApplication::start", 
						String.format("Application started in %s second(s)!!!", 
								GenericHelper.doubleStringPrecision(AnnotationEngine.getAnnotationEngineExecutionElapsedTimeSeconds(), 2)) );
			}
		}));
	}
	
	// Test manual injectable declaration into the configuration
	// This injectable class has an autowired injectable service 
	@TransformCase(KeyCase.LOWER)
	@Injectable(component = @Component(""))
	public MyUnsignedInjectable myUnsingnedInjectableElement() {
		return new MyUnsignedInjectable();
	}
	
}
