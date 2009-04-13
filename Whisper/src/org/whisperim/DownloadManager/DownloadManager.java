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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

// The DownloadManager.
public class DownloadManager extends JFrame
        implements Observer {
    
    // Add download text field.
    private JTextField addTextField_;
    
    // Download table's data model.
    private DownloadsTableModel tableModel_;
    
    // Table listing downloads.
    private JTable table_;
    
    // These are the buttons for managing the selected download.
    private JButton pauseButton_, resumeButton_;
    private JButton cancelButton_, clearButton_;
    
    // Currently selected download.
    private Downloader selectedDownload_;
    
    // Flag for whether or not table selection is being cleared.
    private boolean clearing_;
    
    // Constructor for Download Manager.
    public DownloadManager() {
        // Set application title.
        setTitle("Download Manager");
        
        // Set window size.
        setSize(640, 480);
        
        // Handle window closing events.
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                actionExit();
            }
        });
        
        // Set up file menu.
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        // Encrypt File.
        JMenuItem fileEncryptMenuItem = new JMenuItem("Encrypt File",
                KeyEvent.VK_X);
        fileEncryptMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                encryptFile();
            }
        });
        
        // Decrypt File.
        JMenuItem fileDecryptMenuItem = new JMenuItem("Decrypt File",
                KeyEvent.VK_X);
        fileDecryptMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                decryptFile();
            }
        });
        
        JMenuItem fileExitMenuItem = new JMenuItem("Exit",
                KeyEvent.VK_X);
        fileExitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionExit();
            }
        });
        fileMenu.add(fileEncryptMenuItem);
        fileMenu.add(fileDecryptMenuItem);
        fileMenu.add(fileExitMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
        
        // Set up add panel.
        JPanel addPanel = new JPanel();
        addTextField_ = new JTextField(30);
        addPanel.add(addTextField_);
        JButton addButton = new JButton("Add Download");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionAdd();
            }
        });
        addPanel.add(addButton);
        
        // Set up Downloads table.
        tableModel_ = new DownloadsTableModel();
        table_ = new JTable(tableModel_);
        table_.getSelectionModel().addListSelectionListener(new
                ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                tableSelectionChanged();
            }
        });
        // Allow only one row at a time to be selected.
        table_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Set up ProgressBar as renderer for progress column.
        ProgressRenderer renderer = new ProgressRenderer(0, 100);
        renderer.setStringPainted(true); // show progress text
        table_.setDefaultRenderer(JProgressBar.class, renderer);
        
        // Set table's row height large enough to fit JProgressBar.
        table_.setRowHeight(
                (int) renderer.getPreferredSize().getHeight());
        
        // Set up downloads panel.
        JPanel downloadsPanel = new JPanel();
        downloadsPanel.setBorder(
                BorderFactory.createTitledBorder("Downloads"));
        downloadsPanel.setLayout(new BorderLayout());
        downloadsPanel.add(new JScrollPane(table_),
                BorderLayout.CENTER);
        
        // Set up buttons panel.
        JPanel buttonsPanel = new JPanel();
        pauseButton_ = new JButton("Pause");
        pauseButton_.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionPause();
            }
        });
        pauseButton_.setEnabled(false);
        buttonsPanel.add(pauseButton_);
        resumeButton_ = new JButton("Resume");
        resumeButton_.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionResume();
            }
        });
        resumeButton_.setEnabled(false);
        buttonsPanel.add(resumeButton_);
        cancelButton_ = new JButton("Cancel");
        cancelButton_.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionCancel();
            }
        });
        cancelButton_.setEnabled(false);
        buttonsPanel.add(cancelButton_);
        clearButton_ = new JButton("Clear");
        clearButton_.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionClear();
            }
        });
        clearButton_.setEnabled(false);
        buttonsPanel.add(clearButton_);
        
        // Add panels to display.
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(addPanel, BorderLayout.NORTH);
        getContentPane().add(downloadsPanel, BorderLayout.CENTER);
        getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
    }
    
    
    
    // Encrypt File.
    private void encryptFile(){
    	
    }
    
    // Decrypt File.
    private void decryptFile(){
    	
    }
   
    // Exit this program.
    private void actionExit() {
        System.exit(0);
    }
    
    // Add a new download.
    private void actionAdd() {
        URL verifiedUrl = verifyUrl(addTextField_.getText());
        if (verifiedUrl != null) {
            tableModel_.addDownload(new Downloader(verifiedUrl));
            addTextField_.setText(""); // reset add text field
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid Download URL", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Verify download URL.
    private URL verifyUrl(String url) {
        // Only allow HTTP URLs.
        if (!url.toLowerCase().startsWith("http://"))
            return null;
        
        // Verify format of URL.
        URL verifiedUrl = null;
        try {
            verifiedUrl = new URL(url);
        } catch (Exception e) {
            return null;
        }
        
        // Make sure URL specifies a file.
        if (verifiedUrl.getFile().length() < 2)
            return null;
        
        return verifiedUrl;
    }
    
    // Called when table row selection changes.
    private void tableSelectionChanged() {
    /* Unregister from receiving notifications
       from the last selected download. */
        if (selectedDownload_ != null)
            selectedDownload_.deleteObserver(DownloadManager.this);
        
    /* If not in the middle of clearing a download,
       set the selected download and register to
       receive notifications from it. */
        if (!clearing_) {
            selectedDownload_ =
                    tableModel_.getDownload(table_.getSelectedRow());
            selectedDownload_.addObserver(DownloadManager.this);
            updateButtons();
        }
    }
    
    // Pause the selected download.
    private void actionPause() {
        selectedDownload_.pause();
        updateButtons();
    }
    
    // Resume the selected download.
    private void actionResume() {
        selectedDownload_.resume();
        updateButtons();
    }
    
    // Cancel the selected download.
    private void actionCancel() {
        selectedDownload_.cancel();
        updateButtons();
    }
    
    // Clear the selected download.
    private void actionClear() {
        clearing_ = true;
        tableModel_.clearDownload(table_.getSelectedRow());
        clearing_ = false;
        selectedDownload_ = null;
        updateButtons();
    }
    
  /* Update each button's state based off of the
     currently selected download's status. */
    private void updateButtons() {
        if (selectedDownload_ != null) {
            int status = selectedDownload_.getStatus();
            switch (status) {
                case Downloader.DOWNLOADING:
                    pauseButton_.setEnabled(true);
                    resumeButton_.setEnabled(false);
                    cancelButton_.setEnabled(true);
                    clearButton_.setEnabled(false);
                    break;
                case Downloader.PAUSED:
                    pauseButton_.setEnabled(false);
                    resumeButton_.setEnabled(true);
                    cancelButton_.setEnabled(true);
                    clearButton_.setEnabled(false);
                    break;
                case Downloader.ERROR:
                    pauseButton_.setEnabled(false);
                    resumeButton_.setEnabled(true);
                    cancelButton_.setEnabled(false);
                    clearButton_.setEnabled(true);
                    break;
                default: // COMPLETE or CANCELLED
                    pauseButton_.setEnabled(false);
                    resumeButton_.setEnabled(false);
                    cancelButton_.setEnabled(false);
                    clearButton_.setEnabled(true);
            }
        } else {
            // No download is selected in table.
            pauseButton_.setEnabled(false);
            resumeButton_.setEnabled(false);
            cancelButton_.setEnabled(false);
            clearButton_.setEnabled(false);
        }
    }
    
  /* Update is called when a Download notifies its
     observers of any changes. */
    public void update(Observable o, Object arg) {
        // Update buttons if the selected download has changed.
        if (selectedDownload_ != null && selectedDownload_.equals(o))
            updateButtons();
    }
    
}  

