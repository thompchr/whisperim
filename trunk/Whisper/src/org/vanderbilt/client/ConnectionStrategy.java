package org.vanderbilt.client;

import java.util.ArrayList;

public interface ConnectionStrategy {

	public void signOn(String username, String password);
	
	public void signOff();
	
	public void sendMessage(Message message);
	
	public void receiveMessage(Message message);
	
	public void receiveBuddies(ArrayList<String> buddies);

    public void statusUpdate(String status);
}
