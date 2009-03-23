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

/**
 * PreferencesWindow.java
 * @author Cory Plastek
 * 
 */

package org.whisperim.prefs;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.whisperim.renderers.PreferencesWindowCategoryRenderer;

public class PreferencesWindow extends JFrame implements ListSelectionListener,ActionListener,WindowListener {
	
	private static final long serialVersionUID = 8374303255348687523L;
	
	private static final String WHISPER_PREFERENCES_ = "Whisper Preferences";
	private static final String GENERAL_ = "General";
	private static final String ACCOUNTS_ = "Accounts";
	private static final String LOGGING_ = "Logging";
	private static final String SECURITY_ = "Security";
	private static final String PLUGINS_ = "Plugins";
	private static final String SOUNDS_ = "Sounds";
	private static final String WHISPERBOT_ = "Whisper Bot";
	private static final String ABOUT_ = "About";
	private static final String categoriesList_[] =  {GENERAL_, ACCOUNTS_, LOGGING_, SECURITY_, PLUGINS_, SOUNDS_, WHISPERBOT_, ABOUT_};

	private Dimension frameSize_ = new Dimension(600,550);
	private JList categories_;
	private JScrollPane categoriesScrollPane_;
	private JPanel generalPrefs_;
	private JPanel accountPrefs_;
	private JPanel loggingPrefs_;
	private JPanel securityPrefs_;
	private JPanel pluginsPrefs_;
	private JPanel soundsPrefs_;
	private JPanel whisperbotPrefs_;
	private JPanel aboutInfo_;
	private JPanel mainContent_;
	private JPanel rightContent_;
	private JPanel content_;
	private JPanel buttonPanel_;
	private JButton doneButton_;
	private final String done_ = "Done";
	private Dimension categoriesSize_ = new Dimension(125,550);
	private Dimension contentSize_ = new Dimension(650,500);
	private Dimension buttonPanelSize_ = new Dimension(650,50);
	
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
		//set background color equal to categories list and button pane
		this.setBackground(new Color(239,239,239));
		this.setMinimumSize(frameSize_);
		this.setResizable(false);
		
		//add categories to list
		//general
		//accounts
		//logging
		//security
		//plugins
		//sounds
		//whisperbot
		//about
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
		categoriesScrollPane_.setMinimumSize(categoriesSize_);
		categoriesScrollPane_.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		categoriesScrollPane_.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		categoriesScrollPane_.setBorder(null);
		
		
		generalPrefs_ = new PreferencesWindowGeneral();
		accountPrefs_ = new PreferencesWindowAccounts();
		loggingPrefs_ = new PreferencesWindowLogging();
		securityPrefs_ = new PreferencesWindowSecurity();
		pluginsPrefs_ = new PreferencesWindowPlugins();
		soundsPrefs_ = new PreferencesWindowSounds();
		whisperbotPrefs_ = new PreferencesWindowWhisperBot();
		aboutInfo_ = new PreferencesWindowAbout();
		
		content_ = new JPanel(new CardLayout());
		content_.setPreferredSize(contentSize_);
		content_.setMinimumSize(contentSize_);
		content_.setAlignmentY(CENTER_ALIGNMENT);
		
		content_.add(generalPrefs_, GENERAL_);
		content_.add(accountPrefs_, ACCOUNTS_);
		content_.add(loggingPrefs_, LOGGING_);
		content_.add(securityPrefs_, SECURITY_);
		content_.add(pluginsPrefs_, PLUGINS_);
		content_.add(soundsPrefs_, SOUNDS_);
		content_.add(whisperbotPrefs_, WHISPERBOT_);
		content_.add(aboutInfo_, ABOUT_);
		
		contentLayout_ = (CardLayout)(content_.getLayout());
		showPrefs(GENERAL_);
		
		buttonPanel_ = new JPanel();
		buttonPanel_.setLayout(new BoxLayout(buttonPanel_, BoxLayout.LINE_AXIS));
		buttonPanel_.setSize(buttonPanelSize_);
		//set the button panel color equal to the categories list color
		buttonPanel_.setBackground(new Color(239,239,239));
		
		doneButton_ = new JButton(done_);
		doneButton_.addActionListener(this);
		buttonPanel_.add(doneButton_);
		
		rightContent_ = new JPanel();
		rightContent_.setLayout(new BoxLayout(rightContent_, BoxLayout.PAGE_AXIS));
		rightContent_.setBackground(new Color(239,239,239));
		rightContent_.setMinimumSize(contentSize_);
		rightContent_.add(content_);
		rightContent_.add(buttonPanel_);
		
		mainContent_ = new JPanel();
		mainContent_.setLayout(new BoxLayout(mainContent_, BoxLayout.LINE_AXIS));
		mainContent_.add(categoriesScrollPane_);
		mainContent_.add(rightContent_);
		this.add(mainContent_);
		this.pack();
		this.setVisible(true);
		
    }
	
	
	/*
	 * setPreferencesCategory - public interface allows other parts of whisper
	 * to open the PreferencesWindow to a specific category, like 'Profile'
	 * takes a case insensitive string argument, possible categories are 'General',
	 * 'Accounts', 'Logging', 'Security', 'Plugins', 'Sounds' and 'About'
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
	 * available categories are 'General', 'Accounts', 'Logging;',
	 * 'Security', 'Plugins' 'Sounds' 'WhisperBot' and 'About'
	 * strings are not case sensitive
	 */
	private void showPrefs (String category) {
		for(int i = 0; i < categoriesList_.length; i++) {
			if(category.equalsIgnoreCase(categoriesList_[i])) {
				contentLayout_.show(content_, categoriesList_[i]);
			}
		}
	}


	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if(actionCommand.equals(doneButton_.getActionCommand())) {
			//done should really just call close to do all these things
			//save all prefs
			//call update
			//close window
			this.dispose();
		}
	}

	
	//these are all of the functions needed for windowlistener
	//not used besides windowClosed
	public void windowClosed(WindowEvent e) {
		//ask user if they want to save changed prefs?
		//save all prefs
		//call update
		//dispose window
		this.dispose();
	}


	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

}
