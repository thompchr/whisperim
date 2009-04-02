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
package org.whisperim.renderers;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.whisperim.client.Buddy;
import org.whisperim.prefs.Preferences;

/**
 * @author Cory Plastek
 *
 */
public class BuddyListRenderer implements ListCellRenderer {
	protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
	
	private ImageIcon serviceIcon_ = null;
	private final ImageIcon aimIcon_ = Preferences.getInstance().getAimIconSmall();
		
	public Component getListCellRendererComponent(JList list, Object buddy, int index, boolean isSelected, boolean hasFocus) {

		Component renderer = defaultRenderer.getListCellRendererComponent(list, buddy, index, isSelected, hasFocus);

		    if (((Buddy) buddy).getProtocolID().equals("AIM")) {
		      //buddy is on aim
		      serviceIcon_ =  aimIcon_;
		    } else {
		      //serviceIcon_ = aimIcon_;
		    }
		    ((JLabel) renderer).setIcon((Icon)serviceIcon_);
		    return renderer;
	}
}
