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
 * PreferencesWindow.java
 *
 * 
 */

package org.whisperim.prefs;

import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.whisperim.renderers.PreferencesWindowCategoryRenderer;

/**
 *
 * @author Cory Plastek
 */
public class PreferencesWindow extends JFrame implements ListSelectionListener {
	
	private static final long serialVersionUID = 8374303255348687523L;
	
	private static final String WHISPER_PREFERENCES_ = "Whisper Preferences";
	private static final String categoriesList_[] =  {"General", "Accounts", "Security"};
	private static final String GENERAL_ = "General";
	private static final String ACCOUNTS_ = "Accounts";
	private static final String SECURITY_ = "Security";
	
	
	private JList categories_;
	private JScrollPane categoriesScrollPane_;
	private JPanel generalPrefs_;
	private JPanel accountPrefs_;
	private JPanel securityPrefs_;
	private JPanel mainContent_;
	private JPanel content_;
	private Dimension categoriesSize_ = new Dimension(125,500);
	private Dimension contentSize_ = new Dimension(550,500);
	
	private CardLayout contentLayout_;
	

	public PreferencesWindow() {

		//set native look and feel
		try  {  
			//Tell the UIManager to use the platform look and feel  
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());  
		}  
		catch(Exception e) {  
			//Do nothing  
		}
		
		this.setTitle(WHISPER_PREFERENCES_);
		this.setResizable(false);
		
		//add categories to list
		//general
		//accounts
		//security
		categories_ = new JList(categoriesList_);
		
		//use custom cell renderer
		PreferencesWindowCategoryRenderer categoryRenderer = new PreferencesWindowCategoryRenderer();
		categoryRenderer.setSize(categoriesSize_);
		categories_.setCellRenderer(categoryRenderer);
		categories_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		categories_.setSelectedIndex(0);
		categories_.setMinimumSize(categoriesSize_);
		categories_.setPreferredSize(categoriesSize_);
		categories_.setAlignmentY(TOP_ALIGNMENT);
		categories_.addListSelectionListener(this);	
		
		//do we really need a scroll pane? not really, but its the easiest way to get it to look the way we want
		categoriesScrollPane_ = new JScrollPane(categories_);
		categoriesScrollPane_.setPreferredSize(categoriesSize_);
		categoriesScrollPane_.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		categoriesScrollPane_.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		categoriesScrollPane_.setBorder(null);
		
		
		generalPrefs_ = new PreferencesWindowGeneral();
		accountPrefs_ = new PreferencesWindowAccounts();
		securityPrefs_ = new PreferencesWindowSecurity();
		
		content_ = new JPanel(new CardLayout());
		content_.setPreferredSize(contentSize_);
		content_.setMinimumSize(contentSize_);
		content_.setAlignmentY(CENTER_ALIGNMENT);
		
		content_.add(generalPrefs_, GENERAL_);
		content_.add(accountPrefs_, ACCOUNTS_);
		content_.add(securityPrefs_, SECURITY_);
		
		contentLayout_ = (CardLayout)(content_.getLayout());
		showPrefs(GENERAL_);
		
		mainContent_ = new JPanel();
		mainContent_.setLayout(new BoxLayout(mainContent_, BoxLayout.LINE_AXIS));
		mainContent_.add(categoriesScrollPane_);
		mainContent_.add(content_);
		this.add(mainContent_);
		this.pack();
		this.setVisible(true);
		
    }
	
	
	/*
	 * setPreferencesCategory - public interface allows other parts of whisper
	 * to open the PreferencesWindow to a specific category, like 'Profile'
	 * takes a case insensitive string argument, possible categories are 'General',
	 * 'Accounts', 'Encryption'
	 */
	public void setPreferencesCategory(String category) {
		showPrefs(category);
	}
	
	
	//calls setContent if the selection has changed
	public void valueChanged (ListSelectionEvent e) {
		//if the user is moving from one selection to another
		//don't load every content pane between the start and end selection
		if(e.getValueIsAdjusting()) {
			//do nothing
		}
		else {
			JList list = (JList)e.getSource();
	        showPrefs(categoriesList_[list.getSelectedIndex()]);
		}
		
	}
	
	
	/* setContent changes the pane displayed in Preferences
	 * available categories are 'General', 'Accounts', and 'Encryption'
	 * strings are not case sensitive
	 */
	private void showPrefs (String category) {
		if(category.equalsIgnoreCase(GENERAL_)) {
			contentLayout_.show(content_, GENERAL_);
		}
		else if(category.equalsIgnoreCase(ACCOUNTS_)) {
			contentLayout_.show(content_, ACCOUNTS_);
		}
		else if (category.equalsIgnoreCase(SECURITY_)) {
			contentLayout_.show(content_, SECURITY_);
		}
		else {
			//something went wrong
			//don't change selection
			System.out.println("something went wrong. report this bug!");
		}
		
	}

}
