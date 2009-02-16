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
import java.awt.List;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.WindowConstants;

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
public class WhisperClient extends JFrame {

	private String[] buddyString_;
	private int numOfBuddies_ = 0;
	private String myHandle_;
	private Timer myTimer_;
	private IdleTT myTaskTimer_;    
	private ConnectionManager manager_;


	private HashMap<String, WhisperIM> windows = new HashMap<String, WhisperIM>();
	
	/** Creates new form WhisperClient */
	public WhisperClient(String handle, ConnectionManager manager) {
		initComponents();
		manager_ = manager;
		manager_.setClient(this);
		myHandle_ = handle;
		this.setTitle("Whisper");
		resetTimer(5000);   


		//this.setAwayMessage("Away!!!", true);

		this.setAwayMessage("Away put your weapons, I mean you no harm", true);
	}

	private void resetTimer(int timeToIdle)
	{
		if(myTaskTimer_ != null)
		{
			this.setTitle("Whisper");

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
		public void run() {
			setTitle("Whisper [Idle]");
		}
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	private void initComponents() {

		jPopupMenu1 = new JPopupMenu();
		jPopupMenu2 = new JPopupMenu();
		jPopupMenu3 = new JPopupMenu();
		popupMenu1 = new PopupMenu();
		menuBar1 = new MenuBar();
		menu1 = new Menu();
		menu2 = new Menu();
		menuBar2 = new MenuBar();
		menu3 = new Menu();
		menu4 = new Menu();
		menuBar3 = new MenuBar();
		menu5 = new Menu();
		menu6 = new Menu();
		jMenuBar2 = new JMenuBar();
		jMenu3 = new JMenu();
		jMenu4 = new JMenu();
		Buddies = new List();
		jMenuBar1 = new JMenuBar();
		jMenu1 = new JMenu();
		jMenuItem2 = new JMenuItem();
		jMenu2 = new JMenu();
		jMenuItem1 = new JMenuItem();

		popupMenu1.setLabel("popupMenu1");

		menu1.setLabel("File");
		menuBar1.add(menu1);

		menu2.setLabel("Edit");
		menuBar1.add(menu2);

		menu3.setLabel("File");
		menuBar2.add(menu3);

		menu4.setLabel("Edit");
		menuBar2.add(menu4);

		menu5.setLabel("File");
		menuBar3.add(menu5);

		menu6.setLabel("Edit");
		menuBar3.add(menu6);

		jMenu3.setText("File");
		jMenuBar2.add(jMenu3);

		jMenu4.setText("Edit");
		jMenuBar2.add(jMenu4);

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				formWindowClosing(evt);
			}
		});

		Buddies.addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent evt) {
				BuddiesComponentShown(evt);
			}
		});
		Buddies.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				BuddiesActionPerformed(evt);
			}
		});

		jMenu1.setText("File");

		jMenuItem2.setText("Quit");
		jMenu1.add(jMenuItem2);

		jMenuBar1.add(jMenu1);

		jMenu2.setText("Preferences");

		jMenuItem1.setText("Encryption");
		jMenu2.add(jMenuItem1);

		jMenuBar1.add(jMenu2);

		setJMenuBar(jMenuBar1);

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.LEADING)
				.add(Buddies, GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.LEADING)
				.add(GroupLayout.TRAILING, layout.createSequentialGroup()
						.addContainerGap()
						.add(Buddies, GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE))
		);

		pack();
	}

	private void BuddiesActionPerformed(final ActionEvent evt) {
		//Create IM window
		final WhisperClient client = this;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				WhisperIM window = new WhisperIM(evt.getActionCommand(), myHandle_, client, manager_.getPrivateKey());
				window.setVisible(true);
				windows.put(evt.getActionCommand(), window);
			}
		});
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
				Encryptor.writeKeyToFile(keyText, message.getFrom() + ":" +message.getProtocol());


				try {
					X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(Base64.decode(keyText.getBytes()));
					KeyFactory rsaKeyFac = null;
					rsaKeyFac = KeyFactory.getInstance("RSA");
					final PublicKey recKey = rsaKeyFac.generatePublic(pubKeySpec);
					if (windows.get(message.getFrom()) == null){
						//There isn't currently a window associated with that buddy
						final WhisperClient client = this;
						java.awt.EventQueue.invokeLater(new Runnable() {
							public void run() {
								WhisperIM window = new WhisperIM(message.getFrom(), myHandle_, client, manager_.getPrivateKey());
								window.setVisible(true);
								windows.put(message.getFrom(), window);
								window.enableEncryption(recKey);
							}
						});
					}else{
						windows.get(message.getFrom().toLowerCase().replace(" ", "")).enableEncryption(recKey);
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
			if (windows.get(message.getFrom()) == null){
				//There isn't currently a window associated with that buddy
				final WhisperClient client = this;
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						WhisperIM window = new WhisperIM(message.getFrom(), myHandle_, client, manager_.getPrivateKey());
						window.setVisible(true);
						windows.put(message.getFrom(), window);
						window.receiveMsg(message);
					}
				});

			}else{
				windows.get(message.getFrom().toLowerCase().replace(" ", "")).receiveMsg(message);
			}

		}

	}

	public void sendMessage (Message message){
		manager_.sendMessage(message);

		resetTimer(5000);
	}


	public void updateBuddyList(ArrayList<Buddy> newBuddies)
	{
		numOfBuddies_ = newBuddies.size();

		for(int i=0; i<newBuddies.size(); i++)
		{
			Buddies.add(newBuddies.get(i).getHandle());
		}

	}

	public void onWindowClose(String handle){
		windows.remove(handle);
	}


	private List Buddies;
	private JMenu jMenu1;
	private JMenu jMenu2;
	private JMenu jMenu3;
	private JMenu jMenu4;
	private JMenuBar jMenuBar1;
	private JMenuBar jMenuBar2;
	private JMenuItem jMenuItem1;
	private JMenuItem jMenuItem2;
	private JPopupMenu jPopupMenu1;
	private JPopupMenu jPopupMenu2;
	private JPopupMenu jPopupMenu3;
	private Menu menu1;
	private Menu menu2;
	private Menu menu3;
	private Menu menu4;
	private Menu menu5;
	private Menu menu6;
	private MenuBar menuBar1;
	private MenuBar menuBar2;
	private MenuBar menuBar3;
	private PopupMenu popupMenu1;

	private void setAwayMessage(String message, boolean away){
		manager_.setAwayMessage(message, away);
	}

}
