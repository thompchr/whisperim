package org.whisperim.keys;

import java.security.*;
import java.util.HashMap;

public class KeyContainer {

	private KeyPair myKeys_;
	private HashMap<String, PublicKey> foreignKeys_;
	
	public KeyContainer (KeyPair keys){
		myKeys_ = keys;
		foreignKeys_ = new HashMap<String, PublicKey>();
	}
	
	public PublicKey getKey(String handle){
		return foreignKeys_.get(handle);
	}
	
	public void addKey (String handle, PublicKey key){
		foreignKeys_.put(handle, key);
	}
	
	public PrivateKey getMyPrivateKey(){
		return myKeys_.getPrivate();
	}
	
	public PublicKey getMyPublicKey(){
		return myKeys_.getPublic();
	}
	
}
