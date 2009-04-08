package org.whisperim.android;

import org.whisperim.android.ui.Controller;
import org.whisperim.client.ConnectionManager;
import org.whisperim.client.MessageProcessor;
import org.whisperim.client.MessageProcessorImpl;
import org.whisperim.client.Whisper;

import android.app.Activity;
import android.os.Bundle;

public class WhisperIM extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //Start the UI
        MessageProcessor mp = new MessageProcessorImpl(new Whisper().getKeys());
        ConnectionManager cm = new ConnectionManager(mp);
        new Controller(this, cm);
        
    }
}