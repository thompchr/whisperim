/**************************************************************************
 * Copyright 2009 Nick Krieble                                             *
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
package org.whisperim.SocialSiteDump;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.aetrion.flickr.FlickrException;

public class SocialSiteManager extends JFrame {

	// Table Listing notifications.
	private JTable socialSiteTable_;

	// Button for updating social feeds
	private JButton updateButton_;

	public SocialSiteManager() {
		// Set window title.
		setTitle("Social Site Manager");

		// Set window size;
		setSize(640, 480);

		// Handle window closing event.
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});

		// Setup a file menu.
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		JMenuItem fileCloseMenuItem = new JMenuItem(
				"Close Social Site Manager", KeyEvent.VK_X);
		fileCloseMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionClosed();
			}
		});
		fileMenu.add(fileCloseMenuItem);
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);

		// Setup update panel.
		JPanel updatePanel = new JPanel();
		JButton updateButton = new JButton("Update (Get Notifications)");
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionUpdate();
			}
		});
		updatePanel.add(updateButton);

		// Setup Notifications Table;
		NotificationsTableModel notificationTableModel = new NotificationsTableModel();
		socialSiteTable_ = new JTable(notificationTableModel);

		// Set height for each row.
		socialSiteTable_.setRowHeight(20);

		// Setup notifications panel.
		JPanel notificationsPanel = new JPanel();
		notificationsPanel.setBorder(BorderFactory
				.createTitledBorder("Your Notifications"));
		notificationsPanel.setLayout(new BorderLayout());
		notificationsPanel.add(new JScrollPane(socialSiteTable_),
				BorderLayout.CENTER);

		// Setup Buttons Panel.
		JPanel buttonsPanel = new JPanel();
		updateButton_ = new JButton("Update (Get Notifications)");
		updateButton_.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionUpdate();
			}
		});
		updateButton_.setEnabled(false);
		buttonsPanel.add(updateButton_);
	}

	// Action performed when exit is selected or window is closed.
	private void actionClosed() {
		this.dispose();
	}

	// This is the action performed when the update button is selected.
	private void actionUpdate() 
	{
		FlickrService fs;
		try {
			fs = new FlickrService();
			fs.authorizeFlickrAccount();
			fs.getNotifications();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FlickrException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}

	}
}
