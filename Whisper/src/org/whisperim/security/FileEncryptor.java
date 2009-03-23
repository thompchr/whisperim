	/**************************************************************************
	 * Copyright 2009 Chris Thompson modified by Nick Krieble                  *
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

	import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

public class FileEncryptor {
	/**
	 * This class forms the backbone of the security functionality of the whisper client's encrypted file transfer feature.
	 * It contains methods for generating RSA keypairs as well as performing the encryption/decryption
	 * operations on the files.  It also reads the key file to import a given buddy's public key.
	 * 
	 * @author Chris Thompson
	 *
	 */
		private PublicKey publicKey_ = null;
		private PrivateKey privateKey_ = null;


		/**
		 * Encryptor class constructor.  Requires a PublicKey (foreign) and a PrivateKey(local)
		 * as parameters.
		 * @param key
		 * @param myKey
		 */
		public FileEncryptor (PublicKey key, PrivateKey myKey){
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
				NodeList buddies = doc.getElementsByTagName("Buddy");

		
				Element buddy = null;

				if (buddies.getLength() != 0){
					//We need to see if we already have a key for this buddy
					for (int i = 0; i < buddies.getLength(); ++i){
						Element temp = (Element)buddies.item(i);

						if (temp.getAttribute("handle").equalsIgnoreCase(handle)){
							//We found our buddy
							buddy = temp;
							break;
						}
					}
					if (buddy == null){
						return null;
					}
					
					NodeList children = buddy.getChildNodes();
					Element key = null;
					for (int i = 0; i < children.getLength(); ++i){
						if (children.item(i).getNodeName().compareToIgnoreCase("Key") == 0){
							key = (Element) children.item(i);
							break;
						}
					}
					if (key == null){
						return null;
					}
					
					String keyString = key.getTextContent();
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

		/**
		 * This method is designed to write a key into the key store file. It takes a string
		 * that has been received from the buddy as well as the buddy's handle.  It will also
		 * verify that the key does not already exist in the file.  If it does, it will ignore
		 * the request.  If the buddy has a different key associated with their buddy handle,
		 * this method will assume the new key is valid and the old key has been discarded.
		 * It will then overwrite the old key.
		 * @param keyText
		 * @param handle
		 */
		public static void writeKeyToFile(String keyText, String handle){
			//Identifier will be handle:protocol
			//The handle string passed to the encryptor is already formatted correctly,
			//no need to get any more information, just write the bugger.  Key text is
			//Base64 encoded

			File keyFile = new File(System.getProperty("user.home") + File.separator + "Whisper" + File.separator + "keys");
			Document doc;
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			
			try {

				docBuilder = docBuilderFactory.newDocumentBuilder();
				doc = docBuilder.parse(keyFile);
				
				Node root = doc.getElementsByTagName("Keys").item(0);
				
				NodeList buddies = doc.getElementsByTagName("Buddy");
				
				Element buddy = null;
				
				if (buddies.getLength() != 0){
					//We need to see if we already have a key for this buddy
					for (int i = 0; i < buddies.getLength(); ++i){
						Element temp = (Element)buddies.item(i);

						if (temp.getAttribute(handle) == null){
							//Not our buddy
						}else {
							//We found our buddy
							buddy = (Element) buddies.item(i);

							break;
						}
					}
				}
				
				if (buddy == null){
					//We didn't find the buddy
					buddy = doc.createElement("Buddy");
					buddy.setAttribute("handle", handle);
					Element key = doc.createElement("Key");
					key.setTextContent(keyText);
					buddy.appendChild(key);
					root.appendChild(buddy);
					
									
				}else {
					
					NodeList children = buddy.getChildNodes();
					boolean found = false;
					
					for (int i = 0; i < children.getLength(); ++i){
						if (children.item(i).getNodeName().compareToIgnoreCase("Key") == 0){
							//Key node already exists
							children.item(i).setTextContent(keyText);
							found = true;
							break;
						}
					}
					if (!found){
						Element key = doc.createElement("Key");
						key.setTextContent(keyText);
						buddy.appendChild(key);
					}
					
				}
				
				
				OutputFormat format = new OutputFormat(doc);
				format.setIndenting(true);

				XMLSerializer serializer = new XMLSerializer(
				new FileOutputStream(keyFile), format);

				serializer.serialize(doc);


			} catch (ParserConfigurationException e) {
				
				e.printStackTrace();
			} catch (SAXException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}

		}

		public static KeyPair generateRSAKeyPairFromString(String[] keys) throws Base64DecodingException{
			KeyFactory rsaKeyFac = null;

			try {
				rsaKeyFac = KeyFactory.getInstance("RSA");
			} catch (NoSuchAlgorithmException e) {
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

		 public static byte[] fileToByteArray(File file) throws IOException {
		        InputStream is = new FileInputStream(file);
		    
		        // Get the size of the file
		        long length = file.length();
		    
		        if (length > Integer.MAX_VALUE) {
		            // File is too large
		        }
		    
		        // Create the byte array to hold the data
		        byte[] bytes = new byte[(int)length];
		    
		        // Read in the bytes
		        int offset = 0;
		        int numRead = 0;
		        while (offset < bytes.length
		               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
		            offset += numRead;
		        }
		    
		        // Ensure all the bytes have been read in
		        if (offset < bytes.length) {
		            throw new IOException("Could not completely read file "+file.getName());
		        }
		    
		        // Close the input stream and return bytes
		        is.close();
		        return bytes;
		    } 
		
		/**
		 * This method is used to generate the encrypted file.  The plaintext is provided as a parameter
		 * and the ciphertext with the encrypted session key including the <key> and <message> tags is returned.
		 * @param file
		 * @return file
		 */
		/*public String generateCipherFile(File file){
			File encryptedFile = null;
				try{
				Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

				KeyGenerator sessionKeyGen = KeyGenerator.getInstance("AES");

				sessionKeyGen.init(128);

				SecretKey sessionKey = sessionKeyGen.generateKey();

				aesCipher.init(Cipher.ENCRYPT_MODE, sessionKey);

				// Change to byte array
				byte [] byteArray = fileToByteArray(file);
				
				// LEAVE OFF HERE ********************************
				
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
			
			// Return Encrypted file.
			return encryptedMessage;
		}
		
		public static String getMyPublicKey() throws ParserConfigurationException, SAXException, IOException{
			File keyFile = new File(System.getProperty("user.home") + File.separator + "Whisper" + File.separator + "keys");
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc;
			doc = docBuilder.parse(keyFile);
			// normalize text representation
			doc.getDocumentElement().normalize();
			NodeList buddies = doc.getElementsByTagName("PublicKey");
			
			
			return  buddies.item(0).getTextContent();
		}
		*/
		/**
		 * This function provides the decryption of an entire message.
		 * It provides the functionality to initially decrypt the session key
		 * using the member variable privateKey_ as well as then to use the output
		 * of that decryption to decrypt the body of the message itself.
		 * @param message
		 */
	/*	public void decryptFile(File file){

			// Shift file to byte array to string.
			try {
				byte[] fileBytes = fileToByteArray(file);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String encryptedFile = fileBytes.toString();
			String outputMessage = null;
			try {

				String encKey = encryptedFile.substring(5, encryptedFile.indexOf("</key>"));
				byte [] keyBytes = Base64.decode(encKey.getBytes("UTF-8"));


				String encMessage = encryptedFile.substring(encryptedFile.indexOf("</key>") + 15, encryptedFile.indexOf("</message>"));

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

			// Write string to file. Will require "playing with" to support file types and such.
			try{
				BufferedWriter output = new BufferedWriter(new FileWriter("result.txt"));
				output.write(outputMessage);
				output.close();
			}catch (IOException e)
			{
				e.printStackTrace();
			}
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

