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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * @author Cory Plastek
 *
 */
@SuppressWarnings("serial")
public class PreferencesWindowCategoryRenderer extends DefaultListCellRenderer {

	final Font normalFont_ = new Font("Heletica", Font.PLAIN, 12);
	final Font selectedFont_ = new Font("Helvetica", Font.BOLD, 12);
	JLabel renderer_;
	Dimension rendererSize_ = new Dimension(100,25);
	
	public PreferencesWindowCategoryRenderer() {
		super();
		setOpaque(true);
		setHorizontalAlignment(LEFT);
		setVerticalAlignment(CENTER);
	}
	
	public Component getListCellRendererComponent(JList list, Object category, int index, boolean isSelected, boolean hasFocus) {

		renderer_ = (JLabel) super.getListCellRendererComponent(list, category, index, isSelected, hasFocus);
		renderer_.setPreferredSize(rendererSize_);
		renderer_.setMinimumSize(rendererSize_);
		
		
		list.setBackground(new Color(239,239,239));
		list.setBorder(null);
		list.setFont(normalFont_);
		
        if(isSelected) {
        	setFont(selectedFont_);
        	setBackground(Color.white);
        	setForeground(Color.black);
        	setBorder(noFocusBorder);
        }
		
        return renderer_;
	}
}
