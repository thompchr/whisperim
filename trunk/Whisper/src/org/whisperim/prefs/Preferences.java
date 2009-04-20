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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


public class Preferences {

	// for singleton
	private static Preferences instance = null;
	
	// for prefs listener
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
	
	/*
	 * General Preferences
	 */
	//look and feel
	//acceptable strings 'Metal' 'System'
	public static final String THEME_ = "Theme";
	public static final String METAL_ = "Metal"; //UIManager.getCrossPlatformLookAndFeelClassName();
	public static final String SYSTEM_ = "Native"; //UIManager.getSystemLookAndFeelClassName();
	private String lookAndFeel_ = SYSTEM_;	

	//images
	private final static String imagesDir_ = System.getProperty("user.dir").replace("lib","images")+File.separator;
	private static final ImageIcon whisperIconSmall_ = new ImageIcon(imagesDir_+"WhisperIMLogo-Small.png","");
	private static final ImageIcon whisperIconBig_ = new ImageIcon(imagesDir_+"WhisperIMLogo-Big.png","");
	private static final ImageIcon aimIconSmall_ = new ImageIcon(imagesDir_+"aim_icon_small.png","AIM");
	private static final ImageIcon aimIconMed_ = new ImageIcon(imagesDir_+"aim_icon_med.png","AIM");
	private static final ImageIcon aimIconBig_ = new ImageIcon(imagesDir_+"aim_icon_big.png","AIM");
	
	
	/*
	 * Logging Preferences
	 */
	private boolean loggingEnabled_ = true;
	public static final String LOGGING_ = "Logging";
	
	private String loggingDir_ = System.getProperty("user.home")+File.separator+"Whisper Logs"+File.separator;

	/*
	 * Security Preferences
	 */
	//locking
	private boolean locked_ = false;
	public static final String LOCKED_ = "Locked";
	private static char[] lockPassword_;
	
	private boolean encryptionEnabled_ = true;
	public static final String ENCRYPTION_ = "Encryption";

	/*
	 * Sound Preferences
	 */
	private boolean soundsEnabled_ = false;
	public static final String SOUNDS_ = "Sounds";

	/*
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

		//ignore images
		xstream.omitField(Preferences.class, "whisperIconSmall_");
		xstream.omitField(Preferences.class, "whisperIconBig_");
		xstream.omitField(Preferences.class, "aimIconSmall_");
		xstream.omitField(Preferences.class, "aimIconMed_");
		xstream.omitField(Preferences.class, "aimIconBig_");
		
		//ignore listeners
		xstream.omitField(Preferences.class, "listeners_");
		
		//ignore the locked status
		xstream.omitField(Preferences.class, "locked_");
		
		// Class setup
		xstream.alias("Preferences", Preferences.class);
		try {
			FileWriter out = new FileWriter(new File(PREFS_FILE));
			xstream.toXML(this, out);
			out.flush();
			out.close();
		} catch (Exception e) {

		}
	}

	// for singleton
	public static Preferences getInstance() {
		if (instance == null) {
			try {
				XStream xstr = new XStream(new DomDriver());
				xstr.alias("Preferences", Preferences.class);

				instance = (Preferences) xstr.fromXML(new FileInputStream(
						new File(PREFS_FILE)));
				instance.listeners_ = new ArrayList<PrefListener>();
			} catch (Exception e) {
				e.printStackTrace();
				instance = new Preferences();
			}

		}
		return instance;
	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	// accessors/mutators
	/*
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
	
	
	/*
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

	/*
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

	//locking
	public boolean isLocked() {
		return locked_;
	}
	
	public void setLocked(boolean lockWhisper) {
		locked_ = lockWhisper;
		for (PrefListener listener : listeners_) {
			listener.prefChanged(LOCKED_, locked_);
		}
	}
	
	public char[] getLockPassword() {
		return lockPassword_;
	}
	
	public void setLockPassword(char[] password) {
		lockPassword_ = password;
	}
	
	
	/*
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

	/*
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

	/*
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
