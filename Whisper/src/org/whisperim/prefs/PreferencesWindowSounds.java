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

package org.whisperim.prefs;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class PreferencesWindowSounds extends JPanel implements ItemListener {

	private static final long serialVersionUID = -2242877811598122618L;
	
	private boolean soundsEnabled_ = Preferences.getInstance().getSoundsEnabled();
	private JCheckBox soundsCheckBox_ ;
	
	PreferencesWindowSounds() {
		
		setBackground(Color.white);
		
		soundsCheckBox_ = new JCheckBox("Sounds Enabled");
		soundsCheckBox_.setBackground(Color.white);
		soundsCheckBox_.setSelected(soundsEnabled_);
		soundsCheckBox_.setMnemonic(KeyEvent.VK_L);
		soundsCheckBox_.addItemListener(this);
		add(soundsCheckBox_);
		
		 Preferences.getInstance().getListeners().add(new PrefListener() {
				private boolean locked = false;
				@Override
				public void prefChanged(String name, Object o) {
					if(Preferences.SOUNDS_.equals(name) && !locked){
						locked = true;
						if(!o.equals(soundsCheckBox_.isSelected())){
							soundsCheckBox_.setSelected(!soundsCheckBox_.isSelected());
						}
						locked = false;
					}
				}
			});
	}

	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItem();
		if(source == soundsCheckBox_) {
			soundsEnabled_ = soundsCheckBox_.isSelected();
			Preferences.getInstance().setSoundsEnabled(soundsEnabled_);		
			System.out.println("sounds " + ((soundsEnabled_)?"enabled":"not enabled").toString());
			}
		
	}
}
