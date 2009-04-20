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
package org.whisperim.android.tests;

import java.util.ArrayList;

import org.whisperim.client.Buddy;
import org.whisperim.client.ConnectionManager;
import org.whisperim.client.Message;
import org.whisperim.plugins.ConnectionPluginAdapter;

import android.util.Log;

public class TestConnection extends ConnectionPluginAdapter {
	
	private ConnectionManager cm_;
	

	@Override
	public String getHandle() {
		
		return "TestBuddyParent";
	}
	
	@Override
	public void signOn(ConnectionManager cm, String username, String password){
		cm_ = cm;
		ArrayList<Buddy> buddies = new ArrayList<Buddy>();
		for (int i = 0; i < 10; ++i){
			buddies.add(new Buddy("TestBuddy" + i, "TestBuddyParent", "testProto"));
		}
		cm.receiveBuddies(buddies);
	}

	@Override
	public String getIdentifier() {
		
		return "TestProto:TestBuddyParent";
	}
	
	@Override
	public void sendMessage(Message message) {
		Log.i("WhisperIM", "TestConnection: Message Received: " + message.getFrom() + ": " + message.getMessage());
		cm_.messageReceived(new Message(new Buddy(message.getFrom(), message.getTo(), message.getProtocol()), message.getFromBuddy(), 
				message.getMessage(), message.getProtocol(), message.getTimeSent()));

	}

	@Override
	public String getProtocol() {
	
		return "TestProto";
	}

	@Override
	public String getPluginIconLocation() {
	
		return null;
	}

	@Override
	public String getPluginName() {
	
		return "TestConnectionPlugin";
	}

	@Override
	public void setIconLocation(String location) {


	}

	@Override
	public void setPluginName(String name) {


	}

	@Override
	public int getStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setHandle(String handle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setIdle() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setInvisible(boolean visible) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setStatusMessage(String message) {
		// TODO Auto-generated method stub

	}

}
