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

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.GroupLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.whisperim.aim.AIMStrategy;
import org.whisperim.lastfm.LastFM;
import org.whisperim.models.BuddyListModel;
import org.whisperim.models.PluginListModel;
import org.whisperim.plugins.Plugin;
import org.whisperim.plugins.PluginLoader;
import org.whisperim.prefs.Preferences;
import org.whisperim.prefs.PreferencesWindow;
import org.whisperim.renderers.BuddyListRenderer;
import org.whisperim.security.Encryptor;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

/**
 * This class handles the creation of the swing interface components that 
 * includes the buddy list and sign-on dialog.  The class also handles the
 * sending of messages, setting the idle status locally, and delegating
 * UI updates to the appropriate places.
 * 
 * @author Kirk Banks, Chris Thompson, John Dlugokecki, Cory Plastek
 */
public class WhisperClient extends JFrame implements ActionListener {


	private static final long serialVersionUID = -2916085324340552469L;
	
	//Enums used for plugin identification
	public static final int CONNECTION = 0;
	public static final int LOOK_AND_FEEL = 1;

	
	//We need to figure this out, like this, it doesn't make
	//sense to store the instance here if we can't call methods
	//on it
	private Preferences prefs_ = Preferences.getInstance();
	private static final Image whisperIcon_ = Preferences.getInstance().getWhisperIconSmall();
	
	private PluginListModel plm_;

	private Timer myTimer_;
	private IdleTT myTaskTimer_;    
	private static ConnectionManager manager_;
	private BuddyListModel blm_ = new BuddyListModel();

	private PluginLoader pluginLoader_;
	private JList buddyList_;
	private JScrollPane buddyListScroll_;
	private JMenu whisperMenu_;
	private JFrame accountInfoPane_;
	//private JMenu preferencesMenu_;

	private JMenuBar menuBar_;

	//private JMenuItem encryption_;
	private JMenuItem newIm_;
	private JCheckBoxMenuItem setStatus_;
	private JMenuItem plugins_;
	private JMenuItem preferences_;
	private JMenuItem accounts_;
	private JCheckBoxMenuItem sound_;
	private JMenuItem quit_;
	
	private WhisperSystemTray tray;
	//private PopupMenu popupMenu1;

	private static final String BUDDY_LIST_ = "Buddy List";
	private static final String BUDDY_LIST_IDLE_ = "Buddy List [Idle]";

	//first menu\\
	private static final String WHISPER_ = "Whisper"; //menu 1 header
	private static final String NEWIM_ = "New Instant Message";
	private static final String SET_STATUS_ = "Set Status to Last.fm";
	private static final String PLUGINS_ = "Plugins";
	private static final String ACCOUNTS_ = "Accounts";
	private static final String PREFERENCES_ = "Preferences";
	private static final String SOUND_ = "Sound";
	private static final String QUIT_ = "Quit";

	//List of Listeners used by WhisperSystemTray
	private List<ClientListener> clientListeners_ = new ArrayList<ClientListener>();
	
	//second menu\\ - not used!
	//menu 2 header
	//private static final String ENCRYPTION_ = "Encryption"; //menu 2 first item

	//end menus\\

	//Directory constants
	private static final String WHISPER_HOME_DIR_ = System.getProperty("user.home") + File.separator + "Whisper";
	private static final String ACCOUNTS_FILE_ = WHISPER_HOME_DIR_ + File.separator + "accounts";

	//Error messages
	private static final String ERROR_CREATING_ACCOUNTS_ = "An error has occured creating the file to store account information, try restarting Whisper.";
	private static final String ERROR_READING_ACCOUNTS_ = "An error has occured reading the file that stores account information, try restarting Whisper.";
	private static final String IO_ERROR_ = "IO Error";

	//Status update types
	public static final String OFFLINE = "Offline";
	public static final String RATE_LIMITED = "Rate Limited";
	public static final String INVALID_USERNAME_PASSWORD = "Invalid credentials";
	public static final String SERVICE_UNREACHABLE = "Service Unreachable";



