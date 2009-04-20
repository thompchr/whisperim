 /**************************************************************************
 * Copyright 2009 John Dlugokecki                                         *
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
package org.whisperim.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;

import org.whisperim.SocialSiteDump.SocialSiteManager;
import org.whisperim.client.Buddy;
import org.whisperim.client.Logger;
import org.whisperim.prefs.PrefListener;
import org.whisperim.prefs.Preferences;

/**
 * The majority of this class is auto-generated code using Java's swing UI
 * components. The primary method of interest is the WhisperIM constructor that
 * is called from WhisperClient.java
 * 
 * @author Kirk Banks, Chris Thompson, John Dlugokecki, Cory Plastek, Nick Krieble
 */
public class WhisperIM extends JFrame implements ActionListener, WindowListener{
    //Encryptor for the chat session
  
    

     /* 
     * WhisperBot GUI setup for enable/disable.
     */
     
	private static final long serialVersionUID = 1991002584679884126L;

	private static final ImageIcon whisperIcon_ = Preferences.getInstance().getWhisperIconSmall(); 
   
    private WhisperPane mainPain_;
	private JMenu fileMenu_, conversationMenu_;
	private JMenuItem exit_, about_, closeTab_, newTab_;
	private static final String FILE_ = "File";
	private static final String CLOSE_TAB_ = "Close tab";
	private static final String EXIT_ = "Exit";
	private static final String CONVERSATION_ = "Conversation";
	private static final String LOGGING_ ="Logging";
	private static final String WHISPER_BOT_ = "Whisper Bot";
	private static final String START_WHITEBOARD_ = "Start Whiteboard";
	private static final String START_SOCIAL_SITE_DUMP_ = "Start Social Site Notification Service";
	private static final String SET_LOCATION_ = "Set location...";
	private static final String NEW_TAB_ = "New tab";
	private static final String SMSTEXT_ = "Send SMS Text";
	

	private JCheckBoxMenuItem logging_;
	private JCheckBoxMenuItem socialSites_;
	private JCheckBoxMenuItem setLocation_;
	private JMenuItem sendSMSText_;
	
    private WhisperClient myParent_;
    private PrivateKey myKey_;
    private Logger log_;
    private boolean doLogging_;
    private HashMap<String, WhisperIMPanel> tabHash_ = new HashMap<String,WhisperIMPanel>();
    private SMSText sms;

    

    private ImageIcon defaultIcon_ = new ImageIcon("..\\images\\default.ico");
	private ImageIcon aimIcon_ = new ImageIcon("..\\images\\aim_icon_small.png");

    /** Creates new form WhisperIM */
    public WhisperIM(WhisperClient parent, PrivateKey myKey) {
    	
    	//Create frame, its menu, and the TabbedPane 
    	super("Whisper IM Conversation");
    	
    	//need to set look and feel here so it will propagate down to tabbed pane
    	//set themes
		try {
			if(Preferences.getInstance().getLookAndFeel().equalsIgnoreCase(Preferences.SYSTEM_)) {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				//UIManager.setLookAndFeel(Preferences.SYSTEM_); 
			}
			else {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				//UIManager.setLookAndFeel(Preferences.METAL_); 
			}
		}
		catch (ClassNotFoundException e) {
			//e.printStackTrace();
		} catch (InstantiationException e) {
			//e.printStackTrace();
		} catch (IllegalAccessException e) {
			//e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			//e.printStackTrace();
		}
		
		Preferences.getInstance().getListeners().add(new PrefListener() {
			private boolean locked = false;
			@Override
			public void prefChanged(String name, Object o) {
				if(Preferences.THEME_.equals(name) && !locked){
					locked = true;
					try {
						if(Preferences.getInstance().getLookAndFeel().equals(Preferences.METAL_))
							UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
						else
							UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					}
					catch (Exception e) {
						//do nothing
					}
					packAndRepaint();
					locked = false;
				}
			}
		});
    	
    	
    	
    	createMenu();    
    	
    	mainPain_ = new WhisperPane();
    	myParent_ = parent;
        myKey_ = myKey;
    	mainPain_.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
    	mainPain_.addFocusListener(new FocusListener(){

			@Override
			public void focusGained(FocusEvent e) {
				MenuSelectionManager.defaultManager().clearSelectedPath();
				
			}

			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}
    		
    		
    	});
    	
