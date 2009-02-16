 /**************************************************************************
 * Copyright 2009 Nick Krieble                                             *
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
		cm.addStrategy(test, null);
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