	private HashMap<String, WhisperIM> openBuddies_ = new HashMap<String, WhisperIM>();
	/**
	 * Constructor.
	 * @param manager - Connection manager to be associated with this instance
	 */
	public WhisperClient(ConnectionManager manager) {

		//set program icon
		setIconImage(whisperIcon_);
		
		
		//set frame title
		setTitle(WHISPER_);
		
		
		//gui stuff will be seperated from code
		//right now its just ugly
		initComponents();
		setLocation(new Point(Toolkit.getDefaultToolkit().getScreenSize().width / 3,Toolkit.getDefaultToolkit().getScreenSize().height / 4));
		
		
		//start sounds
		Sound sound = new Sound();
		getClientListeners().add(sound);
		sound.playSound(this, "Open.wav");
		
		
		//start system tray
		tray = new WhisperSystemTray();
		tray.startSystemTray(this, manager_);
		
		
		//reset idle timer
		resetTimer(5000);   

		
		//set connection manager
		manager_ = manager;
		manager_.setClient(this);
		
		
		//This must be called after the manager_ member is set.
		pluginLoader_ = new PluginLoader(this);
		try {
			pluginLoader_.loadPlugins();
		} catch (Exception e) {

			e.printStackTrace();
		}

		registerPlugin("AIM", CONNECTION, new AIMStrategy());
		loadAccounts();
		
		this.setVisible(true);
	}

