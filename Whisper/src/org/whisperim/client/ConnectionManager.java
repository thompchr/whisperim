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

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.whisperim.aim.AIMStrategy;

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
	
	private Login login_;
	private WhisperClient client_ = null;
	private PrivateKey myPrivateKey_;
	private PublicKey myPublicKey_;


	public static final int AIM_SESSION = 0; 

	/**
	 * This data member holds currently active sessions.
	 */
	private HashMap<String, ConnectionStrategy> strategies_ = new HashMap<String, ConnectionStrategy>();

	
	/**
	 * This data member will serve as a registry for the connection strategies that
	 * are available, but potentially not loaded.
	 */
	private HashMap<String, Class<ConnectionStrategy> > registeredStrategies_ = new HashMap<String, Class<ConnectionStrategy> > ();
	
	/**
	 * This method registers a class that implements the ConnectionStrategy interface
	 * @param cs - Class<ConnectionStrategy> The strategy to be 
	 * @param name -  Name of the connection type (AOL, GTalk, etc.) to be 
	 * 					displayed when the "New Account" window is displayed.
	 */
	public void registerConnection(String name, Class<ConnectionStrategy> cs){
		registeredStrategies_.put(name, cs);
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
	 * Constructor for Connection Manager.
	 */
	public ConnectionManager(int strategy, String handle, String password, Login login, PrivateKey privKey, PublicKey pubKey) throws IllegalArgumentException{
		if (handle == null){
			throw new IllegalArgumentException();
		}

		ConnectionStrategy cs;
		// Switch statement for strategies. 
		switch(strategy){
			case 0:
			{
				cs = new AIMStrategy(this, handle);
				strategies_.put(cs.getIdentifier(), cs);
				break;
			}
			default:
			{
				System.err.println("Error: No such strategy.");
				cs = null;
				break;
			}
		}

		myPrivateKey_ = privKey;
		myPublicKey_ = pubKey;
		login_ = login;
		cs.signOn(handle, password);

	}


	/**
	 * Set client_ to specified chat client
	 * @param client is the chat client. 
	 */
	public void setClient(WhisperClient client){
		client_ = client;
	}

	/**
	 * Action taken when message is received from client.
	 */
	public void messageReceived(Message message){
		//System.out.println("Received message: " + message.getMessage());
		client_.recieveMessage(message);

	}

	/**
	 * Updates status.
	 */
	public void statusUpdate (String status){
		login_.statusUpdate(status);
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
		System.out.println("Sending message: " + message.getMessage());
		String identifier = message.getProtocol() + ":" + message.getFrom().toLowerCase().replace(" ","");
		strategies_.get(identifier).sendMessage(message);
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
	 public void setAwayMessage(String message, boolean away){
		 for (Entry<String, ConnectionStrategy> entry:strategies_.entrySet()){
			 entry.getValue().setAwayMessage(message, away);
		 }
		 
	 }


}
