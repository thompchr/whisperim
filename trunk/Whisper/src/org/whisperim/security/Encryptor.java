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

public class Encryptor {
	
	private SecretKey sessionKey;
	
	
	private PublicKey publicKey = null;
	private PrivateKey privateKey = null;
	
	public Encryptor (PublicKey key, PrivateKey myKey){
		publicKey = key;
		privateKey = myKey;
			
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
	 * This method will be used to generate the encrypted message.
	 * @param message
	 * @return String
	 */
	public String generateCipherText(String message){
		String encryptedMessage = null;
		try{
			Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			
			KeyGenerator sessionKeyGen = KeyGenerator.getInstance("AES");
			
			sessionKeyGen.init(128);
			
			sessionKey = sessionKeyGen.generateKey();
			
			
			aesCipher.init(Cipher.ENCRYPT_MODE, sessionKey);
			
			byte [] byteArray = message.getBytes("UTF-8");
			
			String cipherMessage = new String (Base64.encode(aesCipher.doFinal(byteArray)));
			
			encryptedMessage = "<key>";
			
			Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			
			rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
			
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
			rsaCipherDecrypt.init(Cipher.DECRYPT_MODE, privateKey);
			
			
			
			Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			SecretKeySpec aesKeyDec = new SecretKeySpec(rsaCipherDecrypt.doFinal(keyBytes), "AES");
			aesCipher.init(Cipher.DECRYPT_MODE, aesKeyDec);

			outputMessage = new String(aesCipher.doFinal(messageBytes));

		}catch (Exception e) {
			e.printStackTrace();
		} 
		
		return outputMessage;
		
	}
	
	public void setPublicKey(PublicKey key){
		publicKey = key;
	}
	
	public void setPrivateKey(PrivateKey key){
		privateKey = key;
	}
	
	
}
