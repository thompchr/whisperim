package org.whisperim.tests;


import junit.framework.TestCase;
import java.security.*;
import java.util.Random;

import org.whisperim.security.*;


public class EncryptorTest extends TestCase {

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

			System.out.println("- Encryption time: " + (finish-start));
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
	

}
