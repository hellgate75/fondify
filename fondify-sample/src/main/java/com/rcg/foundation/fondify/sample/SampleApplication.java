/**
 * 
 */
package com.rcg.foundation.fondify.sample;


import java.util.Optional;

import com.rcg.foundation.fondify.annotations.AnnotationEngine;
import com.rcg.foundation.fondify.annotations.annotations.Application;
import com.rcg.foundation.fondify.annotations.annotations.Configuration;
import com.rcg.foundation.fondify.annotations.annotations.TransformCase;
import com.rcg.foundation.fondify.components.ComponentsManagerImpl;
import com.rcg.foundation.fondify.components.annotations.Component;
import com.rcg.foundation.fondify.components.annotations.Injectable;
import com.rcg.foundation.fondify.components.helpers.AnnotationHelper;
import com.rcg.foundation.fondify.core.domain.ApplicationType;
import com.rcg.foundation.fondify.core.domain.KeyCase;
import com.rcg.foundation.fondify.core.helpers.ArgumentsHelper;
import com.rcg.foundation.fondify.core.helpers.BeansHelper;
import com.rcg.foundation.fondify.core.helpers.GenericHelper;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;
import com.rcg.foundation.fondify.core.typings.AnnotationDeclaration;
import com.rcg.foundation.fondify.core.typings.lifecycle.ComponentManagerProvider;
import com.rcg.foundation.fondify.core.typings.lifecycle.ComponentsManager;
import com.rcg.foundation.fondify.sample.components.MyUnsignedInjectable;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
@Application(scope = ApplicationType.CONSOLE, hasCommandShell = true)
@Configuration(packages = {"com.rcg.foundation.fondify.sample.*", "com.rcg.foundation.fondify.sample.components.*"})
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
					SampleApplication.traceBeanDefinitions();
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
	
	protected static void traceBeanDefinitions() {
		ComponentsManagerImpl componentsManager = new ComponentsManagerImpl();

		LoggerHelper.logText("------------");
		LoggerHelper.logText("B E A N S : ");
		LoggerHelper.logText("------------");
		LoggerHelper.logText("");
		LoggerHelper.logText("");
		LoggerHelper.logText("-------------------------");
		LoggerHelper.logText("D E C L A R A T I O N S :");
		LoggerHelper.logText("-------------------------");
		LoggerHelper.logText("");
		LoggerHelper.logText("-------------------");
		LoggerHelper.logText("INJECTABLE BEANS : ");
		LoggerHelper.logText("-------------------");
		componentsManager
			.getInjectableBeanDefinitions()
			.forEach( beanDef -> {
				AnnotationDeclaration ad = beanDef.getDeclaration();
				LoggerHelper.logText("INJECTABLE BEAN NAME : " + AnnotationHelper.getClassBeanName(ad.getAnnotatedClass(), ad.getAnnotatedClass().getSimpleName()));
				LoggerHelper.logText("INJECTABLE BEAN CLASS : " + ad.getAnnotatedClass().getName());
			});
		componentsManager
		.getInjectableMethodDefinitions()
		.forEach( methodExec -> {
			AnnotationDeclaration ad = methodExec.getDescriptor();
			LoggerHelper.logText("INJECTABLE METHOD BEAN NAME : " + AnnotationHelper.getClassMethodBeanName(ad.getAnnotationMethod(), ad.getAnnotationMethod().getName()));
			LoggerHelper.logText("INJECTABLE METHOD BEAN CLASS : " + methodExec.getTargetClass().getName());
		});
		LoggerHelper.logText("");
		LoggerHelper.logText("------------------");
		LoggerHelper.logText("COMPONENT BEANS : ");
		LoggerHelper.logText("------------------");
		componentsManager
				.getComponentBeanDefinitions()
				.forEach( beanDef -> {
					AnnotationDeclaration ad = beanDef.getDeclaration();
					LoggerHelper.logText("COMPONENT BEAN NAME : " + AnnotationHelper.getClassBeanName(ad.getAnnotatedClass(), ad.getAnnotatedClass().getSimpleName()));
					LoggerHelper.logText("COMPONENT BEAN CLASS : " + ad.getAnnotatedClass().getName());
			});
		
		LoggerHelper.logText("");
		LoggerHelper.logText("");
		LoggerHelper.logText("-------------------");
		LoggerHelper.logText("I N S T A N C E S :");
		LoggerHelper.logText("-------------------");
		LoggerHelper.logText("");
		LoggerHelper.logText("-------------------");
		LoggerHelper.logText("INJECTABLE BEANS : ");
		LoggerHelper.logText("-------------------");
		componentsManager
		.getInjectableBeanReferences()
		.forEach( instance -> {
			Class<?> cls = instance.getClass();
			LoggerHelper.logText("INJECTABLE BEAN NAME : " + AnnotationHelper.getClassBeanName(cls, cls.getSimpleName()));
			LoggerHelper.logText("INJECTABLE BEAN CLASS : " + cls.getName());
			LoggerHelper.logText("INJECTABLE BEAN STRING IMAGE : " + instance);
		});
		LoggerHelper.logText("");
		LoggerHelper.logText("------------------");
		LoggerHelper.logText("COMPONENT BEANS : ");
		LoggerHelper.logText("------------------");
		componentsManager
		.getComponentsBeanReferences()
		.forEach( instance -> {
			Class<?> cls = instance.getClass();
			LoggerHelper.logText("COMPONENT BEAN NAME : " + AnnotationHelper.getClassBeanName(cls, cls.getSimpleName()));
			LoggerHelper.logText("COMPONENT BEAN CLASS : " + cls.getName());
			LoggerHelper.logText("COMPONENT BEAN STRING IMAGE : " + instance);
		});
	}
	
}
