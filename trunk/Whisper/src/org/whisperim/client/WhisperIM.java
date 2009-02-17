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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.whisperim.security.Encryptor;

/**
 * The majority of this class is auto-generated code using Java's swing UI
 * components. The primary method of interest is the WhisperIM constructor that
 * is called from WhisperClient.java
 * 
 * @author Kirk Banks, Chris Thompson, John Dlugokecki, Cory Plastek
 */
public class WhisperIM extends JFrame implements ActionListener {
    //Encryptor for the chat session
    private Encryptor encrypt;
    private boolean doEncryption = false;
    private String theirHandle_;
    private String myHandle_;
    
    private static final String ENCRYPTION_OFF_ = "Encryption: Off";
    private static final String ENCRYPTION_ON_ = "Encryption: On";
    private static final String SEND_ = "Send";
    private static final String SEND_KEY_ = "Send Key";
    private static final String TOGGLE_ENCRYPTION_ = "Toggle Encryption";
    
    private WhisperClient myParent_;
    private PrivateKey myKey_;
    
    private ImageIcon defaultIcon_ = new ImageIcon("..\\images\\default.ico");
	private ImageIcon aimIcon_ = new ImageIcon("..\\images\\aim_icon_small.png");

    /*
     * The logging portion should be toggled by the user.  Until the additional UI
     * is implemented, the logger is created by default, and logging is universal
     */
    //Logger for this session.
    private Logger log_ = new Logger();
    private boolean toggleLogging = false;

