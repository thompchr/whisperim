package org.whisperim.SocialSiteDump;

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
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import org.xml.sax.SAXException;

import com.aetrion.flickr.FlickrException;

// This class manages the notification table's data.
public class NotificationsTableModel extends AbstractTableModel {

	// Names the columns. Can add social site names later.
	private static final String[] columnNames = { "Notifications" };

	// Table's list of notifications.
	private ArrayList notificationList_ = new ArrayList();

	private static final Class[] columnClasses = { String.class };

	// Add a new dump of notifications to the table.
	public void addNotification(FlickrService flickr) {

		try {
			notificationList_ = flickr.getNotifications();
		} catch (FlickrException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// row insertion notification to table.
		fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
	}

	// Get a set of notifications for the specified row.
	public FlickrService getNotifications(int row) {
		return (FlickrService) notificationList_.get(row);
	}

	// Get table's column count.
	public int getColumnCount() {
		return columnNames.length;
	}

	// Get a column's class.
	public Class getColumnClass(int col) {
		return columnClasses[col];
	}

	// Get a column's name
	public String getColumnNames(int col) {
		return columnNames[col];
	}

	// Get a row's count
	public int getRowCount() {
		return notificationList_.size();
	}

	public Object getValueAt(int row, int col) {
		FlickrService flickrService = (FlickrService) notificationList_
				.get(row);
		switch (col) {
		case 0:
			// return flickrService.getNotifications();
		}
		return "";
	}
}
