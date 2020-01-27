/**
 * 
 */
package com.rcg.foundation.fondify.annotations.lifecycle;

import java.util.Collection;
import java.util.UUID;

import com.rcg.foundation.fondify.core.typings.lifecycle.Session;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public interface ApplicationManager {
	/**
	 * Retrieve current {@link ApplicationContext}
	 * 
	 * @return Current {@link ApplicationContext}
	 */
	ApplicationContext getApplicationContext();
	
	/**
	 * Recover Session, from creation session {@link UUID}
	 * 
	 * @param sessionId required session {@link UUID}
	 * @return Provided {@link Session} or null, in case UUID is not part of registered session 
	 */
	public Session getSession(UUID sessionId);
	
	/**
	 * Recover Session Context, from creation session {@link UUID}
	 * 
	 * @return Provided {@link SessionContext} or null, in case thread is the ore registered the session 
	 */
	public SessionContext getSessionContext(UUID sessionId);

	
	/**
	 * Recover Session Context, from running thread
	 * 
	 * @return Provided {@link SessionContext} or null, in case thread is the ore registered the session 
	 */
	public SessionContext getSessionContext();

	/**
	 * Recover Session, from running thread
	 * 
	 * @return Provided {@link Session} or null, in case thread is the ore registered the session 
	 */
	public Session getSession();
	
	/**
	 * Create new Session and return the session {@link UUID}
	 * 
	 * @return Session {@link UUID}
	 */
	public UUID createNewSession();
	
	/**
	 * Retrieve current thread session id
	 * 
	 * @return {{@link UUID}} Current thread Session Identifier
	 */
	public UUID getCurrentSessionId();

	/**
	 * List all session unique identifiers
	 * @return <Collection<UUID>> Collection of registered session id
	 */
	public Collection<UUID> listSessionIds();
	
	/**
	 * Unregister required session
	 * @param sessionId Current session Id
	 */
	public void unregisterSession(UUID sessionId);

}
