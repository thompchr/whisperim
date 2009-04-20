package org.whisperim.client;

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
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.HashMap;

import org.whisperim.events.EncryptionEvent;
import org.whisperim.keys.KeyContainer;
import org.whisperim.security.Encryptor;
import org.whisperim.ui.UIController;



public class MessageProcessorImpl implements MessageProcessor {

	private KeyContainer kc_;
	private HashMap<Buddy, Encryptor> encConfig_ = new HashMap<Buddy, Encryptor>();
	private ConnectionManager cm_;
	private UIController ui_;

	public MessageProcessorImpl(KeyPair kp){
		kc_ = new KeyContainer(kp);
	}

	public MessageProcessorImpl(KeyContainer kc){
		kc_ = kc;
	}

	@Override
	public void setConnectionManager(ConnectionManager cm){
		cm_ = cm;
	}

	@Override
	public void disableEncryption(Buddy b) {
		if (encConfig_.containsKey(b)){
			encConfig_.remove(b);	
		}
	}

	@Override
	public void enableEncryption(Buddy b) {
		if (encConfig_.containsKey(b)){
			encConfig_.remove(b);
		}

		EncryptionEvent e = new EncryptionEvent(b);
		if (haveKey(b)){
			encConfig_.put(b, new Encryptor(kc_.getKey(b), kc_.getMyPrivateKey()));
			e.setEncryptionStatus(EncryptionEvent.ENCRYPTION_ENABLED);


		}else{
			e.setEncryptionStatus(EncryptionEvent.ENCRYPTION_DISABLED);
		}

		ui_.processEvent(e);
	}

	@Override
	public void receiveMessage(Message m) {
		if (m.getMessage().contains("<whisperim")){
			//This is an instruction to the client, we need to parse it
			//and then we'll swallow it or print out a message to the
			//user.

			if (m.getMessage().contains("keyspec=")){
				//A public key has been sent.

				//Key information will be Base64 encoded to allow for easy transport
				String keyText = m.getMessage().substring(
						m.getMessage().indexOf("keyspec=")+ 8, 
						m.getMessage().indexOf("--"));

				kc_.addKey(m.getFromBuddy(), Encryptor.getPublicKeyFromString(keyText));

				ui_.keyReceived(m.getFromBuddy());

				return;
			}

			
			
		}
		if (m.getMessage().contains("<key>")){
			m.setMessage(" (Encrypted Message) " + 
					encConfig_.get(m.getFrom()).decryptMessage(m.getMessage()));
		}
		ui_.receiveMessage(m);
	}

	@Override
	public void sendMessage(Message m) {
		//Has encryption been enabled?
		if (encConfig_.containsKey(m.getTo())){
			//Encrypt the message
			m.setMessage(encConfig_.get(m.getTo()).generateCipherText(m.getMessage()));
		}

		cm_.sendMessage(m);

	}

	@Override
	public boolean haveKey(Buddy b) {
		if (!kc_.contains(b)){
			PublicKey key = Encryptor.getPublicKeyForBuddy(b);
			if (key != null){
				kc_.addKey(b, key);
			}
		}

		return kc_.contains(b);

	}
	
	@Override
	public PublicKey getMyPublicKey(){
		return kc_.getMyPublicKey();
	}

	@Override
	public void registerKey(Buddy b, PublicKey key) {
		kc_.addKey(b, key);		
	}

	@Override
	public void setUI(UIController ui) {
		ui_ = ui;

	}

}
