/**
 * 
 */
package com.rcg.foundation.fondify.sample;


import java.util.Optional;

import com.rcg.foundation.fondify.annotations.AnnotationEngine;
import com.rcg.foundation.fondify.annotations.annotations.Application;
import com.rcg.foundation.fondify.annotations.annotations.ComponentsScan;
import com.rcg.foundation.fondify.annotations.annotations.Configuration;
import com.rcg.foundation.fondify.annotations.annotations.TransformCase;
import com.rcg.foundation.fondify.components.annotations.Component;
import com.rcg.foundation.fondify.components.annotations.Injectable;
import com.rcg.foundation.fondify.components.helpers.ComponentsHelper;
import com.rcg.foundation.fondify.core.domain.ApplicationType;
import com.rcg.foundation.fondify.core.domain.KeyCase;
import com.rcg.foundation.fondify.core.helpers.BeansHelper;
import com.rcg.foundation.fondify.core.typings.components.ComponentManagerProvider;
import com.rcg.foundation.fondify.core.typings.lifecycle.ComponentsManager;
import com.rcg.foundation.fondify.sample.components.MyUnsignedInjectable;
import com.rcg.foundation.fondify.utils.helpers.ArgumentsHelper;
import com.rcg.foundation.fondify.utils.helpers.GenericHelper;
import com.rcg.foundation.fondify.utils.helpers.LoggerHelper;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
@Application(scope = ApplicationType.CONSOLE, hasCommandShell = true)
@Configuration()
@ComponentsScan(includes = {"com.rcg.foundation.fondify.sample.*", "com.rcg.foundation.fondify.sample.components.*"})
public class SampleApplication {

	public static final void main(String[] arguments) throws Exception {
		AnnotationEngine.run(SampleApplication.class, null, new Runnable() {
			
			@Override
			public void run() {
				
				Optional<ComponentManagerProvider> providerOpt = BeansHelper.getImplementedType(ComponentManagerProvider.class);
				
				ComponentsManager manager = null;
				
				if ( providerOpt.isPresent() ) {
					manager = providerOpt.get().getComponentManager();
				}
				
				if ( manager!=null ) {
					// Register custom bean for local application use
					manager.registerComponent("arguments", ArgumentsHelper.getArguments());
					manager.registerComponent("numericIncrement", new Integer(1));
				}
				
				if ( ArgumentsHelper.debug ) {
					ComponentsHelper.traceBeanDefinitions();
				}
			}
		}, arguments);
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
