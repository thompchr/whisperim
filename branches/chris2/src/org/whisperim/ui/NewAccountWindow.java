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
package org.whisperim.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.whisperim.client.ConnectionManager;
import org.whisperim.client.ConnectionStrategy;
import org.whisperim.models.ActiveAccountModel;
import org.whisperim.prefs.GlobalPreferences;
import org.whisperim.prefs.PrefListener;
import org.whisperim.prefs.Preferences;
import org.whisperim.renderers.ConnectionRenderer;
import org.xml.sax.SAXException;


/**
 * This class acts as a visual method for creating a new
 * account using protocols that have already been loaded in.
 * It will be showed by default when the program detects no currently
 * saved account information.  It can also be launched from
 * the buddy list window (WhisperClient class). 
 * 
 * As soon as a protocol is loaded (either at program load or
 * dynamically using the plugin framework) it is available
 * to use in this window.
 * 
 * Currently each protocol uses the same form to create a new account.
 * In the future, additional fields can be specified in the plugin
 * manifest file to allow for greater extensibility in protocols.
 * @author Chris Thompson
 *
 */
public class NewAccountWindow extends JFrame implements ActionListener {
	
	//UI Elements
	private JComboBox connectionPicker_;
	
	private JLabel protocolLbl_;
	private JLabel handleLbl_;
	private JLabel pwLbl_;
	private JLabel localAliasLbl_;
	private JLabel headerLbl_;
	
	private JTextField handleField_;
	private JPasswordField pwField_;
	private JTextField localAliasField_;
	
	private JCheckBox savePwBox_;
	private JCheckBox autoSigninBox_;
		
	private JButton saveBtn_;
	private JButton cancelBtn_;
	
	private JSeparator hr_;
	
	//String Constants\\
	
	//UI Strings
	private static final String PROTOCOL_ = "Protocol:";
	private static final String HANDLE_ = "Handle:";
	private static final String PASSWORD_ = "Password:";
	private static final String SAVE_PASSWORD_ = "Save Password";
	private static final String LOCAL_ALIAS_ = "Local Alias:";
	private static final String AUTO_SIGNIN_ = "Auto Sign In";
	private static final String SAVE_ = "Save";
	private static final String CANCEL_ = "Cancel";
	private static final String CREATE_NEW_ACCOUNT_= "Create a new account";
	
	//File System Constants
	private OutputStream ostream = GlobalPreferences.getInstance().getFSC().getOutputStream("accounts");
	
	
	//Data members\\
	
	//Connection manager
	ConnectionManager manager_;
	
	//Members to hold the state of check boxes
	private String savePw_ = "false";
	private String autoLogin_ = "false";
	
	
	//Account Model
	private ActiveAccountModel aam_ = null;
	
	public NewAccountWindow(ConnectionManager manager){
		
		manager_ = manager;
		
		initComponents();
		
	}
	
	public NewAccountWindow(ConnectionManager manager, ActiveAccountModel aam){
		
		manager_ = manager;
		aam_ = aam;
		initComponents();
		
	}


