package org.whisperim.client;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;

import org.whisperim.keys.KeyContainer;
import org.whisperim.security.Encryptor;
import org.whisperim.ui.UIController;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

public class MessageProcessorImpl implements MessageProcessor {

	private KeyContainer kc_;
	private HashMap<Buddy, Encryptor> encConfig_ = new HashMap<Buddy, Encryptor>();
	private ConnectionManager cm_;
	private UIController ui_;

	public MessageProcessorImpl(KeyPair kp){
		kc_ = new KeyContainer(kp);
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
		if (haveKey(b)){
			encConfig_.put(b, new Encryptor(kc_.getKey(b), kc_.getMyPrivateKey()));
		}else{
			//Notify the appropriate window that encryption
			//could not be enabled because we do not
			//have the key
		}
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

				//Now we have the text of the key, pass it to the Encryptor
				//to parse it and store it.  We will also probably want to
				//fire some sort of event that allows the encryption to be
				//enabled.
				Encryptor.writeKeyToFile(keyText, m.getProtocol() + ":" + m.getFrom());

				try {
					X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(Base64.decode(keyText.getBytes()));
					kc_.addKey(m.getFromBuddy(),KeyFactory.getInstance("RSA").generatePublic(pubKeySpec));
					
					ui_.keyReceived(m.getFromBuddy());
					
					return;

				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (Base64DecodingException e) {
					e.printStackTrace();
				} catch (InvalidKeySpecException e) {
					e.printStackTrace();
				}
			}
		}
		
		if (m.getMessage().contains("<key>")){
			m.setMessage(encConfig_.get(m.getFrom()).decryptMessage(m.getMessage()));
		}	
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
		return kc_.contains(b);
	}

	@Override
	public void registerKey(Buddy b, PublicKey key) {
		kc_.addKey(b, key);		
	}

}
