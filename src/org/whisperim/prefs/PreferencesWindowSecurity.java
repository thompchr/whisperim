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
 * PreferencesWindowSecurity.java
 */

package org.whisperim.prefs;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.whisperim.client.ThreadServices;
import org.whisperim.security.Locking;

public class PreferencesWindowSecurity extends JPanel implements ItemListener {

	private static final long serialVersionUID = 5359436260113518838L;
	
	private static final String LOCKED_ = "Locked";
	private boolean locked_ = Preferences.getInstance().isLocked();
	private JCheckBox lockCheckBox_;
	
	PreferencesWindowSecurity() {
		
		setBackground(Color.white);	
		
		lockCheckBox_ = new JCheckBox(LOCKED_);
		lockCheckBox_.setBackground(Color.white);
		lockCheckBox_.setSelected(locked_);
		lockCheckBox_.setMnemonic(KeyEvent.VK_L);
		lockCheckBox_.addItemListener(this);
		add(lockCheckBox_);
		
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItem();
		if(source == lockCheckBox_) {
			ThreadServices.get().runInThread(new Runnable() {
				public void run() {
					Locking locking_ = new Locking();
				}
			});
			
			locked_ = Preferences.getInstance().isLocked();
			lockCheckBox_.setSelected(locked_);
		}
	}	
}
