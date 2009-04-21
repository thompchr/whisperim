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
package org.whisperim.android.ui;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.whisperim.client.Buddy;
import org.whisperim.client.Message;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChatWindow extends LinearLayout implements OnKeyListener, OnMenuItemClickListener {


	private Context android_;
	private TextView messageHistory_;
	private Controller parent_;
	private EditText messageBox_;
	private Buddy buddy_;
	private MenuItem encryption_ = null;
	
	private static final int SEND_KEY = 0;
	private static final int ENCRYPTION = 1;

	private Handler handler_ = new Handler();


	public ChatWindow(Context context, Buddy buddy, Controller parent) {
		super(context);

		android_ = context;
		parent_ = parent;
		buddy_ = buddy;
		Log.i("WhisperIM", "Creating ChatWindow Object");


		LinearLayout.LayoutParams hlp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 350);
		LinearLayout.LayoutParams mlp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 60);

		setOrientation(VERTICAL);


		messageHistory_ = new TextView(android_);
		messageBox_ = new EditText(android_);


		messageBox_.setOnKeyListener(this);

		addView(messageHistory_, hlp);
		addView(messageBox_, mlp);

		messageHistory_.setVisibility(VISIBLE);
		messageBox_.setVisibility(VISIBLE);


		if (android_ instanceof Activity){
			((Activity)android_).registerForContextMenu(this);
		}
		show();



	}



	public void show(){

		Log.i("WhisperIM", "Attempting to show ChatWindow Layout");
		if (android_ instanceof Activity){
			Log.i("WhisperIM", "Set Title:" + buddy_.getAlias());
			((Activity) android_).setTitle("WhisperIM - Chat with " + buddy_.getAlias());
		}
		parent_.setView(this);
	}


	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN)){
			Log.i("WhisperIM", "KeyCodeIntercepted: " + keyCode);

			if (messageBox_.getText().toString().equalsIgnoreCase("")){
				return true;
			}

			sendMessage();
			return true;
		}
		return false;
	}

	private void sendMessage(){
		Date da = Calendar.getInstance().getTime();
		Message temp = new Message(new Buddy(buddy_.getAssociatedLocalHandle(), buddy_.getAssociatedLocalHandle(), 
				buddy_.getProtocolID()), buddy_, messageBox_.getText().toString(), buddy_.getProtocolID(), da);
		messageBox_.setText("");
		parent_.sendMessage(temp);
		appendMessage(temp);
	}

	public void receiveMessage(final Message m){
		handler_.post(new Runnable(){

			@Override
			public void run() {
				receiveMessageImpl(m);
			}
		});
	}

	private void receiveMessageImpl(Message m){
		Log.i("WhisperIM", "Message processed by ChatWindow:" + m.getMessage());
		appendMessage(m);
	}

	private void appendMessage(Message m){
		DateFormat d = DateFormat.getTimeInstance(DateFormat.MEDIUM);
		messageHistory_.setText(messageHistory_.getText() + m.getFromBuddy().getAlias() + " (" + d.format(m.getTimeSent()) + "): " + m.getMessage() + "\n");
	}

	public String clearHTMLTags(String strHTML){
		Pattern pattern = Pattern.compile("<[^>]*>");
		return pattern.matcher(strHTML).replaceAll(""); 
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		Log.i("WhisperIM", "ChatWindow attached to window.");
		if (android_ instanceof Activity){
			Log.i("WhisperIM", "Set Title:" + buddy_.getAlias());
			((Activity) android_).setTitle("WhisperIM - Chat with " + buddy_.getAlias());
		}
	}

	@Override
	protected void onCreateContextMenu(ContextMenu menu) {
		super.onCreateContextMenu(menu);

		MenuItem sendKey = menu.add(0, SEND_KEY, 0, "Send Key");
		sendKey.setOnMenuItemClickListener(this);
		encryption_ = menu.add(0, ENCRYPTION, 0, "Encryption");
		encryption_.setOnMenuItemClickListener(this);
		encryption_.setCheckable(true);
		if (!parent_.haveKey(buddy_)){
			encryption_.setEnabled(false);
		}


	}
	
	public void keyReceived(){
		if (encryption_ != null){
			encryption_.setEnabled(true);
		}
	}



	@Override
	public boolean onMenuItemClick(MenuItem item) {
		if (item.getItemId() == SEND_KEY){
			Base64 b64 = new Base64();
			Message keyMsg = new Message(new Buddy(buddy_.getAssociatedLocalHandle(), buddy_.getAssociatedLocalHandle(), buddy_.getProtocolID()), 
					buddy_, 
					"<whisperim keyspec=" + new String(b64.encode(parent_.getMyKey().getEncoded())) + "-- />", buddy_.getProtocolID(),
					Calendar.getInstance().getTime());
			parent_.sendMessage(keyMsg);
			return true;
		}else if(item.getItemId() == ENCRYPTION){
			if (!item.isChecked()){
				if (parent_.haveKey(buddy_)){
					parent_.enableEncryption(buddy_);
					item.setChecked(true);
					return true;
				}

			}else{
				parent_.disableEncryption(buddy_);
				item.setCheckable(false);
			}
		}
		return false;
	}





}
