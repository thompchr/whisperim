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

package org.whisperim.plugins;

import java.util.ArrayList;

import org.whisperim.client.Buddy;
import org.whisperim.client.ConnectionManager;
import org.whisperim.client.ConnectionStrategy;
import org.whisperim.client.Message;

/**
 * This class will serve as an adapter to allow users to define their
 * own connections.  It has several abstract methods that require
 * subclasses to implement because they are required for proper operation
 * of the client.  As long as subclasses implement these methods, they can
 * function as a ConnectionStrategy.  This class also implements the Plugin
 * interface to allow it to be read in dynamically as a user plugin.
 * 
 * Even though not all methods are abstract, certain methods, in practice,
 * must be implemented in order to be a functional connection.
 * @author Chris Thompson
 *
 */
public abstract class ConnectionPluginAdapter implements Plugin, ConnectionStrategy {

	/**
	 * This method can use data members initialized after construction.
	 * For instance, one identifier might use a combination of the 
	 * handle and the protocol.  The handle will not be initialized 
	 * until signOn() is called.
	 */
	@Override
	public abstract String getIdentifier();

	/**
	 * This method must never cause a null pointer exception.
	 * The data members it accesses must be initialized with the class.
	 */
	@Override
	public abstract String getProtocol();
	
	/**
	 * This method must be implemented in order to properly identify the
	 * protocol when account information is saved.  This method will never
	 * be called before signOn() is called.
	 */
	@Override
	public abstract String getHandle();
	

	@Override
	public void receiveBuddies(ArrayList<Buddy> buddies) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveMessage(Message message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendMessage(Message message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAwayMessage(String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void signOff() {
		// TODO Auto-generated method stub

	}

	@Override
	public void signOn(ConnectionManager cm, String username, String password) {
		// TODO Auto-generated method stub

	}

	@Override
	public void statusUpdate(String status) {
		// TODO Auto-generated method stub

	}

}
