/**
 * 
 */
package org.vanderbilt.client;


import org.vanderbilt.aim.*;
import java.util.ArrayList;


/**
 * @author Chris Thompson
 *
 * This is the concrete implementation of the ConnectionStrategy class for
 * an AIM-based chat.
 */
public class AIMStrategy implements ConnectionStrategy {
	
	AIMSession session;
	ConnectionManager manager_;
	
	
	public AIMStrategy(ConnectionManager manager){

		manager_ = manager;
		session = new AIMSession (this);
		
	}


	@Override
	public void receiveMessage(Message message) {
		// TODO Auto-generated method stub
		manager_.messageReceived(message);
	}


	@Override
	public void sendMessage(Message message) {
		session.addOperation(AIMOperation.createSendMessage(message.getTo(), message.getMessage()));
	}


	@Override
	public void signOff() {
		
		session.addOperation(AIMOperation.createSignOut());

	}


	@Override
	public void signOn(String username, String password) {

		session.addOperation(AIMOperation.createSignIn(username, password));
	}
	
	@Override
	public void receiveBuddies(ArrayList<String> buddies){
		manager_.receiveBuddies(buddies);
	}

    @Override
    public void statusUpdate(String update){
        manager_.statusUpdate(update);
    }

}
