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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.UIManager;

import org.whisperim.plugins.PluginLoader;

public class LoadPluginWindow extends JFrame implements ActionListener {

	/**
	 * What does this do? Makes Eclipse shut up, that's what...
	 */
	private static final long serialVersionUID = -6394784624437418361L;


	/**
	 * Look and feel declaration
	 */
	private static final String LOOK_AND_FEEL_ = UIManager.getSystemLookAndFeelClassName();
	
	
	private static final String LOCATION_ = "Location: ";
	private static final String OK_ = "OK";
	private static final String CANCEL_ = "Cancel";
	private static final String WINDOW_TITLE_ = "Load Plugin";
	private static final String FILE_CHOOSER_ACCEPT_ = "Open";
	private static final String ERROR_OCCURRED_MESSAGE = "An error has occurred while attempting to load the plugin.";
	private static final String FILE_NOT_FOUND_MESSAGE = "The selected file could not be found.";
	
	private static final String ERROR_ = "Error";
	private static final String BROWSE_ = "Browse";

	private JTextField locationBox_ = new JTextField();
	

	private JLabel locationLbl_ = new JLabel(LOCATION_);
	private JButton okBtn_ = new JButton(OK_);
	private JButton cancelBtn_ = new JButton(CANCEL_);
	private JButton browseBtn_ = new JButton(BROWSE_);
	
	
	private PluginLoader pm_;
	
		
	public LoadPluginWindow(PluginLoader pl){
		
		pm_ = pl;
		
		locationBox_.addKeyListener(new KeyAdapter(){

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == '\n'){
					//Enter key
					actionPerformed(new ActionEvent(locationBox_, Integer.MAX_VALUE, OK_));
				}
			}
		});
			
		//set native look and feel
		try  {  
			//Tell the UIManager to use the platform look and feel  
			//This should be added to a global prefs registry
			UIManager.setLookAndFeel(LOOK_AND_FEEL_);  
		}  
		catch(Exception e) {  
			//Do nothing  
		}
		
		SpringLayout sl = new SpringLayout();
		Container cp = getContentPane();
		cp.setLayout(sl);
		
		setTitle(WINDOW_TITLE_);
		
		cp.add(locationLbl_);
		cp.add(locationBox_);
		cp.add(browseBtn_);
		cp.add(okBtn_);
		cp.add(cancelBtn_);
		
		
		setMinimumSize(new Dimension(350, 130));
		setMaximumSize(new Dimension(350, 130));
		
		okBtn_.setMinimumSize(new Dimension(75, 26));
		okBtn_.setMaximumSize(new Dimension(75, 26));
		okBtn_.setPreferredSize(new Dimension(75, 26));
		okBtn_.setActionCommand(OK_);
		okBtn_.addActionListener(this);
		
		cancelBtn_.setMinimumSize(new Dimension(75, 26));
		cancelBtn_.setMaximumSize(new Dimension(75, 26));
		cancelBtn_.setPreferredSize(new Dimension(75, 26));
		cancelBtn_.setActionCommand(CANCEL_);
		cancelBtn_.addActionListener(this);
		
		browseBtn_.setMinimumSize(new Dimension(75, 26));
		browseBtn_.setMaximumSize(new Dimension(75, 26));
		browseBtn_.setPreferredSize(new Dimension(75, 26));
		browseBtn_.setActionCommand(BROWSE_);
		browseBtn_.addActionListener(this);
		
		
		locationBox_.setMinimumSize(new Dimension(150, 23));
		locationBox_.setMaximumSize(new Dimension(150, 23));
		locationBox_.setPreferredSize(new Dimension(150, 23));
		

		
		setTitle(WINDOW_TITLE_);
		
		
		//Constraints
		sl.putConstraint(SpringLayout.WEST, locationLbl_, 20, SpringLayout.WEST, cp);
		sl.putConstraint(SpringLayout.NORTH, locationLbl_, 20, SpringLayout.NORTH, cp);
		
		sl.putConstraint(SpringLayout.WEST, locationBox_, 5, SpringLayout.EAST, locationLbl_);
		sl.putConstraint(SpringLayout.NORTH, locationBox_, 17, SpringLayout.NORTH, cp);
		
		sl.putConstraint(SpringLayout.NORTH, cancelBtn_, 25, SpringLayout.NORTH, locationBox_);
		sl.putConstraint(SpringLayout.WEST, cancelBtn_, 20, SpringLayout.EAST, okBtn_);
		
		sl.putConstraint(SpringLayout.WEST, okBtn_, 90, SpringLayout.WEST, cp);
		sl.putConstraint(SpringLayout.NORTH, okBtn_, 25, SpringLayout.NORTH, locationBox_);
		
		sl.putConstraint(SpringLayout.WEST, browseBtn_, 15, SpringLayout.EAST, locationBox_);
		sl.putConstraint(SpringLayout.NORTH, browseBtn_, 16, SpringLayout.NORTH, cp);
		
		
		pack();
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		String ac = evt.getActionCommand();
		if (ac.equals(OK_)){
			if (locationBox_.getText().equalsIgnoreCase("")){
				locationBox_.requestFocus();
				return;
			}
			
			try{
				pm_.loadPluginFromExtLoc(locationBox_.getText());
			}catch(Exception e){
				JOptionPane.showMessageDialog(this, ERROR_OCCURRED_MESSAGE, ERROR_, JOptionPane.ERROR_MESSAGE);
			}
			dispose();
		}
		
		if(ac.equals(BROWSE_)){
			JFileChooser fc = new JFileChooser(System.clearProperty("user.home"));
			fc.setFileFilter(new XMLFilter());
			//fc.setAcceptAllFileFilterUsed(false);
			int returnval = fc.showDialog(LoadPluginWindow.this, FILE_CHOOSER_ACCEPT_);
			if (returnval == JFileChooser.APPROVE_OPTION){
				try {
					locationBox_.setText(fc.getSelectedFile().getCanonicalPath());
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this, ERROR_OCCURRED_MESSAGE, ERROR_, JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
			
			
		}
		
		if(ac.equals(CANCEL_)){
			dispose();
		}
		
	}
	
	

}
