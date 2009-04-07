package org.whisperim.client;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;

import org.whisperim.events.EncryptionEvent;
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

				//Now we have the text of the key, pass it to the Encryptor
				//to parse it and store it.  We will also probably want to
				//fire some sort of event that allows the encryption to be
				//enabled.
				Encryptor.writeKeyToFile(keyText, m.getFromBuddy());

				try {
					X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(Base64.decode(keyText.getBytes()));
					kc_.addKey(m.getFromBuddy(),KeyFactory.getInstance("RSA").generatePublic(pubKeySpec));
					
					ui_.keyReceived(m.getFromBuddy());
					
					return;

				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
					m.setMessage("<b><font color=\"red\">An error occurred reading the key that was received.  Please have your Buddy resend it.</font></b>");

				} catch (Base64DecodingException e) {
					e.printStackTrace();
					m.setMessage("<b><font color=\"red\">An error occurred reading the key that was received.  Please have your Buddy resend it.</font></b>");
				} catch (InvalidKeySpecException e) {
					e.printStackTrace();
					m.setMessage("<b><font color=\"red\">An error occurred reading the key that was received.  Please have your Buddy resend it.</font></b>");
				}
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
	public void registerKey(Buddy b, PublicKey key) {
		kc_.addKey(b, key);		
	}

	@Override
	public void setUI(UIController ui) {
		ui_ = ui;
		
	}

}