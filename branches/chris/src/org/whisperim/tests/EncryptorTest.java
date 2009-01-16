package org.whisperim.tests;


import junit.framework.TestCase;
import java.security.*;
import java.util.Random;

import org.whisperim.security.*;

import java.lang.Math;


public class EncryptorTest extends TestCase {

	public void testEncryptionDecryption (){
		
		String pt = "";
		
		char[] chars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '1', '2', '3', '4',
				'5', '6', '7', '8', '9', '0', '?', '!', '@', '#', '$', '%', '&', '*', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
                'U', 'V', 'W', 'X', 'Y', 'Z', ' ', ':', ';', '[', ']', '.', '(', ')', '{', '}', '\\', '/' };
		Random rnd = new Random ();
		
		
		int numTests = (int) (rnd.nextDouble() * 1000);
		
		for (int i = 0; i < numTests; ++i){
			
			int strLen = (int)(rnd.nextDouble() * 10000);
			
			for (int j = 0; j < strLen; ++j){
				pt = pt + chars[(int) (rnd.nextDouble() * chars.length)];
			}
			
			
			KeyPair kp = Encryptor.generateRSAKeyPair();
			Encryptor enc = new Encryptor(kp.getPublic(), kp.getPrivate());
			String ct;

			

			ct = enc.generateCipherText(pt);

			assertTrue(!pt.equalsIgnoreCase(ct));

			String dec = enc.decryptMessage(ct);

			assertTrue(pt.equalsIgnoreCase(dec));

		}
		
		

		
		
		
		

	}

}
