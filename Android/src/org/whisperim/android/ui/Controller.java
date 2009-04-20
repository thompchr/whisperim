 /**************************************************************************
u * Copyright 2009 Chris Thompson                                           *
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
package org.whisperim.android.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.whisperim.android.WhisperIM;
import org.whisperim.android.buddylist.BuddyListModel;
import org.whisperim.android.tests.TestConnection;
import org.whisperim.client.Buddy;
import org.whisperim.client.ConnectionManager;
import org.whisperim.client.Message;
import org.whisperim.client.MessageProcessor;
import org.whisperim.events.EncryptionEvent;
import org.whisperim.events.SessionEvent;
import org.whisperim.ui.UIController;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Controller implements UIController {
	
	private Context android_;
	private MessageProcessor mp_;
	private ConnectionManager cm_;
	private HashMap<Buddy, View> openWindows_ = new HashMap<Buddy, View>();
	private BuddyListModel blm_ = new BuddyListModel();
	private ListView buddyList_;
	public Controller(Context context, ConnectionManager cm){
		android_ = context;
		cm_ = cm;
		cm_.setClient(this);	
		

		//Create the buddy list
		buddyList_ = new BuddyList(android_);
		if (android_ instanceof Activity){
			((Activity)android_).setContentView(buddyList_);
			((WhisperIM) android_).setCurView(buddyList_);
			((Activity)android_).setTitle("WhisperIM -- Buddy List");
		}
		
        buddyList_.setAdapter(blm_);
        
        buddyList_.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long arg3) {
				Buddy b = null;
				if (parent.getItemAtPosition(position) instanceof Buddy){
					b = (Buddy)parent.getItemAtPosition(position);
				}
				if (openWindows_.get(b) == null){
					
					ChatWindow cw = new ChatWindow(android_, b, Controller.this);
					openWindows_.put(b, cw);
				}else{
					
				}
				
			}
        	
        });
        
        //Register the test connection
        cm_.registerConnection("Testconnection", new TestConnection());
        cm_.loadConnection("Testconnection", "Username", "Password");
        
        
	}
	
	public void cleanUp(){
		cm_.signOff();
	}
	
	public void sendMessage(Message m){
		mp_.sendMessage(m);
	}

	@Override
	public void addBuddies(ArrayList<Buddy> buddies) {
		for(Buddy b:buddies){
			blm_.add(b);
		}

		
	}

	@Override
	public void keyReceived(Buddy b) {
		

	}

	@Override
	public void processEvent(EncryptionEvent e) {
	

	}

	@Override
	public void processEvent(SessionEvent e) {


	}

	@Override
	public void receiveMessage(Message m) {


	}

	@Override
	public void removeBuddies(ArrayList<Buddy> buddies) {


	}

	@Override
	public void setMessageProcessor(MessageProcessor mp) {
		mp_ = mp;
	}

	@Override
	public void statusUpdate(String status, String account) {

		
	}

	@Override
	public void updateBuddyList(ArrayList<Buddy> buddies) {
		
		for(Buddy b:buddies){
			blm_.add(b);
		}

		
	}

}
