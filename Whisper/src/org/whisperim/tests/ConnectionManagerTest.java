package org.whisperim.tests;

import java.security.KeyPair;

import org.whisperim.client.ConnectionManager;
import org.whisperim.client.Login;
import org.whisperim.security.Encryptor;

import junit.framework.TestCase;

public class ConnectionManagerTest extends TestCase {
	
	private static final String test = null;
	private static final String test2 = null;
	private static final Login testUser = null;

	KeyPair kp = Encryptor.generateRSAKeyPair();
	ConnectionManager cm = 
		new ConnectionManager(1, test, test2, 
		testUser, kp.getPrivate(), kp.getPublic());
	
	public void testAddToMap()
	{
		cm.addToMap(test, null);
		assertTrue(cm.getStrategy(test) != null);
	}
	
	public void testRemoveStrategy()
	{
		cm.removeStrategy(test);
		assertTrue(cm.isHashEmpty());
	}
	
	

}
