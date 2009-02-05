 /**************************************************************************
 * Copyright 2009 John Dlugokecki                                          *
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
package org.whisperim.client;


import java.util.ArrayList;

import org.whisperim.aim.AIMOperation;
import org.whisperim.aim.AIMSession;


/**
 * @author Chris Thompson
 *
 * This is the concrete implementation of the ConnectionStrategy interface for
 * an AIM-based chat.
 */
public class AIMStrategy implements ConnectionStrategy {
	
	AIMSession session_;
	ConnectionManager manager_;
	String protocol_ = "aol";

	
	public AIMStrategy(ConnectionManager manager){

		manager_ = manager;
		session_ = new AIMSession (this);
		
	}


	@Override
	public void receiveMessage(Message message) {
		manager_.messageReceived(message);
	}


	@Override
	public void sendMessage(Message message) {
		session_.addOperation(AIMOperation.createSendMessage(message.getTo(), message.getMessage()));
	}

	@Override
	public void signOff() {
		
		session_.addOperation(AIMOperation.createSignOut());
	}

	@Override
	public void signOn(String username, String password) {

		session_.addOperation(AIMOperation.createSignIn(username, password));
	}
	
	@Override
	public void receiveBuddies(ArrayList<String> buddies){
		manager_.receiveBuddies(buddies);
	}

    @Override
    public void statusUpdate(String update){
        manager_.statusUpdate(update);
    }

    public void setAwayMessage(String message, boolean away){
    	session_.setAwayMessage(message, away);
    }


	@Override
	public String getProtocol() {
		return protocol_;
	}
}