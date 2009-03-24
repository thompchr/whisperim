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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.whisperim.commandline.CommandLineExecutor;

import com.sun.org.apache.xml.internal.security.utils.Base64;

public class CommandLineEncryptor {

	private OutputStream os_;
	private PublicKey pk_;

	public CommandLineEncryptor(OutputStream os, PublicKey pk){
		os_ = os;
		pk_ = pk;

	}

	public void execute(String command, String dir){
		CommandLineExecutor cle = new CommandLineExecutor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(cle.execute(command, dir)[0]));
		
		String line = null;
		
		try {
			while ((line = reader.readLine()) != null){
				
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	private String encryptLine(String line){

		Cipher rsaCipher;
		try {

			rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

			rsaCipher.init(Cipher.ENCRYPT_MODE, pk_);

			byte [] encryptedKeyBytes = rsaCipher.doFinal(line.getBytes("UTF-8"));
			
			return new String (Base64.encode(encryptedKeyBytes));
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
