package org.whisperim.android;

import org.whisperim.android.files.AndroidFileStreamCoordinator;
import org.whisperim.android.ui.BuddyList;
import org.whisperim.android.ui.Controller;
import org.whisperim.client.ConnectionManager;
import org.whisperim.client.MessageProcessor;
import org.whisperim.client.MessageProcessorImpl;
import org.whisperim.client.Whisper;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

public class WhisperIM extends Activity {
	
	//This data member will allow us to intercept the back button
	//and allow it to behave as expected
	private View backView_;
	private View curView_;
	private Controller c_;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        
        //Start the UI
        
        MessageProcessor mp = new MessageProcessorImpl(new Whisper(new AndroidFileStreamCoordinator(this)).getKeys());
        ConnectionManager cm = new ConnectionManager(mp);
        
        c_ = new Controller(this, cm);
        
    }
    
    public void setBackView(View view){
    	backView_ = view;
    }
    
    public void setCurView(View view){
    	curView_ = view;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
    	if (keyCode == KeyEvent.KEYCODE_BACK){
    		if (curView_ instanceof BuddyList){
    			//Right now this exits, what would be
    			//awesome is if we had it simply hide
    			c_.cleanUp();
    			super.finish();
    		}else{
    			curView_ = backView_;
        		setContentView(backView_);
    		}
    		
    	}
    	return true;
    }
}