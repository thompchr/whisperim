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
package org.whisperim.client;



import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class WhisperNewIMWindow extends JFrame{

	private JTextField foreignHandleBox_ = new JTextField();
	private JComboBox protocolSelector_ = new JComboBox();
	private JLabel foreignHandleLbl_ = new JLabel("Screen Name: ");
	private JButton okBtn_ = new JButton("Ok");
	private JButton cancelBtn_ = new JButton("Cancel");
	
	public WhisperNewIMWindow(ConnectionManager manager){
		SpringLayout sl = new SpringLayout();
		Container cp = getContentPane();
		cp.setLayout(sl);
		
		cp.add(foreignHandleLbl_);
		cp.add(foreignHandleBox_);
		cp.add(protocolSelector_);
		cp.add(okBtn_);
		cp.add(cancelBtn_);
		
		setMinimumSize(new Dimension(350, 175));
		setMaximumSize(new Dimension(350, 175));
		
		okBtn_.setMinimumSize(new Dimension(75, 26));
		okBtn_.setMaximumSize(new Dimension(75, 26));
		okBtn_.setPreferredSize(new Dimension(75, 26));
		
		cancelBtn_.setMinimumSize(new Dimension(75, 26));
		cancelBtn_.setMaximumSize(new Dimension(75, 26));
		cancelBtn_.setPreferredSize(new Dimension(75, 26));
		
		foreignHandleBox_.setMinimumSize(new Dimension(150, 23));
		foreignHandleBox_.setMaximumSize(new Dimension(150, 23));
		foreignHandleBox_.setPreferredSize(new Dimension(150, 23));
		
		protocolSelector_.setMinimumSize(new Dimension(240, 30));
		protocolSelector_.setMaximumSize(new Dimension(240, 30));
		protocolSelector_.setPreferredSize(new Dimension(240, 30));
		
		setTitle("New Instant Message");
		
		
		//Constraints
		sl.putConstraint(SpringLayout.WEST, foreignHandleLbl_, 20, SpringLayout.WEST, cp);
		sl.putConstraint(SpringLayout.NORTH, foreignHandleLbl_, 20, SpringLayout.NORTH, cp);
		
		sl.putConstraint(SpringLayout.WEST, foreignHandleBox_, 5, SpringLayout.EAST, foreignHandleLbl_);
		sl.putConstraint(SpringLayout.NORTH, foreignHandleBox_, 17, SpringLayout.NORTH, cp);
		
		sl.putConstraint(SpringLayout.NORTH, protocolSelector_, 50, SpringLayout.NORTH, cp);
		sl.putConstraint(SpringLayout.WEST, protocolSelector_, 60, SpringLayout.WEST, cp);
		
		sl.putConstraint(SpringLayout.NORTH, cancelBtn_, 50, SpringLayout.NORTH, protocolSelector_);
		sl.putConstraint(SpringLayout.WEST, cancelBtn_, 20, SpringLayout.EAST, okBtn_);
		
		sl.putConstraint(SpringLayout.WEST, okBtn_, 90, SpringLayout.WEST, cp);
		sl.putConstraint(SpringLayout.NORTH, okBtn_, 50, SpringLayout.NORTH, protocolSelector_);
		
		
		for (Entry<String, ConnectionStrategy> entry:manager.getStrategies().entrySet()){
			ConnectionStrategy cs = (ConnectionStrategy)entry.getValue();
			protocolSelector_.addItem(cs.getIdentifier());
		}
		
		
		pack();
		setVisible(true);
		
		
	}
	
	
	
	
}