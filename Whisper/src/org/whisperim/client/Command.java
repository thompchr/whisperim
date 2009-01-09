/**
 * 
 */
package org.whisperim.client;

/**
 * @author Chris Thompson
 *
 * Interface used to pass messages from the each session thread to the main class
 */
public interface Command {
	
	public void execute();

}
