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
	private JButton okBtn_ = new JButton("OK");
	private JButton cancelBtn_ = new JButton("Cancel");
	
	public WhisperNewIMWindow(){
		SpringLayout sl = new SpringLayout();
		Container cp = getContentPane();
		cp.setLayout(sl);
		
		cp.add(foreignHandleLbl_);
		cp.add(foreignHandleBox_);
		cp.add(protocolSelector_);
		cp.add(okBtn_);
		cp.add(cancelBtn_);
		
		//Constraints
		sl.putConstraint(SpringLayout.WEST, foreignHandleLbl_, 10, SpringLayout.WEST, cp);
		sl.putConstraint(SpringLayout.WEST, foreignHandleBox_, 5, SpringLayout.EAST, foreignHandleLbl_);
		sl.putConstraint(SpringLayout.NORTH, protocolSelector_, 20, SpringLayout.NORTH, cp);
		
		pack();
		setVisible(true);
		
		
	}
	
	
	
}
