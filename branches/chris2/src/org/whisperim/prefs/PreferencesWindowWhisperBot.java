/**************************************************************************
 * Copyright 2009 Nick Krieble                                             *
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
package org.whisperim.prefs;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.whisperim.client.Whisper;
import org.whisperim.whisperbot.WhisperBot;

import com.aol.acc.AccException;

public class PreferencesWindowWhisperBot extends JPanel implements ItemListener {

	private static final long serialVersionUID = -2596728130834832867L;
	
	private boolean whisperbotEnabled_ = Preferences.getInstance().getWhisperBotEnabled();
	private JCheckBox whisperbotCheckBox_ ;
	
	PreferencesWindowWhisperBot () {
		
		setBackground(Color.white);
		
		whisperbotCheckBox_ = new JCheckBox("WhisperBot Enabled");
		whisperbotCheckBox_.setBackground(Color.white);
		whisperbotCheckBox_.setSelected(whisperbotEnabled_);
		whisperbotCheckBox_.setMnemonic(KeyEvent.VK_L);
		whisperbotCheckBox_.addItemListener(this);
		add(whisperbotCheckBox_);

	
	Preferences.getInstance().getListeners().add(new PrefListener() {
		private boolean locked = false;
		@Override
		public void prefChanged(String name, Object o) {
			if(Preferences.WHISPERBOT_.equals(name) && !locked){
				locked = true;
				if(!o.equals(whisperbotCheckBox_.isSelected())){
					whisperbotCheckBox_.setSelected(!whisperbotCheckBox_.isSelected());
					// Start up whisperbot.
					try {
						new WhisperBot();
					} catch (AccException e2) {
						e2.printStackTrace();
					}
				}else{
					//new Whisper();
				}
				locked = false;
			}
		}
	});}
	
	public void itemStateChanged(ItemEvent e) {
		Object sources = e.getItem();
		if(sources == whisperbotCheckBox_) {
			whisperbotEnabled_ = whisperbotCheckBox_.isSelected();
			Preferences.getInstance().setWhisperBotEnabled(whisperbotEnabled_);
			System.out.println("whisperbot " + ((whisperbotEnabled_)?"enabled":"not enabled").toString());
			
		}
		
	}

}
