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

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.table.AbstractTableModel;

import org.whisperim.SocialSiteDump.FlickrService;

public class emailTableModel extends AbstractTableModel {

	// Names the columns. Can add social site names later.
	private static final String[] columnNames = {"From", "Subject"};

	// These are the classes for each column's values.
	private static final Class[] columnClasses = { String.class, String.class };

	ExpressWhisperMail mail;
	// Table's list of email.
	private ArrayList emailList_ = new ArrayList();

	// Add a new dump of notifications to the table.
	public void addEmail() {

		try {
			System.arraycopy(mail.checkNewMessages(), 0, emailList_, 0, mail
					.checkNewMessages().length);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// row insertion notification to table.
		fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
	}

	// Get a set of notifications for the specified row.
	public ExpressWhisperMail getEmail(int row) {
		return (ExpressWhisperMail) emailList_.get(row);
	}

	// Get table's column count.
	public int getColumnCount() {
		return columnNames.length;
	}

	// Get a column's name
	public String getColumnNames(int col) {
		return columnNames[col];
	}

	// Get a column's class.
	public Class getColumnClass(int col) {
		return columnClasses[col];
	}

	// Get a row's count
	public int getRowCount() {
		return emailList_.size();
	}

	public Object getValueAt(int row, int col) {
		ExpressWhisperMail mail = (ExpressWhisperMail) emailList_.get(row);
		switch (col) {
		case 0: // From
			return mail.getFrom();
		}
		return "";
	}
}
