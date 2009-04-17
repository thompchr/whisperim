package org.whisperim.ui;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.whisperim.client.Buddy;
import org.whisperim.client.Logger;
import org.whisperim.client.Message;
import org.whisperim.security.Encryptor;


public class WhisperIMPanel extends JPanel implements ActionListener, ChangeListener {

	private boolean doWhisperBot_ = false;
	private boolean doEncryption_ = false;
	private String theirHandle_;
	private String myHandle_;
	private boolean doLogging_ = false;
	private int myIndex_;
	private Timer flash_, noFlash_;
	private Color flashColor_ = new Color(30,144,255);
	private String currentColor_ = "default";

	private Buddy buddy_;
	private WhisperIM window_;
	private Whiteboard whiteboard_; 

	//This needs to be changed to support using the icon associated with the plugin
	//Perhaps store it in the buddy object
	private static final String AOL_ = "AIM";
	private static final String ENCRYPTION_OFF_ = "Encryption: Off";
	private static final String ENCRYPTION_ON_ = "Encryption: On";
	private static final String SEND_ = "Send";
	private static final String SEND_KEY_ = "Send Key";
	private static final String TOGGLE_ENCRYPTION_ = "Toggle Encryption";
	private static final String START_WHITEBOARD_ = "Start Whiteboard";

	private ImageIcon serviceIcon_;
	private JLabel buddyName_;
	private JScrollPane talkAreaScroll_;
	private JTextArea messageArea_;
	private JScrollPane messageAreaScroll_;
	private JButton sendBtn_;
	private JTextArea talkArea_;
	private JToggleButton toggleEncryption_;
	private JButton sendKeyBtn_;
	private JCheckBoxMenuItem logging_;
	private JButton whiteboardBtn_;
	private JButton close_;
	private JPanel head_;

	private Logger log_;

	private ImageIcon defaultIcon_ = new ImageIcon("..\\images\\default.ico");
	private ImageIcon aimIcon_ = new ImageIcon("..\\images\\aim_icon_small.png");
	private ImageIcon closeIcon_ = new ImageIcon("..\\images\\close.jpg");

	public WhisperIMPanel (Buddy buddy, WhisperIM window){

		super(true);


		buddy_ = buddy;
		window_ = window;
		theirHandle_ = buddy.getHandle();
		myHandle_ = buddy.getAssociatedLocalHandle();


		initComponents();
		toggleEncryption_.setSelected(doEncryption_);
		toggleEncryption_.setText(ENCRYPTION_OFF_);


		if (window_.getMyParent().haveKey(buddy_)){
			toggleEncryption_.setEnabled(true);
		}else {
			toggleEncryption_.setEnabled(false);
		}

		toggleEncryption_.setSelected(doEncryption_);
		toggleEncryption_.setText(ENCRYPTION_OFF_);
		//talkArea_.requestFocus();


		addTab();


		this.requestFocusInWindow();
	}
	
	public Buddy getBuddy(){
		return buddy_;
	}


	public void enableEncryption(){

		toggleEncryption_.setEnabled(true);
		talkArea_.append("<b>Key received. Encryption is now available.</b>\n");

	}	
	
	public void disableEncryption(){
		toggleEncryption_.setEnabled(false);
		talkArea_.append("<b>Encryption Disabled.</b>\n");
	}

