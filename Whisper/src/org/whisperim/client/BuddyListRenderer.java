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
package org.whisperim.client;

import java.awt.Component;
import java.util.Hashtable;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * @author Cory Plastek
 *
 */
public class BuddyListRenderer implements ListCellRenderer {
	protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
	
	/** Returns an ImageIcon, or null if the path was invalid. */
	protected ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}
	
	private ImageIcon serviceIcon_ = new ImageIcon("..\\images\\default.ico");
	private ImageIcon aimIcon_ = new ImageIcon("..\\images\\aim_icon_small.png");
	
		
	public Component getListCellRendererComponent(JList list, Object buddy, int index, boolean isSelected, boolean hasFocus) {

		JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, buddy, index, isSelected, hasFocus);

		    if (((Buddy) buddy).getProtocolID().equals("aol")) {
		      //buddy is on aim
		      serviceIcon_ =  aimIcon_;
		    } else {
		      //use default service icon
		    }
		    renderer.setIcon(serviceIcon_);
		    return renderer;
	}
}
