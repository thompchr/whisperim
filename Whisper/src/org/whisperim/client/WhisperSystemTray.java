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

import org.whisperim.prefs.PrefListener;
import org.whisperim.prefs.Preferences;

public class WhisperSystemTray implements Runnable,ActionListener,ItemListener{
	
	private static final String NEW_IM_IMAGE_LOCATION_ = "..\\images\\newIM.jpg";
	private static final String TRAY_ICON_LOCATION_ = "..\\images\\WhisperIMLogo-Small.png";
	private static final String EXIT_ = "Exit";
	private static final String ABOUT_ = "About";
	private static final String WHISPER_ = "Whisper";
	private static final String AVAILABLE_ = "Available";
	private static final String INVISIBLE_ = "Invisible";
	private static final String AWAY_ = "Away";
	private static final String MINIMIZEWHISPER_ = "Minimize Whisper";
	private static final String NEWIM_ = "New IM";
	private static final String PLUGINS_ = "Plugins";
	private static final String ACCOUNTMANAGER_ = "Account Manager";
	private static final String PREFERENCES_ = "Preferences";
	private static final String SOUND_ = "Sound";
	private static final String HIDETRAY_ = "Hide System Tray";
	private static final String STATUS_ = "Status";
	private static final String OPTIONS_ = "Options";

	private MenuItem exitItem;
    private MenuItem aboutItem;
    private MenuItem openWhisperItem;
    private Menu statusMenu;
    private CheckboxMenuItem statusAvailableItem;
    private CheckboxMenuItem statusInvisibleItem;
    private CheckboxMenuItem statusAwayItem;	        
    private Menu whisperMenu;
    private MenuItem minimizeWhisperItem;
    private MenuItem newImItem;
    private MenuItem pluginsItem;
    private MenuItem accountItem;
    private Menu prefMenu;
    private MenuItem prefItem;
    private MenuItem hideTrayItem;
    private CheckboxMenuItem soundItem;
    
    private WhisperClient client_;
    private ConnectionManager manager_;
	
	private static TrayIcon trayIcon = null;
	
	private final Image recIMImage = Toolkit.getDefaultToolkit().getImage(NEW_IM_IMAGE_LOCATION_);
    private final Image trayIMImage = Toolkit.getDefaultToolkit().getImage(TRAY_ICON_LOCATION_);
	
	public void startSystemTray(WhisperClient client, ConnectionManager manager){
		
		client_= client;
		manager_ = manager;
					
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
		    
		    //New IM option on Whisper menu
		    newImItem = new MenuItem(NEWIM_);
		    newImItem.addActionListener(this);
		    
		    //Exit option on menu
		    exitItem = new MenuItem(EXIT_);
		    exitItem.addActionListener(this);
		    
		    //About option on menu
		    aboutItem = new MenuItem(ABOUT_);
		    aboutItem.addActionListener(this);
		         
		    //Open account manager from menu
		    accountItem = new MenuItem(ACCOUNTMANAGER_);
		    accountItem.addActionListener(this);
		    
		    //Whisper menu
		    whisperMenu = new Menu(OPTIONS_);
		    
		    //Preferences menu
		    prefMenu = new Menu(PREFERENCES_);
		    
		    //About option on menu
		    statusMenu = new Menu(STATUS_);
		    
		    //Available option on Status menu
		    statusAvailableItem = new CheckboxMenuItem(AVAILABLE_);
		    statusAvailableItem.addItemListener(this);
		    statusAvailableItem.setState(true);
		    
		    //Invisible option on Status menu
		    statusInvisibleItem = new CheckboxMenuItem(INVISIBLE_);
		    statusInvisibleItem.addItemListener(this);
		    statusInvisibleItem.setState(false);
		    	
		    //Away option on Status menu
		    statusAwayItem = new CheckboxMenuItem(AWAY_);
		    statusAwayItem.addItemListener(this);
		    statusInvisibleItem.setState(false);
		    
		    //Preferences option on Preference menu
		    prefItem = new MenuItem(PREFERENCES_);
		    prefItem.addActionListener(this);
		    
		    //Plugins option on Whisper menu
		    pluginsItem = new MenuItem(PLUGINS_);
		    pluginsItem.addActionListener(this);
		    
		    //Toggle sound off and on
		    soundItem = new CheckboxMenuItem(SOUND_);
		    soundItem.setState(Preferences.getInstance().getSoundsEnabled());
		    soundItem.addItemListener(this);

		    //Minimize Whisper Client Option
		    minimizeWhisperItem = new MenuItem(MINIMIZEWHISPER_);
		    minimizeWhisperItem.addActionListener(this);
		    
		    //Open Whisper Client
		    openWhisperItem = new MenuItem(WHISPER_);
		    openWhisperItem.addActionListener(this);
		    
		    //Hide System Tray
		    hideTrayItem = new MenuItem(HIDETRAY_);
		    hideTrayItem.addActionListener(this);
		    
		    PopupMenu popup = new PopupMenu();
			trayIcon = new TrayIcon(image, WHISPER_, popup);     
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
	        whisperMenu.add(minimizeWhisperItem);
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
				public void sentMessage(WhisperClient client) {
					// TODO Auto-generated method stub
					
				}
			});
	        
		    Preferences.getInstance().getListeners().add(new PrefListener() {
				private boolean locked = false;
				@Override
				public void prefChanged(String name, Object o) {
					if(Preferences.SOUNDS_.equals(name) && !locked){
						locked = true;
						if(!o.equals(soundItem.getState())){
							trayStateChange(1);
						}
						locked = false;
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

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == exitItem){
            System.exit(0);
		}
		else if(source == newImItem){
	        client_.createNewIMWindow();
		}
		else if(source == aboutItem){
	        client_.openAboutPage();
		}
		else if(source == pluginsItem){
	    	client_.openPluginsPage();
		}
		else if(source == prefItem){
	    	client_.openPreferencesWindow();
		}
		else if(source == accountItem){
	    	client_.openAccountsPage();  
		}
		else if(source == minimizeWhisperItem){
	    	client_.setVisible(false);
		}
		else if(source == openWhisperItem){
	    	client_.setVisible(true);
		}
		else if(source == hideTrayItem){
			closeTray();
		}
	}

	public void trayStateChange(int x) {
		if(x == 1){
	    		this.soundItem.setState(!this.soundItem.getState());        	
	    	}
	    }

	
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		Object itemSource = e.getItemSelectable();
		if(itemSource == soundItem){
			client_.changeClientSound();
		}
		if(itemSource == statusAvailableItem){
			statusAvailableItem.setState(true);
			statusInvisibleItem.setState(false);
			statusAwayItem.setState(false);
			manager_.setState(ConnectionManager.AVAILABLE);
		}
		if(itemSource == statusInvisibleItem){
			statusAvailableItem.setState(false);
			statusInvisibleItem.setState(true);
			statusAwayItem.setState(false);
			manager_.setState(ConnectionManager.INVISIBLE);
		}
		if(itemSource == statusAwayItem){
			statusAvailableItem.setState(false);
			statusInvisibleItem.setState(false);
			statusAwayItem.setState(true);
			manager_.setState(ConnectionManager.AWAY);
		}
	};		
}