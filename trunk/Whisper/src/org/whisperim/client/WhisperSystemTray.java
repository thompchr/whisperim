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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

//Things to do: User can change status, Mute all sounds, open account manager, Hide system Tray, *Blink with new IM*

public class WhisperSystemTray {
	
	public static void startSystemTray(WhisperClient client){
		
		final WhisperClient client_ = client;
		final TrayIcon trayIcon;
					
		if (SystemTray.isSupported()) {
		    SystemTray tray = SystemTray.getSystemTray();
		    Image image = Toolkit.getDefaultToolkit().getImage("..\\images\\tray.jpg");
	  
		    MouseListener mouseListener = new MouseListener() {      
		        public void mouseClicked(MouseEvent e) {}	
		        public void mouseEntered(MouseEvent e) {}
		        public void mouseExited(MouseEvent e) {}
		        public void mousePressed(MouseEvent e) {}
		        public void mouseReleased(MouseEvent e) {}
		    };		    
		    
		    //Exit Listener for menu
		    ActionListener exitListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		            System.out.println("System Tray - Exiting");
		            System.exit(0);
		        }
		    };
		    
		    //New IM Listener for menu
		    ActionListener newImListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		            System.out.println("System Tray - Creating new IM");
		            client_.createNewIMWindow();
		        }
		    };
		    
		    //About Listener for menu
		    ActionListener aboutListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		            System.out.println("System Tray - Opening About");
		            client_.openAboutPage();
		        }
		    };
		    
		    //Plugins Listener for menu
		    ActionListener pluginsListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		            System.out.println("System Tray - Opening Plugins");
		        	client_.openPluginsPage();
		        }
		    };

		    //Preferences Listener for menu
		    ActionListener prefListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		            System.out.println("System Tray - Opening Preferences");
		        	client_.openPrefPage();
		        }
		    };	
		
		    //Minimize Whisper Client for menu
		    ActionListener statusListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		        	//Code to Change Status ****************
		        	//Waiting for Chris to commit the status change options so I can implement them into the tray
		        }
		    };
		    
		    //Sound Listener for menu
		    ActionListener soundListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		        	//Code to toggle off sound***********************
		        }
		    };	
		    
		    //Minimize Whisper Client for menu
		    ActionListener accountListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		        	//Waiting for Chris to commit code so I know what to call to open Account Manager     	
		        }
		    };
		    
		    //Minimize Whisper Client for menu
		    ActionListener minimizeListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		        	client_.setVisible(false);
		        }
		    };
		    
		    //Open Whisper Client from menu
		    ActionListener openWhisperListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		        	client_.setVisible(true);
		        }
		    };
		    
		    //Hide System Tray Icon
		    ActionListener hideTrayListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		        	//SystemTray.getSystemTray().remove(trayIcon);
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
		    
		    //Change status on menu
		    MenuItem statusItem = new MenuItem("Change Status");
		    statusItem.addActionListener(statusListener);
		    
		    //Open account manager from menu
		    MenuItem accountItem = new MenuItem("Account Manager");
		    accountItem.addActionListener(accountListener);
		    
		    //Whisper menu
		    Menu whisperMenu = new Menu("Options");
		    
		    //Preferences menu
		    Menu prefMenu = new Menu("Preferences");
		    
		    //Encryption option on Preference menu
		    MenuItem prefItem = new MenuItem("Preferences");
		    prefItem.addActionListener(prefListener);
		    
		    //Plugins option on Whisper menu
		    MenuItem pluginsItem = new MenuItem("Plugins");
		    pluginsItem.addActionListener(pluginsListener);
		    
		    //Toggle sound off and on
		    CheckboxMenuItem soundItem = new CheckboxMenuItem("Sound");
		    soundItem.addActionListener(soundListener);
		    
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
	        popup.add(whisperMenu);
	        whisperMenu.add(minimizeItem);
	        whisperMenu.add(newImItem);
	        whisperMenu.add(statusItem);
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
				public void messageRec(Message m, String recIM) {
					Image recIMImage = Toolkit.getDefaultToolkit().getImage("..\\images\\newIM.jpg");
					trayIcon.setImage(recIMImage);
					trayIcon.setImageAutoSize(true);
					String temp = "New IM from " + recIM;
					trayIcon.displayMessage("New IM from ",temp, TrayIcon.MessageType.INFO);
				}

				@Override
				public void statusChange() {
					//Waiting for implementation of Status Change to implement this method
					
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
}