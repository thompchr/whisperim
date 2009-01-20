package org.whisperim.client;


/**
 * This class implements the Singleton pattern and is used for
 * generating an id for the connection manager.
 * @author Chris Thompson
 *
 */
public class IDGenerator {

	private static IDGenerator instance_ = null;
	private int number = 0;
	
	private IDGenerator(){}
	
	public static IDGenerator getInstance(){
		if (instance_ == null){
			instance_ = new IDGenerator();
		}
		return instance_;
	}
	
	public int generateID(){
		return ++number;
	}
	
}
