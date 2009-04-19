package org.whisperim.EmailManager;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.table.AbstractTableModel;

import org.whisperim.SocialSiteDump.FlickrService;

public class emailTableModel extends AbstractTableModel {

	// Names the columns. Can add social site names later.
	private static final String[] columnNames = {"From", "Subject"};
	
	 // These are the classes for each column's values.
    private static final Class[] columnClasses = {String.class,
    String.class, String.class};

	// Table's list of email.
	private ArrayList emailList_ = new ArrayList();

	// Add a new dump of notifications to the table.
	public void addEmail(ExpressWhisperMail mail) {
		emailList_.add(mail);
		
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

