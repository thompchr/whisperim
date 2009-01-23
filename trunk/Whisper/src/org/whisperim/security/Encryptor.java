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

package org.whisperim.security;

import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * This class forms the backbone of the security functionality of the whisper client.
 * It contains methods for generating RSA keypairs as well as performing the encryption/decryption
 * operations on the messages.  It also reads the key file to import a given buddy's public key.
 * 
 * @author Chris Thompson
 *
 */
public class Encryptor {
	
	private PublicKey publicKey_ = null;
	private PrivateKey privateKey_ = null;
	
	
	/**
	 * Encryptor class constructor.  Requires a PublicKey (foreign) and a PrivateKey(local)
	 * as parameters.
	 * @param key
	 * @param myKey
	 */
	public Encryptor (PublicKey key, PrivateKey myKey){
		publicKey_ = key;
		privateKey_ = myKey;
			
	}
	
	
	/**
	 * Method used for generating an RSA key pair.  This method is 
	 * static and will only be called when a user first uses the program.
	 * @return java.security.KeyPair
	 */
	public static KeyPair generateRSAKeyPair(){
		KeyPairGenerator kpg = null;
		
		try{
			kpg = KeyPairGenerator.getInstance("RSA");
			
		}catch(NoSuchAlgorithmException e){
			//No such algorithm
			System.err.println(
            "usage: java AsymmetricKeyMaker <RSA | DSA>");

		}
		
		kpg.initialize(1024);
		return kpg.generateKeyPair();
		
	}

	/**
	 * The function provides the ability to read the key file from the local file system
	 * and retrieve the previously acquired public key for a given buddy.  Currently requires
	 * only a "handle" as a parameter but should, in the future, require a protocol to 
	 * correctly identify the public key.
	 * @param handle
	 * @return PublicKey
	 */
    public static PublicKey getPublicKeyForBuddy(String handle){
        try {
            File keyFile = new File(System.getProperty("user.home") + File.separator + "Whisper" + File.separator + "keys");
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc;
            doc = docBuilder.parse(keyFile);
            // normalize text representation
            doc.getDocumentElement().normalize();
            NodeList theirKeys = doc.getElementsByTagName(handle);

            if (theirKeys.getLength() == 0)
                //Keys haven't been added
                return null;

            String keyString = theirKeys.item(0).getFirstChild().getTextContent();
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(Base64.decode(keyString.getBytes()));
            
    		KeyFactory rsaKeyFac = null;
    		
    		try {
    			rsaKeyFac = KeyFactory.getInstance("RSA");
    			return rsaKeyFac.generatePublic(pubKeySpec);
    		} catch (NoSuchAlgorithmException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
            return null;
        } catch (SAXException ex) {
            Logger.getLogger(Encryptor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Encryptor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Encryptor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Base64DecodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
    }
	
	public static KeyPair generateRSAKeyPairFromString(String[] keys) throws Base64DecodingException{
		KeyFactory rsaKeyFac = null;
		
		try {
			rsaKeyFac = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(Base64.decode(keys[0].getBytes()));
		PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(Base64.decode(keys[1].getBytes()));
		
		PublicKey pubKey = null;
		PrivateKey privKey = null;
		
		if (rsaKeyFac != null){

			try {
				
				pubKey = rsaKeyFac.generatePublic(pubKeySpec);
				privKey = rsaKeyFac.generatePrivate(privKeySpec);

			} catch (InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if ((pubKey == null) || (privKey == null)){
			return null;
		}else{
			return new KeyPair(pubKey, privKey);
		}
		
	}
	
	/**
	 * This method is used to generate the encrypted message.  The plaintext is provided as a parameter
	 * and the ciphertext with the encrypted session key including the <key> and <message> tags is returned.
	 * @param message
	 * @return String
	 */
	public String generateCipherText(String message){
		String encryptedMessage = null;
		try{
			
			Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			
			KeyGenerator sessionKeyGen = KeyGenerator.getInstance("AES");
			
			sessionKeyGen.init(128);
			
			SecretKey sessionKey = sessionKeyGen.generateKey();
			
			aesCipher.init(Cipher.ENCRYPT_MODE, sessionKey);
			
			byte [] byteArray = message.getBytes("UTF-8");
			
			String cipherMessage = new String (Base64.encode(aesCipher.doFinal(byteArray)));
			
			encryptedMessage = "<key>";
			
			Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			
			rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey_);
			
			byte [] encryptedKeyBytes = rsaCipher.doFinal(sessionKey.getEncoded());
			
			String encryptedKeyString = new String (Base64.encode(encryptedKeyBytes));
			
			encryptedMessage += encryptedKeyString +"</key>" +
				"<message>" + cipherMessage + "</message>";


		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return encryptedMessage;
		
	}
	
	/**
	 * This function provides the decryption of an entire message.
	 * It provides the functionality to initially decrypt the session key
	 * using the member variable privateKey_ as well as then to use the output
	 * of that decryption to decrypt the body of the message itself.
	 * @param message
	 * @return  String
	 */
	public String decryptMessage(String message){
		
		String outputMessage = null;
		try {

			String encKey = message.substring(5, message.indexOf("</key>"));
			byte [] keyBytes = Base64.decode(encKey.getBytes("UTF-8"));
			

			String encMessage = message.substring(message.indexOf("</key>") + 15, message.indexOf("</message>"));
			
			byte [] messageBytes = Base64.decode(encMessage.getBytes("UTF-8"));
			
			Cipher rsaCipherDecrypt = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			rsaCipherDecrypt.init(Cipher.DECRYPT_MODE, privateKey_);
			
			
			
			Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			SecretKeySpec aesKeyDec = new SecretKeySpec(rsaCipherDecrypt.doFinal(keyBytes), "AES");
			aesCipher.init(Cipher.DECRYPT_MODE, aesKeyDec);

			outputMessage = new String(aesCipher.doFinal(messageBytes));

		}catch (Exception e) {
			e.printStackTrace();
		} 
		
		return outputMessage;
		
	}
	
	
	/**
	 * Set method for the publicKey_ member variable.
	 * @param key
	 */
	public void setPublicKey(PublicKey key){
		publicKey_ = key;
	}
	
	/**
	 * Set method for the privateKey_ member variable.
	 * @param key
	 */
	public void setPrivateKey(PrivateKey key){
		privateKey_ = key;
	}
	
	
}
