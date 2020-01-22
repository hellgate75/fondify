/**
 * 
 */
package com.rcg.foundation.fondify.properties.annotations.executors;

import com.rcg.foundation.fondify.core.domain.PropertiesFormat;
import com.rcg.foundation.fondify.core.exceptions.IOException;
import com.rcg.foundation.fondify.core.exceptions.ProcessException;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;
import com.rcg.foundation.fondify.core.properties.BasePropertyManager;
import com.rcg.foundation.fondify.core.typings.AnnotationDeclaration;
import com.rcg.foundation.fondify.core.typings.AnnotationExecutor;
import com.rcg.foundation.fondify.core.typings.ExecutionAnswer;
import com.rcg.foundation.fondify.properties.annotations.PropertiesSet;
import com.rcg.foundation.fondify.properties.properties.SpringConfigServerPropertyManager;
import com.rcg.foundation.fondify.properties.properties.YamlPropertyManager;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class PropertiesSetExecutor implements AnnotationExecutor<PropertiesSet> {

	/**
	 * 
	 */
	public PropertiesSetExecutor() {
		super();
	}

	@Override
	public Class<? extends PropertiesSet> getAnnotationClass() {
		return PropertiesSet.class;
	}

	@Override
	public boolean containsResults() {
		return false;
	}

	@Override
	public String getComponentName() {
		return null;
	}

	@Override
	public ExecutionAnswer<PropertiesSet> executeAnnotation(AnnotationDeclaration t) throws ProcessException {
		String message = "";
		boolean warnings = false;
		PropertiesSet set = (PropertiesSet) t.getAnnotation();
		PropertiesFormat format = set.format();
		if ( format == null )
			format = PropertiesFormat.PROPERTIES;
		String[] propertiesArray = set.value();
		if ( propertiesArray != null && propertiesArray.length > 0 ) {
			for( String pi:propertiesArray ) {
				try {
					if ( pi.contains(":") ) {
						if ( pi.startsWith("spring-config-server:") || format == PropertiesFormat.SPRING_CLOUD ) {
							new SpringConfigServerPropertyManager().load(pi);
						} else if ( format == PropertiesFormat.PROPERTIES ) {
							new BasePropertyManager().load(pi);
						} else if ( format == PropertiesFormat.YAML ) {
							new YamlPropertyManager().load(pi);
						} else {
							String errMessage = String.format("Unable to identify executions of property ref: %s, format: %s", pi, format.name());
							LoggerHelper.logWarn("PropertiesSetExecutor::executeAnnotation", errMessage, null);
							warnings = true;
							message += (message.length() > 0 ? ", ": "") + errMessage;
						}
					} else {
						new BasePropertyManager().load(pi);
					}
				} catch (IOException e) {
					String errMessage = String.format("Error loading properties file: %s, format: %s", pi, format.name());
					LoggerHelper.logError("PropertiesSetExecutor::executeAnnotation", errMessage, e);
					warnings = true;
					message += (message.length() > 0 ? ", ": "") + errMessage;
				}
			}
		}
		return new ExecutionAnswer<>(PropertiesSet.class, message, warnings, false);
	}

}
