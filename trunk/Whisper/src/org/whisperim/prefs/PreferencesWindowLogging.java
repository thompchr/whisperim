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
 * PreferencesWindowLogging.java
 * 
 * Controls the logging functionality
 */


package org.whisperim.prefs;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.whisperim.prefs.Preferences;
/**
 * @author Cory Plastek
 */
public class PreferencesWindowLogging extends JPanel implements ItemListener {

	private static final long serialVersionUID = 6555858513398336341L;
	
	private boolean loggingEnabled_ = Preferences.getInstance().getLoggingEnabled(); 
	private JCheckBox loggingCheckBox_;
	
	PreferencesWindowLogging() {
		
		loggingEnabled_ = Preferences.getInstance().getLoggingEnabled();
		
		setBackground(Color.white);
		
		loggingCheckBox_ = new JCheckBox("Logging Enabled");
		loggingCheckBox_.setBackground(Color.white);
		loggingCheckBox_.setSelected(loggingEnabled_);
		loggingCheckBox_.setMnemonic(KeyEvent.VK_L);
		loggingCheckBox_.addItemListener(this);
		add(loggingCheckBox_);
		
	}

	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItem();
		if(source == loggingCheckBox_) {
			loggingEnabled_ = loggingCheckBox_.isSelected();
			Preferences.getInstance().setLoggingEnabled(loggingEnabled_);
			System.out.println("logging " + ((loggingEnabled_)?"enabled":"not enabled").toString());
			}
	}
	
}
