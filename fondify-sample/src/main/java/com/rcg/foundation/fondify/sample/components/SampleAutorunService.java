/**
 * 
 */
package com.rcg.foundation.fondify.sample.components;

import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

import com.rcg.foundation.fondify.annotations.lifecycle.ApplicationContext;
import com.rcg.foundation.fondify.annotations.lifecycle.SessionContext;
import com.rcg.foundation.fondify.context.runtime.AutorunWithContext;
import com.rcg.foundation.fondify.core.typings.lifecycle.Session;
import com.rcg.foundation.fondify.utils.helpers.LoggerHelper;

/**
 * Sample Autorun service, to run this sample please execute the application
 * with the autorun activation parameter:
 * java -jar xxxxx.jar -enable.autorun=true -unlimited.autorun.threads=true
 * 
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class SampleAutorunService implements AutorunWithContext {

	private SessionContext sessionContext = null;

	private ApplicationContext applicationContext = null;

	private Session session = null;
	
	private UUID sessionUUID = null;
	
	/**
	 * 
	 */
	public SampleAutorunService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(Properties arguments) {
		String message1 = String.format("Current Arguments: %s",
										Arrays.toString(
											arguments
												.entrySet()
												.stream()
												.map(entry -> "" + entry.getKey() + "=" + entry.getValue())
												.collect(Collectors.toList())
												.toArray()
												)
												);
		LoggerHelper.logInfo("SampleAutorunService::run", message1);
		String message2 = String.format("Session Context: %s", ""+sessionContext);
		LoggerHelper.logInfo("SampleAutorunService::run", message2);
		String message3 = String.format("Application Context: %s", ""+applicationContext);
		LoggerHelper.logInfo("SampleAutorunService::run", message3);
		String message4 = String.format("Session: %s", ""+session);
		LoggerHelper.logInfo("SampleAutorunService::run", message4);
		String message5 = String.format("Session Id: %s", ""+sessionUUID);
		LoggerHelper.logInfo("SampleAutorunService::run", message5);
		if ( session != null ) {
			LoggerHelper.logInfo("SampleAutorunService::run", String.format("Session -> Id: %s", session.getSessionId()));
			LoggerHelper.logInfo("SampleAutorunService::run", String.format("Context -> Id: %s", sessionContext.getSessionId()));
			LoggerHelper.logInfo("SampleAutorunService::run", "WELCOME TO AUTORUN WITH CONTEXT -> TEST COMPLETE!!");
		} else {
			LoggerHelper.logInfo("SampleAutorunService::run", "WELCOME TO AUTORUN WITH CONTEXT -> TEST FAILED!!");
		}
	}

	@Override
	public void setContext(SessionContext context) {
		sessionContext = context;
	}

	@Override
	public void setApplicationContext(ApplicationContext context) {
		applicationContext = context;
	}

	@Override
	public void setSession(Session session) {
		this.session = session; 
	}

	@Override
	public void setSessionId(UUID sessionId) {
		sessionUUID = sessionId;
	}

	
}
