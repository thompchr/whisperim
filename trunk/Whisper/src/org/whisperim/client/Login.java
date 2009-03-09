 /**************************************************************************
 * Copyright 2009 John Dlugokecki                                         *
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


import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.UIManager;
import javax.swing.WindowConstants;



/**
 * This class handles the login and authentication of users to the AIM
 * system.  Some of these responsibilities are passed on to the
 * connection manager.
 *
 * @author Chris Thompson, Cory Plastek, John Dlugokecki
 */
public class Login extends JFrame implements ActionListener {

    private ConnectionManager manager_ = null;
    private PublicKey myPublic_;
    private PrivateKey myPrivate_;
    
    private static final String USERNAME_ = "Username:";
	private static final String PASSWORD_ = "Password:";
	private static final String SIGN_ON_ = "Sign On";
	private static final String INVALID_USERNAME_OR_PASSWORD_ = "Invalid Username or Password";
	private static final String ERROR_ = "Error";
	private static final String SIGNED_IN_ = "Signed In";
	
    /** Creates new form Login */
    public Login(PublicKey pubKey, PrivateKey privKey) {
        initComponents();
        myPublic_ = pubKey;
        myPrivate_ = privKey;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {

    	//set native look and feel
		try  {  
			//Tell the UIManager to use the platform look and feel  
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());  
		}  
		catch(Exception e) {  
			//Do nothing  
		}  
    	
    	usernameLbl_ = new JLabel();
    	usernameLbl_.setText(USERNAME_);
		usernameTxtBox_ = new JTextField();
        usernameTxtBox_.addKeyListener (
        		new KeyAdapter() {
        			public void keyTyped(KeyEvent e) {
        	            if (e.getKeyChar() == '\n') signOn();
        			}
        		}
        	);
        
        passwordLbl_ = new JLabel();
        passwordLbl_.setText(PASSWORD_);
        passwordTxtBox_ = new JPasswordField();
        passwordTxtBox_.addKeyListener (
        		new KeyAdapter() {
        			public void keyTyped(KeyEvent e) {
        	            if (e.getKeyChar() == '\n') signOn();
        			}
        		}
        	);
        
		signOnButton_ = new JButton();
		signOnButton_.setText(SIGN_ON_);
		signOnButton_.setActionCommand(SIGN_ON_);
		signOnButton_.addActionListener(this);
		signOnButton_.addKeyListener (
        		new KeyAdapter() {
        			public void keyTyped(KeyEvent e) {
        	            if (e.getKeyChar() == '\n') signOn();
        			}
        		}
        	);
		
		cancelButton_ = new JButton();
		cancelButton_.setText("Cancel");
		cancelButton_.addActionListener(this);
		
		
		statusLbl_ = new JLabel();
        
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(usernameTxtBox_, GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                        .addComponent(usernameLbl_)
                        .addComponent(passwordLbl_)
                        .addComponent(passwordTxtBox_, GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE))
                    .addComponent(signOnButton_)
                    .addComponent(cancelButton_))
                .addContainerGap(28, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(statusLbl_, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(51, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(usernameLbl_)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usernameTxtBox_, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(statusLbl_)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(passwordLbl_)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(passwordTxtBox_, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(signOnButton_)
                .addComponent(cancelButton_)
                .addContainerGap(25, Short.MAX_VALUE))
        );
        
        pack();
        setVisibility(true);
        
    }

    private void signOn() {
    	setVisibility(false);
        manager_ = new ConnectionManager(ConnectionManager.AIM_SESSION, usernameTxtBox_.getText(), new String(passwordTxtBox_.getPassword()), this, myPrivate_, myPublic_);
    }

	/**
	 * Helper method to toggle the visibility of the form objects on the 
	 * login object.
	 * 
	 * @param visible - indicates whether the objects should be visible or not.
	 * 
	 * 					
	 */
	private void setVisibility(boolean visible) {
		usernameLbl_.setVisible(visible);
        passwordLbl_.setVisible(visible);
        usernameTxtBox_.setVisible(visible);
        passwordTxtBox_.setVisible(visible);
        signOnButton_.setVisible(visible);
        
        //The status label is opposite the others.
        //It is only visible when the others are not.
        statusLbl_.setVisible(!visible);
        cancelButton_.setVisible(!visible);
        if (!visible){
        	cancelButton_.requestFocus();
        }
	}
	
	//Handles actions generated in login
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(signOnButton_.getActionCommand())) {
			signOn();
		}
		else if (e.getActionCommand().equals(cancelButton_.getActionCommand())){
			//Might need some additional connection killing stuff..
			setVisibility(true);
		}
	}

    public void statusUpdate (String status){
        statusLbl_.setText(status);
        if (status.equalsIgnoreCase(SIGNED_IN_)){
        	EventQueue.invokeLater(new Runnable() {
        		public void run() {
        			WhisperClient client = new WhisperClient(manager_);
        			client.setVisible(true);
        		}
        	});
            this.dispose();
        }
        if (status.equalsIgnoreCase(INVALID_USERNAME_OR_PASSWORD_)){
        	JOptionPane.showMessageDialog(this, status, ERROR_, JOptionPane.ERROR_MESSAGE);
        	setVisibility(true);
        	passwordTxtBox_.requestFocus();
        	passwordTxtBox_.select(0, new String(passwordTxtBox_.getPassword()).length());
        }
    }
    	
    private JLabel passwordLbl_;
    private JPasswordField passwordTxtBox_;
    private JButton signOnButton_;
    private JLabel statusLbl_;
    private JLabel usernameLbl_;
    private JTextField usernameTxtBox_;
    private JButton cancelButton_;
    
}
