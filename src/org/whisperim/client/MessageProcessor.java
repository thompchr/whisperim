package org.whisperim.client;

import java.security.PublicKey;

import org.whisperim.ui.UIController;


public interface MessageProcessor {

	public void receiveMessage(Message m);
	
	public void sendMessage(Message m);
	
	public void enableEncryption(Buddy b);
	
	public void disableEncryption(Buddy b);
	
	public boolean haveKey(Buddy b);
	
	public PublicKey getMyPublicKey();
	
	public void registerKey(Buddy b, PublicKey key);
	
	public void saveKeys();

	public void setConnectionManager(ConnectionManager cm);
	
	public void setUI(UIController ui);
	
	
}
