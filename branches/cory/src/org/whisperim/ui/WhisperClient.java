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

package org.whisperim.ui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.speech.AudioException;
import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.text.BadLocationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.jivesoftware.smack.XMPPException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.whisperim.Browser.BrowserLite;
import org.whisperim.DownloadManager.DownloadManager;
import org.whisperim.EmailManager.ExpressWhisperMailManager;
import org.whisperim.JXTA_P2P.Peer2PeerPlugIn;
import org.whisperim.SocialSiteDump.SocialSiteManager;
import org.whisperim.aim.AIMStrategy;
import org.whisperim.client.Buddy;
import org.whisperim.client.ConnectionManager;
import org.whisperim.client.ConnectionStrategy;
import org.whisperim.client.Message;
import org.whisperim.gtalk.Gtalk;
import org.whisperim.lastfm.LastFM;
import org.whisperim.listeners.ClientListener;
import org.whisperim.models.BuddyListModel;
import org.whisperim.models.PluginListModel;
import org.whisperim.plugins.Plugin;
import org.whisperim.plugins.PluginLoader;
import org.whisperim.prefs.PrefListener;
import org.whisperim.prefs.Preferences;
import org.whisperim.prefs.PreferencesWindow;
import org.whisperim.renderers.BuddyListRenderer;
import org.whisperim.security.Encryptor;
import org.whisperim.security.Locking;
import org.whisperim.whisperbot.WhisperBot;
import org.xml.sax.SAXException;

