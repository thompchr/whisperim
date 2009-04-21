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

package org.whisperim.security;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.whisperim.prefs.Preferences;

public class Locking extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static final String UNLOCK_PASSWORD_ = "Unlock Password:";
	private static final String LOCK_PASSWORD_ = "Lock Password:";
	private static final String CONFIRM_ = "Confirm:";
	private static final String LOCK_ = "Lock";
	private static final String UNLOCK_ = "Unlock";
	
	private boolean locked_ = false;
	private char[] lockPassword_;// = Preferences.getInstance().getLockPassword();
	private static final Dimension screenSize_ = Toolkit.getDefaultToolkit().getScreenSize();
	private JPanel content_;
	private static final Dimension contentSize_ = new Dimension(250,150);
	private CardLayout contentLayout_;
	private JPanel lock_;
	private JPanel unlock_;
	private JLabel password_;
	private JPasswordField passwordField_;
	private JButton lockButton_;
	private JButton cancelButton_;
	private static final String CANCEL_ = "Cancel";
	private JLabel passwordConfirm_;
	private JPasswordField passwordConfirmField_;	
	private JLabel unlockPassword_;
	private JPasswordField unlockPasswordField_;
	private JButton unlockButton_;
	
	private static final long waitTime_ = 500;
	
	public Locking() {
		locked_ = false;
		
		//set modality type
		this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		
		//set themes
		try {
			if(Preferences.getInstance().getLookAndFeel().equalsIgnoreCase(Preferences.SYSTEM_)) {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				//UIManager.setLookAndFeel(Preferences.SYSTEM_); 
			}
			else {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				//UIManager.setLookAndFeel(Preferences.METAL_); 
			}
		}
		catch (ClassNotFoundException e) {
			//e.printStackTrace();
		} catch (InstantiationException e) {
			//e.printStackTrace();
		} catch (IllegalAccessException e) {
			//e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			//e.printStackTrace();
		}
		
		this.setLayout(new BorderLayout());
		this.setBackground(Color.BLUE);
		this.setUndecorated(true);
		this.setBounds(0,0,screenSize_.width, screenSize_.height);
		this.setSize(screenSize_);
		this.setResizable(false);

		contentLayout_ = new CardLayout();
		content_ = new JPanel(contentLayout_);
		content_.setSize(contentSize_);
		content_.setLocation(new Point(Toolkit.getDefaultToolkit().getScreenSize().width/2,Toolkit.getDefaultToolkit().getScreenSize().height/2));
		
		SpringLayout layout_ = new SpringLayout();
		
		//unlock dialog
		unlock_ = new JPanel(layout_);
		
		unlockPassword_ = new JLabel(UNLOCK_PASSWORD_);
		layout_.putConstraint(SpringLayout.WEST, unlockPassword_, 10, SpringLayout.WEST, unlock_);
		layout_.putConstraint(SpringLayout.NORTH, unlockPassword_, 10, SpringLayout.NORTH, unlock_);
		unlock_.add(unlockPassword_);
		
		unlockPasswordField_ = new JPasswordField(20);
		layout_.putConstraint(SpringLayout.WEST, unlockPasswordField_, 10, SpringLayout.EAST, unlockPassword_);
		layout_.putConstraint(SpringLayout.NORTH, unlockPasswordField_, 10, SpringLayout.NORTH, unlock_);
		unlockPasswordField_.addKeyListener (
	       		new KeyAdapter() {
	       			public void keyTyped(KeyEvent e) {
	       	            if (e.getKeyChar() == '\n') unlock(unlockPasswordField_.getPassword());
	       			}
	       		}
	       	);
		unlock_.add(unlockPasswordField_);
		
		unlockButton_ = new JButton(UNLOCK_);
		layout_.putConstraint(SpringLayout.EAST, unlockButton_, 0, SpringLayout.EAST, unlockPasswordField_);
		layout_.putConstraint(SpringLayout.NORTH, unlockButton_, 10, SpringLayout.SOUTH, unlockPasswordField_);
		unlockButton_.addActionListener(this);
		unlock_.add(unlockButton_);
		
		content_.add(unlock_, UNLOCK_);
		
		//lock dialog
		lock_ = new JPanel(layout_);
		
		password_ = new JLabel(LOCK_PASSWORD_);
		layout_.putConstraint(SpringLayout.WEST, password_, 10, SpringLayout.WEST, lock_);
		layout_.putConstraint(SpringLayout.NORTH, password_, 10, SpringLayout.NORTH, lock_);
		lock_.add(password_);
			
		passwordField_ = new JPasswordField(20);
		layout_.putConstraint(SpringLayout.WEST, passwordField_, 10, SpringLayout.EAST, password_);
		layout_.putConstraint(SpringLayout.NORTH, passwordField_, 10, SpringLayout.NORTH, lock_);
		passwordField_.addKeyListener (
	       		new KeyAdapter() {
	       			public void keyTyped(KeyEvent e) {
	       	            if (e.getKeyChar() == '\n') 
	       	            	lock(passwordField_.getPassword(), passwordConfirmField_.getPassword());
	       			}
	       		}
	       	);
		lock_.add(passwordField_);
		
		passwordConfirm_ = new JLabel(CONFIRM_);
		layout_.putConstraint(SpringLayout.WEST, passwordConfirm_, 10, SpringLayout.WEST, lock_);
		layout_.putConstraint(SpringLayout.NORTH, passwordConfirm_, 10, SpringLayout.SOUTH, passwordField_);
		lock_.add(passwordConfirm_);
			
		passwordConfirmField_ = new JPasswordField(20);
		layout_.putConstraint(SpringLayout.WEST, passwordConfirmField_, 0, SpringLayout.WEST, passwordField_);
		layout_.putConstraint(SpringLayout.NORTH, passwordConfirmField_, 10, SpringLayout.SOUTH, passwordField_);
		passwordConfirmField_.addKeyListener (
	       		new KeyAdapter() {
	       			public void keyTyped(KeyEvent e) {
	       				if (e.getKeyChar() == '\n') 
	       	            	lock(passwordField_.getPassword(), passwordConfirmField_.getPassword());
	       			}
	       		}
	       	);
		lock_.add(passwordConfirmField_);
		
		lockButton_ = new JButton(LOCK_);
		cancelButton_ = new JButton(CANCEL_);
		
		layout_.putConstraint(SpringLayout.EAST, cancelButton_, 0, SpringLayout.EAST, passwordField_);
		layout_.putConstraint(SpringLayout.NORTH, cancelButton_, 10, SpringLayout.SOUTH, passwordConfirmField_);
		cancelButton_.addActionListener(this);
		lock_.add(cancelButton_);
		
		layout_.putConstraint(SpringLayout.WEST, lockButton_, 0, SpringLayout.WEST, passwordField_);
		layout_.putConstraint(SpringLayout.NORTH, lockButton_, 10, SpringLayout.SOUTH, passwordConfirmField_);
		lockButton_.addActionListener(this);
		lock_.add(lockButton_);
		
		
		content_.add(lock_, LOCK_);
		
		contentLayout_.show(content_, LOCK_);
		this.add(content_, BorderLayout.CENTER);
		
		this.setVisible(true);
	}
	
	private void lock(char[] password, char[] confirm) {
		//try to lock, change color send message if don't match
		//if not equal, clear the fields
		if(Arrays.equals(password, confirm) && !Arrays.equals(password, new char[]{})) {
			lockPassword_ = password;
			locked_ = true;
			Preferences.getInstance().setLocked(locked_);
			Preferences.getInstance().setLockPassword(lockPassword_);
			
			passwordField_.setBackground(Color.GREEN);
			passwordConfirmField_.setBackground(Color.GREEN);
			passwordField_.repaint();
			passwordConfirmField_.repaint();
			
			contentLayout_.show(content_, UNLOCK_);
			unlockPasswordField_.requestFocus();
		}
		else {
			passwordField_.setBackground(Color.RED);
			passwordConfirmField_.setBackground(Color.RED);
			passwordField_.repaint();
			passwordConfirmField_.repaint();
			
			passwordField_.requestFocus();
		}
		
		try {
			Thread.currentThread().sleep(waitTime_);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//clear password fields
		passwordField_.setText("");
		passwordConfirmField_.setText("");
	}
	
	private void unlock(char[] unlockPassword){
		//try to unlock
		if(Arrays.equals(unlockPassword, lockPassword_)) {
			//unlock whisper
			locked_ = false;
			Preferences.getInstance().setLocked(locked_);			
			this.dispose();
		}
		else {
			unlockPasswordField_.setBackground(Color.RED);
			unlockPasswordField_.repaint();
			
			try {
				Thread.sleep(waitTime_);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			unlockPasswordField_.setBackground(Color.WHITE);
			unlockPasswordField_.repaint();
			
			//clear text field
			unlockPasswordField_.setText("");
		}
	}

	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if(actionCommand.equals(LOCK_)) {
			lock(passwordField_.getPassword(), passwordConfirmField_.getPassword());
		}
		else if(actionCommand.equals(CANCEL_)) {
			this.dispose();
		}
		else if(actionCommand.equals(UNLOCK_)) {
			unlock(unlockPasswordField_.getPassword());
		}
		else {
			//do nothing
		}
	}
}
