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
	
	
	private static JList categories_;
	private static JScrollPane categoriesScrollPane_;
	private JPanel generalPrefs_;
	private JPanel accountPrefs_;
	private JPanel securityPrefs_;
	private JPanel content_;
	private static JScrollPane contentScrollPane_;
	private static JSplitPane splitPane_;
	
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
		categories_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		categories_.setSelectedIndex(0);
		categories_.setPreferredSize(new Dimension(150,500));
		categories_.addListSelectionListener(this);
		
		
		/*do we really need a scroll pane? probably not now - possibly later if we add enough categories
		categoriesScrollPane_ = new JScrollPane(categories_);
		categoriesScrollPane_.setPreferredSize(new Dimension(100,500));
		categoriesScrollPane_.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		categoriesScrollPane_.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		*/
		
		generalPrefs_ = new PreferencesWindowGeneral();
		//accountPrefs_ = new PreferencesWindowAccounts();
		securityPrefs_ = new PreferencesWindowSecurity();
		
		content_ = new JPanel(new CardLayout());
		content_.setPreferredSize(new Dimension(550,500));
		content_.add(generalPrefs_, GENERAL_);
		//content_.add(accountPrefs_, ACCOUNTS_);
		content_.add(securityPrefs_, SECURITY_);
		
		contentLayout_ = (CardLayout)(content_.getLayout());
		
		showPrefs(GENERAL_);
		
		/*while the categories scroll pane is optional, the content scroll is not
		//scroll pane is used so that if one section contains more options than the fixed preferences
		//window can show, it can be viewed by scrolling
		contentScrollPane_ = new JScrollPane(content_);
		contentScrollPane_.setPreferredSize(new Dimension(500,600));
		*/
		
		//organize the categories and content panes in a split pane
		//categories on the left side
		//content pane on the right side
		splitPane_ = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, categories_, content_);
		splitPane_.setDividerLocation(150);

		
		Container mainContent_ = getContentPane();
		mainContent_.add(splitPane_);
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
