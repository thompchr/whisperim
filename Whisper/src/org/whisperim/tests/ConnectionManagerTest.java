package org.whisperim.tests;

import java.security.KeyPair;

import org.whisperim.client.ConnectionManager;
import org.whisperim.client.Login;
import org.whisperim.security.Encryptor;

import junit.framework.TestCase;

/**
 * This class is designed to provided a set of tests for the ConnectionManager class.
 * 
 * @author Nick Krieble
 *
 */
public class ConnectionManagerTest extends TestCase {
	
	private static final String test = null;
	private static final String test2 = null;
	private static final Login testUser = null;

	KeyPair kp = Encryptor.generateRSAKeyPair();
	ConnectionManager cm = 
		new ConnectionManager(1, test, test2, 
		testUser, kp.getPrivate(), kp.getPublic());
	
	/**
	 * This method tests the manager's ability to add a connection strategy
	 * to the hashmap of currently activated protocols.
	 */
	public void testAddToMap()
	{
		cm.addToMap(test, null);
		assertTrue(cm.getStrategy(test) != null);
	}
	
	/**
	 * This method tests the manager's ability to remove connection strategies
	 * from the hashmap.
	 */
	public void testRemoveStrategy()
	{
		cm.removeStrategy(test);
		assertTrue(cm.isHashEmpty());
	}
	
	

}