        add(mainPain_, BorderLayout.CENTER);


        //We should add a small version of their buddy icon into the "null" value here

    	this.setIconImage(whisperIcon_.getImage());
    	this.addWindowListener(this);
    	
        setMinimumSize(new Dimension(550, 310));
        pack();
    }

    
    /*
     * Future use for hotkeys in the IM Window, coming soon!
    public void keyTyped(KeyEvent e) {
    	
        if (e.getKeyCode() == '') {
            mainPain_.remove(mainPain_.getSelectedIndex());
        }
        
        //else if (e.getKeyChar() == '')
    }
    
    */


    public void actionPerformed(ActionEvent e) {
    	
    	if (e.getSource() == exit_) {
    		this.dispose();
    	}
    	else if (e.getSource() == logging_) {
    		doLogging_ = logging_.getState();
    		Preferences.getInstance().setLoggingEnabled(doLogging_);
    		if (log_ == null) {
    			log_ = new Logger();
    		}
    	}
    	else if (e.getSource() == closeTab_) {
    		tabHash_.remove(mainPain_.getSelectedComponent().getName().toLowerCase().replace(" ", ""));
    		myParent_.onWindowClose(mainPain_.getSelectedComponent().getName().toLowerCase().replace(" ", ""));
    		mainPain_.remove(mainPain_.getSelectedComponent());
    		if (mainPain_.getTabCount() == 0) {
    			this.dispose();
    		}
    	}
    	else if (e.getSource() == newTab_){
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					new WhisperNewIMWindow(myParent_.getConnectionManager(), myParent_);
				}
			});
    	}
    	else if (e.getSource() == setLocation_){		
			if(setLocation_.isSelected())	{
				String zipcode = JOptionPane.showInputDialog(null, "Please enter your zip code: ");
				if(zipcode != "") {
					if(setLocation_.isSelected())	{
						broadcastLocation(zipcode);
					} 
				}
			}
			else {
				setLocation_.setSelected(false);
			}
    	}
    	else if (e.getSource() == sendSMSText_){
			sms = new SMSText();
			try {
				sms.sendText();
			} catch (AddressException e1) {
				e1.printStackTrace();
			} catch (MessagingException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
    	}
    }
	
	//Menu creation is separate from UI layout
	private void createMenu(){
		final JMenuBar mb = new JMenuBar();
		this.setJMenuBar(mb);
		//first menu
		//file (f)
			//new tab (hidden)
			//close tab
		    //Send SMS Text
			//exit (x)
		fileMenu_ = new JMenu(FILE_);
		fileMenu_.setMnemonic(KeyEvent.VK_F);
		
		
		newTab_ = new JMenuItem(NEW_TAB_);
		newTab_.addActionListener(this);
		newTab_.setAccelerator(KeyStroke.getKeyStroke("control T"));
		fileMenu_.add(newTab_);
		
		closeTab_ = new JMenuItem(CLOSE_TAB_);
		closeTab_.addActionListener(this);
		closeTab_.setAccelerator(KeyStroke.getKeyStroke("control W"));
		fileMenu_.add(closeTab_);
		
		sendSMSText_ = new JMenuItem(SMSTEXT_);
		sendSMSText_.addActionListener(this);
		sendSMSText_.setAccelerator(KeyStroke.getKeyStroke("control S"));
		fileMenu_.add(sendSMSText_);
		
		exit_ = new JMenuItem(EXIT_);
		exit_.setMnemonic(KeyEvent.VK_X);
		exit_.addActionListener(this);
		fileMenu_.add(exit_);
		
		//second menu
		//conversation (c)
			//logging (l)
			//whisper bot
			//start whiteboard
		conversationMenu_ = new JMenu(CONVERSATION_);
		conversationMenu_.setMnemonic(KeyEvent.VK_C);
		
		logging_ = new JCheckBoxMenuItem(LOGGING_);
		logging_.setMnemonic(KeyEvent.VK_L);
		logging_.setState(Preferences.getInstance().getLoggingEnabled());
		Preferences.getInstance().getListeners().add(new PrefListener() {
			private boolean locked = false;
			@Override
			public void prefChanged(String name, Object o) {
				if(Preferences.LOGGING_.equals(name) && !locked){
					locked = true;
					if(!o.equals(logging_.getState())){
						logging_.setSelected(!logging_.isSelected());
					}
					locked = false;
				}
			}
		});
		logging_.addActionListener(this);
		conversationMenu_.add(logging_);
		
		setLocation_ = new JCheckBoxMenuItem(SET_LOCATION_);
		setLocation_.setMnemonic(KeyEvent.VK_O);
		setLocation_.addActionListener(this);
		conversationMenu_.add(setLocation_);
		
		//Future use...
		//W_ = KeyStroke.getKeyStroke(KeyEvent.VK_W, Event.CTRL_MASK);
		
		mb.add(fileMenu_);
		mb.add(conversationMenu_);
	}

    public void windowClosing(WindowEvent evt) {
    	System.out.println("Window closing");
    	//Close the open file
    	if (log_ != null)
    		log_.close();
    	for (String key : tabHash_.keySet())
    		myParent_.onWindowClose(key);
    }

    public WhisperClient getMyParent(){
    	return myParent_;
    }
    

    
    
    public void addPanel(Buddy buddy, WhisperIMPanel panel){
    	//We should add a small version of their buddy icon into the "null" value here
    	

    	
		mainPain_.addTab(buddy.getHandle(), panel);
		
		mainPain_.setTabComponentAt(mainPain_.getTabCount()-1, panel.getTabHead());

    	tabHash_.put(buddy.getHandle().toLowerCase().replace(" ", ""),panel);
    	panel.setName(buddy.getHandle());
    	
    	this.requestFocus();
    	
    	
    	mainPain_.setSelectedComponent(panel);
    	mainPain_.addChangeListener(panel);
    }
    
    public WhisperIMPanel getTab(String buddy){
    	return tabHash_.get(buddy.toLowerCase().replace(" ", ""));
    }

    public boolean isFocused(JPanel panel){
    	if (panel == mainPain_.getSelectedComponent())
    		return true;
    	else
    		return false;
    }

	


    
	public void closeTab(WhisperIMPanel panel){
		
		tabHash_.remove(panel.getName().toLowerCase().replace(" ", ""));
		myParent_.onWindowClose(panel.getName().toLowerCase().replace(" ", ""));
		mainPain_.remove(panel);
		if (mainPain_.getTabCount() == 0)
			dispose();
		
	}
	
	private void broadcastLocation(String zipcode) {
		Collection <WhisperIMPanel> p = tabHash_.values();
		for (WhisperIMPanel w : p)
		{
	        w.setText(" System Message: \n My location can be viewed \n <a href='http://maps.google.com/maps?f=q&source=s_q&hl=en&geocode=&q="+zipcode+"'>here</a> \n");
	        w.sendMsg();
		}
	}
	
	protected void paintContentBorderRightEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h)
	{
		// Do nothing
	}

	protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h)
	{
		// Do nothing
	}

	protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h)
	{
		// Do nothing
	}


	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	private void packAndRepaint() {
		SwingUtilities.updateComponentTreeUI(this);
		this.repaint();
	}
	
	
	//This modifies the panel on a drag...
	class PanelManager{
		
		WhisperIMPanel panel_;
		
		JTextPane talk_;
		
		public PanelManager(WhisperIMPanel panel){
			
			System.out.println("Built a panel manager");
			panel_ = panel;
			talk_ = panel_.getTextArea();
			
		}
		
		public void addFiles(String[] files){
			
			int i = 0;
			while (files[i] != null)
				panel_.addFiles(files[i++]);
			
		}
		
		public void addIcon(Icon icon){
			//Do it!
			
		}
		
		public void addIcon(){
			
			System.out.println("Adding icon to the window");
			talk_.setText("File has been added for transfer");
			//talk_.add(UIManager.getIcon("FileView.hardDriveIcon"));
			
		}
		
		
	}
	
	
	
	class TabTransferHandler extends TransferHandler{
		
		private DataFlavor flavaFlav_;
		
		private PanelManager manager_;
		
		private JTextArea clipBoard_;
		
		private Position start = null, end = null;
		
		public TabTransferHandler(PanelManager m){
		
			System.out.println("Built a transfer handler");
			flavaFlav_ = DataFlavor.javaFileListFlavor;
			manager_ = m;
		}
		
		//Import data from file
		public void importData(Transferable t){
			
			//Need to import the file in to a buffer,  
			
			if (hasFileFlavor(t.getTransferDataFlavors())) {
		        String str = null;
		        java.util.List files;
				
		        
		        try {
					files = (java.util.List) t
					    .getTransferData(flavaFlav_);
				 
		        System.out.println("Reading the data...");
		        String[] fileBuffer = new String[files.size()];
		        for (int i = 0; i < files.size(); i++) {
		          File file = (File) files.get(i);
		          

		          BufferedReader in = null;

		          try {
		            in = new BufferedReader(new FileReader(file));

		            while ((str = in.readLine()) != null) {
		            	fileBuffer[i] += str;
		            }
		          } catch (IOException ioe) {
		            System.out
		                .println("importData: Unable to read from file "
		                    + file.toString());
		          } finally {
		            if (in != null) {
		              try {
		                in.close();
		              } catch (IOException ioe) {
		                System.out
		                    .println("importData: Unable to close file "
		                        + file.toString());
		              }      		          	
		            }
		          }
		          //Replace with attempt to grab file's icon
		          manager_.addIcon();
		          
		        }
			}
		        catch (UnsupportedFlavorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//Add the file buffer(s) to the calling panel
			
			
		}
		
		protected Transferable createTransferable(JComponent c) {
		    clipBoard_ = (JTextArea) c;
		    int begin = clipBoard_.getSelectionStart();
		    int endd = clipBoard_.getSelectionEnd();
		    Document doc = clipBoard_.getDocument();
		    if (start == end) {
		      return null;
		    }
		    try {
		      start = doc.createPosition(begin);
		      end = doc.createPosition(endd);
		    } catch (BadLocationException e) {
		      System.out
		          .println("Can't create position - unable to remove text from source.");
		    }
		    
		    String data = clipBoard_.getSelectedText();
		    return new StringSelection(data);
		  }
		
		protected void exportDone(JComponent c, Transferable data, int action){
			
	          JTextComponent tc = (JTextComponent) c;
	          try {
				tc.getDocument().remove(start.getOffset(),
				      end.getOffset() - start.getOffset());
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			clipBoard_ = null;
			
		}
		
		public int getSourceActions(JComponent c) {
			    return COPY_OR_MOVE;
			  }
		
		public boolean canImport(JComponent c, DataFlavor[] flavors) {
			    if (hasFileFlavor(flavors)) {
			      return true;
			    }
			    System.out.println("Couldnt import the file");
			    return false;
		}
				
		private boolean hasFileFlavor(DataFlavor[] flavors) {
		    for (int i = 0; i < flavors.length; i++) {
		      if (flavaFlav_.equals(flavors[i])) {
		        return true;
		      }
		      else
		      {
		    	  System.out.println(flavors[i]);
		      }
		    }
		    System.out.println("Didnt have file flavor requested for drag");
		    
		    return false;
		  }
	}
	}

	

