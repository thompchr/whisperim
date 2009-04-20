package org.whisperim.EmailManager;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.mail.Message;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.UIManager;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ComposeEmail extends JFrame implements ActionListener {

	private static final String MESSAGE_ = "Message";
	private static final String SENDMAIL_ = "Send Mail";
	private static final String CANCEL_ = "Cancel";
	private static final String WINDOW_TITLE_ = "Compose Email";
	private static final String TO_ = "To:";
	private static final String SUBJECT_ = "Subject:";

	private JTextField toBox_ = new JTextField();
	private JTextField subjectBox_ = new JTextField();
	private JTextField messageBox_ = new JTextField();
	
	private JTextArea message_ = new JTextArea(MESSAGE_);
	private JButton sendButton_ = new JButton(SENDMAIL_);
	private JButton cancelButton_ = new JButton(CANCEL_);
	
	private Message emailMessage_ = null;
	private Date sentDate_ = null;
	private String to_ = null;
	private String subject_ = null;

	public void composeWindow(){
		
		
		messageBox_.addKeyListener(new KeyAdapter(){

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == '\n'){
					//Enter key
					actionPerformed(new ActionEvent(messageBox_, Integer.MAX_VALUE, SENDMAIL_));
				}
			}
		});
			
		
		SpringLayout sl = new SpringLayout();
		Container cp = getContentPane();
		cp.setLayout(sl);
		
		setTitle(WINDOW_TITLE_);
		
		cp.add(toBox_);
		cp.add(subjectBox_);
		cp.add(message_);
		cp.add(messageBox_);
		cp.add(sendButton_);
		cp.add(cancelButton_);
		
		
		setMinimumSize(new Dimension(400, 400));
		setMaximumSize(new Dimension(400, 400));
		
		sendButton_.setMinimumSize(new Dimension(75, 26));
		sendButton_.setMaximumSize(new Dimension(75, 26));
		sendButton_.setPreferredSize(new Dimension(75, 26));
		sendButton_.setActionCommand(SENDMAIL_);
		sendButton_.addActionListener(this);
		
		cancelButton_.setMinimumSize(new Dimension(75, 26));
		cancelButton_.setMaximumSize(new Dimension(75, 26));
		cancelButton_.setPreferredSize(new Dimension(75, 26));
		cancelButton_.setActionCommand(CANCEL_);
		cancelButton_.addActionListener(this);
	
		messageBox_.setMinimumSize(new Dimension(300, 300));
		messageBox_.setPreferredSize(new Dimension(300, 300));

		
		
		//Constraints
		sl.putConstraint(SpringLayout.EAST, toBox_, 30, SpringLayout.EAST, toBox_);

		sl.putConstraint(SpringLayout.WEST, message_, 20, SpringLayout.WEST, cp);
		sl.putConstraint(SpringLayout.NORTH, message_, 20, SpringLayout.NORTH, cp);
		
		sl.putConstraint(SpringLayout.WEST, messageBox_, 5, SpringLayout.EAST, message_);
		sl.putConstraint(SpringLayout.NORTH, messageBox_, 17, SpringLayout.NORTH, cp);
		
		sl.putConstraint(SpringLayout.SOUTH, cancelButton_, 25, SpringLayout.SOUTH, messageBox_);
		sl.putConstraint(SpringLayout.WEST, cancelButton_, 20, SpringLayout.EAST, sendButton_);
		
		sl.putConstraint(SpringLayout.SOUTH, sendButton_, 90, SpringLayout.SOUTH, cp);
		sl.putConstraint(SpringLayout.WEST, sendButton_, 25, SpringLayout.WEST, messageBox_);

		pack();
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		Calendar cal = new GregorianCalendar();
		String ac = evt.getActionCommand();
		if (ac.equals(SENDMAIL_)){
			ExpressWhisperMail ewm = new ExpressWhisperMail();
			try {
				
				// Still need to set emailMessage_ to the text input and set variations.
				
				ewm.sendMessage(ewm.getFrom(), sentDate_, to_, subject_, emailMessage_);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}else if(ac.equals(CANCEL_)){
			this.dispose();
		}
	}
}
		
