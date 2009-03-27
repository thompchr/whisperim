/**************************************************************************
 * Copyright 2009 Cory Plastek modified by Nick Krieble                    *
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import sun.security.jca.GetInstance.Instance;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Cory Plastek
 * 
 */
public class Preferences {

	// for singleton
	private static Preferences instance = null;
	private List<PrefListener> listeners_ = new ArrayList<PrefListener>();

	// general
	// logging
	// security
	// sounds
	// whisperbot

	private static final String PREFS_FILE = System.getProperty("user.home") + File.separator + "Whisper" + File.separator + "prefs.xml";
	/**
	 * General Preferences
	 */

	private Image whisperIconSmall_ = Toolkit.getDefaultToolkit().getImage(
			"..//images//WhisperIMLogo-Small.png");
	private Image whisperIconBig_ = Toolkit.getDefaultToolkit().getImage(
			"..//images//WhisperIMLogo-Big.png");

	//look and feel
	//acceptable strings 'Metal' 'System'
	public static final String THEME_ = "Theme";
	public static final String METAL_ = "Metal"; //UIManager.getCrossPlatformLookAndFeelClassName();
	public static final String SYSTEM_ = "Native"; //UIManager.getSystemLookAndFeelClassName();
	private String lookAndFeel_ = SYSTEM_;

	/**
	 * Logging Preferences
	 */
	private boolean loggingEnabled_ = true;
	public static final String LOGGING_ = "Logging";

	/**
	 * Security Preferences
	 */
	private boolean encryptionEnabled_ = true;
	public static final String ENCRYPTION_ = "Encryption";

	/**
	 * Sound Preferences
	 */
	private boolean soundsEnabled_ = false;
	public static final String SOUNDS_ = "Sounds";

	/**
	 * Whisperbot Preferences
	 */
	private boolean whisperBotEnabled_ = false;
	public static final String WHISPERBOT_ = "WhisperBot";
	

	protected Preferences() {

	}

	// Saves prefs and writes to disk. This needs to be called when whisper
	// closes.
	public void savePrefs() {
		XStream xstream = new XStream(new DomDriver());

		// Class setup
		xstream.alias("Preferences", Preferences.class);
		try {
			xstream.toXML(this, new FileWriter(new File(PREFS_FILE)));
		} catch (Exception e) {

		}
	}

	// for singleton
	public static Preferences getInstance() {
		if (instance == null) {
			try {
				XStream xstream = new XStream(new DomDriver());
				xstream.alias("Preferences", Preferences.class);

				instance = (Preferences) xstream.fromXML(new FileInputStream(
						new File(PREFS_FILE)));
			} catch (Exception e) {
				instance = new Preferences();
			}

		}
		return instance;
	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	// accessors/mutators
	/**
	 * General
	 */
	public String getLookAndFeel() {
		return lookAndFeel_;
	}
	
	public void setLookAndFeel(String laf) {
		if(laf.equalsIgnoreCase(METAL_)) {
			lookAndFeel_ = laf;
		}
		else if(laf.equalsIgnoreCase(SYSTEM_)) {
			lookAndFeel_ = laf;
		}
		else {
			//look and feel not supported
		}
		for(PrefListener listener:listeners_){
			listener.prefChanged(THEME_, lookAndFeel_);
		}
	}
	
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
		for (PrefListener listener : listeners_) {
			listener.prefChanged(LOGGING_, loggingEnabled_);
		}
	}

	/**
	 * Security
	 */
	public boolean getEncryptionEnabled() {
		return encryptionEnabled_;
	}

	public void setEncryptionEnabled(boolean encryption) {
		encryptionEnabled_ = encryption;
		for (PrefListener listener : listeners_) {
			listener.prefChanged(ENCRYPTION_, encryptionEnabled_);
		}
	}

	/**
	 * Sounds
	 */
	public boolean getSoundsEnabled() {
		return soundsEnabled_;
	}

	public void setSoundsEnabled(boolean soundsEnabled) {
		soundsEnabled_ = soundsEnabled;
		for (PrefListener listener : listeners_) {
			listener.prefChanged(SOUNDS_, soundsEnabled_);
		}
	}

	/**
	 * WhisperBot
	 */
	public boolean getWhisperBotEnabled() {
		return whisperBotEnabled_;
	}

	public void setWhisperBotEnabled(boolean whisperbotEnabled) {
		whisperBotEnabled_ = whisperbotEnabled;
		for (PrefListener listener : listeners_) {
			listener.prefChanged(WHISPERBOT_, whisperBotEnabled_);
		}
	}

	/**
	 * Preferences Listener
	 */
	public List<PrefListener> getListeners() {
		return listeners_;
	}

	public void setListeners(List<PrefListener> listeners) {
		listeners_ = listeners;
	}
	
	//There really should be a an add/remove method for the listeners that takes a listener

}
