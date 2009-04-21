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
import org.whisperim.android.connections.GTalkStrategy;
import org.whisperim.android.connections.SettingsDialog;
import org.whisperim.client.Buddy;
import org.whisperim.client.ConnectionManager;
import org.whisperim.client.Message;
import org.whisperim.client.MessageProcessor;
import org.whisperim.events.EncryptionEvent;
import org.whisperim.events.SessionEvent;
import org.whisperim.ui.UIController;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Controller implements UIController {

	private Context android_;
	private WhisperIM parent_;
	private MessageProcessor mp_;
	private ConnectionManager cm_;
	private HashMap<Buddy, ChatWindow> openWindows_ = new HashMap<Buddy, ChatWindow>();
	private BuddyListModel blm_ = new BuddyListModel();
	private ListView buddyList_;
	public Controller(Context context, ConnectionManager cm){
		android_ = context;
		parent_ = (WhisperIM)context;
		cm_ = cm;
		cm_.setClient(this);	


		//Create the buddy list
		buddyList_ = new BuddyList(android_);

		setView(buddyList_);
		
		if (android_ instanceof Activity){
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
				}else{
					Log.e("WhisperIM", "Invalid Buddy Object");
					return;
				}
				if (openWindows_.get(b) == null){

					ChatWindow cw = new ChatWindow(android_, b, Controller.this);
					openWindows_.put(b, cw);
				}else{
					openWindows_.get(b).show();
				}

			}

		});

		//Register the test connection
		//cm_.registerConnection("Testconnection", new TestConnection());
		//cm_.loadConnection("Testconnection", "Username", "Password");
		cm_.registerConnection("GTalk", new GTalkStrategy());
		new SettingsDialog(android_, this).show();


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

	public void setView(View view){
		parent_.setView(view);
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
	
	public void signOnGtalk(String username, String password){
		cm_.loadConnection("GTalk", username, password);
	}

	@Override
	public void receiveMessage(Message m) {
		Log.i("WhisperIM", "Message Received: " + m.getTo());
		Log.i("WhisperIM", "		    From: " + m.getFrom());
		Log.i("WhisperIM", "		 Message: " + m.getMessage());

		if (openWindows_.get(m.getFromBuddy()) != null){
			Log.i("WhisperIM", "Open window found.");
			openWindows_.get(m.getFromBuddy()).receiveMessage(m);
		}else{
			//Open a new window
			Log.i("WhisperIM", "Window could not be found");
			ChatWindow cw = new ChatWindow(android_, m.getFromBuddy(), Controller.this);
			openWindows_.put(m.getFromBuddy(), cw);
			cw.receiveMessage(m);
			
		}


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
