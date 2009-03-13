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
 * Preferences.java
 *
 * Created on Dec 5, 2008, 5:17:55 PM
 */

package org.whisperim.client;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Cory Plastek
 */
public class Preferences extends JFrame implements ListSelectionListener {
	

    public Preferences() {

		//set native look and feel
		try  {  
			//Tell the UIManager to use the platform look and feel  
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());  
		}  
		catch(Exception e) {  
			//Do nothing  
		}
		
		//add categories to list
		//general
		//profiles
		//security
		String categoriesList[] = {"General", "Profiles", "Security"};
		JList categories = new JList(categoriesList);
		categories.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		categories.setSelectedIndex(0);
		categories.addListSelectionListener(this);
		
		
		//do we need a scroll pane? probably not now - possibly later if we add enough categories
		JScrollPane categoriesScrollPane = new JScrollPane(categories);
		categoriesScrollPane.setSize(50,400);
		categoriesScrollPane.setSize(new Dimension(50,400));
		categoriesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		categoriesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		JPanel content = new JPanel();
		
		//while the categories scroll pane is optional, the content scroll is not
		//scroll pane is used so that if one section contains more options than the fixed preferences
		//window can show, it can be viewed by scrolling
		JScrollPane contentScrollPane = new JScrollPane(content);
		contentScrollPane.setSize(400,400);
		contentScrollPane.setPreferredSize(new Dimension(400,400));
		
		//organize the categories and content panes in a split pane, with categories on the left side
		//of the split, and the content pane on the right side of the split
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, categoriesScrollPane, contentScrollPane);
		splitPane.setDividerLocation(150);

		Container mainContent = getContentPane();
		mainContent.setLayout(new FlowLayout());
		
		mainContent.add(categoriesScrollPane);
		mainContent.add(contentScrollPane);
		pack();
		
		/*
		private static final String ENCRYPTION_ = "Encryption";
		
		setTitle("Preferences");
 
		JCheckBox Encrypt = new JCheckBox();
	    Encrypt.setText(ENCRYPTION_);

	    content.add(Encrypt);
	    */
    }

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
