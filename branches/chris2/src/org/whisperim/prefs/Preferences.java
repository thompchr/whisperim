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

/**
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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

/**
 * @author Cory Plastek
 * 
 */
public class Preferences {

	// for singleton
	private static Preferences instance = null;
	private List<PrefListener> listeners_ = new ArrayList<PrefListener>();

	//general
	//accounts
	//logging
	//security
	//plugins
	//sounds
	//whisperbot
	//about

	private static final String PREFS_FILE = System.getProperty("user.home") + File.separator + "Whisper" + File.separator + "prefs.xml";
	
	/**
	 * General Preferences
	 */
	//look and feel
	//acceptable strings 'Metal' 'System'
	public static final String THEME_ = "Theme";
	public static final String METAL_ = "Metal"; //UIManager.getCrossPlatformLookAndFeelClassName();
	public static final String SYSTEM_ = "Native"; //UIManager.getSystemLookAndFeelClassName();
	private String lookAndFeel_ = SYSTEM_;

	
	private final static String imagesDir_ = System.getProperty("user.dir").replace("lib", "images")+File.separator;
	private ImageIcon whisperIconSmall_ = new ImageIcon(imagesDir_+"WhisperIMLogo-Small.png","");
	private ImageIcon whisperIconBig_ = new ImageIcon(imagesDir_+"WhisperIMLogo-Big.png","");
	private ImageIcon aimIconSmall_ = new ImageIcon(imagesDir_+"aim_icon_small.png","AIM");
	private ImageIcon aimIconMed_ = new ImageIcon(imagesDir_+"aim_icon_med.png","AIM");
	private ImageIcon aimIconBig_ = new ImageIcon(imagesDir_+"aim_icon_big.png","AIM");
	
	
	/**
	 * Logging Preferences
	 */
	private boolean loggingEnabled_ = true;
	public static final String LOGGING_ = "Logging";
	
	private static String loggingDir_ = System.getProperty("user.home")+File.separator+"Whisper Logs"+File.separator;

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
				XStream xstr = new XStream();
				xstr.alias("Preferences", Preferences.class);

				instance = (Preferences) xstr.fromXML(new FileInputStream(
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
	
	public ImageIcon getWhisperIconBig() {
		return whisperIconBig_;
	}

	public ImageIcon getWhisperIconSmall() {
		return whisperIconSmall_;
	}

	public ImageIcon getAimIconBig() {
		return aimIconBig_;
	}
	
	public ImageIcon getAimIconMed() {
		return aimIconMed_;
	}
	
	public ImageIcon getAimIconSmall() {
		return aimIconSmall_;
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
	
	public String getLoggingDir() {
		return loggingDir_;
	}
	
	public void setLoggingDir(String logDir) {
		//check actual directory?
		loggingDir_ = logDir;
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
