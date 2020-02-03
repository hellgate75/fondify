/**
 * 
 */
package com.rcg.foundation.fondify.utils.process;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Class that allows elements to lock the application in checkpoints, 
 * in order to manage Multi-Threaded processes or Waiting for synchronous external
 * processes.
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class GlobalProcessTracker {
	private static GlobalProcessTracker instance = null;

	private Queue<UUID> activeProcesses = new ConcurrentLinkedQueue<UUID>();

	/**
	 * Default Constructor
	 */
	private GlobalProcessTracker() {
		super();
	}
	
	/**
	 * Lock a process and collect unlock {@link UUID}.
	 * @return process unlock {@link UUID}
	 */
	public final UUID lockProcess() {
		UUID uuid = UUID.randomUUID();
		if ( activeProcesses.offer(uuid) )
			return uuid;
		return null;
	}
	
	/**
	 * Release a process lock using {@link UUID} given during
	 * lock phase.
	 * @param registrationUUID Given registered lock {@link UUID}
	 * @return (boolean) unlock success state
	 */
	public final boolean releaseProcess(UUID registrationUUID) {
		if ( registrationUUID != null && activeProcesses.contains(registrationUUID)) {
			return activeProcesses.remove(registrationUUID);
		}
		return false;
	}
	
	/**
	 * Verify presence of lock for a given {@link UUID}
	 * @param registrationUUID Given registered lock {@link UUID}
	 * @return (boolean) lock state
	 */
	public final boolean isLocked(UUID registrationUUID) {
		if ( registrationUUID != null) {
			return activeProcesses.contains(registrationUUID);
		}
		return false;
	}

	/**
	 * Requires to {@link GlobalProcessTracker} the number of active locks 
	 * @return (int) Number of active locks
	 */
	public final synchronized int numberOfActiveLocks() {
		return activeProcesses.size();
	}
	
	/**
	 * Get Singleton instance of the {@link GlobalProcessTracker}. 
	 * @return Singleton instance of the {@link GlobalProcessTracker}
	 */
	public static synchronized final GlobalProcessTracker getInstance() {
		if ( instance == null ) { 
			instance = new GlobalProcessTracker();
		}
		return instance;
	}

}
