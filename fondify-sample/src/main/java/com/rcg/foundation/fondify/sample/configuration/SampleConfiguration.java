/**
 * 
 */
package com.rcg.foundation.fondify.sample.configuration;

import com.rcg.foundation.fondify.annotations.annotations.ComponentsScan;
import com.rcg.foundation.fondify.annotations.annotations.Configuration;
import com.rcg.foundation.fondify.annotations.annotations.TransformCase;
import com.rcg.foundation.fondify.components.annotations.Component;
import com.rcg.foundation.fondify.components.annotations.Injectable;
import com.rcg.foundation.fondify.core.domain.KeyCase;
import com.rcg.foundation.fondify.sample.components.MyUnsignedInjectable;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
@Configuration
@ComponentsScan(includes = {"com.rcg.foundation.fondify.sample.*", "com.rcg.foundation.fondify.sample.components.*"})
public class SampleConfiguration {

	
	@TransformCase(KeyCase.LOWER)
	@Injectable(component = @Component(""))
	public MyUnsignedInjectable myUnsingnedInjectableElement() {
		return new MyUnsignedInjectable();
	}

}
