package org.whisperim.EmailManager;

/**************************************************************************
 * Copyright 2009 Nick Krieble                                             *
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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.Security;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.whisperim.prefs.Preferences;

import com.sun.net.ssl.internal.ssl.Provider;

public class ComposeEmail extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static final String MESSAGE_ = "Message:";
	private static final String SENDMAIL_ = "Send";
	private static final String CANCEL_ = "Cancel";
	private static final String WINDOW_TITLE_ = "Compose Email";
	private static final String TO_ = "To:";
	private static final String SUBJECT_ = "Subject:";
	private static final String HEADER_ = "Compose Email";
	private static final String PORT_ = "Port Number:";
	private static final String SMTP_ = "SMTP:";
	private static final String FROM_ = "From:";
	private static final String PASSWORD_ = "Password:";

	private JLabel headerLbl_ = new JLabel(HEADER_);
	private JLabel toLbl_ = new JLabel(TO_);
	private JLabel subjectLbl_ = new JLabel(SUBJECT_);
	private JLabel messageLbl_ = new JLabel(MESSAGE_);

	private JTextField toBox_ = new JTextField(TO_);
	private JTextField subjectBox_ = new JTextField(SUBJECT_);


	private JTextArea messageBox_ = new JTextArea(MESSAGE_);

	private JButton sendButton_ = new JButton(SENDMAIL_);
	private JButton cancelButton_ = new JButton(CANCEL_);


	public void composeWindow() {

		SpringLayout sl = new SpringLayout();
		Container cp = getContentPane();
		cp.setLayout(sl);

		setTitle(WINDOW_TITLE_);

		cp.add(headerLbl_);
		cp.add(toLbl_);
		cp.add(toBox_);
		cp.add(subjectLbl_);
		cp.add(subjectBox_);
		cp.add(messageLbl_);
		cp.add(messageBox_);
		cp.add(sendButton_);
		cp.add(cancelButton_);

		setMinimumSize(new Dimension(500, 500));
		setMaximumSize(new Dimension(500, 500));
		setPreferredSize(new Dimension(500, 500));

		sendButton_.setMinimumSize(new Dimension(75, 26));
		sendButton_.setMaximumSize(new Dimension(75, 26));
		sendButton_.setPreferredSize(new Dimension(75, 26));
		sendButton_.setActionCommand(SENDMAIL_);
		sendButton_.addActionListener(this);

		cancelButton_.setMinimumSize(new Dimension(75, 26));
		cancelButton_.setMaximumSize(new Dimension(75, 26));
		cancelButton_.setPreferredSize(new Dimension(75, 26));
		cancelButton_.setActionCommand(CANCEL_);
		cancelButton_.addActionListener(this);

		toBox_.setMinimumSize(new Dimension(120, 20));
		toBox_.setMaximumSize(new Dimension(120, 20));
		toBox_.setPreferredSize(new Dimension(120, 20));

		subjectBox_.setMinimumSize(new Dimension(300, 20));
		subjectBox_.setMaximumSize(new Dimension(300, 20));
		subjectBox_.setPreferredSize(new Dimension(300, 20));

		messageBox_.setMinimumSize(new Dimension(300, 200));
		messageBox_.setMaximumSize(new Dimension(300, 200));
		messageBox_.setPreferredSize(new Dimension(300, 200));
		

		// Constraints
		// Title Label
		sl.putConstraint(SpringLayout.NORTH, headerLbl_, 15,
				SpringLayout.NORTH, this);
		sl.putConstraint(SpringLayout.WEST, headerLbl_, 20, SpringLayout.WEST,
				this);

		// To Label
		sl.putConstraint(SpringLayout.NORTH, toLbl_, 10, SpringLayout.SOUTH,
				headerLbl_);
		sl.putConstraint(SpringLayout.WEST, toLbl_, 0, SpringLayout.WEST,
				headerLbl_);

		// To Field
		sl.putConstraint(SpringLayout.NORTH, toBox_, 10, SpringLayout.SOUTH,
				toLbl_);
		sl.putConstraint(SpringLayout.WEST, toBox_, 0, SpringLayout.EAST,
				toLbl_);
		
		// Subject Label
		sl.putConstraint(SpringLayout.NORTH, subjectLbl_, 10,
				SpringLayout.SOUTH, toBox_);
		sl.putConstraint(SpringLayout.EAST, subjectLbl_, 30, SpringLayout.EAST,
				toLbl_);

		// Subject Field
		sl.putConstraint(SpringLayout.NORTH, subjectBox_, 0,
				SpringLayout.SOUTH, subjectLbl_);
		sl.putConstraint(SpringLayout.WEST, subjectBox_, 30, SpringLayout.WEST,
				subjectLbl_);

		// Message label
		sl.putConstraint(SpringLayout.NORTH, messageLbl_, 10,
				SpringLayout.SOUTH, subjectBox_);
		sl.putConstraint(SpringLayout.WEST, messageLbl_, 0, SpringLayout.WEST,
				toLbl_);

		// Message field
		sl.putConstraint(SpringLayout.NORTH, messageBox_, 20,
				SpringLayout.NORTH, messageLbl_);
		sl.putConstraint(SpringLayout.WEST, messageBox_, 30, SpringLayout.WEST,
				messageLbl_);
		
		// Send Button
		sl.putConstraint(SpringLayout.NORTH, sendButton_, 20,
				SpringLayout.SOUTH, messageBox_);
		sl.putConstraint(SpringLayout.WEST, sendButton_, 0, SpringLayout.WEST,
				messageBox_);

		// Cancel Button
		sl.putConstraint(SpringLayout.NORTH, cancelButton_, 0,
				SpringLayout.NORTH, sendButton_);
		sl.putConstraint(SpringLayout.WEST, cancelButton_, 20,
				SpringLayout.EAST, sendButton_);

		pack();
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		String ac = evt.getActionCommand();
		if (ac.equals(SENDMAIL_)) {
			Preferences prefs = Preferences.getInstance();
				try {
					Security.addProvider(new Provider());
					//new SendGmail((String)fromBox_.getText(), (String)passwordBox_.getText(), (String)smtpBox_.getText(), Integer.parseInt(portBox_.getText())).sendSSLMessage((String)toBox_.getText(),(String)subjectBox_.getText(),(String)messageBox_.getText() );
					new SendGmail((String)prefs.getUsername(), (String)prefs.getPassword(), (String)prefs.getSMTP(), Integer.parseInt(prefs.getSMTPPort())).sendSSLMessage((String)toBox_.getText(),(String)subjectBox_.getText(),(String)messageBox_.getText() );
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		} else if (ac.equals(CANCEL_)) {
			this.dispose();
		}
	}
}
