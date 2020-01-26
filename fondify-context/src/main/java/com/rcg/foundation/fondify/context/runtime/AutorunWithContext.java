/**
 * 
 */
package com.rcg.foundation.fondify.context.runtime;

import com.rcg.foundation.fondify.annotations.lifecycle.ApplicationContext;
import com.rcg.foundation.fondify.annotations.lifecycle.SessionContext;
import com.rcg.foundation.fondify.core.typings.autorun.Autorun;
import com.rcg.foundation.fondify.core.typings.lifecycle.Session;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 * @see Autorun
 */
public interface AutorunWithContext extends Autorun {
	
	void setContext(SessionContext context);
	
	void setApplicationContext(ApplicationContext context);
	
	void setSession(Session session);
}