	/**
	 * This helper method loads saved account information from the accounts
	 * file.  If the file does not exist it will create one.  If it determines
	 * that there is no saved account information, it will display to the user
	 * the new account window so that they can create an account.
	 */
	private void loadAccounts(){

		File accounts = new File(ACCOUNTS_FILE_);
		Document dom;
		if (!accounts.exists()){
			//Accounts file doesn't exist,
			//likely a first time use


			try {
				accounts.createNewFile();
				dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

				Element root = dom.createElement("Accounts");
				dom.appendChild(root);

				OutputFormat format = new OutputFormat(dom);
				format.setIndenting(true);

				XMLSerializer serializer = new XMLSerializer(
						new FileOutputStream(ACCOUNTS_FILE_), format);

				serializer.serialize(dom);

			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, ERROR_CREATING_ACCOUNTS_ + " " + IO_ERROR_ + ": " + e.getMessage(),
						IO_ERROR_, JOptionPane.ERROR_MESSAGE);
				return;
			}catch (ParserConfigurationException e) {

				e.printStackTrace();
				return;
			}


			//Show the new account window
			EventQueue.invokeLater(new Runnable(){

				@Override
				public void run(){
					new NewAccountWindow(manager_);
				}
			});

		}else {
			//Load the account information
			try {
				dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(accounts);

				NodeList accountList = dom.getElementsByTagName("Accounts").item(0).getChildNodes();

				if (accountList.getLength() == 0){
					//No accounts exist in the file, show the 
					//new account window and exit

					EventQueue.invokeLater(new Runnable(){

						@Override
						public void run(){
							new NewAccountWindow(manager_);
						}
					});

					return;

				}else{
					//Accounts have been saved, load them up
					for (int i = 0; i < accountList.getLength(); ++i){
						if (accountList.item(i).getNodeType() == Node.TEXT_NODE){

						}else{

							Element e = (Element) accountList.item(i);

							String handle = e.getTagName().substring(e.getTagName().indexOf(":") + 1);

							String protocol = e.getTagName().substring(0, e.getTagName().indexOf(":"));

							String pw = ((Element)e.getElementsByTagName("Password").item(0)).getAttribute("value");

							HashMap<String, ConnectionStrategy> potentialConnections = manager_.getRegisteredStrategies();

							if(e.getAttribute("autosignin").equalsIgnoreCase("true")){
								ConnectionStrategy cs = potentialConnections.get(protocol);
								cs.signOn(manager_, handle, pw);
								manager_.addStrategy(cs);
							}else{
								ConnectionStrategy cs = potentialConnections.get(protocol);
								cs.setHandle(handle);
								manager_.addStrategy(cs);
							}
						}


					}

				}

			} catch (SAXException e) {

				e.printStackTrace();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, ERROR_READING_ACCOUNTS_ + " " + IO_ERROR_ + ": " + e.getMessage(),
						IO_ERROR_, JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			} catch (ParserConfigurationException e) {

				e.printStackTrace();
			}



		}
	}



	/**
	 * This method resets the timer responsible for keeping track of
	 * how much time is left before the user goes idle.  It resets the 
	 * timer to the provided value
	 * @param timeToIdle - Time that should elapse before the user 
	 * 		becomes idle
	 */
	private void resetTimer(int timeToIdle)
	{
		if(myTaskTimer_ != null)
		{
			this.setTitle(BUDDY_LIST_);

			myTaskTimer_.cancel();
			myTaskTimer_ = new IdleTT();
			myTimer_.schedule(myTaskTimer_, timeToIdle);
		}
		else
		{
			myTimer_ = new Timer();
			myTaskTimer_ = new IdleTT();
			myTimer_.schedule(myTaskTimer_, timeToIdle); //user goes idle after 5 seconds for demo/test purposes
		}
	}

	/**
	 * Inner class responsible for setting the user to idle.
	 * @author Chris Thompson
	 *
	 */
	class IdleTT extends TimerTask {
		@Override
		public void run() {
			setTitle(BUDDY_LIST_IDLE_);
		}
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

		//create the menu bar
		menuBar_ = new JMenuBar();


		//first menu\\
		//File
			//New IM
			//Set Away
			//Accounts
			//Plugins
			//Preferences
			//Quit
		whisperMenu_ = new JMenu();
		whisperMenu_.setText(WHISPER_);
		menuBar_.add(whisperMenu_);

		newIm_ = new JMenuItem();
		newIm_.setText(NEWIM_);
		newIm_.setActionCommand(NEWIM_);
		newIm_.addActionListener(this);
		whisperMenu_.add(newIm_);
		
		setStatus_ = new JCheckBoxMenuItem();
		setStatus_.setText(SET_STATUS_);
		setStatus_.setActionCommand(SET_STATUS_);
		setStatus_.addActionListener(this);
		whisperMenu_.add(setStatus_);
		
		accounts_ = new JMenuItem();
		accounts_.setText(ACCOUNTS_);
		accounts_.setActionCommand(ACCOUNTS_);
		accounts_.addActionListener(this);
		whisperMenu_.add(accounts_);

		plugins_ = new JMenuItem();
		plugins_.setText(PLUGINS_);
		plugins_.setActionCommand(PLUGINS_);
		plugins_.addActionListener(this);
		whisperMenu_.add(plugins_);

		preferences_ = new JMenuItem();
		preferences_.setText(PREFERENCES_);
		preferences_.setActionCommand(PREFERENCES_);
		preferences_.addActionListener(this);
		whisperMenu_.add(preferences_);

		
		sound_ = new JCheckBoxMenuItem(SOUND_);
		sound_.setSelected(Preferences.getInstance().getSoundsEnabled());
		sound_.setActionCommand(SOUND_);
		sound_.addActionListener(this);
		whisperMenu_.add(sound_);
		
		quit_ = new JMenuItem();
		quit_.setText(QUIT_);
		quit_.setActionCommand(QUIT_);
		quit_.addActionListener(this);
		whisperMenu_.add(quit_);
		
		//second menu\\ - not used
		//Preferences
		//Encryption
		/*
		preferencesMenu_ = new JMenu();
		preferencesMenu_.setText(PREFERENCES_);
		menuBar_.add(preferencesMenu_);

		encryption_ = new JMenuItem();
		encryption_.setText(ENCRYPTION_);
		encryption_.setActionCommand(ENCRYPTION_);
		encryption_.addActionListener(this);
		preferencesMenu_.add(encryption_);
		 */

		setJMenuBar(menuBar_);

		/* popup menus do right-click stuff
		 * we don't need this right now, implement later
		popupMenu1 = new PopupMenu();
		popupMenu1.setLabel("popupMenu1");
		popupMenu1.addActionListener(this);
		 */

		buddyList_ = new JList(blm_);
		buddyList_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION );

		BuddyListRenderer buddyListRenderer_ = new BuddyListRenderer();
		buddyList_.setCellRenderer(buddyListRenderer_);

		buddyListScroll_ = new JScrollPane(buddyList_);
		buddyListScroll_.setViewportView(buddyList_);      
		buddyListScroll_.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		buddyListScroll_.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		plm_ = new PluginListModel();


		blm_.addListDataListener(new ListDataListener(){

			@Override
			public void contentsChanged(ListDataEvent e) {
				if (e.getSource() instanceof ListModel){
					buddyList_.setModel((ListModel) e.getSource());
				}
			}

			@Override
			public void intervalAdded(ListDataEvent e) {

			}

			@Override
			public void intervalRemoved(ListDataEvent e) {


			}

		});

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				formWindowClosing(evt);
			}
		});

		buddyList_.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent evt) {
				BuddiesComponentShown(evt);
			}
		});

		buddyList_.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				JList Buddies = (JList) mouseEvent.getSource();
				if (mouseEvent.getClickCount() == 2) {
					int index = Buddies.locationToIndex(mouseEvent.getPoint());
					if (index >= 0) {
						final Buddy selectedBuddy_ = (Buddy) Buddies.getModel().getElementAt(index);
						//need to start new chat window

						EventQueue.invokeLater(new Runnable() {
							public void run() {
								newIMWindow(selectedBuddy_);
							}
						});
					}
				}
			}
		});


		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup()
				.addComponent(buddyListScroll_, 100, 182, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
				layout.createParallelGroup()
				.addComponent(buddyListScroll_, GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
		);
		
		accountInfoPane_ = new JFrame();
		accountInfoPane_.setAlwaysOnTop(true);
		accountInfoPane_.setMinimumSize(new Dimension(100, 50));
		


		pack();


	}

	/**
	 * This is a helper method to generate a new IM window.
	 * @param selectedBuddy_
	 * @param client
	 */
	public WhisperIM newIMWindow(final Buddy selectedBuddy_) {

		WhisperIM window;
		WhisperIMPanel panel;

		if (openBuddies_.isEmpty()){
			//There is no window

			window = new WhisperIM(this, manager_.getPrivateKey());
			panel = new WhisperIMPanel(selectedBuddy_, window, manager_.getPrivateKey());
			window.addPanel(selectedBuddy_, panel);
			window.setVisible(true);

			openBuddies_.put(selectedBuddy_.getHandle().toLowerCase().replace(" ", ""), window);

		}
		else
		{
			//As is, this randomly chooses an open window to put the panel in
			window = openBuddies_.values().iterator().next();
			panel = new WhisperIMPanel(selectedBuddy_, window, manager_.getPrivateKey());
			window.addPanel(selectedBuddy_, panel);
			openBuddies_.put(selectedBuddy_.getHandle().toLowerCase().replace(" ", ""), window);

		}
		window.requestFocus();
		return window;

	}

	
	//  Methods for Whisper System Tray  \\
	
	//Simple method to create a new blank IM
	public void createNewIMWindow()
	{
		new WhisperNewIMWindow(manager_, this);
	}
	
	//Simple method to open about page
	public void openAboutPage()
	{
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				PreferencesWindow prefs = PreferencesWindow.getInstance();
				prefs.setPreferencesCategory(PreferencesWindow.ABOUT_);
			}
		});
	}
	
	//Simple method to open preference page
	public void openPreferencesWindow()
	{
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				PreferencesWindow prefs = PreferencesWindow.getInstance();
				prefs.setPreferencesCategory(PreferencesWindow.GENERAL_);
			}
		});
	}
	
	//Simple method to open plugins page
	public void openPluginsPage()
	{
		new WhisperPluginManagerWindow(pluginLoader_, plm_);
	}
	
	//Simple method to open Account Manager
	public void openAccountsPage()
	{
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				PreferencesWindow prefs = PreferencesWindow.getInstance();
				prefs.setPreferencesCategory(PreferencesWindow.ACCOUNTS_);
			}
		});
	}
	
	private void BuddiesComponentShown(ComponentEvent evt) {

	}

	/**
	 * Method that allows the ConnectionManager class to notify
	 * the WhisperClient class of a change in status for an open
	 * connection.  
	 * @param status - The String containing the status message
	 * @param account - The identifier for the account
	 */
	public void statusUpdate(String status, String account){

	}

	private void formWindowClosing(WindowEvent evt) {
		WhisperSystemTray.closeTray();
		manager_.signOff();
	}

	public void recieveMessage(final Message message){
		//First we need to check to see if it contains
		//information that is intended for the client
		//to interpret (key file, etc.)
		if (message.getMessage().startsWith("<whisperim")){
			//This is an instruction to the client, we need to parse it
			//and then we'll swallow it or print out a message to the
			//user.
			
			System.out.println(message.toString());
				
			if (message.getMessage().contains("keyspec=")){
				//A public key has been sent.

				//Key information will be Base64 encoded to allow for easy transport
				String keyText = message.getMessage().substring(
						message.getMessage().indexOf("keyspec=")+ 8, 
						message.getMessage().indexOf("--"));

				//Now we have the text of the key, pass it to the Encryptor
				//to parse it and store it.  We will also probably want to
				//fire some sort of event that allows the encryption to be
				//enabled.
				Encryptor.writeKeyToFile(keyText, message.getProtocol() + ":" + message.getFrom());


				try {
					X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(Base64.decode(keyText.getBytes()));
					KeyFactory rsaKeyFac = null;
					rsaKeyFac = KeyFactory.getInstance("RSA");
					final PublicKey recKey = rsaKeyFac.generatePublic(pubKeySpec);
					
					if (openBuddies_.get(message.getFrom().toLowerCase().replace(" ", "")) == null){
						//There isn't currently a window associated with that buddy
						java.awt.EventQueue.invokeLater(new Runnable() {
							public void run() {
								//needs to go to an buddy object version
								newIMWindow(new Buddy(message.getFrom(), message.getTo(), message.getProtocol()));
								openBuddies_.get(message.getFrom().toLowerCase().replace(" ", "")).getTab(message.getFrom().toLowerCase().replace(" ", "")).enableEncryption(recKey);
							}
						});
					}else{
						openBuddies_.get(message.getFrom().toLowerCase().replace(" ", "")).getTab(message.getFrom()).enableEncryption(recKey);
					}
					
					//Listener to update SystemTray if IM is received
					for(ClientListener l:clientListeners_){l.messageRec(this, message, message.getFrom());}
				
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (Base64DecodingException e) {
					e.printStackTrace();
				} catch (InvalidKeySpecException e) {
					e.printStackTrace();
				}
			}
		}else {   		
			//It would be really great if we could allow system messages to be passed around
			//That is, if the Whisper program could alert the user of things by adding messages
			//to the chat window.
			if (openBuddies_.get(message.getFrom().toLowerCase().replace(" ", "")) == null){
				//There isn't currently a window associated with that buddy

				EventQueue.invokeLater(new Runnable() {
					public void run() {
						//needs to go to an buddy object version
						newIMWindow(new Buddy(message.getFrom(), message.getTo(), message.getProtocol()));
						openBuddies_.get(message.getFrom().toLowerCase().replace(" ", "")).getTab(message.getFrom().toLowerCase().replace(" ", "")).receiveMsg(message);
					}
				});

			}else{
				openBuddies_.get(message.getFrom().toLowerCase().replace(" ", "")).getTab(message.getFrom()).receiveMsg(message);
			}

		//Listener to update SystemTray if IM is received
		for(ClientListener l:clientListeners_){l.messageRec(this, message, message.getFrom());}
		}
	}

	public void sendMessage (Message message){
		//Listener to update sound if IM is received
		for(ClientListener l:clientListeners_){l.sentMessage(this);}
		manager_.sendMessage(message);
		resetTimer(5000);
	}


	/**
	 * This method will update the buddy list with a new list.
	 * All buddies in the list must be associated with the same session.
	 * @param 
	 * 		newBuddies - The list of buddies to be added
	 */
	public void updateBuddyList(ArrayList<Buddy> newBuddies)
	{
		if (newBuddies.size() == 0)
			return;

		if (blm_.getSize() == 0){
			//List is empty, we don't need to check anything
			blm_.addBuddies(newBuddies);
		}else{
			//We will need to see what changed
			String protocol = newBuddies.get(0).getProtocolID();
			String assocHandle = newBuddies.get(0).getAssociatedLocalHandle();
			blm_.removeBuddies(protocol, assocHandle);
			blm_.addBuddies(newBuddies);
		}

	}

	public void onWindowClose(String handle){
		openBuddies_.remove(handle.toLowerCase().replace(" ", ""));
	}


	public void setAwayMessage(String message){
		manager_.setAwayMessage(message);
	}

	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		//Quit
		if (actionCommand.equals(quit_.getActionCommand())) {
			processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}

		//New blank IM window
		if (actionCommand.equals(newIm_.getActionCommand())) {
			final WhisperClient wc = WhisperClient.this;
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					new WhisperNewIMWindow(manager_, wc);
				}
			});
		}
		
		//Set / Unset Status
		if (actionCommand.equals(setStatus_.getActionCommand())){			
			if(setStatus_.isSelected())
			{
				String username = JOptionPane.showInputDialog(null, "Please enter your Last.FM username:");
				
				if(username != "")
				{
					if(setStatus_.isSelected())
					{
						//set away status
						manager_.setStatusMessage(new LastFM(username).getLastSong());
					}
					else
						manager_.setStatusMessage("");
				}
			}
			else
			{
				manager_.setStatusMessage("");
				setStatus_.setSelected(false);
			}
		}

		//Open the plugins window
		if (actionCommand.equals(plugins_.getActionCommand())){

			EventQueue.invokeLater(new Runnable() {
				public void run() {
					new WhisperPluginManagerWindow(pluginLoader_, plm_);
				}
			});
		}

		//Preferences
		if (actionCommand.equals(preferences_.getActionCommand())) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					PreferencesWindow prefs = PreferencesWindow.getInstance();
					prefs.setPreferencesCategory(PreferencesWindow.GENERAL_);
				}
			});
		}
		
		//Account Management Window
		if (actionCommand.equals(accounts_.getActionCommand())){
			EventQueue.invokeLater(new Runnable(){
				public void run(){
					PreferencesWindow prefs = PreferencesWindow.getInstance();
					prefs.setPreferencesCategory(PreferencesWindow.ACCOUNTS_);
				}
			});
		}

		//Sound
		if (actionCommand.equals(sound_.getActionCommand())) {
			toggleSound();
			this.getSystemTray().trayStateChange(1);
		}
	}
	
	public void toggleSound(){
		System.out.println("Sound toggled");
		Preferences.getInstance().setSoundsEnabled(!Preferences.getInstance().getSoundsEnabled());
	}
	
	public JCheckBoxMenuItem getSound_() {
		return sound_;
	}

	public void setSound_(JCheckBoxMenuItem sound_) {
		this.sound_ = sound_;
	}

	/**
	 * This method will be used to register a loaded plugin.  The WhisperClient
	 * object will keep track of all currently running plugins and hand them off to
	 * the appropriate objects that need them.
	 * @param name - The string name of the plugin
	 * @param type - Enum for the type of the plugin
	 * @param c - The "Class" object representing the plugin
	 */
	public void registerPlugin(String name, int type, Plugin p){

		plm_.addPlugin(p);
		switch (type){

		case CONNECTION:
			//Stuff for loading a connection
			manager_.registerConnection(name, p);
			break;
		case LOOK_AND_FEEL:
			//Stuff for loading a look and feel
			break;
		default:
			//Handle a plugin that doesn't conform
			break;
		}
	}

	public List<ClientListener> getClientListeners() {
		return clientListeners_;
	}

	public void setClientListeners(List<ClientListener> clientListeners_) {
		this.clientListeners_ = clientListeners_;
	}

	public WhisperSystemTray getSystemTray()
	{
		return this.tray;	
	}
	
	public void setSystemTray()
	{	
		
	}
	
	public static ConnectionManager getConnectionManager() {
		return manager_;
	}
}