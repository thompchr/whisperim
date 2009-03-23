 /**************************************************************************
 * Copyright 2009                                                          *
 * Kirk Banks   				                                           *
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

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

//Things to do: User can change status, Mute all sounds from tray

public class WhisperSystemTray implements Runnable{
	
	private static final String NEW_IM_IMAGE_LOCATION_ = "..\\images\\newIM.jpg";
	private static final String TRAY_ICON_LOCATION_ = "..\\images\\WhisperIMLogo-Small.png";



	private static TrayIcon trayIcon = null;
	
	 
	
	private final Image recIMImage = Toolkit.getDefaultToolkit().getImage(NEW_IM_IMAGE_LOCATION_);
    private final Image trayIMImage = Toolkit.getDefaultToolkit().getImage(TRAY_ICON_LOCATION_);
	
	public static void startSystemTray(WhisperClient client){
		
		final WhisperClient client_= client;
					
		if (SystemTray.isSupported()) {
		    SystemTray tray = SystemTray.getSystemTray();
		    Image image = Toolkit.getDefaultToolkit().getImage(TRAY_ICON_LOCATION_);
	  
		    MouseListener mouseListener = new MouseListener() {      
		        public void mouseClicked(MouseEvent e) {}	
		        public void mouseEntered(MouseEvent e) {}
		        public void mouseExited(MouseEvent e) {}
		        public void mousePressed(MouseEvent e) {}
		        public void mouseReleased(MouseEvent e) {}
		    };		    
		    
		    //Exit Listener for tray
		    ActionListener exitListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		            System.out.println("System Tray - Exiting");
		            System.exit(0);
		        }
		    };
		    
		    //New IM Listener for tray
		    ActionListener newImListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		            System.out.println("System Tray - Creating new IM");
		            client_.createNewIMWindow();
		        }
		    };
		    
		    //About Listener for tray
		    ActionListener aboutListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		            System.out.println("System Tray - Opening About");
		            client_.openAboutPage();
		        }
		    };
		    
		    //Plugins Listener for tray
		    ActionListener pluginsListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		            System.out.println("System Tray - Opening Plugins");
		        	client_.openPluginsPage();
		        }
		    };

		    //Preferences Listener for tray
		    ActionListener prefListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		            System.out.println("System Tray - Opening Preferences");
		        	client_.openPreferencesWindow();
		        }
		    };	
		    
		    //Available Status
		    ActionListener statusAvailableListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		        	//Default Status
		        	//Code to Change to Available Status
		        }
		    };
		    
		    //Away Status
		    ActionListener statusAwayListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		        	//Code to Change to Away Status
		        }
		    };
		    
		    //Invisible Status
		    ActionListener statusInvisibleListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		        	
		        }
		    };
		    
		    //Mute all sounds from tray
		    ItemListener soundListener = new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent arg0) {
					System.out.println("clicked");
		        	client_.toggleSound();
				}
		    };	
		    
		    //Open Account Listener from tray
		    ActionListener accountListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		        	client_.openAccountsPage();    	
		        }
		    };
		    
		    //Minimize Whisper Client from tray
		    ActionListener minimizeListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		        	client_.setVisible(false);
		        }
		    };
		    
		    //Open Whisper Client from tray
		    ActionListener openWhisperListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		        	client_.setVisible(true);
		        }
		    };
		    
		    //Hide System Tray Icon
		    ActionListener hideTrayListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		        	closeTray();
		        }
		    };
		    
		    //New IM option on Whisper menu
		    MenuItem newImItem = new MenuItem("New Message");
		    newImItem.addActionListener(newImListener);
		    
		    //Exit option on menu
		    MenuItem exitItem = new MenuItem("Exit");
		    exitItem.addActionListener(exitListener);
		    
		    //About option on menu
		    MenuItem aboutItem = new MenuItem("About");
		    aboutItem.addActionListener(aboutListener);
		         
		    //Open account manager from menu
		    MenuItem accountItem = new MenuItem("Account Manager");
		    accountItem.addActionListener(accountListener);
		    
		    //Whisper menu
		    Menu whisperMenu = new Menu("Options");
		    
		    //Preferences menu
		    Menu prefMenu = new Menu("Preferences");
		    
		    //About option on menu
		    Menu statusMenu = new Menu("Status");
		    
		    //Available option on Status menu
		    CheckboxMenuItem statusAvailableItem = new CheckboxMenuItem("Available");
		    statusAvailableItem.addActionListener(statusAvailableListener);
		    statusAvailableItem.setState(true);
		    
		    //Invisible option on Status menu
		    CheckboxMenuItem statusInvisibleItem = new CheckboxMenuItem("Invisible");
		    statusInvisibleItem.addActionListener(statusInvisibleListener);
		    statusInvisibleItem.setState(false);
		    	
		    //Away option on Status menu
		    CheckboxMenuItem statusAwayItem = new CheckboxMenuItem("Away");
		    statusAwayItem.addActionListener(statusAwayListener);
		    statusInvisibleItem.setState(false);
		    
		    //Encryption option on Preference menu
		    MenuItem prefItem = new MenuItem("Preferences");
		    prefItem.addActionListener(prefListener);
		    
		    //Plugins option on Whisper menu
		    MenuItem pluginsItem = new MenuItem("Plugins");
		    pluginsItem.addActionListener(pluginsListener);
		    
		    //Toggle sound off and on
		    final CheckboxMenuItem soundItem = new CheckboxMenuItem("Sound");
		    soundItem.addItemListener(soundListener);
		    soundItem.setState(client_.getSound_());
		    
		    //Minimize Whisper Client Option
		    MenuItem minimizeItem = new MenuItem("Hide Whisper");
		    minimizeItem.addActionListener(minimizeListener);
		    
		    //Open Whisper Client
		    MenuItem openWhisperItem = new MenuItem("Whisper");
		    openWhisperItem.addActionListener(openWhisperListener);
		    
		    //Hide System Tray
		    MenuItem hideTrayItem = new MenuItem("Hide System Tray");
		    hideTrayItem.addActionListener(hideTrayListener);
		    
		    PopupMenu popup = new PopupMenu();
			trayIcon = new TrayIcon(image, "Whisper", popup);     
		    trayIcon.setImageAutoSize(true);
		    trayIcon.addMouseListener(mouseListener);
		
		    //Main system tray menu     
	        popup.add(aboutItem);
	        popup.addSeparator();
	        popup.add(openWhisperItem);
	        popup.add(statusMenu);
	        statusMenu.add(statusAvailableItem);	//default status 
	        statusMenu.add(statusInvisibleItem);
	        statusMenu.add(statusAwayItem);	        
	        popup.add(whisperMenu);
	        whisperMenu.add(minimizeItem);
	        whisperMenu.add(newImItem);
	        whisperMenu.add(pluginsItem);
	        whisperMenu.add(accountItem);
	        popup.add(prefMenu);
	        prefMenu.add(prefItem);
	        prefMenu.add(soundItem);
	        prefMenu.add(hideTrayItem);
	        popup.addSeparator();
	        popup.add(exitItem);

		    //Handling of listeners located in WhisperClient
		    client_.getClientListeners().add(new ClientListener() {
			
				@Override
				public void messageRec(WhisperClient client, Message m, final String recIM) {    	
	  		    	String temp = "New IM from " + recIM;
	  		    	trayIcon.displayMessage("New IM",temp, TrayIcon.MessageType.INFO);

	  		      Runnable runnable = new WhisperSystemTray();
	  		      Thread thread = new Thread(runnable);
	  		      thread.start();
	  		      thread.yield();
				}
				
				@Override
				public void statusChange() {
					//Waiting for implementation of Status Change to implement this method
				}

				@Override
				public void sentMessage(WhisperClient client) {
					// TODO Auto-generated method stub
					
				}

				
				public void soundChange(WhisperClient client) {
					if(client_.getSound_()){
						soundItem.setState(true);
					}
					else {
						soundItem.setState(false);
					}
				}
			});
	        
		    try {
		        tray.add(trayIcon);
		    } catch (AWTException e) {
		        System.err.println("TrayIcon could not be added.");
		    }
		} 
		else {
		    //  System Tray not supported by OS
		}
	}

	//Run method is used in a thread to blink tray icon on new message received
	@Override
	public void run() {
	    	for(int i=0; i<5; i++){
  		    	trayIcon.setImage(recIMImage);
  		    	try{
  		    			Thread.currentThread().sleep(800);
		    	  }catch(Exception e){}
		    	trayIcon.setImage(trayIMImage);
	  		    try{
  		    			Thread.currentThread().sleep(800);
		    	  }catch(Exception e){}
	    	}	
	} 
	
    public static void closeTray(){
    	SystemTray.getSystemTray().remove(trayIcon);
    }
}