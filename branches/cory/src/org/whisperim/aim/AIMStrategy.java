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
package org.whisperim.aim;


import java.util.ArrayList;

import javax.speech.AudioException;
import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.swing.text.BadLocationException;

import org.whisperim.client.Buddy;
import org.whisperim.client.ConnectionManager;
import org.whisperim.client.ConnectionStrategy;
import org.whisperim.client.Message;
import org.whisperim.prefs.Preferences;


/**
 * @author Chris Thompson
 *
 * This is the concrete implementation of the ConnectionStrategy interface for
 * an AIM-based chat.
 */
public class AIMStrategy implements ConnectionStrategy {
	
	private AIMSession session_;
	private ConnectionManager manager_;
	private String protocol_ = "AIM";
	private String localHandle_;
	//private String iconLocation_ = Preferences.getInstance().getAimIconSmallLocation();
	private String iconLocation_ = "..\\images\\aim_icon_small.png";
	private String name_ = "AIM Connection";
	private int status_ = ConnectionStrategy.OFFLINE;
	
	public AIMStrategy(){

	}

	
	@Override
	public void 
	receiveMessage(Message message) throws IllegalArgumentException, EngineException, EngineStateError, AudioException, InterruptedException {
		try {
			manager_.messageReceived(message);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void sendMessage(Message message) {
		session_.addOperation(AIMOperation.createSendMessage(message.getTo(), message.getMessage()));
	}

	@Override
	public void signOff() {
		session_.addOperation(AIMOperation.createSignOut());
		status_ = ConnectionStrategy.OFFLINE;
		receiveBuddies(new ArrayList<Buddy>());
	}

	@Override
	public void signOn(ConnectionManager cm, String username, String password) {
		session_ = new AIMSession(this);
		localHandle_ = username;
		manager_ = cm;
		session_.addOperation(AIMOperation.createSignIn(username, password));
		session_.setLocalHandle(username);
	}
	
	@Override
	public void receiveBuddies(ArrayList<Buddy> buddies){
		manager_.receiveBuddies(buddies);
	}

    @Override
    public void statusUpdate(String update){
        //manager_.statusUpdate(update, getIdentifier());
        if (update.equalsIgnoreCase(AIMSession.INVALID_USERNAME_OR_PASSWORD)){
        	status_ = ConnectionStrategy.INVALID_PASSWORD;
        }else if(update.equalsIgnoreCase(AIMSession.RATE_LIMITED)){
        	status_ = ConnectionStrategy.RATE_LIMITED;
        }else if (update.equalsIgnoreCase(AIMSession.SIGNED_IN)){
        	status_ = ConnectionStrategy.ACTIVE;
        }else if (update.equalsIgnoreCase(AIMSession.SERVICE_UNAVAILABLE)){
        	status_ = ConnectionStrategy.SERVICE_UNAVAILABLE;
        }
    }

    public void setAwayMessage(String message){
    	session_.addOperation(AIMOperation.createSatusChange(AIMOperation.STATUS_AWAY, message));
    }

    public void setStatusMessage(String message){
    	session_.addOperation(AIMOperation.setStatusMessage(message));
    }
    
	@Override
	public String getProtocol() {
		return protocol_;
	}

	@Override
	public String getIdentifier() {
		return protocol_ + ":" + localHandle_.toLowerCase().replace(" ", "");
	}
	
	@Override
	public String toString(){
		return protocol_ + ":" + localHandle_;
	}

	@Override
	public String getPluginIconLocation() {
		return iconLocation_;
	}

	@Override
	public String getPluginName() {
		return name_;
	}

	@Override
	public void setIconLocation(String location) {
		iconLocation_ = location;
	}

	@Override
	public void setPluginName(String name) {
		name_ = name;
	}
	
	@Override
	public String getHandle() {
		return localHandle_;
	}


	@Override
	public int getStatus() {
		return status_;
	}


	@Override
	public void setHandle(String handle) {
		localHandle_ = handle;
		
	}


	@Override
	public void setIdle() {
		session_.addOperation(AIMOperation.createSatusChange(AIMOperation.STATUS_IDLE, ""));
		
	}


	@Override
	public void setInvisible(boolean visible) {
		if (visible){
			session_.addOperation(AIMOperation.createSatusChange(AIMOperation.STATUS_INVISIBLE, ""));
		}else{
			session_.addOperation(AIMOperation.createSatusChange(AIMOperation.STATUS_VISIBLE, ""));
		}
		
		
	}


}
