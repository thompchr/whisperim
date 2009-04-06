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

package org.whisperim.tests;


import junit.framework.TestCase;
import java.security.*;
import java.util.Random;

import org.whisperim.security.*;

import com.sun.org.apache.xml.internal.security.utils.Base64;

/**
 * This class tests the Encryptor class.  It provides tests for the stability
 * as well as performance of the encryption algorithms.
 * @author Chris Thompson
 *
 */
public class EncryptorTest extends TestCase {

	/**
	 * This method runs a random number of encryption/decryption cycles that each use a string
	 * of random characters and random size. This functions to test the stability of the encryption/decryption
	 * process and assure that all strings will be encrypted and decrypted without error.  Further, this test
	 * verifies that the product of the encryption is not equal to the original plain text.  At the end, it 
	 * verifies that the output of the decryption function is the same as the original plain text.
	 * Currently, this test outputs its results and information for each iteration to the command line. 
	 */
	public void testEncryptionDecryptionStability (){
		
		String pt = "";
		
		char[] chars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '1', '2', '3', '4',
				'5', '6', '7', '8', '9', '0', '?', '!', '@', '#', '$', '%', '&', '*', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
                'U', 'V', 'W', 'X', 'Y', 'Z', ' ', ':', ';', '[', ']', '.', '(', ')', '{', '}', '\\', '/' };
		Random rnd = new Random ();
		
		
		int numTests = (int) (rnd.nextDouble() * 25);
		
		System.out.println("Running " + numTests + " tests...");
		
		for (int i = 0; i < numTests; ++i){
			
			System.out.println("Test " + i);
			
			int strLen = (int)(rnd.nextDouble() * 10000);
			System.out.println("----------------------------");
			System.out.println("- String length: " + strLen);
						
			for (int j = 0; j < strLen; ++j){
				pt = pt + chars[(int) (rnd.nextDouble() * chars.length)];
			}
			
			
			KeyPair kp = Encryptor.generateRSAKeyPair();
			Encryptor enc = new Encryptor(kp.getPublic(), kp.getPrivate());
			String ct;
			
			Long start = System.currentTimeMillis(); 
			ct = enc.generateCipherText(pt);
			Long finish = System.currentTimeMillis();

			System.out.println("- Encryption time: " + (finish - start));
			assertTrue(!pt.equalsIgnoreCase(ct));

			start = System.currentTimeMillis(); 
			String dec = enc.decryptMessage(ct);
			finish = System.currentTimeMillis();
			
			System.out.println("- Decryption time: " + (finish-start));
			System.out.println("----------------------------");
			System.out.println();
			assertTrue(pt.equalsIgnoreCase(dec));

		}
		

	}
	
	/**
	 * This method is designed to test the performance of the encryption/decryption process.  
	 * It uses a random string of 100 characters.  This test assumes that the encryption/decryption
	 * process is functioning correctly.
	 */
	public void testEncryptionDecryptionPerformance() {
		String pt = "";
		
		char[] chars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '1', '2', '3', '4',
				'5', '6', '7', '8', '9', '0', '?', '!', '@', '#', '$', '%', '&', '*', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
                'U', 'V', 'W', 'X', 'Y', 'Z', ' ', ':', ';', '[', ']', '.', '(', ')', '{', '}', '\\', '/' };
		Random rnd = new Random ();

		for (int j = 0; j < 100; ++j){
			pt = pt + chars[(int) (rnd.nextDouble() * chars.length)];
		}
		
		
		KeyPair kp = Encryptor.generateRSAKeyPair();
		Encryptor enc = new Encryptor(kp.getPublic(), kp.getPrivate());
		String ct = enc.generateCipherText(pt);
		
		enc.decryptMessage(ct);

	}
	
	
//	public void testWriteToFile(){
//		Encryptor.writeKeyToFile(Base64.encode(Encryptor.generateRSAKeyPair().getPublic().getEncoded()),
//				"fightinggator13:aol");
//	}
	

}
