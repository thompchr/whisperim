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
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.jdesktop.layout.GroupLayout;
import org.whisperim.security.Encryptor;

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

	
	private int numOfBuddies_ = 0;
	private String myHandle_;
	private Timer myTimer_;
	private IdleTT myTaskTimer_;    
	private ConnectionManager manager_;
	private BuddyListModel blm_ = new BuddyListModel();
	
	private JList buddyList_;
	private JScrollPane buddyListScroll_;
	private JMenu whisperMenu_;
	private JMenu preferencesMenu_;
	
	private JMenuBar menuBar_;
	
	private JMenuItem encryption_;
	private JMenuItem quit_;
	private JMenuItem newIm_;
	private PopupMenu popupMenu1;
	
	private static final String BUDDY_LIST_ = "Buddy List";
	private static final String BUDDY_LIST_IDLE_ = "Buddy List [Idle]";
	
	//first menu\\
	private static final String WHISPER_ = "Whisper"; //menu 1 header
	private static final String NEWIM_ = "New Instant Message"; //menu 1 first item
	private static final String QUIT_ = "Quit"; //menu 1 second item
	
	//second menu\\

	private static final String PREFERENCES_ = "Preferences"; //menu 2 header
	private static final String ENCRYPTION_ = "Encryption"; //menu 2 first item

	//end menus\\
	
	private HashMap<String, WhisperIM> windows_ = new HashMap<String, WhisperIM>();
	
	/** Creates new form WhisperClient */
	public WhisperClient(String handle, ConnectionManager manager) {
		initComponents();
		manager_ = manager;
		manager_.setClient(this);
		myHandle_ = handle;
		this.setTitle(WHISPER_);
		resetTimer(5000);   


		//this.setAwayMessage("Away!!!", true);

		this.setAwayMessage("Away put your weapons, I mean you no harm", true);
	}
	


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
			//Quit
		whisperMenu_ = new JMenu();
		whisperMenu_.setText(WHISPER_);
		menuBar_.add(whisperMenu_);
		
		newIm_ = new JMenuItem();
		newIm_.setText(NEWIM_);
		newIm_.setActionCommand(NEWIM_);
		newIm_.addActionListener(this);
		whisperMenu_.add(newIm_);
		
		quit_ = new JMenuItem();
		quit_.setText(QUIT_);
		quit_.setActionCommand(QUIT_);
		quit_.addActionListener(this);
		whisperMenu_.add(quit_);
		
		
		//second menu\\
		//Preferences
			//Encryption
		preferencesMenu_ = new JMenu();
		preferencesMenu_.setText(PREFERENCES_);
		menuBar_.add(preferencesMenu_);
		
		encryption_ = new JMenuItem();
		encryption_.setText(ENCRYPTION_);
		encryption_.setActionCommand(ENCRYPTION_);
		encryption_.addActionListener(this);
		preferencesMenu_.add(encryption_);

		
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
						final WhisperClient client = WhisperClient.this;						
						EventQueue.invokeLater(new Runnable() {
							public void run() {
								WhisperIM window = new WhisperIM(selectedBuddy_, myHandle_, client, manager_.getPrivateKey());
								window.setVisible(true);
								windows_.put(selectedBuddy_.getHandle().toLowerCase().replace(" ", ""), window);
							}
						});
					}
				}
			}
		});

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.LEADING)
				.add(buddyListScroll_, 100, 182, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.LEADING)
				.add(GroupLayout.TRAILING, layout.createSequentialGroup()
						.add(buddyListScroll_, GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE))
		);

		pack();
	}

	private void BuddiesComponentShown(ComponentEvent evt) {


	}

	private void formWindowClosing(WindowEvent evt) {
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
					if (windows_.get(message.getFrom()) == null){
						//There isn't currently a window associated with that buddy
						final WhisperClient client = this;
						java.awt.EventQueue.invokeLater(new Runnable() {
							public void run() {
								//needs to go to an buddy object version
								WhisperIM window = new WhisperIM(new Buddy(message.getFrom(), myHandle_, message.getProtocol()), 
										myHandle_, client, manager_.getPrivateKey());
								window.setVisible(true);
								windows_.put(message.getFrom(), window);
								window.enableEncryption(recKey);
							}
						});
					}else{
						windows_.get(message.getFrom().toLowerCase().replace(" ", "")).enableEncryption(recKey);
					}
					
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (Base64DecodingException e) {
					e.printStackTrace();
				} catch (InvalidKeySpecException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


			}
		}else {   		
			//It would be really great if we could allow system messages to be passed around
			//That is, if the Whisper program could alert the user of things by adding messages
			//to the chat window.
			if (windows_.get(message.getFrom()) == null){
				//There isn't currently a window associated with that buddy
				final WhisperClient client = this;
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						//needs to go to an buddy object version
						WhisperIM window = new WhisperIM(new Buddy(message.getFrom(), myHandle_, message.getProtocol()), 
								myHandle_, client, manager_.getPrivateKey());
						window.setVisible(true);
						windows_.put(message.getFrom(), window);
						window.receiveMsg(message);
					}
				});

			}else{
				windows_.get(message.getFrom().toLowerCase().replace(" ", "")).receiveMsg(message);
			}

		}

	}

	public void sendMessage (Message message){
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
		windows_.remove(handle);
	}


	private void setAwayMessage(String message, boolean away){
		manager_.setAwayMessage(message, away);
	}


	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		//Quit
		if (actionCommand.equals(quit_.getActionCommand())) {
			processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
		if (actionCommand.equals(encryption_.getActionCommand())) {
			WhisperPref prefs = new WhisperPref();
			prefs.setSize(150, 150);
			prefs.setLayout(getLayout());
			prefs.setVisible(true);
		}
	}

}
