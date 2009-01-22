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

	private ConnectionStrategy strategy_;
	private String localHandle_;
	private Login login_;
	private WhisperClient client_ = null;
	private PrivateKey myPrivateKey_;
	private PublicKey myPublicKey_;


	public static final int AIM_SESSION = 0; 

	private HashMap<String, ConnectionStrategy> strategies_ = new HashMap<String, ConnectionStrategy>();

	public boolean isHashEmpty()
	{
		return (strategies_.isEmpty());
	}
	
	public ConnectionStrategy getStrategy(String strategy)
	{
		return strategies_.get(strategy);
	}

	public void removeStrategy(String strategy)
	{
		if(!strategy.isEmpty())
		{
			strategies_.remove(strategy);
		}else{
			System.out.println("Error: Hashmap is already empty.");
		}
	}

	public void addToMap(String name, ConnectionStrategy strategy)
	{
		strategies_.put(name, strategy);
	}

	public ConnectionManager(int strategy, String handle, String password, Login login, PrivateKey privKey, PublicKey pubKey) throws IllegalArgumentException{
		if (handle == null){
			throw new IllegalArgumentException();
		}


		switch(strategy){
		case 0: {
			strategy_ = new AIMStrategy(this);
		}
		}

		myPrivateKey_ = privKey;
		myPublicKey_ = pubKey;
		login_ = login;
		localHandle_ = handle;
		strategy_.signOn(handle, password);

	}

	public ConnectionStrategy getStrategy(){
		return strategy_;
	}

	public void setClient(WhisperClient client){
		client_ = client;
	}

	public String getHandle(){
		return localHandle_;
	}

	public void messageReceived(Message message){
		System.out.println("Received message: " + message.getMessage());
		client_.recieveMessage(message);

	}

	public void statusUpdate (String status){
		login_.statusUpdate(status);
	}

	public void receiveBuddies(ArrayList<String> buddies){
		client_.updateBuddyList(buddies);
	}

	public void sendMessage(Message message){
		System.out.println("Sending message: " + message.getMessage());
		strategy_.sendMessage(message);
	}

	public void signOff(){
		strategy_.signOff();
	}

	public PrivateKey getPrivateKey(){
		return myPrivateKey_;
	}

	public PublicKey getPublicKey(){
		return myPublicKey_;
	}



}