import com.aol.acc.AccException;
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
	private static final ImageIcon whisperIcon_ = Preferences.getInstance().getWhisperIconSmall();
	
	private PluginListModel plm_;

	private Timer myTimer_;
	private IdleTT myTaskTimer_;    
	private static ConnectionManager manager_; 
	private BuddyListModel blm_ = new BuddyListModel();
	private ProfileEditor editor;
	private SMSText sms;

	private PluginLoader pluginLoader_;
	private JList buddyList_;
	private JScrollPane buddyListScroll_;
	private JMenu whisperMenu_;
	private JMenuBar menuBar_;
	private JMenuItem newIm_;
	private JCheckBoxMenuItem setStatus_;
	private JMenuItem plugins_;
	private JMenuItem preferences_;
	private JMenuItem accounts_;
	private JCheckBoxMenuItem sound_;
	private JPopupMenu popupMenu_;
	private JMenuItem popupNewIM_;
	private JMenuItem popupNewWindow_;
	private boolean soundsEnabled_;
	private JMenuItem socialSites_;
	private JMenuItem downloadManager_;
	private JMenuItem emailManager_;
	private JMenuItem browserLite_;
	private JMenuItem quit_;
	private boolean alwaysNewWindow_ = false;
	private JMenuItem newWindow_;
	private JMenuItem profile_;
	private JMenuItem newSMSText_;
	private JMenuItem lockWhisper_;
	private JMenu optionsMenu_;
	
	// Speech Here.
	private boolean enableSpeech_ = true;
	
	private Dimension frameMinSize_ = new Dimension(175,400);
	private Dimension framePrefSize_ = new Dimension(175,500);
	
	private WhisperSystemTray tray_;

	private static final String BUDDY_LIST_ = "Buddy List";
	private static final String BUDDY_LIST_IDLE_ = "Buddy List [Idle]";

	//first menu\\
	private static final String WHISPER_ = "Whisper"; 
	private static final String NEWIM_ = "New IM...";
	private static final String NEWUSERIM_ = "IM Selected User";
	private static final String SMSTEXT_ = "Send SMS Text...";
	private static final String SET_STATUS_ = "Set Status...";
	private static final String SOCIAL_SITE_MANAGER_ = "Social Site Notifications...";
	private static final String DOWNLOAD_MANAGER_ = "Download Manger...";
	private static final String EMAIL_MANAGER_ = "Express Whisper Mail...";
	private static final String BROWSER_LITE_ = "Whisper Web Browser";
	private static final String QUIT_ = "Quit";
	
	//second menu\\
	private static final String OPTIONS_ = "Options";
	private static final String PREFERENCES_ = "Preferences...";
	private static final String SOUND_ = "Sound";
	private static final String ACCOUNTS_ = "Accounts";
	private static final String PROFILE_ = "Profile...";
	private static final String PLUGINS_ = "Plugins";
	private static final String LOCK_WHISPER_ = "Lock Whisper...";
	

	
	//end menus\\

	//List of Listeners used by WhisperSystemTray
	private List<ClientListener> clientListeners_ = new ArrayList<ClientListener>();
	
	
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
	 * @throws AccException 
	 */
	public WhisperClient(ConnectionManager manager) throws AccException {		
		manager_ = manager;
		manager_.setClient(this);
		
		//start preferences - speeds up loading time for later
		/*Runnable prefsRunnable = new Runnable() {
			public void run() {
				PreferencesWindow prefs = PreferencesWindow.getInstance();
				prefs.setVisible(false);
			}
		};
		Thread prefsThread = new Thread(prefsRunnable);
		prefsThread.start();
		*/
		final WhisperClient jf = this;
		
		//start system tray
		Runnable systrayRunnable = new Runnable() {
			public void run() {
				tray_ = new WhisperSystemTray();
				tray_.startSystemTray(jf, manager_);
			}
		};
		Thread systray = new Thread(systrayRunnable);
		systray.start();
		
		//start sounds
		final Sound sound = new Sound();
		getClientListeners().add(sound);
		
		//play login sound
		Runnable soundsRunnable = new Runnable() {
			public void run() {
				sound.playSound(jf, "Open.wav");
			}
		};
		Thread soundsThread = new Thread(soundsRunnable);
		soundsThread.start();
		
		Preferences.getInstance().getListeners().add(new PrefListener() {
			private boolean locked = false;
			@Override
			public void prefChanged(String name, Object o) {
				if(Preferences.SOUNDS_.equals(name) && !locked){
					locked = true;
					if(!o.equals(getSound_().getState())){
						getSound_().setState(((Boolean)o).booleanValue());
					}
					locked = false;
				}
			}
		});
		
		
		//reset idle timer
		resetTimer(5000);		
		
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
		
		Runnable menuRunnable = new Runnable() {
			public void run() {
				createMenu();
				jf.validate();
				jf.repaint();
			}
		};
		Thread menuThread = new Thread(menuRunnable);
		menuThread.start();
		
		Runnable buddyRunnable = new Runnable() {
			public void run() {
				createBuddyList();
				jf.validate();
				jf.repaint();
			}
		};
		Thread buddyThread = new Thread(buddyRunnable);
		buddyThread.start();
		
		plm_ = new PluginListModel();
		//This must be called after the manager_ member is set.
		pluginLoader_ = new PluginLoader(this);
		try {
			pluginLoader_.loadPlugins();
		} catch (Exception e) {

			e.printStackTrace();
		}

		registerPlugin("AIM", CONNECTION, new AIMStrategy());
		
		registerPlugin("GTALK", CONNECTION, new Gtalk());
		
		registerPlugin("JXTA_P2P", CONNECTION, new Peer2PeerPlugIn());
		registerPlugin("AIM BOT", CONNECTION, new WhisperBot());
		loadAccounts();
		
		//set sizes and show
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				formWindowClosing(evt);
			}
		});
		this.setIconImage(whisperIcon_.getImage());
		this.setLocation(new Point(Toolkit.getDefaultToolkit().getScreenSize().width / 3,Toolkit.getDefaultToolkit().getScreenSize().height / 4));
		this.setMinimumSize(frameMinSize_);
		this.setPreferredSize(framePrefSize_);
		this.setTitle(WHISPER_); 
		this.setVisible(true);
		System.out.println("whisperclient displayed");
		
		
		//gtalk testing!
		/*Gtalk gtalkTest = new Gtalk();
		gtalkTest.signOn("whisperimtest1@gmail.com", "cs279_whisperim");
		gtalkTest.displayBuddyList();
		gtalkTest.sendMessage("Hello from whisper gtalk", "cory.plastek@gmail.com");
		gtalkTest.sendMessage("Hello from whisper gtalk", "whisperimtes1@gmail.com");
		gtalkTest.setAway("I'm away");
		
		/*try {
			Thread.currentThread().sleep(5000);
		} catch (InterruptedException e) {
			System.out.println("Thread didn't wait 10 secs");
		}
		
		gtalkTest.setAvailable();
		
		try {
			Thread.currentThread().sleep(5000);
		} catch (InterruptedException e) {
			System.out.println("Thread didn't wait 10 secs");
		}
		gtalkTest.setStatusMessage("I'm back and I'm awesome");
		
		try {
			Thread.currentThread().sleep(5000);
		} catch (InterruptedException e) {
			System.out.println("Thread didn't wait 10 secs");
		}
		
		gtalkTest.setIdle();
		
		try {
			Thread.currentThread().sleep(5000);
		} catch (InterruptedException e) {
			System.out.println("Thread didn't wait 10 secs");
		}
		System.out.println("signing off");
		
		gtalkTest.signOff(); */
		System.out.println(Preferences.getInstance().getGtalkIconSmallLocation());
	}

	
	private void createBuddyList() {
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
		buddyList_ = new JList(blm_);
		buddyList_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION );
		BuddyListRenderer buddyListRenderer_ = new BuddyListRenderer();
		buddyList_.setCellRenderer(buddyListRenderer_);
		buddyList_.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent evt) {
				BuddiesComponentShown(evt);
			}
		});
		
		//right click menu
		popupMenu_ = new JPopupMenu();
		
		popupNewIM_ = new JMenuItem("New IM");
		popupNewIM_.addActionListener(this);
		popupMenu_.add(popupNewIM_);
		
		popupNewWindow_ = new JMenuItem("New Window");
		popupNewWindow_.addActionListener(this);
		popupMenu_.add(popupNewWindow_);
		
		buddyList_.add(popupMenu_);
		//end right click menu
		buddyList_.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				final Buddy selectedBuddy_ = (Buddy)buddyList_.getSelectedValue();
				if (e.getKeyChar() == '\n') {
	            	if(selectedBuddy_ != null) {
	            		SwingUtilities.invokeLater(new Runnable() {
	            			public void run() {
	            				newIMWindow(selectedBuddy_,alwaysNewWindow_);
	            			}
	            		});
	            	}
	            }
	         }
	    });
		buddyList_.addMouseListener(new MouseAdapter() {
			private void showIfPopupTrigger(MouseEvent mouseEvent) {
				if(popupMenu_.isPopupTrigger(mouseEvent)) {
					JList buddies = (JList) mouseEvent.getSource();
					int index = buddies.locationToIndex(mouseEvent.getPoint());
					//setselected buddylist index based on mouse position
					if (index >= 0) {
						buddyList_.setSelectedIndex(index);
						popupMenu_.show(buddies, mouseEvent.getX(), mouseEvent.getY());
					}
				}
			}
			
			public void mousePressed(MouseEvent mouseEvent) {
				showIfPopupTrigger(mouseEvent);
			}
			
			public void mouseReleased(MouseEvent mouseEvent) {
				showIfPopupTrigger(mouseEvent);
			}
			
			
			public void mouseClicked(MouseEvent mouseEvent) {
				JList Buddies = (JList) mouseEvent.getSource();
				if (mouseEvent.getClickCount() == 1)
					newWindow_.setEnabled(true);
					
				if (mouseEvent.getClickCount() == 2) {
					int index = Buddies.locationToIndex(mouseEvent.getPoint());
					if (index >= 0) {
						final Buddy selectedBuddy_ = (Buddy) Buddies.getModel().getElementAt(index);
						//need to start new chat window

						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								newIMWindow(selectedBuddy_,alwaysNewWindow_);
							}
						});
					}
				}
			}
		});
		
		buddyListScroll_ = new JScrollPane(buddyList_);
		buddyListScroll_.setViewportView(buddyList_);      
		buddyListScroll_.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		buddyListScroll_.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.add(buddyListScroll_);
	}	
	
	private void createMenu() {
		//create the menu bar
		menuBar_ = new JMenuBar();
		this.setJMenuBar(menuBar_);

		//first menu\\
		//Whisper (w)
			//New IM (n)
			//New IM in New Window
			//Send SMS text
			//Set Status
			//Social Site Manager
			//Download Manager
			//Web Browser Lite
			//Quit (q)
		
		whisperMenu_ = new JMenu(WHISPER_);
		whisperMenu_.setMnemonic(KeyEvent.VK_W);

		newIm_ = new JMenuItem(NEWIM_);
		newIm_.setMnemonic(KeyEvent.VK_N);
		newIm_.addActionListener(this);
		whisperMenu_.add(newIm_);
		
		newWindow_ = new JMenuItem(NEWUSERIM_);
		newWindow_.addActionListener(this);
		whisperMenu_.add(newWindow_);
		newWindow_.setEnabled(false);
		
		newSMSText_ = new JMenuItem(SMSTEXT_);
		newSMSText_.addActionListener(this);
		whisperMenu_.add(newSMSText_);
		
		whisperMenu_.add(new JSeparator()); 
		
		setStatus_ = new JCheckBoxMenuItem(SET_STATUS_);
		setStatus_.addActionListener(this);
		whisperMenu_.add(setStatus_);
		
		whisperMenu_.add(new JSeparator()); 
		
		socialSites_ = new JMenuItem(SOCIAL_SITE_MANAGER_);
		socialSites_.addActionListener(this);
		whisperMenu_.add(socialSites_);
		
		downloadManager_ = new JMenuItem(DOWNLOAD_MANAGER_);
		downloadManager_.addActionListener(this);
		whisperMenu_.add(downloadManager_);
		
		emailManager_ = new JMenuItem(EMAIL_MANAGER_);
		emailManager_.addActionListener(this);
		whisperMenu_.add(emailManager_);
		
		whisperMenu_.add(new JSeparator()); 
		
		browserLite_ = new JMenuItem(BROWSER_LITE_);
		browserLite_.addActionListener(this);
		whisperMenu_.add(browserLite_);
		
		whisperMenu_.add(new JSeparator()); 
		
		quit_ = new JMenuItem(QUIT_);
		quit_.setMnemonic(KeyEvent.VK_Q);
		quit_.addActionListener(this);
		whisperMenu_.add(quit_);
		 
		//Second Menu\\
		//Options (o)
			//Preferences (p)
			//Sound
			//Account
			//Profile
			//Plugins
			//Lock

		optionsMenu_ = new JMenu(OPTIONS_);
		optionsMenu_.setMnemonic(KeyEvent.VK_O);
		
		preferences_ = new JMenuItem(PREFERENCES_);
		preferences_.setMnemonic(KeyEvent.VK_P);
		preferences_.addActionListener(this);
		optionsMenu_.add(preferences_);
		
		soundsEnabled_ = Preferences.getInstance().getSoundsEnabled();
		sound_ = new JCheckBoxMenuItem(SOUND_);
		sound_.setSelected(soundsEnabled_);
		sound_.setActionCommand(SOUND_);
		sound_.addActionListener(this);
		optionsMenu_.add(sound_);
		
		optionsMenu_.add(new JSeparator()); 
		
		accounts_ = new JMenuItem(ACCOUNTS_);
		accounts_.setMnemonic(KeyEvent.VK_A);
		accounts_.addActionListener(this);
		optionsMenu_.add(accounts_);
		
		profile_ = new JMenuItem(PROFILE_);
		profile_.addActionListener(this);
		optionsMenu_.add(profile_);
		
		optionsMenu_.add(new JSeparator()); 

		plugins_ = new JMenuItem(PLUGINS_);
		plugins_.addActionListener(this);
		optionsMenu_.add(plugins_);
		
		lockWhisper_ = new JMenuItem(LOCK_WHISPER_);
		lockWhisper_.addActionListener(this);
		optionsMenu_.add(lockWhisper_);
		
		menuBar_.add(whisperMenu_);
		menuBar_.add(optionsMenu_);
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
			SwingUtilities.invokeLater(new Runnable(){

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

					SwingUtilities.invokeLater(new Runnable(){

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
		if(myTaskTimer_ != null) {
			this.setTitle(BUDDY_LIST_);

			myTaskTimer_.cancel();
			myTaskTimer_ = new IdleTT();
			myTimer_.schedule(myTaskTimer_, timeToIdle);
		}
		else {
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

	/**
	 * This is a helper method to generate a new IM window.
	 * @param selectedBuddy_
	 * @param client
	 */
	public WhisperIM newIMWindow(final Buddy selectedBuddy_, boolean newWindow) {

		WhisperIM window;
		WhisperIMPanel panel;

		if (openBuddies_.isEmpty() || newWindow){
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
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				PreferencesWindow prefs = PreferencesWindow.getInstance();
				prefs.setPreferencesCategory(PreferencesWindow.ABOUT_);
			}
		});
	}
	
	//Simple method to open preference page
	public void openPreferencesWindow()
	{
		SwingUtilities.invokeLater(new Runnable() {
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
		SwingUtilities.invokeLater(new Runnable() {
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
		Preferences.getInstance().savePrefs();
	}

	public void recieveMessage(final Message message) throws BadLocationException, IllegalArgumentException, InterruptedException, EngineException, EngineStateError, AudioException{
		//First we need to check to see if it contains
		//information that is intended for the client
		//to interpret (key file, etc.)
		
		String clearMessage = clearHTMLTags(message.getMessage().toString(), -1);
		
		if (message.getMessage().startsWith("<whisperim")){
			//This is an instruction to the client, we need to parse it
			//and then we'll swallow it or print out a message to the
			//user.
			
			// Read text.
			if(enableSpeech_ == true){
			
			/*
			// Create synthesizer for English Lang.
			Synthesizer synthesizer = Central.createSynthesizer( new SynthesizerModeDesc(Locale.ENGLISH));
			
			// Set up for speaking.						
			synthesizer.allocate();
			synthesizer.resume();
			
			
			//Speak the message.
			synthesizer.speakPlainText(clearMessage,null);
			
			// Wait until the speaker is done.
			synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
			
			// Deallocate.
			synthesizer.deallocate();
			}
			*/
			
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
								newIMWindow(new Buddy(message.getFrom(), message.getTo(), message.getProtocol()),alwaysNewWindow_);
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

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						//needs to go to an buddy object version
						newIMWindow(new Buddy(message.getFrom(), message.getTo(), message.getProtocol()), alwaysNewWindow_);
						try {
							openBuddies_.get(message.getFrom().toLowerCase().replace(" ", "")).getTab(message.getFrom().toLowerCase().replace(" ", "")).receiveMsg(message);
							
							
							
						} catch (BadLocationException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						}
						}});

			}else{
				openBuddies_.get(message.getFrom().toLowerCase().replace(" ", "")).getTab(message.getFrom()).receiveMsg(message);
				
			}
			}
		//Listener to update SystemTray if IM is received
		for(ClientListener l:clientListeners_) {
			l.messageRec(this, message, message.getFrom());}
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
	
	public void signOffSN(String protocol, String handle){
		blm_.removeBuddies(protocol, handle);
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
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					new WhisperNewIMWindow(manager_, wc);
				}
			});
		}
		
		//Set / Unset Status
		if (actionCommand.equals(setStatus_.getActionCommand())){			
			if(setStatus_.isSelected())	{
				String username = JOptionPane.showInputDialog(null, "Please enter your Last.FM username:");
				if(username != "") {
					if(setStatus_.isSelected())	{
						//set away status
						manager_.setStatusMessage(new LastFM(username).getLastSong());
					} 
					else
						manager_.setStatusMessage("");
				}
			}
			else {
				manager_.setStatusMessage("");
				setStatus_.setSelected(false);
			}
		}
		
		//Open the plugins window
		if (actionCommand.equals(plugins_.getActionCommand())){

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					new WhisperPluginManagerWindow(pluginLoader_, plm_);
				}
			});
		}

		//Preferences
		if (actionCommand.equals(preferences_.getActionCommand())) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					PreferencesWindow prefs = PreferencesWindow.getInstance();
					prefs.setPreferencesCategory(PreferencesWindow.GENERAL_);
				}
			});
		}
		
		//Account Management Window
		if (actionCommand.equals(accounts_.getActionCommand())){
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					PreferencesWindow prefs = PreferencesWindow.getInstance();
					prefs.setPreferencesCategory(PreferencesWindow.ACCOUNTS_);
				}
			});
		}

		//Sound
		if (actionCommand.equals(sound_.getActionCommand())) {
			toggleSound();
		}
		
		if (actionCommand.equals(newWindow_.getActionCommand())){
			
			if (buddyList_.getSelectedIndex() == -1){
				newWindow_.setEnabled(false);
			}
			else
			{
				final Buddy selectedBuddy_ = (Buddy) buddyList_.getModel().getElementAt(buddyList_.getSelectedIndex());
				//need to start new chat window
			

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						newIMWindow(selectedBuddy_,true);
					}
				});	
			}	
		}
		
		if(actionCommand.equals(popupNewIM_.getActionCommand())) {
			//JList Buddies = (JList) e.getSource();
			//assumes that the popup came from the buddy list
			int index = buddyList_.getSelectedIndex();
			if(index >= 0 ) {
				final Buddy selectedBuddy_ = (Buddy) buddyList_.getModel().getElementAt(index);
				//need to start new chat window

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						newIMWindow(selectedBuddy_,alwaysNewWindow_);
					}
				});
			}
		}
		
		if(actionCommand.equals(popupNewWindow_.getActionCommand())) {
			//JList Buddies = (JList) e.getSource();
			//assumes that the popup came from the buddy list
			int index = buddyList_.getSelectedIndex();
			if(index >= 0) {
				final Buddy selectedBuddy_ = (Buddy) buddyList_.getModel().getElementAt(index);
				//need to start new chat window

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						newIMWindow(selectedBuddy_,true);
					}
				});
			}
		}
		
		// This launches the social site manager.
		if(actionCommand.equals(socialSites_.getActionCommand())){
			SocialSiteManager ssm = new SocialSiteManager();
			ssm.setVisible(true);
		}
		
		// This launches the download manager.
		if(actionCommand.equals(downloadManager_.getActionCommand())){
			DownloadManager dlm = new DownloadManager();
			dlm.setVisible(true);
		}
		
		// Launches the email manager.
		if(actionCommand.equals(emailManager_.getActionCommand())){
			ExpressWhisperMailManager ewmm = new ExpressWhisperMailManager();
			ewmm.setVisible(true);
		}
		
		// This launches the download manager.
		if(actionCommand.equals(browserLite_.getActionCommand())){
			BrowserLite bl = new BrowserLite();
			bl.setVisible(true);
		}
		
		//Profile
		if(actionCommand.equals(profile_.getActionCommand())){
			@SuppressWarnings("unused")
			ProfileEditor editor = new ProfileEditor();
		}
		
		//SMS Text
		if(actionCommand.equals(newSMSText_.getActionCommand())){
			sms = new SMSText();
			try {
				sms.sendText();
			} catch (AddressException e1) {
				e1.printStackTrace();
			} catch (MessagingException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
		if(actionCommand.equals(lockWhisper_.getActionCommand())){
			Runnable lockRunnable = new Runnable() {
				public void run() {
					Locking lock = new Locking();
				}
			};
			Thread lockThread = new Thread(lockRunnable);
			lockThread.start();
			
		}
	}

	public void toggleSound(){
		Preferences.getInstance().setSoundsEnabled(!Preferences.getInstance().getSoundsEnabled());
	}
	
	public JCheckBoxMenuItem getSound_() {
		return sound_;
	}

	public void setSound_(JCheckBoxMenuItem sound) {
		sound_ = sound;
	}
	
	public void changeClientSound(){
		JCheckBoxMenuItem temp = this.getSound_();
		temp.setState(!temp.getState());
		this.setSound_(temp);
		this.toggleSound();
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
		return tray_;	
	}

	public boolean getWindowPref(){
		return alwaysNewWindow_;
	}
	
	public static ConnectionManager getConnectionManager() {
		return manager_;
	}
	
	private void packAndRepaint() {
		SwingUtilities.updateComponentTreeUI(this);
		this.repaint();
	}

	public void setEditor(ProfileEditor editor) {
		this.editor = editor;
	}

	public ProfileEditor getEditor() {
		return editor;
	}
	
	public static String clearHTMLTags(String strHTML, int doWork){
		 
        Pattern pattern = null;
        String htmlChars;
        String strTagLess = null; 
        strTagLess = strHTML; 

        if(doWork == -1)
        {
              htmlChars = "<[^>]*>";
              pattern = Pattern.compile(htmlChars);
              strTagLess = pattern.matcher(strTagLess).replaceAll(""); 
        }

        return strTagLess;
	}
}