    /** Creates new form WhisperIM */
    public WhisperIM(Buddy buddy, String myHandle, WhisperClient myParent, PrivateKey myKey) {
    	super("Whisper IM Conversation with " + buddy.getHandle());
    	
    	initComponents();
        
    	if (buddy.getProtocolID().equals("aol")) {
		      //buddy is on aim
		      serviceIcon_ =  aimIcon_;
		} else {
		      serviceIcon_ = defaultIcon_;
		}
    	buddyName_ = new JLabel(buddy.getHandle(), serviceIcon_, SwingConstants.RIGHT);
        PublicKey theirKey = Encryptor.getPublicKeyForBuddy(buddy.getHandle());        
        if (theirKey != null){
        	encrypt = new Encryptor(theirKey, myKey);
        }else {
        	toggleEncryption_.setEnabled(false);
        }
        myKey_ = myKey;
        toggleEncryption_.setSelected(doEncryption);
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
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {

        jDialog1_ = new JDialog();
        jDialog2_ = new JDialog();
        
        messageArea_ = new JTextArea();
        messageArea_.setLineWrap(true);
        messageArea_.setWrapStyleWord(true);
        messageAreaScroll_ = new JScrollPane(messageArea_);
        
        sendBtn_ = new JButton();
        
        talkAreaScroll_ = new JScrollPane();
        talkArea_ = new JTextArea();
        talkArea_.setLineWrap(true);
        talkArea_.setWrapStyleWord(true);
        
        buddyName_ = new JLabel();
        
        toggleEncryption_ = new JToggleButton();
        
        sendKeyBtn_ = new JButton();

        GroupLayout jDialog1Layout = new GroupLayout(jDialog1_.getContentPane());
        jDialog1_.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(GroupLayout.LEADING)
            .add(0, 400, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(GroupLayout.LEADING)
            .add(0, 300, Short.MAX_VALUE)
        );

        GroupLayout jDialog2Layout = new GroupLayout(jDialog2_.getContentPane());
        jDialog2_.getContentPane().setLayout(jDialog2Layout);
        jDialog2Layout.setHorizontalGroup(
            jDialog2Layout.createParallelGroup(GroupLayout.LEADING)
            .add(0, 400, Short.MAX_VALUE)
        );
        jDialog2Layout.setVerticalGroup(
            jDialog2Layout.createParallelGroup(GroupLayout.LEADING)
            .add(0, 300, Short.MAX_VALUE)
        );

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

        sendBtn_.setText(SEND_);
        sendBtn_.setActionCommand(SEND_);
        sendBtn_.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                WhisperIM.this.actionPerformed(evt);
            }
        });
        sendKeyBtn_.setText(SEND_KEY_);
        sendKeyBtn_.setActionCommand(SEND_KEY_);
        sendKeyBtn_.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	WhisperIM.this.actionPerformed(evt);
            }
        });

        messageArea_.setColumns(20);
        messageAreaScroll_.setViewportView(messageArea_);      
        messageAreaScroll_.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        messageAreaScroll_.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        talkArea_.setColumns(20);
        talkArea_.setRows(5);
        talkArea_.setEditable(false);
        talkAreaScroll_.setViewportView(talkArea_);
        talkAreaScroll_.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        talkAreaScroll_.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        buddyName_.setText("jLabel1");

        toggleEncryption_.setActionCommand(TOGGLE_ENCRYPTION_);
        toggleEncryption_.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	WhisperIM.this.actionPerformed(evt);
            }
        });
        

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
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
			                    		.add(160, 160, 160)
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
		                .addPreferredGap(LayoutStyle.RELATED)
		                .add(talkAreaScroll_, GroupLayout.PREFERRED_SIZE, 153, GroupLayout.PREFERRED_SIZE)
		                .addPreferredGap(LayoutStyle.RELATED, 20, Short.MAX_VALUE)
                		.add(messageAreaScroll_, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
		                .addPreferredGap(LayoutStyle.RELATED)
		                .add(layout.createParallelGroup()
		                		.add(toggleEncryption_)
		                		.add(sendKeyBtn_)
		                		.add(sendBtn_))
		                .addContainerGap()
	            )
        );
        
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

        buddyName_.getAccessibleContext().setAccessibleName("Buddy");
    }

    
    public void actionPerformed(ActionEvent e) {
    	String actionCommand = e.getActionCommand();
    	if (actionCommand.equals(sendBtn_.getActionCommand())) {
    		sendMsg();
    	}else if(actionCommand.equals(toggleEncryption_.getActionCommand())) {
    		if (doEncryption) {
                doEncryption = false;
                toggleEncryption_.setText(ENCRYPTION_OFF_);
            }
            else {
                doEncryption = true;
                toggleEncryption_.setText(ENCRYPTION_ON_);
            }
    	}else if (actionCommand.equals(sendKeyBtn_.getActionCommand())) {
    		try{
        		Message keyMsg = new Message(myHandle_, theirHandle_, "<whisperim keyspec=" + Encryptor.getMyPublicKey() + "--", Calendar.getInstance().getTime());
        		myParent_.sendMessage(keyMsg);
        		talkArea_.append("Public key sent\n");
        		
        	}catch (Exception ex){
        		talkArea_.append("An error has occurred sending the key.\n");
        	}
    	}
    	else {
    		//problem with button interface
    		//didn't listen to the right Action Command
    		System.out.println("Problem with button interface");
    	}
    }
    
    private void formWindowClosed(WindowEvent evt) {

    }

    private void formWindowClosing(WindowEvent evt) {
    	System.out.println("Window closing");
    	//Close the open file
    	log_.close();
    	myParent_.onWindowClose(theirHandle_);
    }
    public void receiveMsg(Message message)
    {
    	
    	
    	DateFormat d = DateFormat.getTimeInstance(DateFormat.MEDIUM);
    	talkArea_.append("(" + d.format(message.getTimeSent()) + ") ");
    	talkArea_.append(message.getFrom() + ": ");
        
    	if (!doEncryption || !message.getMessage().contains("<key>")){
    		talkArea_.append(message.getMessage());
    	}else{
    		
    		talkArea_.append("(Encrypted Message) " + encrypt.decryptMessage(message.getMessage()));
    	}
        
    	if (toggleLogging)
        	log_.write(message, message.getFrom());
        	
        talkArea_.append("\n");
    }

    public void sendMsg()
    {
    	//if the message is empty, do nothing
    	if (messageArea_.getText().equalsIgnoreCase("")) {
    		//do nothing
    	
    	//else send message
    	}else {
    		
	        String messageText;
	        Calendar now = Calendar.getInstance();
	        Date d = now.getTime();
	        DateFormat df1 = DateFormat.getTimeInstance(DateFormat.MEDIUM);
	        if (doEncryption) {
	            //Message will be encrypted
	        	talkArea_.append("(" + df1.format(d) + ") " + myHandle_ + ":  (Ecrypted Message) " + messageArea_.getText() + "\n");
	            messageText = encrypt.generateCipherText(messageArea_.getText());
	        }
	        else {
	        	talkArea_.append("(" + df1.format(d) + ") " + myHandle_ + ": " + messageArea_.getText() + "\n");
	            messageText = messageArea_.getText();
	        }
	
	        Message message = new Message(myHandle_, theirHandle_, messageText, Calendar.getInstance().getTime());
	        
	        myParent_.sendMessage(message);
	        messageArea_.setText("");
    	}
    }
    

    private ImageIcon serviceIcon_;
    private JLabel buddyName_;
    private JDialog jDialog1_;
    private JDialog jDialog2_;
    private JScrollPane talkAreaScroll_;
    private JTextArea messageArea_;
    private JScrollPane messageAreaScroll_;
    private JButton sendBtn_;
    private JTextArea talkArea_;
    private JToggleButton toggleEncryption_;
    private JButton sendKeyBtn_;

}
