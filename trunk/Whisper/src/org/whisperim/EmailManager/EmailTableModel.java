package org.whisperim.EmailManager;
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
import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.swing.JFrame;
import javax.swing.table.AbstractTableModel;

import org.whisperim.prefs.Preferences;

public class EmailTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 529609970416251721L;

	private String[] columnNames_ = { "Date", "From", "Subject", "Message" };
	
	Object[][] displayMsg_ = new Object[20][4];
	
	// This method is needed to copy the array of messages into the multidimensional array.
	public Object[][] copyMessageToObject(Object[][] object, Message[] msg) {
		for (int i = 0; i <= msg.length; i++) {
			for (int j = 0; j <= 3; j++) {
				if (j == 0) {
					try {
						object[i][j] = "Date goes here";//(String) msg[i].getSentDate();
						msg[i].getSentDate();
					} catch (MessagingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (j == 1) {
					try {
						object[i][j] = "From";//msg[i].getFrom();
						msg[i].getFrom();
					} catch (MessagingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (j == 2) {
					try {
						object[i][j] = (String) msg[i].getSubject();
					} catch (MessagingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (j == 3) {
					try {
						object[i][j] = (String) msg[i].getContent();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MessagingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return object;
	}

	public int getColumnCount() {
		return columnNames_.length;
	}

	public int getRowCount() {
		return displayMsg_.length;
	}

	public String getColumnName(int col) {
		return columnNames_[col];
	}

	public Object getValueAt(int row, int col) {
		return displayMsg_[row][col];
	}

	@SuppressWarnings("unchecked")
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	// Set value at particular cell in table.
	public void setValueAt(Object value, int row, int col) {

		displayMsg_[row][col] = value;
		fireTableCellUpdated(row, col);
	}

	// Create the GUI and display it.
	public void createAndShowGUI() {
		
		Preferences prefs = Preferences.getInstance();
		
		// Populate object for GUI.
		//GetGmail gmail = new GetGmail("nkrieble@gmail.com", "pop.gmail.com", "password here", 995);
		GetGmail gmail = new GetGmail((String)prefs.getUsername(),(String)prefs.getPOP3() ,(String)prefs.getPassword(), Integer.parseInt(prefs.getPopPort()));
		try {
			gmail.connect();
			gmail.openFolder("INBOX");
			copyMessageToObject(displayMsg_, gmail.fetchMessages());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Create and set up the window.
		JFrame frame = new JFrame("Express Whisper Mail");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Create/ Initialize the content pane.
		TableFilter newContentPane = new TableFilter();
		newContentPane.setOpaque(true);
		frame.setContentPane(newContentPane);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

}
