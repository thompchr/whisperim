/**************************************************************************
 * Copyright 2009 Cory Plastek                                             *
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


/*
 * Preferences.java
 */

package org.whisperim.prefs;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Cory Plastek
 * 
 */
public class Preferences {
	
	//for singleton
	private static Preferences instance = null;
	private List<PrefListener> listeners = new ArrayList<PrefListener>();
	
	//general
	//logging
	//security
	//sounds
	//whisperbot
	
	/**
	 * General Preferences
	 */
	private Image whisperIconSmall_ = Toolkit.getDefaultToolkit().getImage("..//images//WhisperIMLogo-Small.png");
	private Image whisperIconBig_ = Toolkit.getDefaultToolkit().getImage("..//images//WhisperIMLogo-Big.png");

	/**
	 * Logging Preferences
	 */
	private boolean loggingEnabled_;
	
	/**
	 * Security Preferences
	 */
	private boolean encryptionEnabled_;
	
	/**
	 * Sound Preferences
	 */
	private boolean soundsEnabled_;
	
	/**
	 * Whisperbot Preferences
	 */
	private boolean whisperBotEnabled_;
	
	
	
	protected Preferences() {
	    
		// Instantiating Xstream
		
		//XStream xstream = new XStream();
		//http://xstream.codehaus.org/tutorial.html
	
		
	}
	
	//for singleton
	public static Preferences getInstance() {
		if(instance == null) {
			instance = new Preferences();
		}
		return instance;
	}
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	//accessors/mutators
	/**
	 * General
	 */
	public Image getWhisperIconBig() {
		return whisperIconBig_;
	}
	
	public Image getWhisperIconSmall() {
		return whisperIconSmall_;
	}
	
	/**
	 * Logging
	 */
	public boolean getLoggingEnabled() {
		return loggingEnabled_;
	}
	
	public void setLoggingEnabled(boolean logging) {
		loggingEnabled_ = logging;
	}
	
	
	/**
	 * Security
	 */
	public boolean getEncryptionEnabled() {
		return encryptionEnabled_;
	}
	
	public void setEncryptionEnabled(boolean encryption) {
		encryptionEnabled_ = encryption;
	}
	
	
	/**
	 * Sounds
	 */
	public boolean getSoundsEnabled() {
		return soundsEnabled_;
	}
	
	public void setSoundsEnabled(boolean sounds) {
		soundsEnabled_ = sounds;
		for(PrefListener listener:listeners){
			listener.prefChanged("Sound", sounds);
		}
	}
	
	
	/**
	 * WhisperBot
	 */
	public boolean getWhisperBotEnabled() {
		return whisperBotEnabled_;
	}
	
	public void setWhisperBotEnabled(boolean whisperbot) {
		whisperBotEnabled_ = whisperbot;
	}

	public List<PrefListener> getListeners() {
		return listeners;
	}

	public void setListeners(List<PrefListener> listeners) {
		this.listeners = listeners;
	}

	
}
