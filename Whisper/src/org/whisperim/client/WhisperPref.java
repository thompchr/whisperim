/**************************************************************************
 * Copyright 2009 Cory Plastek                                         *
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
 * WhisperPref.java
 *
 * Created on Dec 5, 2008, 5:17:55 PM
 */

package org.whisperim.client;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 *
 * @author Cory Plastek
 */
public class WhisperPref extends JPanel {
	
	private static final String ENCRYPT_ = "Encrypt";

    /** Creates new form WhisperPref */
    public WhisperPref() {

		//set native look and feel
		try  {  
			//Tell the UIManager to use the platform look and feel  
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());  
		}  
		catch(Exception e) {  
			//Do nothing  
		}
		
	    Encrypt = new JCheckBox();
	    Encrypt.setText(ENCRYPT_);
	
	    GroupLayout layout = new GroupLayout(this);
	    this.setLayout(layout);
	    layout.setHorizontalGroup(
	    		layout.createSequentialGroup()
	            	.addContainerGap()
	            	.addComponent(Encrypt, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	            	.addContainerGap()
	     );
	    layout.setVerticalGroup(
	        layout.createParallelGroup()
	        	.addGroup(layout.createSequentialGroup()
	        			.addContainerGap()
	        			.addComponent(Encrypt, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	        			.addContainerGap()
	        	)
	    );
	    pack();
    }

    private JCheckBox Encrypt;

}
