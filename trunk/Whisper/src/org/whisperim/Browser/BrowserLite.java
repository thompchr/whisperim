package org.whisperim.Browser;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.*;

// Whisper Web Browser Lite.
public class BrowserLite extends JFrame implements HyperlinkListener {
	
	// Forward and back buttons to navigate web pages.
	private JButton backButton_;
	private JButton forwardButton_;

	// Page location text field.
	private JTextField urlField_;

	// Editor pane for displaying pages.
	private JEditorPane displayEditorPane_;

	// Browser's list of pages that have been visited.
	private ArrayList pageHistory_ = new ArrayList();

	// Constructor for Whisper Web Browser Lite.
	public BrowserLite() {
		// Set application title.
		super("Whisper Browser Lite");

		// Set window size.
		setSize(640, 480);

		// Handle closing events.
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				actionExit();
			}
		});

		// Setup file menu.
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		JMenuItem fileExitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
		fileExitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionExit();
			}
		});
		fileMenu.add(fileExitMenuItem);
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);

		// Setup button panel.
		// Setup back button.
		JPanel buttonPanel = new JPanel();
		backButton_ = new JButton("<< Back");
		backButton_.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionBack();
			}
		});
		backButton_.setEnabled(false);
		buttonPanel.add(backButton_);
		
		// Setup forward button.
		forwardButton_ = new JButton("Forward >>");
		forwardButton_.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionForward();
			}
		});
		forwardButton_.setEnabled(false);
		buttonPanel.add(forwardButton_);
		urlField_ = new JTextField(35);
		urlField_.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					actionGo();
				}
			}
		});
		
		// Go to web page button.
		buttonPanel.add(urlField_);
		JButton goButton = new JButton("GO");
		goButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionGo();
			}
		});
		buttonPanel.add(goButton);

		// Setup page display.
		displayEditorPane_ = new JEditorPane();
		displayEditorPane_.setContentType("text/html");
		displayEditorPane_.setEditable(false);
		displayEditorPane_.addHyperlinkListener(this);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buttonPanel, BorderLayout.NORTH);
		getContentPane().add(new JScrollPane(displayEditorPane_),
				BorderLayout.CENTER);
	}

	// Exit this program.
	private void actionExit() {
		System.exit(0);
	}

	// Go back to the page viewed before the current page.
	private void actionBack() {
		URL currentUrl = displayEditorPane_.getPage();
		int pageIndex = pageHistory_.indexOf(currentUrl.toString());
		try {
			showPage(new URL((String) pageHistory_.get(pageIndex - 1)), false);
		} catch (Exception e) {
		}
	}

	// Go forward to the page viewed after the current page.
	private void actionForward() {
		URL currentUrl = displayEditorPane_.getPage();
		int pageIndex = pageHistory_.indexOf(currentUrl.toString());
		try {
			showPage(new URL((String) pageHistory_.get(pageIndex + 1)), false);
		} catch (Exception e) {
		}
	}

	// Load and show the page specified in the location text field.
	private void actionGo() {
		URL verifiedUrl = verifyUrl(urlField_.getText());
		if (verifiedUrl != null) {
			showPage(verifiedUrl, true);
		} else {
			showError("Invalid URL");
		}
	}

	// Show dialog box with error message.
	private void showError(String errorMessage) {
		JOptionPane.showMessageDialog(this, errorMessage, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	// Verify URL format.
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

		return verifiedUrl;
	}

	// Show the page and add it to the history.
	private void showPage(URL pageUrl, boolean addToList) {
		// Show hour glass cursor while crawling is under way.
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		try {
			// Get URL of page currently being displayed.
			URL currentUrl = displayEditorPane_.getPage();

			// Load and display specified page.
			displayEditorPane_.setPage(pageUrl);

			// Get URL of new page being displayed.
			URL newUrl = displayEditorPane_.getPage();

			// Add page to list if specified.
			if (addToList) {
				int listSize = pageHistory_.size();
				if (listSize > 0) {
					int pageIndex = pageHistory_.indexOf(currentUrl.toString());
					if (pageIndex < listSize - 1) {
						for (int i = listSize - 1; i > pageIndex; i--) {
							pageHistory_.remove(i);
						}
					}
				}
				pageHistory_.add(newUrl.toString());
			}

			// Update location text field with URL of current page.
			urlField_.setText(newUrl.toString());

			// Update buttons based on the page being displayed.
			updateButtons();
		} catch (Exception e) {
			// Show error messsage.
			showError("Unable to load page");
		} finally {
			// Return to default cursor.
			setCursor(Cursor.getDefaultCursor());
		}
	}

	// Update back and forward buttons based on the page being displayed
	private void updateButtons() {
		if (pageHistory_.size() < 2) {
			backButton_.setEnabled(false);
			forwardButton_.setEnabled(false);
		} else {
			URL currentUrl = displayEditorPane_.getPage();
			int pageIndex = pageHistory_.indexOf(currentUrl.toString());
			backButton_.setEnabled(pageIndex > 0);
			forwardButton_.setEnabled(pageIndex < (pageHistory_.size() - 1));
		}
	}

	// Handle hyperlink's being clicked.
	public void hyperlinkUpdate(HyperlinkEvent event) {
		HyperlinkEvent.EventType eventType = event.getEventType();
		if (eventType == HyperlinkEvent.EventType.ACTIVATED) {
			if (event instanceof HTMLFrameHyperlinkEvent) {
				HTMLFrameHyperlinkEvent linkEvent = (HTMLFrameHyperlinkEvent) event;
				HTMLDocument document = (HTMLDocument) displayEditorPane_
						.getDocument();
				document.processHTMLFrameHyperlinkEvent(linkEvent);
			} else {
				showPage(event.getURL(), true);
			}
		}
	}
}