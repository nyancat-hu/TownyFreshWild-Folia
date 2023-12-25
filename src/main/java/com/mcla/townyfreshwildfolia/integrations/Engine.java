package com.mcla.townyfreshwildfolia.integrations;

import org.bukkit.Chunk;
import org.bukkit.event.Listener;

public abstract class Engine implements Listener {

	// ----------------------------------------
	// ABSTRACT METHODS
	// ----------------------------------------
	
	/**
	 * Should we reset the chunk that is already in the log
	 * @param chunk to reset
	 * @return false to cancel, true to continue
	 */
	public abstract Boolean shouldReset(Chunk chunk);
	
	/**
	 * Called after shouldReset on all integrations is checked 
	 * @param chunk being reset
	 * @return false to cancel, true to continue
	 */
	public abstract Boolean runReset(Chunk chunk);
	
	/**
	 * Should a chunk be logged 
	 * @param chunk to log
	 * @return false to cancel, true to continue
	 */
	public abstract Boolean shouldLogAt(Chunk chunk);
	
}