	/**
	 * @param manager
	 */
	private void initComponents() {
		connectionPicker_ = new JComboBox();
		connectionPicker_.setRenderer(new ConnectionRenderer());
		
		hr_ = new JSeparator(SwingConstants.HORIZONTAL);
		
		protocolLbl_ = new JLabel(PROTOCOL_);
		handleLbl_ = new JLabel(HANDLE_);
		pwLbl_ = new JLabel(PASSWORD_);
		localAliasLbl_ = new JLabel(LOCAL_ALIAS_);
		headerLbl_ = new JLabel(CREATE_NEW_ACCOUNT_);
		
		handleField_ = new JTextField();
		pwField_ = new JPasswordField();
		
		localAliasField_ = new JTextField();
		
		savePwBox_ = new JCheckBox(SAVE_PASSWORD_);
		autoSigninBox_ = new JCheckBox(AUTO_SIGNIN_);
		
		saveBtn_ = new JButton(SAVE_);
		cancelBtn_ = new JButton(CANCEL_);
		
		//Action Commands
		saveBtn_.setActionCommand(SAVE_);
		cancelBtn_.setActionCommand(CANCEL_);
		savePwBox_.setActionCommand(SAVE_PASSWORD_);
		autoSigninBox_.setActionCommand(AUTO_SIGNIN_);
		
		
		Container cp = getContentPane();
		
		//Create and set the layout manager
		SpringLayout sl = new SpringLayout();
		cp.setLayout(sl);
		
		
		//Sizes\\
		
		//Window Size
		setMinimumSize(new Dimension(300, 400));
		setPreferredSize(new Dimension(300, 400));
		
		//Save Button Size
		saveBtn_.setMinimumSize(new Dimension(75, 26));
		saveBtn_.setMaximumSize(new Dimension(75, 26));
		saveBtn_.setPreferredSize(new Dimension(75,26));
		
		//Cancel Button Size
		cancelBtn_.setMinimumSize(new Dimension(75, 26));
		cancelBtn_.setMaximumSize(new Dimension(75, 26));
		cancelBtn_.setPreferredSize(new Dimension(75, 26));
		
		//Username Field
		handleField_.setMinimumSize(new Dimension(150, 23));
		handleField_.setMaximumSize(new Dimension(150, 23));
		handleField_.setPreferredSize(new Dimension(150, 23));
		
		//Password Field
		pwField_.setMinimumSize(new Dimension(150, 23));
		pwField_.setMaximumSize(new Dimension(150, 23));
		pwField_.setPreferredSize(new Dimension(150, 23));
		
		//Local Alias Field
		localAliasField_.setMinimumSize(new Dimension(150, 23));
		localAliasField_.setMaximumSize(new Dimension(150, 23));
		localAliasField_.setPreferredSize(new Dimension(150, 23));
		
		//Protocol Picker
		connectionPicker_.setMinimumSize(new Dimension(150, 30));
		connectionPicker_.setMaximumSize(new Dimension(150, 30));
		connectionPicker_.setPreferredSize(new Dimension(150, 30));
		 
		//Horizontal Rule
			//No sizes need to be set
			//Dimensions will be set by the constraints
		
		//Set the window title
		setTitle(CREATE_NEW_ACCOUNT_);
		
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
		sl.putConstraint(SpringLayout.NORTH, handleLbl_, 40, SpringLayout.SOUTH, headerLbl_);
		sl.putConstraint(SpringLayout.WEST, handleLbl_, 30, SpringLayout.WEST, cp);
		
		//Username Field
		sl.putConstraint(SpringLayout.NORTH, handleField_, 39, SpringLayout.SOUTH, headerLbl_);
		sl.putConstraint(SpringLayout.WEST, handleField_, 15, SpringLayout.EAST, handleLbl_);
		
		//Password Label
		sl.putConstraint(SpringLayout.NORTH, pwLbl_, 20, SpringLayout.SOUTH, handleLbl_);
		sl.putConstraint(SpringLayout.EAST, pwLbl_, 0, SpringLayout.EAST, handleLbl_);
		
		//Password Field
		sl.putConstraint(SpringLayout.NORTH, pwField_, 19, SpringLayout.SOUTH, handleLbl_);
		sl.putConstraint(SpringLayout.WEST, pwField_, 15, SpringLayout.EAST, pwLbl_);
		
		//Local Alias Label
		sl.putConstraint(SpringLayout.NORTH, localAliasLbl_, 20, SpringLayout.SOUTH, pwLbl_);
		sl.putConstraint(SpringLayout.EAST, localAliasLbl_, 0, SpringLayout.EAST, handleLbl_);
		
		//Local Alias Field
		sl.putConstraint(SpringLayout.NORTH, localAliasField_, 19, SpringLayout.SOUTH, pwLbl_);
		sl.putConstraint(SpringLayout.WEST, localAliasField_, 15, SpringLayout.EAST, localAliasLbl_);
		
		//Protocol Picker Label
		sl.putConstraint(SpringLayout.NORTH, protocolLbl_, 25, SpringLayout.SOUTH, localAliasLbl_);
		sl.putConstraint(SpringLayout.EAST, protocolLbl_, 0, SpringLayout.EAST, handleLbl_);
		
		//Protocol Picker
		sl.putConstraint(SpringLayout.NORTH, connectionPicker_, 19, SpringLayout.SOUTH, localAliasLbl_);
		sl.putConstraint(SpringLayout.WEST, connectionPicker_, 15, SpringLayout.EAST, protocolLbl_);
		
		//Save Password Checkbox
		sl.putConstraint(SpringLayout.NORTH, savePwBox_, 20, SpringLayout.SOUTH, protocolLbl_);
		sl.putConstraint(SpringLayout.WEST, savePwBox_, 20, SpringLayout.WEST, cp);
		
		//Auto Login Checkbox
		sl.putConstraint(SpringLayout.NORTH, autoSigninBox_, 20, SpringLayout.SOUTH, savePwBox_);
		sl.putConstraint(SpringLayout.WEST, autoSigninBox_, 20, SpringLayout.WEST, cp);	
		
		//Save Button
		sl.putConstraint(SpringLayout.EAST, saveBtn_, -20, SpringLayout.EAST, cp);
		sl.putConstraint(SpringLayout.SOUTH, saveBtn_, -15, SpringLayout.SOUTH, cp);
		
		//Cancel Button
		sl.putConstraint(SpringLayout.EAST, cancelBtn_, -20, SpringLayout.WEST, saveBtn_);
		sl.putConstraint(SpringLayout.SOUTH, cancelBtn_, -15, SpringLayout.SOUTH, cp);
		
		
		//Add the components to the window
		cp.add(headerLbl_);
		cp.add(hr_);
		cp.add(handleLbl_);
		cp.add(handleField_);
		cp.add(pwLbl_);
		cp.add(pwField_);
		cp.add(localAliasLbl_);
		cp.add(localAliasField_);
		cp.add(protocolLbl_);
		cp.add(connectionPicker_);
		cp.add(savePwBox_);
		cp.add(autoSigninBox_);
		cp.add(saveBtn_);
		cp.add(cancelBtn_);
		
		//Populate the protocol picker
		for (Entry<String, ConnectionStrategy> entry:manager_.getRegisteredStrategies().entrySet()){
			connectionPicker_.addItem(entry.getValue());
		}
		
		//Set the location on screen
		setLocation(new Point(Toolkit.getDefaultToolkit().getScreenSize().width / 3,Toolkit.getDefaultToolkit().getScreenSize().height / 4));
		
		//Add action listeners
		saveBtn_.addActionListener(this);
		cancelBtn_.addActionListener(this);
		savePwBox_.addActionListener(this);
		autoSigninBox_.addActionListener(this);
		
		
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
			//Attempt to sign on and write the config to a file
			ConnectionStrategy cs = ((ConnectionStrategy) connectionPicker_.getSelectedItem());
			cs.signOn(manager_, handleField_.getText(), new String(pwField_.getPassword()));
			manager_.addStrategy(cs);
			
			if (aam_ != null){
				aam_.add(cs);
			}
			
			try {
				InputStream istream = null;
				try {
					istream = GlobalPreferences.getInstance().getFSC().getInputStream("accounts");
				}catch (FileNotFoundException e) {
					return;
				}
				
				Document dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(istream);
				
				Element curAccount = dom.createElement(cs.getProtocol() + ":" + cs.getHandle());
				curAccount.setAttribute("localalias", localAliasField_.getText());
				curAccount.setAttribute("savepassword", savePw_);
				curAccount.setAttribute("autosignin", autoLogin_);
				
				Element usernameEle = dom.createElement("Username");
				usernameEle.setAttribute("value", handleField_.getText());
				
				Element passEle = dom.createElement("Password");
				
				if (savePw_.equalsIgnoreCase("true")){
					passEle.setAttribute("value", new String(pwField_.getPassword()));
				}else{
					passEle.setAttribute("value", "");
				}
				
				curAccount.appendChild(usernameEle);
				curAccount.appendChild(passEle);
				
				dom.getElementsByTagName("Accounts").item(0).appendChild(curAccount);
				
				OutputFormat format = new OutputFormat(dom);
				format.setIndenting(true);

				XMLSerializer serializer = new XMLSerializer(
				ostream, format);

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
			
		}else if (ac.equalsIgnoreCase(AUTO_SIGNIN_)){
			
			if (autoLogin_.equalsIgnoreCase("true")){
				autoLogin_ = "false";
			}else{
				autoLogin_ = "true";
			}
			
		}else if (ac.equalsIgnoreCase(SAVE_PASSWORD_)){
			
			if (savePw_.equalsIgnoreCase("true")){
				savePw_ = "false";
			}else{
				savePw_ = "true";
			}
		}

	}
	
	private void packAndRepaint() {
		SwingUtilities.updateComponentTreeUI(this);
		this.repaint();
	}

}
