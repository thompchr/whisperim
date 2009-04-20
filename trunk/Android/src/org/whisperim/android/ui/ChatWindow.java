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

import java.util.Calendar;

import org.whisperim.client.Buddy;
import org.whisperim.client.Message;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.LinearLayout;

public class ChatWindow extends LinearLayout implements OnKeyListener {
	
	private Context android_;
	private EditText messageHistory_;
	private Controller parent_;
	private EditText messageBox_;
	private Buddy buddy_;
	

	public ChatWindow(Context context, Buddy buddy, Controller parent) {
		super(context);
		if (android_ instanceof Activity){
			((Activity)android_).setContentView(R.layout.chatwindow_layout);
		}else {
			return;
		}
		setFocusable(true);
		android_ = context;
		parent_ = parent;
		buddy_ = buddy;
		messageHistory_ = (EditText) findViewById(R.id.messageHistory_);
		messageBox_ = (EditText) findViewById(R.id.messageBox_);
		

		messageBox_.setOnKeyListener(this);
		setOnKeyListener(this);
		
		if (android_ instanceof Activity){
			((Activity) android_).setTitle(buddy_.getAlias());
		}
		
		
		addView(messageHistory_);
		addView(messageBox_);
		
	}
	
	public void show(){
		if (android_ instanceof Activity){
			((Activity)android_).setContentView(R.layout.chatwindow_layout);
		}
	}


	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		Log.i("WhisperIM", "KeyCodeIntercepted: " + keyCode);

		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER){
			parent_.sendMessage(new Message(new Buddy(buddy_.getAssociatedLocalHandle(), buddy_.getAssociatedLocalHandle(), 
					buddy_.getProtocolID()), buddy_, messageBox_.getText().toString(), buddy_.getProtocolID(), Calendar.getInstance().getTime()));
			messageBox_.setText("");
			return true;
		}
		return false;
	}

}
