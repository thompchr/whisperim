package org.whisperim.EmailManager;

	import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JDialog;
import javax.swing.ListSelectionModel;

	public class ExpressWhisperMailManager extends JFrame {
		
		// EWM mem var.
		ExpressWhisperMail ewm_ = new ExpressWhisperMail(); 
		
		// Table Listing notifications.
		private JTable emailTable_;
		private emailTableModel tableModel;

		// Button for updating email;
		private JButton updateButton_;
		private JButton composeButton_;

		// Currently selected email.
		private ExpressWhisperMail selectedEmail_;
		
		public ExpressWhisperMailManager() {
			// Set window title.
			setTitle("Express Whisper Mail");

			// Set window size;
			setSize(640, 480);

			// Handle window closing event.
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					actionClosed();
				}
			});

			// Setup a file menu.
			JMenuBar menuBar = new JMenuBar();
			JMenu fileMenu = new JMenu("File");
			fileMenu.setMnemonic(KeyEvent.VK_F);
			JMenuItem fileCloseMenuItem = new JMenuItem(
					"Close Window", KeyEvent.VK_X);
			fileCloseMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					actionClosed();
				}
			});
			fileMenu.add(fileCloseMenuItem);
			menuBar.add(fileMenu);
			setJMenuBar(menuBar);

			// Set up compose panel(compose, forward, reply.)
	        JPanel composePanel = new JPanel();
	        JButton composeButton = new JButton("Compose");
	        composeButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                actionCompose();
	            }
	        });
	        JButton forwardButton = new JButton("Forward");
	        forwardButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                actionForward();
	            }
	        });
	        JButton replyButton = new JButton("Reply");
	        replyButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                actionReply();
	            }
	        });
	        // Add buttons.
	        composePanel.add(composeButton);
	        composePanel.add(forwardButton);
	        composePanel.add(replyButton);
	        
	     // Set up buttons panel.
	        JPanel updatePanel = new JPanel();
	        updateButton_ = new JButton("Update");
	        updateButton_.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                actionUpdate();
	            }
	        });
			updatePanel.add(updateButton_);
			
	     // Setup email table.
	        tableModel = new emailTableModel();
	        emailTable_ = new JTable(tableModel);
	       
	        // Allow only one row at a time to be selected.
	        emailTable_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        
	        // Set table's row height large enough to fit JProgressBar.
	        emailTable_.setRowHeight(10);
	        
	        // Setup email panel.
	        JPanel emailsPanel = new JPanel();
	        emailsPanel.setBorder(
	                BorderFactory.createTitledBorder("Express Whisper Mail"));
	        emailsPanel.setLayout(new BorderLayout());
	        emailsPanel.add(new JScrollPane(emailTable_),
	                BorderLayout.CENTER);
	        

	        // Add panels to display.
	        getContentPane().setLayout(new BorderLayout());
	        getContentPane().add(composePanel, BorderLayout.NORTH);
	        getContentPane().add(emailsPanel, BorderLayout.CENTER);
	        getContentPane().add(updatePanel, BorderLayout.SOUTH);
	    }
	    
		// Action performed when exit is selected or window is closed.
		private void actionClosed() {
			this.dispose();
		}

		// This is the action performed when the update button is selected.
		private void actionUpdate() 
		{
			ewm_.updateEmail();
		}
		
		private void actionCompose() {
	
			ComposeEmail ce = new ComposeEmail();
			ce.composeWindow();
		}

		private void actionForward() {
			// Spring popup
			
		}

		private void actionReply() {
			// spring popup.
			
		}
	}
