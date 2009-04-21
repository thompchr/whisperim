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

package org.whisperim.prefs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class PreferencesWindowEmailAccounts extends JPanel implements
		ActionListener {

	private static final long serialVersionUID = 2503925217768075611L;

	String username_;
	String password_;
	String smtp_;
	String pop3_;

	private JLabel usernameLbl_;
	private JLabel pwLbl_;
	private JLabel smtpLbl_;
	private JLabel pop3Lbl_;
	private JLabel headerLbl_;

	private JTextField smtpField_;
	private JTextField pop3Field_;

	private JTextField usernameField_;
	private JPasswordField pwField_;
	private JButton saveButton_;
	private JButton cancelButton_;

	private JSeparator hr_;

	// String Constants\\

	// UI Strings
	private static final String HANDLE_ = "Handle:";
	private static final String PASSWORD_ = "Password:";
	private static final String SAVE_PASSWORD_ = "Save Password";
	private static final String LOCAL_ALIAS_ = "Local Alias:";
	private static final String SAVE_ = "Save";
	private static final String CANCEL_ = "Cancel";
	private static final String SMTP_ = "SMTP Server:";
	private static final String POP3_ = "POP3 Server:";
	private static final String HEADER_ = "Email Setup:";

	// File System Constants
	private static final String EMAIL_ACCOUNTS_FILE_ = System
			.getProperty("user.home")
			+ File.separator + "Whisper" + File.separator + "accounts";

	@SuppressWarnings("deprecation")
	public PreferencesWindowEmailAccounts() {

		usernameLbl_ = new JLabel(HANDLE_);
		pwLbl_ = new JLabel(PASSWORD_);
		smtpLbl_ = new JLabel(SMTP_);
		pop3Lbl_ = new JLabel(POP3_);
		headerLbl_ = new JLabel(HEADER_);

		hr_ = new JSeparator();

		usernameField_ = new JTextField();
		pwField_ = new JPasswordField();

		smtpField_ = new JTextField();
		pop3Field_ = new JTextField();

		saveButton_ = new JButton(SAVE_);
		cancelButton_ = new JButton(CANCEL_);

		// Action Commands
		saveButton_.setActionCommand(SAVE_);
		cancelButton_.setActionCommand(CANCEL_);

		// Create and set the layout manager
		SpringLayout sl = new SpringLayout();
		this.setLayout(sl);

		// Sizes\\

		// Save Button Size
		saveButton_.setMinimumSize(new Dimension(75, 26));
		saveButton_.setMaximumSize(new Dimension(75, 26));
		saveButton_.setPreferredSize(new Dimension(75, 26));

		// Cancel Button Size
		cancelButton_.setMinimumSize(new Dimension(75, 26));
		cancelButton_.setMaximumSize(new Dimension(75, 26));
		cancelButton_.setPreferredSize(new Dimension(75, 26));

		// Username Field
		usernameField_.setMinimumSize(new Dimension(150, 23));
		usernameField_.setMaximumSize(new Dimension(150, 23));
		usernameField_.setPreferredSize(new Dimension(150, 23));

		// Password Field
		pwField_.setMinimumSize(new Dimension(150, 23));
		pwField_.setMaximumSize(new Dimension(150, 23));
		pwField_.setPreferredSize(new Dimension(150, 23));

		// smtp field
		smtpField_.setMinimumSize(new Dimension(150, 23));
		smtpField_.setMaximumSize(new Dimension(150, 23));
		smtpField_.setPreferredSize(new Dimension(150, 23));

		// pop3 field
		pop3Field_.setMinimumSize(new Dimension(150, 23));
		pop3Field_.setMaximumSize(new Dimension(150, 23));
		pop3Field_.setPreferredSize(new Dimension(150, 23));

		// Constraints \\

		/**
		 * The header element is 15 pixels below the top of the window and 20
		 * pixels from the left edge. The first element in the "form" is 40
		 * pixels below that.
		 * 
		 * Each row is 20 pixels beneath the previous row. Each text field is 15
		 * pixels to the right of its label. Each label is aligned to be right
		 * justified.
		 * 
		 * Because of some odd alignement issues, these dimensions might vary by
		 * +/- a few pixels
		 */

		// Title Label
		sl.putConstraint(SpringLayout.NORTH, headerLbl_, 15,
				SpringLayout.NORTH, this);
		sl.putConstraint(SpringLayout.WEST, headerLbl_, 20, SpringLayout.WEST,
				this);

		// Separator
		sl.putConstraint(SpringLayout.NORTH, hr_, 15, SpringLayout.SOUTH,
				headerLbl_);
		sl.putConstraint(SpringLayout.WEST, hr_, 3, SpringLayout.WEST, this);
		sl.putConstraint(SpringLayout.EAST, hr_, 3, SpringLayout.EAST, this);

		// Username Label
		sl.putConstraint(SpringLayout.NORTH, usernameLbl_, 40,
				SpringLayout.SOUTH, headerLbl_);
		sl.putConstraint(SpringLayout.WEST, usernameLbl_, 30,
				SpringLayout.WEST, this);

		// Username Field
		sl.putConstraint(SpringLayout.NORTH, usernameField_, 40,
				SpringLayout.SOUTH, headerLbl_);
		sl.putConstraint(SpringLayout.WEST, usernameField_, 15,
				SpringLayout.EAST, usernameLbl_);

		// Password Label
		sl.putConstraint(SpringLayout.NORTH, pwLbl_, 20, SpringLayout.SOUTH,
				usernameLbl_);
		sl.putConstraint(SpringLayout.EAST, pwLbl_, 0, SpringLayout.EAST,
				usernameLbl_);

		// Password Field
		sl.putConstraint(SpringLayout.NORTH, pwField_, 20, SpringLayout.SOUTH,
				usernameLbl_);
		sl.putConstraint(SpringLayout.WEST, pwField_, 0, SpringLayout.WEST,
				usernameField_);

		// smtp label
		sl.putConstraint(SpringLayout.NORTH, smtpLbl_, 20, SpringLayout.SOUTH,
				pwLbl_);
		sl.putConstraint(SpringLayout.WEST, smtpLbl_, 0, SpringLayout.WEST,
				pwLbl_);

		// smtp field
		sl.putConstraint(SpringLayout.NORTH, smtpField_, 0, SpringLayout.NORTH,
				smtpLbl_);
		sl.putConstraint(SpringLayout.WEST, smtpField_, 0, SpringLayout.WEST,
				pwField_);

		// pop3 label
		sl.putConstraint(SpringLayout.NORTH, pop3Lbl_, 20, SpringLayout.SOUTH,
				smtpLbl_);
		sl.putConstraint(SpringLayout.WEST, pop3Lbl_, 0, SpringLayout.WEST,
				smtpLbl_);

		// pop3 field
		sl.putConstraint(SpringLayout.NORTH, pop3Field_, 20,
				SpringLayout.SOUTH, smtpField_);
		sl.putConstraint(SpringLayout.WEST, pop3Field_, 0, SpringLayout.WEST,
				smtpField_);

		// Save Button
		sl.putConstraint(SpringLayout.NORTH, saveButton_, 40,
				SpringLayout.SOUTH, pop3Lbl_);
		sl.putConstraint(SpringLayout.WEST, saveButton_, 0, SpringLayout.WEST,
				pop3Lbl_);

		// Cancel Button
		sl.putConstraint(SpringLayout.NORTH, cancelButton_, 0,
				SpringLayout.NORTH, saveButton_);
		sl.putConstraint(SpringLayout.WEST, cancelButton_, 20,
				SpringLayout.EAST, saveButton_);

		// Add the components to the window
		this.add(headerLbl_);
		this.add(hr_);
		this.add(usernameLbl_);
		this.add(usernameField_);
		this.add(pwLbl_);
		this.add(pwField_);
		this.add(smtpLbl_);
		this.add(smtpField_);
		this.add(pop3Lbl_);
		this.add(pop3Field_);
		this.add(saveButton_);
		this.add(cancelButton_);

		// Add action listeners
		saveButton_.addActionListener(this);
		cancelButton_.addActionListener(this);

		this.setBackground(Color.WHITE);
		// set themes
		try {
			if (Preferences.getInstance().getLookAndFeel().equalsIgnoreCase(
					Preferences.SYSTEM_)) {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
				// UIManager.setLookAndFeel(Preferences.SYSTEM_);
			} else {
				UIManager.setLookAndFeel(UIManager
						.getCrossPlatformLookAndFeelClassName());
				// UIManager.setLookAndFeel(Preferences.METAL_);
			}
		} catch (ClassNotFoundException e) {
			// e.printStackTrace();
		} catch (InstantiationException e) {
			// e.printStackTrace();
		} catch (IllegalAccessException e) {
			// e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// e.printStackTrace();
		}

		Preferences.getInstance().getListeners().add(new PrefListener() {
			private boolean locked = false;

			@Override
			public void prefChanged(String name, Object o) {
				if (Preferences.THEME_.equals(name) && !locked) {
					locked = true;
					try {
						if (Preferences.getInstance().getLookAndFeel().equals(
								Preferences.METAL_))
							UIManager.setLookAndFeel(UIManager
									.getCrossPlatformLookAndFeelClassName());
						else
							UIManager.setLookAndFeel(UIManager
									.getSystemLookAndFeelClassName());
					} catch (Exception e) {
						// do nothing
					}
					packAndRepaint();
					locked = false;
				}
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		String ac = arg0.getActionCommand();
		if (ac.equalsIgnoreCase(SAVE_)) {
			try {

				Document dom = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder().parse(EMAIL_ACCOUNTS_FILE_);

				OutputFormat format = new OutputFormat(dom);
				format.setIndenting(true);

				XMLSerializer serializer = new XMLSerializer(
						new FileOutputStream(new File(EMAIL_ACCOUNTS_FILE_)),
						format);

				serializer.serialize(dom);

				// tell them they're awesome
				JOptionPane.showMessageDialog(this,
						"Whatever you just did worked...finally.");

				// clear the fields
				usernameField_.setText("");
				pwField_.setText("");

			} catch (SAXException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			} catch (ParserConfigurationException e) {

				e.printStackTrace();
			}

		} else if (ac.equalsIgnoreCase(CANCEL_)) {
			// clear the fields
			usernameField_.setText("");
			pwField_.setText("");
		}

	}

	public String getUsername() {
		return username_ = usernameField_.getText();
	}

	public String getPassword() {
		return password_ = pwField_.getText();
	}

	public String getSMTP() {
		return smtp_ = smtpField_.getText();
	}

	public String getPOP3() {
		return pop3_ = pop3Field_.getText();
	}

	private void packAndRepaint() {
		SwingUtilities.updateComponentTreeUI(this);
		this.repaint();
	}
}
