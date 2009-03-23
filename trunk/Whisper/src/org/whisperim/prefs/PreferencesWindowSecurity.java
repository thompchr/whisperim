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

import javax.swing.JLabel;
import javax.swing.JPanel;

public class PreferencesWindowSecurity extends JPanel {

	private static final long serialVersionUID = 5359436260113518838L;
	private JLabel label_;
	
	PreferencesWindowSecurity() {
		
		setBackground(Color.white);
		label_ = new JLabel("security shit");
		add(label_);	
		
	}
	
}
