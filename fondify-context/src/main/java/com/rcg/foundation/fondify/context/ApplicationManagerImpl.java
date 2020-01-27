/**
 * 
 */
package com.rcg.foundation.fondify.context;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.rcg.foundation.fondify.annotations.helpers.AnnotationHelper;
import com.rcg.foundation.fondify.annotations.lifecycle.ApplicationContext;
import com.rcg.foundation.fondify.annotations.lifecycle.ApplicationManager;
import com.rcg.foundation.fondify.annotations.lifecycle.SessionContext;
import com.rcg.foundation.fondify.context.lifecycle.impl.ApplicationContextImpl;
import com.rcg.foundation.fondify.core.helpers.LoggerHelper;
import com.rcg.foundation.fondify.core.typings.lifecycle.Session;
import com.rcg.foundation.fondify.core.typings.lifecycle.SessionSetter;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ApplicationManagerImpl implements ApplicationManager {
	private static ApplicationManagerImpl instance = null;
	
	private static final ApplicationContext context = new ApplicationContextImpl();
	
	private Map<UUID, SessionContext> contextMap = null;
	
	private Map<UUID, Session> sessionMap = null;

	private Map<Long, UUID> threadSessionMap = null;
	
	/**
	 * Create New Application Manager
	 */
	private ApplicationManagerImpl() {
		LoggerHelper.logTrace("ApplicationManager::constructor", "Creating Application and Session Storage...");
		contextMap = new ConcurrentHashMap<>(0);
		sessionMap = new ConcurrentHashMap<>(0);
		threadSessionMap = new ConcurrentHashMap<>(0);
		LoggerHelper.logTrace("ApplicationManager::constructor", "Application and Session Storage created!!");
	}
	
	/**
	 * Retrieve current {@link ApplicationContext}
	 * 
	 * @return Current {@link ApplicationContext}
	 */
	@Override
	public ApplicationContext getApplicationContext() {
		return context;
	}
	
	/**
	 * Recover Session, from creation session {@link UUID}
	 * 
	 * @param sessionId required session {@link UUID}
	 * @return Provided {@link Session} or null, in case UUID is not part of registered session 
	 */
	@Override
	public Session getSession(UUID sessionId) {
		if ( sessionId == null ) {
			return null;
		}
		return sessionMap.get(sessionId);
	}

	
	/**
	 * Recover Session Context, from creation session {@link UUID}
	 * 
	 * @return Provided {@link SessionContext} or null, in case thread is the ore registered the session 
	 */
	@Override
	public SessionContext getSessionContext(UUID sessionId) {
		if ( sessionId == null ) {
			return null;
		}
		return contextMap.get(sessionId);
	}

	/**
	 * Recover Session Context, from running thread
	 * 
	 * @return Provided {@link SessionContext} or null, in case thread is the ore registered the session 
	 */
	@Override
	public SessionContext getSessionContext() {
		UUID sessionId = threadSessionMap.get(Thread.currentThread().getId());
		if ( sessionId == null ) {
			return null;
		}
		return contextMap.get(sessionId);
	}

	/**
	 * Recover Session, from running thread
	 * 
	 * @return Provided {@link Session} or null, in case thread is the ore registered the session 
	 */
	@Override
	public Session getSession() {
		UUID sessionId = threadSessionMap.get(Thread.currentThread().getId());
		if ( sessionId == null ) {
			return null;
		}
		return sessionMap.get(sessionId);
	}
	
	/**
	 * Create new Session and return the session {@link UUID}
	 * 
	 * @return Session {@link UUID}
	 */
	@Override
	public UUID createNewSession() {
		Optional<SessionContext> contextOpt = AnnotationHelper.getImplementedType(SessionContext.class);
		SessionContext context = null;
		if ( contextOpt.isPresent()) {
			context = contextOpt.get();
		} else {
			String message = "SessionContext implementation missing";
			LoggerHelper.logError("ApplicationManager::createSession", message, null);
			throw new IllegalStateException(message);
		}
		Optional<Session> sessionOpt = AnnotationHelper.getImplementedType(Session.class);;
		Session session = null;
		if ( sessionOpt.isPresent()) {
			session = sessionOpt.get();
		} else {
			String message = "Session implementation missing";
			LoggerHelper.logError("ApplicationManager::createSession", message, null);
			throw new IllegalStateException(message);
		}
		UUID uuid = context.getSessionId();
		if ( uuid == null )
			uuid = UUID.randomUUID();
		if ( SessionSetter.class.isAssignableFrom(session.getClass()) ) {
			((SessionSetter)session).setSessionId(uuid);
		}
		if ( SessionSetter.class.isAssignableFrom(context.getClass()) ) {
			((SessionSetter)context).setSessionId(uuid);
		}
		contextMap.put(uuid, context);
		sessionMap.put(uuid, session);
		threadSessionMap.put(Thread.currentThread().getId(), uuid);
		return uuid;
	}
	
	/**
	 * Retrieve current thread session id
	 * 
	 * @return {{@link UUID}} Current thread Session Identifier
	 */
	@Override
	public UUID getCurrentSessionId() {
		return threadSessionMap.get(Thread.currentThread().getId());
	}

	/**
	 * Retrieve current {@link ApplicationManagerImpl} instance
	 * 
	 * @return <{@link ApplicationManagerImpl}> current instance
	 */
	public static synchronized final ApplicationManagerImpl getInstance() {
		if ( instance == null ) {
			instance = new ApplicationManagerImpl();
		}
		return instance;
	}
	
	/**
	 * List all session unique identifiers
	 * @return <Collection<UUID>> Collection of registered session id
	 */
	@Override
	public Collection<UUID> listSessionIds() {
		return contextMap.keySet();
	}
	
	/**
	 * Unregister required session
	 * @param sessionId Current session Id
	 */
	@Override
	public void unregisterSession(UUID sessionId) {
		contextMap.remove(sessionId);
		threadSessionMap
			.entrySet()
			.stream()
			.filter( entry -> entry.getValue().compareTo(sessionId) == 0 )
			.map( entry -> entry.getKey() )
			.forEach(threadSessionMap::remove);
	}
	
}
