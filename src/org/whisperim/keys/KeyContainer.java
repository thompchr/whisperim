 /**************************************************************************
 * Copyright 2009 Chris Thompson                                           *
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

package org.whisperim.keys;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;

import org.whisperim.client.Buddy;

/**
 * This class is currently unused but is designed to hold local and foreign keys
 * in memory.
 * @author Chris Thompson
 *
 */
public class KeyContainer {

	private KeyPair myKeys_;
	private HashMap<Buddy, PublicKey> foreignKeys_;
	
	
	/**
	 * Constructor taking a keypair object that contains the local set of
	 * RSA keys.
	 * @param keys
	 */
	public KeyContainer (KeyPair keys){
		myKeys_ = keys;
		foreignKeys_ = new HashMap<Buddy, PublicKey>();
	}
	
	/**
	 * Method that returns the public key for the given buddy.
	 * @param b
	 * @return PublicKey
	 */
	public PublicKey getKey(Buddy b){
		return foreignKeys_.get(b);
	}
	
	/**
	 * Method that adds a key to the keystore in local memory.  Expects a PublicKey object
	 * and a String object representing the handle of the buddy.
	 * @param b
	 * @param key
	 */
	public void addKey (Buddy b, PublicKey key){
		foreignKeys_.put(b, key);
	}
	
	
	/**
	 * Method to retrieve the local private key from memory.
	 * @return PrivateKey
	 */
	public PrivateKey getMyPrivateKey(){
		return myKeys_.getPrivate();
	}
	
	/**
	 * Method to retrieve the local public key from memory.
	 * @return PublicKey
	 */
	public PublicKey getMyPublicKey(){
		return myKeys_.getPublic();
	}
	
	public boolean contains(Buddy b){
		return foreignKeys_.containsKey(b);
	}
	
}
