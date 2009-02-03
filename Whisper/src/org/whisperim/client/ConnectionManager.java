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

/**
 * This class manages connections and ties them to local buddy handles. 
 * This allows for multiple handles on the same service running concurrently.
 * This class makes use of the IDGenerator singleton to generate a unique id.
 * @author Chris Thompson
 *
 */
public class ConnectionManager {

	/*
	 * This section defines private member variables. 
	 * Each title holds data for the session connection.
	 */
	private ConnectionStrategy strategy_;
	private String localHandle_;
	private Login login_;
	private WhisperClient client_ = null;
	private PrivateKey myPrivateKey_;
	private PublicKey myPublicKey_;


	public static final int AIM_SESSION = 0; 

	/*
	 * Hashmap strategies_ holds session strategies.
	 */
	private HashMap<String, ConnectionStrategy> strategies_ = new HashMap<String, ConnectionStrategy>();

	/*
	 * Returns true if Hashmap strategies_ is empty.
	 */
	public boolean isHashEmpty()
	{
		return (strategies_.isEmpty());
	}
	
	/*
	 * Returns strategies from Hashmap strategies_.
	 * @param strategy is the strategy title to be retrieved from strategies_. 
	 */
	public ConnectionStrategy getStrategy(String strategy)
	{
		return strategies_.get(strategy);
	}

	/*
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

	/*
	 * Adds strategies to Hashmap strategies_.
	 * @param strategy is the strategy title to be added to strategies_.
	 * @param is the key for strategies_. 
	 */
	public void addToMap(String name, ConnectionStrategy strategy)
	{
		strategies_.put(name, strategy);
	}

	/*
	 * Constructor for Connection Manager.
	 */
	public ConnectionManager(int strategy, String handle, String password, Login login, PrivateKey privKey, PublicKey pubKey) throws IllegalArgumentException{
		if (handle == null){
			throw new IllegalArgumentException();
		}


		// Switch statement for strategies. 
		switch(strategy){
			case 0: 
			{
				System.out.println("Error: No strategy found!");
			}
			case 1:
			{
				strategy_ = new AIMStrategy(this);
			}
		}

		myPrivateKey_ = privKey;
		myPublicKey_ = pubKey;
		login_ = login;
		localHandle_ = handle;
		strategy_.signOn(handle, password);

	}

	/*
	 * Returns strategy_.
	 */
	public ConnectionStrategy getStrategy(){
		return strategy_;
	}

	/*
	 * Set client_ to specified chat client
	 * @param client is the chat client. 
	 */
	public void setClient(WhisperClient client){
		client_ = client;
	}

	/*
	 * Return localHandle_.
	 */
	public String getHandle(){
		return localHandle_;
	}

	/*
	 * Action taken when message is received from client.
	 */
	public void messageReceived(Message message){
		System.out.println("Received message: " + message.getMessage());
		client_.recieveMessage(message);

	}

	/*
	 * Updates status.
	 */
	public void statusUpdate (String status){
		login_.statusUpdate(status);
	}

	/*
	 * Updates buddy list when buddy shows up.
	 */
	public void receiveBuddies(ArrayList<String> buddies){
		client_.updateBuddyList(buddies);
	}

	/*
	 * Action to send message.
	 */
	public void sendMessage(Message message){
		System.out.println("Sending message: " + message.getMessage());
		strategy_.sendMessage(message);
	}
	
	/*
	 * Sign off strategy.
	 */
	public void signOff(){
		strategy_.signOff();
	}

	/*
	 * Get Private key for encryption.
	 */
	public PrivateKey getPrivateKey(){
		return myPrivateKey_;
	}

	/*
	 *  Get public key for encryption.
	 */
	public PublicKey getPublicKey(){
		return myPublicKey_;
	}

	/*
	 *  Set status to away.
	 */
	 public void setAwayMessage(String message, boolean away){
		 strategy_.setAwayMessage(message, away);
	 }


}
