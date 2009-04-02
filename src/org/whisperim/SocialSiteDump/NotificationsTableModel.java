package org.whisperim.SocialSiteDump;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

// This class manages the notification table's data.
public class NotificationsTableModel extends AbstractTableModel {

	// Names the columns. Can add social site names later.
	private static final String[] columnNames = { "Notifications" };

	// Table's list of notifications.
	private ArrayList notificationList = new ArrayList();

	// Add a new dump of notifications to the table.
	public void addNotification(FlickrService flickr) {
		notificationList.add(flickr);

		// row insertion notification to table.
		fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
	}

	// Get a set of notifications for the specified row.
	public FlickrService getNotifications(int row) {
		return (FlickrService) notificationList.get(row);
	}

	// Get table's column count.
	public int getColumnCount() {
		return columnNames.length;
	}

	// Get a column's name
	public String getColumnNames(int col) {
		return columnNames[col];
	}

	// Get a row's count
	public int getRowCount() {
		return notificationList.size();
	}

	public Object getValueAt(int row, int col) {
		FlickrService flickrService = (FlickrService) notificationList.get(row);
		switch (col) {
		case 0:
			//return flickrService.getNotifications();
		}
		return "";
	}
}

