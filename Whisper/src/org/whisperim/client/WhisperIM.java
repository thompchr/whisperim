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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
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
    private static final String WHISPERBOT_OFF_ = "WhisperBot: Off.";
    private static final String WHISPERBOT_ON_ = "WhisperBot: On.";
    private static final String TOGGLE_WHISPERBOT_ = "Toggle WhisperBot.";
    
    
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
    
    private ImageIcon defaultIcon_ = new ImageIcon("..\\images\\default.ico");
	private ImageIcon aimIcon_ = new ImageIcon("..\\images\\aim_icon_small.png");

    /*
     * The logging portion should be toggled by the user.  Until the additional UI
     * is implemented, the logger is created by default, and logging is universal
     */
    //Logger for this session.
   

    /** Creates new form WhisperIM */
    public WhisperIM(Buddy buddy, String myHandle, WhisperClient myParent, PrivateKey myKey) {
    	
    	//Create frame, its menu, and the TabbedPane 
    	super("Whisper IM Conversation");
    	createMenu();    	
    	mainPain_ = new JTabbedPane();
    	
    	buddy_ = buddy;
    	
    	//Create tab for initial 'calling' buddy, then add the Pane to the Frame
    	createTab(buddy_.getHandle());
        add(mainPain_, BorderLayout.CENTER);
    	
    	PublicKey theirKey = Encryptor.getPublicKeyForBuddy(buddy.getHandle());        
        if (theirKey != null){
        	encrypt = new Encryptor(theirKey, myKey);
        }else {
        	toggleEncryption_.setEnabled(false);
        }
        myKey_ = myKey;
        toggleEncryption_.setSelected(doEncryption_);
        toggleEncryption_.setText(ENCRYPTION_OFF_);
        talkArea_.requestFocus();
        theirHandle_ = buddy.getHandle();
        myHandle_ = myHandle;
        myParent_ = myParent;
        
    }

    
    public void enableEncryption(PublicKey theirKey){
    	if (theirKey != null){
    		encrypt = new Encryptor(theirKey, myKey_);
    		toggleEncryption_.setEnabled(true);
    		talkArea_.append("Key received. Encryption is now available.\n");
    	}
    }
    
    public void enableWhisperBot()
    {
    	toggleWhisperBot_.setEnabled(true);
    	talkArea_.append("WhisperBot is now Active.\n");
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private JComponent initComponents() {
        
    	//Create panel that will hold the layout, and become a tab
    	JPanel newPanel_ = new JPanel(true);
    	GroupLayout layout = new GroupLayout(newPanel_);
    	
    	
    	//set native look and feel
		try  {  
			//Tell the UIManager to use the platform look and feel  
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());  
		}  
		catch(Exception e) {  
			//Do nothing  
		}  
    	
    	
        if (buddy_.getProtocolID().equals(AOL_)) {
		      //buddy is on aim
		      serviceIcon_ =  aimIcon_;
		} else {
		      serviceIcon_ = defaultIcon_;
		}
        buddyName_ = new JLabel(buddy_.getHandle(), serviceIcon_, SwingConstants.LEFT);
        
        
        talkArea_ = new JTextArea();
        talkArea_.setColumns(20);
        talkArea_.setRows(5);
        talkArea_.setEditable(false);
        talkArea_.setLineWrap(true);
        talkArea_.setWrapStyleWord(true);
        talkAreaScroll_ = new JScrollPane(talkArea_);
        talkAreaScroll_.setViewportView(talkArea_);
        talkAreaScroll_.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        talkAreaScroll_.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        messageArea_ = new JTextArea();
        messageArea_.setColumns(20);
        messageArea_.setLineWrap(true);
        messageArea_.setWrapStyleWord(true);
        messageArea_.addKeyListener (
        		new KeyAdapter() {
        			public void keyTyped(KeyEvent e) {
        	            if (e.getKeyChar() == '\n') {
        	            	String str = messageArea_.getText().trim();
        	            	messageArea_.setText(str);
        	            	sendMsg();
        	            }
        	         }
        	    }
        	);

        messageAreaScroll_ = new JScrollPane(messageArea_);
        messageAreaScroll_.setViewportView(messageArea_);      
        messageAreaScroll_.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        messageAreaScroll_.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        toggleEncryption_ = new JToggleButton();
        toggleEncryption_.setActionCommand(TOGGLE_ENCRYPTION_);
        toggleEncryption_.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	WhisperIM.this.actionPerformed(evt);
            }
        });
        
        sendKeyBtn_ = new JButton();
        sendKeyBtn_.setText(SEND_KEY_);
        sendKeyBtn_.setActionCommand(SEND_KEY_);
        sendKeyBtn_.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	WhisperIM.this.actionPerformed(evt);
            }
        });
        
        sendBtn_ = new JButton();
        sendBtn_.setText(SEND_);
        sendBtn_.setActionCommand(SEND_);
        sendBtn_.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                WhisperIM.this.actionPerformed(evt);
            }
        });
        
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(470, 315));
        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

       
        //getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
	            layout.createParallelGroup(GroupLayout.LEADING)
	            .add(layout.createSequentialGroup()
	            		.addContainerGap()
		                .add(layout.createParallelGroup(GroupLayout.LEADING)
		                		.add(buddyName_, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
			                    .add(talkAreaScroll_, GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
			                    .add(messageAreaScroll_, GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
			                    .add(layout.createSequentialGroup()
			                    		.add(toggleEncryption_)
			                    		.addPreferredGap(LayoutStyle.RELATED)
			                    		.add(sendKeyBtn_)
			                    		.addPreferredGap(LayoutStyle.RELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
			                    		.add(sendBtn_)
			                    )
			            )
		                .addContainerGap()
	             )
        );
        layout.setVerticalGroup(
	            layout.createParallelGroup(GroupLayout.LEADING)
	            .add(GroupLayout.TRAILING, layout.createSequentialGroup()
		                .addContainerGap()
		                .add(buddyName_, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)                
		                .addPreferredGap(LayoutStyle.RELATED)
		                .add(talkAreaScroll_, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
		                .addPreferredGap(LayoutStyle.RELATED, 10, 10)
                		.add(messageAreaScroll_, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
		                .addPreferredGap(LayoutStyle.RELATED)
		                .add(layout.createParallelGroup()
		                		.add(toggleWhisperBot_)
		                		.add(toggleEncryption_)
		                		.add(sendKeyBtn_)
		                		.add(sendBtn_))
		                .addContainerGap()
	            )
        );

        buddyName_.getAccessibleContext().setAccessibleName("Buddy");
        
        newPanel_.setLayout(layout);
        return newPanel_;
    }

    
    public void actionPerformed(ActionEvent e) {
    	String actionCommand = e.getActionCommand();
    	if (actionCommand.equals(sendBtn_.getActionCommand())) {
    		sendMsg();
    	}else if(actionCommand.equals(toggleEncryption_.getActionCommand())) {
    		if (doEncryption_) {
                doEncryption_ = false;
                toggleEncryption_.setText(ENCRYPTION_OFF_);
            }
            else {
                doEncryption_ = true;
                toggleEncryption_.setText(ENCRYPTION_ON_);
            }
    	}else if(actionCommand.equals(toggleWhisperBot_.getActionCommand()))
    	{
    		if (doWhisperBot_)
    		{
    			doWhisperBot_ = false;
    			toggleWhisperBot_.setText(WHISPERBOT_OFF_);
    		}else{
    			doWhisperBot_ = true;
    			toggleWhisperBot_.setText(WHISPERBOT_ON_);
    		}
    	}else if (actionCommand.equals(sendKeyBtn_.getActionCommand())) {
    		try{
        		Message keyMsg = new Message(new Buddy(myHandle_, myHandle_, buddy_.getProtocolID()), 
        				new Buddy(theirHandle_, myHandle_, buddy_.getProtocolID()), 
        				"<whisperim keyspec=" + Encryptor.getMyPublicKey() + "--", buddy_.getProtocolID(),
        				Calendar.getInstance().getTime());
        		myParent_.sendMessage(keyMsg);
        		talkArea_.append("Public key sent\n");
        		
        	}catch (Exception ex){
        		talkArea_.append("An error has occurred sending the key.\n");
        	}
    	}
    	else if (e.getSource() == exit_){
    		formWindowClosing(null);
    		dispose();
    	}
    	else if (e.getSource() == logging_){
    		doLogging_ = logging_.getState();
    		if (log_ == null)
    			log_ = new Logger(); 
    			
    	} else {
    		//problem with button interface
    		//didn't listen to the right Action Command
    		System.out.println("Problem with button interface");
    	}
    }
    
	private void createTab(String buddy){
		JComponent newTab_ = initComponents();
		mainPain_.addTab(buddy, null, newTab_, "This is who you are talking to...");
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
    
    private void formWindowClosed(WindowEvent evt) {

    }

    private void formWindowClosing(WindowEvent evt) {
    	System.out.println("Window closing");
    	//Close the open file
    	if (log_ != null)
    		log_.close();
    	myParent_.onWindowClose(theirHandle_);
    }

    public static String clearHTMLTags(String strHTML, int doWork){
    	
    	Pattern pattern = null;
	   	String htmlChars;
	   	String strTagLess = null; 
    	strTagLess = strHTML; 
    	
    	if(doWork == -1)
  		{
  			htmlChars = "<[^>]*>";
			pattern = Pattern.compile(htmlChars);
			strTagLess = pattern.matcher(strTagLess).replaceAll(""); 
		}
		
		return strTagLess; 
    }
    
    public void autoScroll() {
    	//this code implements autoscroll if the scroll bar is at the end of the box
    	//not what we want it to do in this context
    	//save for preferences
    	
    	//find position of the scroll bar
    	//JScrollBar bar = talkAreaScroll_.getVerticalScrollBar();
    	//boolean autoScroll = ((bar.getValue() + bar.getVisibleAmount()) == bar.getMaximum());

    	// now scroll if we were already at the bottom.
    	//if(autoScroll)
    	talkArea_.setCaretPosition(talkArea_.getDocument().getLength());
    }
    
    public void receiveMsg(Message message)
    {
    	DateFormat d = DateFormat.getTimeInstance(DateFormat.MEDIUM);
    	talkArea_.append("(" + d.format(message.getTimeSent()) + ") ");
    	talkArea_.append(message.getFrom() + ": ");
        
    	if (!doEncryption_ || !message.getMessage().contains("<key>")){
    		talkArea_.append(clearHTMLTags(message.getMessage(), -1));    		
    	}else{
    		
    		talkArea_.append("(Encrypted Message) " + clearHTMLTags(encrypt.decryptMessage(message.getMessage()), -1));
    	}
        
    	if (doLogging_)
        	log_.write(message, message.getFrom());
        	
        talkArea_.append("\n");
        autoScroll();
    }

    public void sendMsg()
    {
    	//if the message is empty, do nothing
    	if (messageArea_.getText().trim().equals("")) {
    		//do nothing
    	
    	//else send message
    	}else {
    		
	        String messageText;
	        Calendar now = Calendar.getInstance();
	        Date d = now.getTime();
	        DateFormat df1 = DateFormat.getTimeInstance(DateFormat.MEDIUM);
	        if (doEncryption_) {
	            //Message will be encrypted
	        	talkArea_.append("(" + df1.format(d) + ") " + myHandle_ + ":  (Ecrypted Message) " + messageArea_.getText() + "\n");
	            messageText = encrypt.generateCipherText(messageArea_.getText());
	        }
	        else {
	        	talkArea_.append("(" + df1.format(d) + ") " + myHandle_ + ": " + messageArea_.getText() + "\n");
	            messageText = messageArea_.getText();
	        }
	
	        Message message = new Message(new Buddy(myHandle_, myHandle_, buddy_.getProtocolID()), 
    				new Buddy(theirHandle_, myHandle_, buddy_.getProtocolID()), 
    				messageText, buddy_.getProtocolID(),Calendar.getInstance().getTime());
	        
	        myParent_.sendMessage(message);
	        
	        autoScroll();
	        
	    	if (doLogging_)
	        	log_.write(message, myHandle_);
	    	
	        messageArea_.setText("");
    	}
    }
    
}
