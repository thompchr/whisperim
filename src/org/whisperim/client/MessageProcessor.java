package org.whisperim.client;

import java.security.PublicKey;


public interface MessageProcessor {

	public void receiveMessage(Message m);
	
	public void sendMessage(Message m);
	
	public void enableEncryption(Buddy b);
	
	public void disableEncryption(Buddy b);
	
	public boolean haveKey(Buddy b);
	
	public void registerKey(Buddy b, PublicKey key);

	void setConnectionManager(ConnectionManager cm);
	
	
}
