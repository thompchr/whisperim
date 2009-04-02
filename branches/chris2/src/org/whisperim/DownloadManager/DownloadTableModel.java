	/**************************************************************************
	 * Copyright 2009 Nick Krieble							                   *
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
package org.whisperim.DownloadManager;

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

// This class manages the download table's data.
class DownloadsTableModel extends AbstractTableModel
        implements Observer {
    
    // These are the names for the table's columns.
    private static final String[] columnNames = {"URL", "Size",
    "Progress", "Status"};
    
    // These are the classes for each column's values.
    private static final Class[] columnClasses = {String.class,
    String.class, JProgressBar.class, String.class};
    
    // The table's list of downloads.
    private ArrayList downloadList = new ArrayList();
    
    // Add a new download to the table.
    public void addDownload(Downloader download) {
        
        // Register to be notified when the download changes.
        download.addObserver(this);
        
        downloadList.add(download);
        
        // Fire table row insertion notification to table.
        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }
    
    // Get a download for the specified row.
    public Downloader getDownload(int row) {
        return (Downloader) downloadList.get(row);
    }
    
    // Remove a download from the list.
    public void clearDownload(int row) {
        downloadList.remove(row);
        
        // Fire table row deletion notification to table.
        fireTableRowsDeleted(row, row);
    }
    
    // Get table's column count.
    public int getColumnCount() {
        return columnNames.length;
    }
    
    // Get a column's name.
    public String getColumnName(int col) {
        return columnNames[col];
    }
    
    // Get a column's class.
    public Class getColumnClass(int col) {
        return columnClasses[col];
    }
    
    // Get table's row count.
    public int getRowCount() {
        return downloadList.size();
    }
    
    // Get value for a specific row and column combination.
    public Object getValueAt(int row, int col) {
        
        Downloader download = (Downloader) downloadList.get(row);
        switch (col) {
            case 0: // URL
                return download.getUrl();
            case 1: // Size
                int size = download.getSize();
                return (size == -1) ? "" : Integer.toString(size);
            case 2: // Progress
                return new Float(download.getProgress());
            case 3: // Status
                return Downloader.STATUSES[download.getStatus()];
        }
        return "";
    }
    
  /* Update is called when a Download notifies its
     observers of any changes */
    public void update(Observable o, Object arg) {
        int index = downloadList.indexOf(o);
        
        // Fire table row update notification to table.
        fireTableRowsUpdated(index, index);
    }
} 