	private JComponent initComponents() {

		//Create panel that will hold the layout, and become a tab




		GroupLayout layout = new GroupLayout(this);

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

		this.setName(buddy_.getHandle());

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
				WhisperIMPanel.this.actionPerformed(evt);
			}
		});

		sendKeyBtn_ = new JButton();
		sendKeyBtn_.setText(SEND_KEY_);
		sendKeyBtn_.setActionCommand(SEND_KEY_);
		sendKeyBtn_.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				WhisperIMPanel.this.actionPerformed(evt);
			}
		});

		sendBtn_ = new JButton();
		sendBtn_.setText(SEND_);
		sendBtn_.setActionCommand(SEND_);
		sendBtn_.addActionListener(this); 

		whiteboardBtn_ = new JButton();
		whiteboardBtn_.setText(START_WHITEBOARD_);
		whiteboardBtn_.setActionCommand(START_WHITEBOARD_);
		whiteboardBtn_.addActionListener(this); 

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
										.add(whiteboardBtn_)
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
								.add(toggleEncryption_)
								.add(whiteboardBtn_)
								.add(sendKeyBtn_)
								.add(sendBtn_))
								.addContainerGap()
				)
		);


		buddyName_.getAccessibleContext().setAccessibleName("Buddy");





		this.setLayout(layout);
		return this;
	}


	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if (actionCommand.equals(sendBtn_.getActionCommand())) {
			sendMsg();
		}else if(actionCommand.equals(toggleEncryption_.getActionCommand())) {
			if (doEncryption_) {
				doEncryption_ = false;
				window_.getMyParent().requestDisableEncryption(buddy_);
				toggleEncryption_.setText(ENCRYPTION_OFF_);
			}
			else {
				doEncryption_ = true;
				window_.getMyParent().requestEnableEncryption(buddy_);
				toggleEncryption_.setText(ENCRYPTION_ON_);
			}
		}else if (actionCommand.equals(sendKeyBtn_.getActionCommand())) {
			try{
				Message keyMsg = new Message(new Buddy(myHandle_, myHandle_, buddy_.getProtocolID()), 
						new Buddy(theirHandle_, myHandle_, buddy_.getProtocolID()), 
						"<whisperim keyspec=" + window_.getMyParent().getMyKey() + "-- />", buddy_.getProtocolID(),
						Calendar.getInstance().getTime());
				window_.getMyParent().sendMessage(keyMsg);
				talkArea_.append("Public key sent\n");

			}catch (Exception ex){
				talkArea_.append("An error has occurred sending the key.\n");
				ex.printStackTrace();
			}
		}
		else if (actionCommand.equals(whiteboardBtn_.getActionCommand())) {
			if(whiteboardBtn_.isEnabled())
			{
				try{
					whiteboard_ = new Whiteboard(buddy_.getHandle(),500,500);
				}catch (Exception ex){
					talkArea_.append("An error has occurred starting the whiteboard.\n");
				}
				whiteboardBtn_.setEnabled(false);
			}
			else
				whiteboardBtn_.setEnabled(false);
		}

		else if (actionCommand.equals(close_.getActionCommand()))
			window_.closeTab(this);

		else if (actionCommand.equals(flash_.getActionCommand()))
		{
			if (currentColor_.equals("flash")){
				head_.setBackground(this.getBackground());
				currentColor_ = "default";
			}
			else{
				head_.setBackground(flashColor_);
				currentColor_ = "flash";
			}
		}

		else {
			//problem with button interface
			//didn't listen to the right Action Command
			System.out.println("Problem with button interface");
		}
	}

	private void addTab(){

		window_.addPanel(buddy_, this);

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

		if (doLogging_)
			log_.write(message, message.getFrom());


		flash(window_.isFocused(this));
		//flash(this.isFocusOwner());
		talkArea_.append(message.getMessage());

		talkArea_.append("\n");
		autoScroll();
	}

	public boolean isWhiteboardMsg(String msg)
	{
		if(msg.contains("<whiteboard>") && msg.contains("</whiteboard>"))//contains whiteboard tags
		{
			msg = msg.replace("<whiteboard>","");
			msg = msg.replace("</whiteboard>","");

			whiteboard_.handleCommand(msg);

			return true;
		}
		else
			return false;
	}

	public void sendMsg()
	{
		//if the message is empty, do nothing
		if (messageArea_.getText().trim().equals("")) {
			//do nothing

			//else send message
		}else {

			Calendar now = Calendar.getInstance();
			Date d = now.getTime();
			DateFormat df1 = DateFormat.getTimeInstance(DateFormat.MEDIUM);
			talkArea_.append("(" + df1.format(d) + ") " + myHandle_ + ":");
			if (doEncryption_) {   	    			    		
				//Message will be encrypted
				boolean b = isWhiteboardMsg(messageArea_.getText());
			}
			talkArea_.append(messageArea_.getText() + "\n");


			Message message = new Message(new Buddy(myHandle_, myHandle_, buddy_.getProtocolID()), 
					new Buddy(theirHandle_, myHandle_, buddy_.getProtocolID()), 
					messageArea_.getText(), buddy_.getProtocolID(),Calendar.getInstance().getTime());

			if (window_.getMyParent() == null)
				System.out.println("This needs to not print...");
			else
				window_.getMyParent().sendMessage(message);

			autoScroll();

			if (doLogging_)
				log_.write(message, myHandle_);

			messageArea_.setText("");
		}
	}

	public void flash(boolean focused){


		if (head_ == null){
			System.out.println("This shouldnt happen");
			System.out.println("Please contact your favorite WhisperIM dev with error code NOT1337");
		}
		else
		{
			if (focused)
			{

				head_.setBackground(this.getBackground());
			}
			else
			{
				if (flash_ == null){
					flash_ = new Timer(750,this);
					flash_.setActionCommand("flash");
				}

				currentColor_ = "flash";
				flash_.start();

			}
		}

	}


	public JPanel getTabHead()
	{
		//We only want one head tab, per tab...
		if (head_ == null){

			head_ = new JPanel();
			head_.add(new JLabel(buddy_.getHandle()));
			close_ = new JButton(closeIcon_);
			close_.addActionListener(this);

			close_.setPreferredSize(new Dimension(closeIcon_.getIconHeight(),closeIcon_.getIconWidth()));

			head_.add(close_);
			return head_;
		}
		else 
			return head_;

	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		if (window_.isFocused(this)){

			//Do nothing, the tab was just created...
			if (flash_ == null){}

			else{
				flash_.stop();
				currentColor_ = "default";
				head_.setBackground(this.getBackground());
			}
		}


	}
}	
