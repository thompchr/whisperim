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
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.whisperim.client.Buddy;
import org.whisperim.security.Encryptor;

public class KeyStringContainer {
	
	private String myPublic;
	private String myPrivate;
	
	private HashMap<Buddy, String> foreignKeys_;
	
	public KeyStringContainer(){
		foreignKeys_ = new HashMap<Buddy, String>();
	}
	
	public KeyStringContainer(String pub, String priv){
		myPrivate = priv;
		myPublic = pub;
	}
	
	public KeyStringContainer(KeyPair kp){
		Base64 b64 = new Base64();
		myPrivate = new String(b64.encode(kp.getPrivate().getEncoded()));
		
		myPublic = new String(b64.encode(kp.getPublic().getEncoded()));
	}
	
	public KeyPair getMyKeys(){
		return new KeyPair(Encryptor.getPublicKeyFromString(myPublic), 
				Encryptor.getPrivateKeyFromString(myPrivate));
	}
	
	public void addKey(Buddy b, String s){
		foreignKeys_.put(b, s);
	}
	
	public void setForeignKeys(HashMap<Buddy, String> keys){
		foreignKeys_ = keys;
	}
	
	public HashMap<Buddy, PublicKey> getKeys(){
		HashMap<Buddy, PublicKey> temp = new HashMap<Buddy, PublicKey>();
		for (Entry e:foreignKeys_.entrySet()){
			temp.put((Buddy)e.getKey(), Encryptor.getPublicKeyFromString((String)e.getValue()));
		}
		
		return temp;
	}

}
