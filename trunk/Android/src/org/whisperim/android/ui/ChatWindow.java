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

import org.whisperim.client.Buddy;
import org.whisperim.client.Message;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChatWindow extends LinearLayout implements OnKeyListener {
	
	
	private Context android_;
	private TextView messageHistory_;
	private Controller parent_;
	private EditText messageBox_;
	private Buddy buddy_;
	

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
			Log.i("WhisperIM", "Set Title:" + buddy_.getAlias());
			((Activity) android_).setTitle("WhisperIM - Chat with " + buddy_.getAlias());
		}
		
		show();
		
	}
	
	public void show(){
		Log.i("WhisperIM", "Attempting to show ChatWindow Layout");
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
	
	public void receiveMessage(Message m){
		Log.i("WhisperIM", "Message processed by ChatWindow:" + m.getMessage());
		appendMessage(m);
	}
	
	private void appendMessage(Message m){
		DateFormat d = DateFormat.getTimeInstance(DateFormat.MEDIUM);
		messageHistory_.append(buddy_.getAlias() + " (" + d.format(m.getTimeSent()) + "): " + clearHTMLTags(m.getMessage()) + "\n");
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

}
