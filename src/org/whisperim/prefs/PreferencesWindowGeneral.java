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
 * PreferencesWindowGeneral.java
 * 
 * Preferences Window - General
 * Contains and draws the 'General' category in PreferencesWindow
 */

package org.whisperim.prefs;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class PreferencesWindowGeneral extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 1843003640261951662L;
	private static final String WHISPER_THEME_ = "Whisper Theme";
	private JLabel whisperTheme_;
	private JRadioButton metal_;
	private JRadioButton system_;
	private ButtonGroup theme_;
	
	PreferencesWindowGeneral() {
		
		setBackground(Color.white);
		
		whisperTheme_ = new JLabel(WHISPER_THEME_);
		add(whisperTheme_);
		
		metal_ = new JRadioButton(Preferences.METAL_);
		metal_.setMnemonic(KeyEvent.VK_M);
		metal_.setBackground(Color.white);
		metal_.setActionCommand(Preferences.METAL_);
		metal_.addActionListener(this);
		
		system_ = new JRadioButton(Preferences.SYSTEM_);
		system_.setMnemonic(KeyEvent.VK_S);
		system_.setBackground(Color.white);
		system_.setActionCommand(Preferences.SYSTEM_);
		system_.addActionListener(this);
		
		if(Preferences.METAL_ == Preferences.getInstance().getLookAndFeel()) {
			metal_.setSelected(true);
		}
		else {
			system_.setSelected(true);
		}
		
		theme_ = new ButtonGroup();
		theme_.add(metal_);
		theme_.add(system_);
		
		add(metal_);
		add(system_);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == metal_) {
			Preferences.getInstance().setLookAndFeel(Preferences.METAL_);
		}
		else if(source == system_) {
			Preferences.getInstance().setLookAndFeel(Preferences.SYSTEM_);
		}
		else {
		}
	}
	
}
