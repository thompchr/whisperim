 /**************************************************************************
 * Copyright 2009
 * Kirk Banks   				                                       *
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

public class WhisperSystemTray {

	public static void startSystemTray(){
		
		final TrayIcon trayIcon;
			
		if (SystemTray.isSupported()) {
		    SystemTray tray = SystemTray.getSystemTray();
		    Image image = Toolkit.getDefaultToolkit().getImage("tray.jpg");
		    /*BufferedImage image = null;
		    try {
		        image = ImageIO.read(new File("tray.jpg"));
		    } catch (IOException e) {
		    	System.out.println("Unable to read in image for system tray");
		    }*/
		  
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
		            System.out.println("Exiting");
		            System.exit(0);
		        }
		    };
		    
		    //New IM Listener for menu
		    ActionListener newImListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		            System.out.println("Creating new IM");
		            //Create new IM window
		            //**Need to add correct call to start a blank IM**
		        }
		    };
		    
		    //About Listener for menu
		    ActionListener aboutListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		            System.out.println("Opening About");
		            //**Create about text file to be opened**
		        }
		    };
		    
		    //Whisper Listener for menu
		    ActionListener pluginsListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		        	//Open plugins menu
		        }
		    };
		    
		    //Preferences Listener for menu
		    ActionListener encrypListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		        	//toggle encryption
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
		    
		    //Whisper menu
		    Menu whisperMenu = new Menu("Whisper");
		    
		    //Preferences menu
		    Menu prefMenu = new Menu("Preferences");
		    
		    //Encryption option on Preference menu
		    CheckboxMenuItem encrypItem = new CheckboxMenuItem("Encryption");
		    encrypItem.addActionListener(encrypListener);
		    
		    //Plugins option on Whisper menu
		    MenuItem pluginsItem = new MenuItem("Plugins");
		    pluginsItem.addActionListener(pluginsListener);
		    
		    PopupMenu popup = new PopupMenu();
			trayIcon = new TrayIcon(image, "Whisper", popup);     
		    trayIcon.setImageAutoSize(true);
		    trayIcon.addMouseListener(mouseListener);
		
		    //Main system tray menu      
	        popup.add(aboutItem);
	        popup.addSeparator();
	        popup.add(whisperMenu);
	        whisperMenu.add(newImItem);
	        whisperMenu.add(pluginsItem);
	        popup.add(prefMenu);
	        prefMenu.add(encrypItem);
	        popup.addSeparator();
	        popup.add(exitItem);

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