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

import java.awt.event.KeyEvent;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.swing.JOptionPane;

/**
 * This class handles the login and authentication of users to the AIM
 * system.  Some of these responsibilities are passed on to the
 * connection manager.
 *
 * @author Chris Thompson, Cory Plastek, John Dlugokecki
 */
public class Login extends javax.swing.JFrame {


    private ConnectionManager manager = null;
    private PublicKey myPublic_;
    private PrivateKey myPrivate_;
    /** Creates new form Login */
    public Login(PublicKey pubKey, PrivateKey privKey) {
        initComponents();
        myPublic_ = pubKey;
        myPrivate_ = privKey;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {

        usernameLbl = new javax.swing.JLabel();
        usernameTxtBox = new javax.swing.JTextField();
        passwordTxtBox = new javax.swing.JPasswordField();
        passwordLbl = new javax.swing.JLabel();
        signOnButton = new javax.swing.JButton();
        statusLbl = new javax.swing.JLabel();
        
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        usernameLbl.setText("Username:");

        usernameTxtBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usernameTxtBoxActionPerformed(evt);
            }
        });

        passwordLbl.setText("Password:");
        
        //cory
        passwordTxtBox.addKeyListener(new java.awt.event.KeyListener() {
        	public void keyPressed(java.awt.event.KeyEvent evt) {
        		if(evt.getKeyCode() == 10){
        			signOn();
        		}
        	}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
        });
        
        
        signOnButton.setText("Sign On");        
        //cory
        signOnButton.addKeyListener(new java.awt.event.KeyListener() {
        	public void keyPressed(java.awt.event.KeyEvent evt) {
        		if(evt.getKeyCode() == 10){
        			signOn();
        		}
        	}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
        });
        
        signOnButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                signOnButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(usernameTxtBox, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                        .addComponent(usernameLbl)
                        .addComponent(passwordLbl)
                        .addComponent(passwordTxtBox, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE))
                    .addComponent(signOnButton))
                .addContainerGap(28, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(statusLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(51, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(usernameLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usernameTxtBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(statusLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(passwordLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(passwordTxtBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(signOnButton)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        pack();
    }

    private void usernameTxtBoxActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
}
    
    private void signOn() {
    	usernameLbl.setVisible(false);
        passwordLbl.setVisible(false);
        usernameTxtBox.setVisible(false);
        passwordTxtBox.setVisible(false);
        signOnButton.setVisible(false);
        manager = new ConnectionManager(ConnectionManager.AIM_SESSION, usernameTxtBox.getText(), new String(passwordTxtBox.getPassword()), this, myPrivate_, myPublic_);
    }

    private void signOnButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	signOn();
}

    public void statusUpdate (String status){
        statusLbl.setText(status);
        if (status.equalsIgnoreCase("Signed In")){

        	java.awt.EventQueue.invokeLater(new Runnable() {
        		public void run() {
        			WhisperClient client = new WhisperClient(usernameTxtBox.getText(), manager);
        			client.setVisible(true);
        		}
        	});
            this.dispose();
        }
        if (status.equalsIgnoreCase("Invalid Username or Password")){
        	JOptionPane.showMessageDialog(this, status, "Error", JOptionPane.ERROR_MESSAGE);
        	usernameLbl.setVisible(true);
            passwordLbl.setVisible(true);
            usernameTxtBox.setVisible(true);
            passwordTxtBox.setVisible(true);
            signOnButton.setVisible(true);
            statusLbl.setText("");
        }
    }

    private javax.swing.JLabel passwordLbl;
    private javax.swing.JPasswordField passwordTxtBox;
    private javax.swing.JButton signOnButton;
    private javax.swing.JLabel statusLbl;
    private javax.swing.JLabel usernameLbl;
    private javax.swing.JTextField usernameTxtBox;

}
