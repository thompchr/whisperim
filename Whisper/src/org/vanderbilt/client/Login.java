/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Login.java
 *
 * Created on Dec 8, 2008, 1:20:03 AM
 */

package org.vanderbilt.client;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 *
 * @author Chris Thompson
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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

        signOnButton.setText("Sign On");
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
    }// </editor-fold>//GEN-END:initComponents

    private void usernameTxtBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usernameTxtBoxActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_usernameTxtBoxActionPerformed

    private void signOnButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signOnButtonActionPerformed
       usernameLbl.setVisible(false);
       passwordLbl.setVisible(false);
       usernameTxtBox.setVisible(false);
       passwordTxtBox.setVisible(false);
       signOnButton.setVisible(false);
       manager = new ConnectionManager(ConnectionManager.AIM_SESSION, usernameTxtBox.getText(), new String(passwordTxtBox.getPassword()), this, myPrivate_, myPublic_);
}//GEN-LAST:event_signOnButtonActionPerformed

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
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel passwordLbl;
    private javax.swing.JPasswordField passwordTxtBox;
    private javax.swing.JButton signOnButton;
    private javax.swing.JLabel statusLbl;
    private javax.swing.JLabel usernameLbl;
    private javax.swing.JTextField usernameTxtBox;
    // End of variables declaration//GEN-END:variables

}
