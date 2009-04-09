 /**************************************************************************
 * Copyright 2009 John Dlugokecki                                         *
 *                                                                         *
 * Licensed under the Apache License, Version 2.0 (the "License");         *
 * you may not use this file except in compliance with the License.        *
 * You may obtain a copy of the License at                                 *
 *                                                                         *
 * http://www.apache.org/licenses/LICENSE-2.0                              *
 *                                                                         *
 * Unless required by applicable law or agreed to in writing, software     *
 * distributed under the License is distributed on an "AS IS" BASIS,       *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.*
 * See the License for the specific language governing permissions and     *
 * limitations under the License.                                          *
 **************************************************************************/

package org.whisperim.client;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.text.BadLocationException;

import org.whisperim.plugins.Plugin;
import org.whisperim.ui.WhisperClient;

/**
 * This class manages connections and ties them to local buddy handles. 
 * This allows for multiple handles on the same service running concurrently.
 * This class makes use of the IDGenerator singleton to generate a unique id.
 * @author Chris Thompson
 *
 */
public class ConnectionManager {

	/**
	 * This section defines private member variables. 
	 * Each title holds data for the session connection.
	 */
	
	
	private WhisperClient client_ = null;
	private PrivateKey myPrivateKey_;
	private PublicKey myPublicKey_;
	
	public static final int INVISIBLE = 0;
	public static final int AVAILABLE = 1;
	public static final int IDLE = 2;
	public static final int AWAY = 3;

	/**
	 * This data member holds currently active sessions.
	 */
	private HashMap<String, ConnectionStrategy> strategies_ = new HashMap<String, ConnectionStrategy>();

	
	/**
	 * This data member will serve as a registry for the connection strategies that
	 * are available, but potentially not loaded.
	 */
	private HashMap<String, ConnectionStrategy> registeredStrategies_ = new HashMap<String, ConnectionStrategy> ();
	
	/**
	 * This method registers a class that implements the ConnectionStrategy interface
	 * @param cs - Class<ConnectionStrategy> The strategy to be 
	 * @param name -  Name of the connection type (AOL, GTalk, etc.) to be 
	 * 					displayed when the "New Account" window is displayed.
	 */
	public void registerConnection(String name, Plugin p){
		registeredStrategies_.put(name, (ConnectionStrategy) p);
	}
	/**
	 * Returns true if strategies_ is empty.
	 */
	public boolean isStrategiesEmpty()
	{
		return (strategies_.isEmpty());
	}
	
	/**
	 * Returns strategies from Hashmap strategies_.
	 * @param strategy is the strategy title to be retrieved from strategies_. 
	 */
	public ConnectionStrategy getStrategy(String strategy)
	{
		return strategies_.get(strategy);
	}

	/**
	 * Removes strategies from Hashmap strategies_.
	 * @param strategy is the strategy title to be removed from strategies_. 
	 */
	public void removeStrategy(String strategy)
	{
		if(!strategy.isEmpty())
		{
			strategies_.remove(strategy);
		}else{
			System.out.println("Error: Hashmap is already empty.");
		}
	}

	/**
	 * Adds strategies to Hashmap strategies_.
	 * @param 
	 * 		strategy - strategy object to be added to the collection. 
	 * @param 
	 * 		name - the unique identifier for the strategy. 
	 */
	public void addStrategy(ConnectionStrategy strategy)
	{
		strategies_.put(strategy.getIdentifier(), strategy);
	}
	
	/**
	 * Loads a connection and opens the connection based on the
	 * strategy initially defined. This method is used to
	 * make a new connection utilizing a predefined protocol.
	 * @param name - Identifier originally used when the connection was stored
	 * @param username - Username for the account associated with that connection
	 * @param password - Password for the account
	 */
	public void loadConnection(String name, String username, String password){
		ConnectionStrategy cs = registeredStrategies_.get(name);
		cs.signOn(this, username, password);
		strategies_.put(cs.getIdentifier(), cs);
	}

	/**
	 * This method is used to retrieve the entire list of currently
	 * registered ConnectionStrategy objects and allows for the calling
	 * object to determine which one to open a new connection with.
	 * 
	 * @return HashMap<String, ConnectionStrategy>
	 */
	public HashMap<String, ConnectionStrategy> getRegisteredStrategies(){
		return registeredStrategies_;
	}
	
	/**
	 * Constructor for Connection Manager.
	 */
	public ConnectionManager(KeyPair kp){
		myPrivateKey_ = kp.getPrivate();
		myPublicKey_ = kp.getPublic();

	}


	/**
	 * Set client_ to specified chat client
	 * @param client -  The current WhisperClient object. 
	 */
	public void setClient(WhisperClient client){
		client_ = client;
	}

	/**
	 * Action taken when message is received from client.
	 * 
	 * @param message - The message object constructed by the
	 * 					session thread from the message received
	 * 					from the service.
	 * @throws BadLocationException 
	 */
	public void messageReceived(Message message) throws BadLocationException{
		client_.recieveMessage(message);

	}

	/**
	 * This method allows the strategy to notify the ConnectionManager
	 * of changes in status in the running sessions.  
	 * @param status - The String indicating the new status
	 * @param account - The account the status change was associated
	 * 					with.
	 */
	public void statusUpdate (String status, String account){
		client_.statusUpdate(status, account);
	}

	/**
	 * Updates buddy list when buddy shows up.
	 */
	public void receiveBuddies(ArrayList<Buddy> buddies){
		client_.updateBuddyList(buddies);
	}

	/**
	 * Action to send message.
	 */
	public void sendMessage(Message message){
		strategies_.get(message.getProtocol() + ":" + message.getFrom().toLowerCase().replace(" ",""))
					.sendMessage(message);
	}
	
	/**
	 * Sign off all connections.
	 * Used when the application terminates.
	 */
	public void signOff(){
		 for (Entry<String, ConnectionStrategy> entry:strategies_.entrySet()){
			 entry.getValue().signOff();
		 }
	}
	

	/**
	 * This method signs off a specific session.
	 * @param 
	 * 		handle - The local username
	 * @param 
	 * 		protocol - The protocol identifier
	 */
	public void signOff(String handle, String protocol){
		strategies_.get(protocol + ":" + handle).signOff();
		
		//Right now this is a problem because in theory, the ui could reflect something that 
		//doesn't exist in the running code
		//client_.signOffSN(protocol, handle);
	}
	
	public HashMap<String, ConnectionStrategy> getStrategies(){
		return strategies_;
	}

	/**
	 * Get Private key for encryption.
	 */
	public PrivateKey getPrivateKey(){
		return myPrivateKey_;
	}

	/**
	 *  Get public key for encryption.
	 */
	public PublicKey getPublicKey(){
		return myPublicKey_;
	}

	/**
	 *  Set status to away.
	 */
	 public void setAwayMessage(String message){
		 for (Entry<String, ConnectionStrategy> entry:strategies_.entrySet()){
			 entry.getValue().setAwayMessage(message);
		 }
	 }
	 
	 public void setStatusMessage(String message){
		 for (Entry<String, ConnectionStrategy> entry:strategies_.entrySet()){
			 entry.getValue().setStatusMessage(message);
		 }
	 }
	 
     public void setState(int newState){
         
     }
}
