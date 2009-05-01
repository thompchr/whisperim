/**************************************************************************
 * Copyright 2009 Chris Thompson                                           *
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
import java.io.File;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.whisperim.client.ConnectionStrategy;

/**
 * 
 * @author Chris Thompson
 *
 */
public class ProtocolRenderer implements ListCellRenderer {

	protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
	private ImageIcon serviceIcon_;
	
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
	
	public Component getListCellRendererComponent(JList list, Object protocol, int index, boolean isSelected, boolean hasFocus) {

		JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, protocol, index, isSelected, hasFocus);

		ConnectionStrategy p = (ConnectionStrategy) protocol;
		if (p.getPluginIconLocation().equalsIgnoreCase("")){
			serviceIcon_ = null;//new ImageIcon(".." + File.separator + "images" + File.separator + "firewall_icon_small.png");
		}else{
			serviceIcon_ = new ImageIcon(p.getPluginIconLocation().replace("/", File.separator).replace("\\", File.separator));
		}
		renderer.setIcon(serviceIcon_);
		renderer.setText(((ConnectionStrategy) protocol).toString().substring(((ConnectionStrategy) protocol).toString().indexOf(":") + 1));
		return renderer;
	}

}
