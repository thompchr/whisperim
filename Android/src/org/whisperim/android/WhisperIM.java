package org.whisperim.android;

import java.util.LinkedList;

import org.whisperim.android.files.AndroidFileStreamCoordinator;
import org.whisperim.android.ui.Controller;
import org.whisperim.client.ConnectionManager;
import org.whisperim.client.MessageProcessor;
import org.whisperim.client.MessageProcessorImpl;
import org.whisperim.client.Whisper;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

public class WhisperIM extends Activity {
	
	//This data member will allow us to intercept the back button
	//and allow it to behave as expected
	private LinkedList<View> viewStack_ = new LinkedList<View>();
	
	private Controller c_;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        Log.i("WhisperIM","WhisperIM is starting");
        
        //Start the UI
        Log.i("WhisperIM","Loading keys");
        MessageProcessor mp = new MessageProcessorImpl(new Whisper(new AndroidFileStreamCoordinator(this)).getKeys());
        ConnectionManager cm = new ConnectionManager(mp);
        
        Log.i("WhisperIM","Starting UI");
        
        c_ = new Controller(this, cm);
        
    }
    
    public void setView(View view){
    	viewStack_.add(view);
    	setContentView(view);
    }
    
    public void setCurView(View view){
    	
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
    	if (keyCode == KeyEvent.KEYCODE_BACK){
    		if (viewStack_.size() == 1){
    			//Right now this exits, what would be
    			//awesome is if we had it simply hide
    			c_.cleanUp();
    			super.finish();
    		}else{
    			viewStack_.removeLast();
    			setContentView(viewStack_.getLast());
    			
    		}
    		
    	}
    	return true;
    }
    

}