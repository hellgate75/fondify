/**
 * 
 */
package com.rcg.foundation.fondify.core.typings.runtime;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class ProcessStateTracker {
	
	private static ProcessStateTracker instance = null;
	
	private final ConcurrentLinkedQueue<ProcessStateReference>
		queue = new ConcurrentLinkedQueue<>();

	/**
	 * 
	 */
	private ProcessStateTracker() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Register new execution state reference, in order to put under track the 
	 * process execution state and provided and overall process state execution
	 * capability for any thread feature.
	 * @param ref
	 */
	public void registerNewProcessStateReference(ProcessStateReference ref) {
		if ( ref != null && ! queue.contains(ref) ) {
			queue.offer(ref);
		}
	}
	
	/**
	 * Retrieve is any of the state references are still active.
	 * 
	 * @return (boolean) registered process sctive state 
	 */
	public synchronized boolean isInWaitState() {
		return queue
				.stream()
				.map( ref -> ref.isRunning() )
				.filter( state -> state )
				.count() > 0;
	}

	/**
	 * Retrieve current singleton element instance
	 * 
	 * @return ({@link ProcessStateTracker}) is the current element instance
	 */
	public static synchronized final ProcessStateTracker getInstance() {
		if ( instance == null ) {
			instance = new ProcessStateTracker();
		}
		return instance;
	}
}
