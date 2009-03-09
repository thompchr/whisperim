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
package org.whisperim.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.security.PrivateKey;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;

import org.whisperim.security.Encryptor;

/**
 * The majority of this class is auto-generated code using Java's swing UI
 * components. The primary method of interest is the WhisperIM constructor that
 * is called from WhisperClient.java
 * 
 * @author Kirk Banks, Chris Thompson, John Dlugokecki, Cory Plastek, Nick Krieble
 */
public class WhisperIM extends JFrame implements ActionListener {
    //Encryptor for the chat session
    private Encryptor encrypt;
    private boolean doEncryption_ = false;
    private String theirHandle_;
    private String myHandle_;
    private boolean doLogging_ = false;
    private boolean doWhisperBot_ = false;
    
    private Buddy buddy_;
    
    private static final String AOL_ = "aol";
    private static final String ENCRYPTION_OFF_ = "Encryption: Off";
    private static final String ENCRYPTION_ON_ = "Encryption: On";
    private static final String SEND_ = "Send";
    private static final String SEND_KEY_ = "Send Key";
    private static final String TOGGLE_ENCRYPTION_ = "Toggle Encryption";
    
    /* 
     * WhisperBot GUI setup for enable/disable.
     */
    private static final String WHISPERBOT_OFF_ = "WhisperBot: Off";
    private static final String WHISPERBOT_ON_ = "WhisperBot: On";
    private static final String TOGGLE_WHISPERBOT_ = "Toggle WhisperBot";
    
    
    private ImageIcon serviceIcon_;
    private JLabel buddyName_;
    private JScrollPane talkAreaScroll_;
    private JTextArea messageArea_;
    private JScrollPane messageAreaScroll_;
    private JButton sendBtn_;
    private JTextArea talkArea_;
    private JToggleButton toggleEncryption_;
    private JButton sendKeyBtn_;
    private JTabbedPane mainPain_;
	private JMenu fileMenu_, optionsMenu_;
	private JMenuItem exit_, about_;
	private JCheckBoxMenuItem logging_;
	private JToggleButton toggleWhisperBot_;
	
    
    private WhisperClient myParent_;
    private PrivateKey myKey_;
    private Logger log_;
    
    private HashMap<String, WhisperIMPanel> tabHash_ = new HashMap<String,WhisperIMPanel>();
    
    private ImageIcon defaultIcon_ = new ImageIcon("..\\images\\default.ico");
	private ImageIcon aimIcon_ = new ImageIcon("..\\images\\aim_icon_small.png");

    /*
     * The logging portion should be toggled by the user.  Until the additional UI
     * is implemented, the logger is created by default, and logging is universal
     */
    //Logger for this session.
   

    /** Creates new form WhisperIM */
    public WhisperIM(PrivateKey myKey) {
    	
    	//Create frame, its menu, and the TabbedPane 
    	super("Whisper IM Conversation");
    	createMenu();    	
    	mainPain_ = new JTabbedPane();

        myKey_ = myKey;
    	
    	
        add(mainPain_, BorderLayout.CENTER);

        //We should add a small version of their buddy icon into the "null" value here
        //mainPain_.addTab(buddy.getHandle(), null, panel, "Conversation with " + buddy.getHandle());
        //tabHash_.put(buddy.getHandle(), panel);

        setMinimumSize(new Dimension(470, 315));
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */

    public void actionPerformed(ActionEvent e) {
    	String actionCommand = e.getActionCommand();
    	
    	if (e.getSource() == exit_){
    		dispose();
    	}
    	
    	else if (e.getSource() == logging_){
    		doLogging_ = logging_.getState();
    		if (log_ == null)
    			log_ = new Logger(); 
    			
    	}
    }
    
	
	//Menu creation is separate from UI layout
	private void createMenu(){
		
		fileMenu_ = new JMenu("File");
		exit_ = new JMenuItem("Exit");
		exit_.setMnemonic(KeyEvent.VK_X);
		fileMenu_.add(exit_);
		optionsMenu_ = new JMenu("Options");
		optionsMenu_.setMnemonic(KeyEvent.VK_O);
		logging_ = new JCheckBoxMenuItem("Enable Logging");
		logging_.setState(false);
		optionsMenu_.add(logging_);
		exit_.addActionListener(this);
		logging_.addActionListener(this);
		
		JMenuBar mb = new JMenuBar();
		mb.add(fileMenu_);
		mb.add(optionsMenu_);
		setJMenuBar(mb);
		
	}

    private void formWindowClosing(WindowEvent evt) {
    	System.out.println("Window closing");
    	//Close the open file
    	if (log_ != null)
    		log_.close();
    	myParent_.onWindowClose(theirHandle_);
    }

    public WhisperClient getMyParent(){
    	return myParent_;
    }
    
    public String getMyHandle()
    {
    	return myHandle_;
    }
    
    
    public void addPanel(Buddy buddy, WhisperIMPanel panel){
    	mainPain_.addTab(buddy.getHandle(), null, panel, "Conversation with " + buddy.getHandle());
    	tabHash_.put(buddy.getHandle(),panel);
    	
    }
    
    public WhisperIMPanel getTab(String buddy){
    	return tabHash_.get(buddy);
    }
    
    
}
