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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.UIManager;

public class WhisperNewIMWindow extends JFrame implements ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1685855175306987312L;
	private static final String SCREEN_NAME_ = "Screen Name: ";
	private static final String OK_ = "OK";
	private static final String CANCEL_ = "Cancel";
	private static final String WINDOW_TITLE_ = "New Instant Message";

	private JTextField foreignHandleBox_ = new JTextField();
	private JComboBox protocolSelector_ = new JComboBox();

	private JLabel foreignHandleLbl_ = new JLabel(SCREEN_NAME_);
	private JButton okBtn_ = new JButton(OK_);
	private JButton cancelBtn_ = new JButton(CANCEL_);
	private WhisperClient parent_;
		
	public WhisperNewIMWindow(ConnectionManager manager, WhisperClient parent){
		parent_ = parent;
		foreignHandleBox_.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == '\n'){
					//Enter key
					actionPerformed(new ActionEvent(foreignHandleBox_, Integer.MAX_VALUE, OK_));
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {

			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
		});
			
		//set native look and feel
		try  {  
			//Tell the UIManager to use the platform look and feel  
			//This should be added to a global prefs registry
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());  
		}  
		catch(Exception e) {  
			//Do nothing  
		}
		
		SpringLayout sl = new SpringLayout();
		Container cp = getContentPane();
		cp.setLayout(sl);
		
		setTitle("New Instant Message");
		
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
		okBtn_.setActionCommand(OK_);
		okBtn_.addActionListener(this);
		
		cancelBtn_.setMinimumSize(new Dimension(75, 26));
		cancelBtn_.setMaximumSize(new Dimension(75, 26));
		cancelBtn_.setPreferredSize(new Dimension(75, 26));
		cancelBtn_.setActionCommand(CANCEL_);
		cancelBtn_.addActionListener(this);
		
		foreignHandleBox_.setMinimumSize(new Dimension(150, 23));
		foreignHandleBox_.setMaximumSize(new Dimension(150, 23));
		foreignHandleBox_.setPreferredSize(new Dimension(150, 23));
		
		protocolSelector_.setMinimumSize(new Dimension(240, 30));
		protocolSelector_.setMaximumSize(new Dimension(240, 30));
		protocolSelector_.setPreferredSize(new Dimension(240, 30));
		
		setTitle(WINDOW_TITLE_);
		
		
		ProtocolRenderer renderer = new ProtocolRenderer();
		protocolSelector_.setRenderer(renderer);
		
		
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
			protocolSelector_.addItem(cs);
		}
		
		
		pack();
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		String ac = evt.getActionCommand();
		if (ac.equals(OK_)){
			if (foreignHandleBox_.getText().equalsIgnoreCase("")){
				foreignHandleBox_.requestFocus();
				return;
			}
			final WhisperClient temp = parent_;
			final String foreign = foreignHandleBox_.getText();
			final ConnectionStrategy cs = (ConnectionStrategy) protocolSelector_.getSelectedItem();
			EventQueue.invokeLater(new Runnable(){

				@Override
				public void run() {
					temp.newIMWindow(new Buddy(foreign, cs.getIdentifier().substring(cs.getIdentifier().lastIndexOf(":") + 1), cs.getProtocol()));
				}
				
			});
			dispose();
		}
		
		if(ac.equals(CANCEL_)){
			dispose();
		}
		
	}
	
	
}