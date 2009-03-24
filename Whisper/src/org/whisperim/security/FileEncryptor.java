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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

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

