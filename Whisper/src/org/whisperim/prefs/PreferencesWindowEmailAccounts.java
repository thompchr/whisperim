package org.whisperim.prefs;

	import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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


	public class PreferencesWindowEmailAccounts extends JFrame implements ActionListener {

		private static final long serialVersionUID = 2503925217768075611L;
		
		private JLabel protocolLbl_;
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
		
		//String Constants\\
		
		//UI Strings
		private static final String PROTOCOL_ = "Protocol:";
		private static final String HANDLE_ = "Handle:";
		private static final String PASSWORD_ = "Password:";
		private static final String SAVE_PASSWORD_ = "Save Password";
		private static final String LOCAL_ALIAS_ = "Local Alias:";
		private static final String SAVE_ = "Save";
		private static final String CANCEL_ = "Cancel";
		private static final String SMTP_ = "SMTP Server:";
		private static final String POP3_ = "POP3 Server:";
		private static final String HEADER_ = "Email Setup:";
		
		//File System Constants
		private static final String EMAIL_ACCOUNTS_FILE_ = System.getProperty("user.home") + File.separator + "Whisper" + File.separator + "accounts";
		
		


		/**
		 * @param manager_
		 */
		private void initComponents() {
			
			protocolLbl_ = new JLabel(PROTOCOL_);
			usernameLbl_ = new JLabel(HANDLE_);
			pwLbl_ = new JLabel(PASSWORD_);
			smtpLbl_ = new JLabel(SMTP_);
			pop3Lbl_ = new JLabel(POP3_);
			headerLbl_ = new JLabel(HEADER_);
			
			usernameField_ = new JTextField();
			pwField_ = new JPasswordField();
			
			smtpField_ = new JTextField();
			pop3Field_ = new JTextField();
			
			saveButton_ = new JButton(SAVE_);
			cancelButton_ = new JButton(CANCEL_);
			
			//Action Commands
			saveButton_.setActionCommand(SAVE_);
			cancelButton_.setActionCommand(CANCEL_);
		
			Container cp = getContentPane();
			
			//Create and set the layout manager
			SpringLayout sl = new SpringLayout();
			cp.setLayout(sl);
			
			
			//Sizes\\
			
			//Window Size
			setMinimumSize(new Dimension(300, 400));
			setPreferredSize(new Dimension(300, 400));
			
			//Save Button Size
			saveButton_.setMinimumSize(new Dimension(75, 26));
			saveButton_.setMaximumSize(new Dimension(75, 26));
			saveButton_.setPreferredSize(new Dimension(75,26));
			
			//Cancel Button Size
			cancelButton_.setMinimumSize(new Dimension(75, 26));
			cancelButton_.setMaximumSize(new Dimension(75, 26));
			cancelButton_.setPreferredSize(new Dimension(75, 26));
			
			//Username Field
			usernameField_.setMinimumSize(new Dimension(150, 23));
			usernameField_.setMaximumSize(new Dimension(150, 23));
			usernameField_.setPreferredSize(new Dimension(150, 23));
			
			//Password Field
			pwField_.setMinimumSize(new Dimension(150, 23));
			pwField_.setMaximumSize(new Dimension(150, 23));
			pwField_.setPreferredSize(new Dimension(150, 23));
			
			 
			//Horizontal Rule
				//No sizes need to be set
				//Dimensions will be set by the constraints
			
			//Set the window title
			setTitle("Email Account Setup");
			
			// Constraints \\ 
			
			/**
			 * The header element is 15 pixels below the top of the window and 20 pixels from
			 * the left edge.  The first element in the "form" is 40 pixels below that.
			 * 
			 * Each row is 20 pixels beneath the previous row.  Each text field is 15 pixels to
			 * the right of its label.  Each label is aligned to be right justified.
			 * 
			 * Because of some odd alignement issues, these dimensions might vary by +/- a fex pixels
			 */
			
			//Title Label
			sl.putConstraint(SpringLayout.NORTH, headerLbl_, 15, SpringLayout.NORTH, cp);
			sl.putConstraint(SpringLayout.WEST, headerLbl_, 20, SpringLayout.WEST, cp);
			
			//Separator
			sl.putConstraint(SpringLayout.NORTH, hr_, 15, SpringLayout.SOUTH, headerLbl_);
			sl.putConstraint(SpringLayout.WEST, hr_, 3, SpringLayout.WEST, cp);
			sl.putConstraint(SpringLayout.EAST, hr_, -3, SpringLayout.EAST, cp);
			
			
			//Username Label
			sl.putConstraint(SpringLayout.NORTH, usernameLbl_, 40, SpringLayout.SOUTH, headerLbl_);
			sl.putConstraint(SpringLayout.WEST, usernameLbl_, 30, SpringLayout.WEST, cp);
			
			//Username Field
			sl.putConstraint(SpringLayout.NORTH, usernameField_, 39, SpringLayout.SOUTH, headerLbl_);
			sl.putConstraint(SpringLayout.WEST, usernameField_, 15, SpringLayout.EAST, usernameLbl_);
			
			//Password Label
			sl.putConstraint(SpringLayout.NORTH, pwLbl_, 20, SpringLayout.SOUTH, usernameLbl_);
			sl.putConstraint(SpringLayout.EAST, pwLbl_, 0, SpringLayout.EAST, usernameLbl_);
			
			//Password Field
			sl.putConstraint(SpringLayout.NORTH, pwField_, 19, SpringLayout.SOUTH, usernameLbl_);
			sl.putConstraint(SpringLayout.WEST, pwField_, 15, SpringLayout.EAST, pwLbl_);
			
			//Save Button
			sl.putConstraint(SpringLayout.EAST, saveButton_, -20, SpringLayout.EAST, cp);
			sl.putConstraint(SpringLayout.SOUTH, saveButton_, -15, SpringLayout.SOUTH, cp);
			
			//Cancel Button
			sl.putConstraint(SpringLayout.EAST, cancelButton_, -20, SpringLayout.WEST, saveButton_);
			sl.putConstraint(SpringLayout.SOUTH, cancelButton_, -15, SpringLayout.SOUTH, cp);
			
			
			//Add the components to the window
			cp.add(headerLbl_);
			cp.add(hr_);
			cp.add(usernameLbl_);
			cp.add(usernameField_);
			cp.add(pwLbl_);
			cp.add(pwField_);
			cp.add(smtpLbl_);
			cp.add(smtpField_);
			cp.add(pop3Lbl_);
			cp.add(pop3Field_);
			cp.add(protocolLbl_);
			cp.add(saveButton_);
			cp.add(cancelButton_);
			
			
			//Set the location on screen
			setLocation(new Point(Toolkit.getDefaultToolkit().getScreenSize().width / 3,Toolkit.getDefaultToolkit().getScreenSize().height / 4));
			
			//Add action listeners
			saveButton_.addActionListener(this);
			cancelButton_.addActionListener(this);
			
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
			
			Preferences.getInstance().getListeners().add(new PrefListener() {
				private boolean locked = false;
				@Override
				public void prefChanged(String name, Object o) {
					if(Preferences.THEME_.equals(name) && !locked){
						locked = true;
						try {
							if(Preferences.getInstance().getLookAndFeel().equals(Preferences.METAL_))
								UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
							else
								UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
						}
						catch (Exception e) {
							//do nothing
						}
						packAndRepaint();
						locked = false;
					}
				}
			});
			
			
			pack();
			setVisible(true);
		}
		

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			String ac = arg0.getActionCommand();
			if (ac.equalsIgnoreCase(SAVE_)){
				try {
					
					Document dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(EMAIL_ACCOUNTS_FILE_);
					
					
					OutputFormat format = new OutputFormat(dom);
					format.setIndenting(true);

					XMLSerializer serializer = new XMLSerializer(
					new FileOutputStream(new File(EMAIL_ACCOUNTS_FILE_)), format);

					serializer.serialize(dom);
					
					//Close the window
					dispose();

				} catch (SAXException e) {
					
					e.printStackTrace();
				} catch (IOException e) {
					
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					
					e.printStackTrace();
				}
				
			}else if (ac.equalsIgnoreCase(CANCEL_)){
				//Discard everything
				dispose();
			}

		}
		
		private void packAndRepaint() {
			SwingUtilities.updateComponentTreeUI(this);
			this.repaint();
		}
}
