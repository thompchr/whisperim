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
import java.awt.event.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.swing.*;


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

    	usernameLbl = new JLabel();
		usernameTxtBox = new JTextField();
		passwordTxtBox = new JPasswordField();
		passwordLbl = new JLabel();
		signOnButton = new JButton();
		statusLbl = new JLabel();
        
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        usernameLbl.setText(USERNAME_);
        usernameTxtBox.addKeyListener (
        		new KeyAdapter() {
        			public void keyPressed(KeyEvent e) {
        	            if (e.getKeyCode() == KeyEvent.VK_ENTER) signOn();
        	    	} //end anonymous class
        		} //end method
        	);
        passwordLbl.setText(PASSWORD_);
        passwordTxtBox.addKeyListener (
        		new KeyAdapter() {
        			public void keyPressed(KeyEvent e) {
        	            if (e.getKeyCode() == KeyEvent.VK_ENTER) signOn();
        	    	} //end anonymous class
        		} //end method
        	);
		signOnButton.setText(SIGN_ON_);
		signOnButton.setActionCommand(SIGN_ON_);
		signOnButton.addActionListener(this);
		signOnButton.addKeyListener (
        		new KeyAdapter() {
        			public void keyPressed(KeyEvent e) {
        	            if (e.getKeyCode() == KeyEvent.VK_ENTER) signOn();
        	    	} //end anonymous class
        		} //end method
        	);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(usernameTxtBox, GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                        .addComponent(usernameLbl)
                        .addComponent(passwordLbl)
                        .addComponent(passwordTxtBox, GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE))
                    .addComponent(signOnButton))
                .addContainerGap(28, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(statusLbl, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(51, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(usernameLbl)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usernameTxtBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(statusLbl)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(passwordLbl)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(passwordTxtBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(signOnButton)
                .addContainerGap(25, Short.MAX_VALUE))
        );
        
        pack();
        
    }

    private void signOn() {
    	setVisibility(false);
        manager_ = new ConnectionManager(ConnectionManager.AIM_SESSION, usernameTxtBox.getText(), new String(passwordTxtBox.getPassword()), this, myPrivate_, myPublic_);
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
		usernameLbl.setVisible(visible);
        passwordLbl.setVisible(visible);
        usernameTxtBox.setVisible(visible);
        passwordTxtBox.setVisible(visible);
        signOnButton.setVisible(visible);
        
        //The status label is opposite the others.
        //It is only visible when the others are not.
        statusLbl.setVisible(!visible);
	}
	
	//Handles actions generated in login
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(signOnButton.getActionCommand())) {
			signOn();
		}
	}

    public void statusUpdate (String status){
        statusLbl.setText(status);
        if (status.equalsIgnoreCase(SIGNED_IN_)){
        	EventQueue.invokeLater(new Runnable() {
        		public void run() {
        			WhisperClient client = new WhisperClient(usernameTxtBox.getText(), manager_);
        			client.setVisible(true);
        		}
        	});
            this.dispose();
        }
        if (status.equalsIgnoreCase(INVALID_USERNAME_OR_PASSWORD_)){
        	JOptionPane.showMessageDialog(this, status, "Error", JOptionPane.ERROR_MESSAGE);
        	setVisibility(true);
        }
    }
    	
    private JLabel passwordLbl;
    private JPasswordField passwordTxtBox;
    private JButton signOnButton;
    private JLabel statusLbl;
    private JLabel usernameLbl;
    private JTextField usernameTxtBox;

}
