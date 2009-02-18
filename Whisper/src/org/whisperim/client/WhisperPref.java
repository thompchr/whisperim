/**************************************************************************
 * Copyright 2009 Nick Krieble                                         *
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

import java.awt.Checkbox;

import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 *
 * @author bankskp, Cory Plastek
 */
public class WhisperPref extends JPanel {

    /** Creates new form WhisperPref */
    public WhisperPref() {
        initComponents();
    }

    private void initComponents() {

    	//set native look and feel
		try  {  
			//Tell the UIManager to use the platform look and feel  
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());  
		}  
		catch(Exception e) {  
			//Do nothing  
		}
    	
        Encrypt = new Checkbox();
        Encrypt.setLabel("Encrypt");

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
        		layout.createSequentialGroup()
                	.addContainerGap(20, Short.MAX_VALUE)
                	.addComponent(Encrypt, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                	.addContainerGap()
         );
        layout.setVerticalGroup(
            layout.createParallelGroup()
            .addComponent(Encrypt, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        );
    }


    private Checkbox Encrypt;

}
