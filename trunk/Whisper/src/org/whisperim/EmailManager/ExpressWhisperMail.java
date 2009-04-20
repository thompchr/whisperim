package org.whisperim.EmailManager;

import java.io.IOException;
import java.util.Date;
import java.util.Observable;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

// This class handles the 
public class ExpressWhisperMail implements Runnable
{
    private static final String INBOX_ = "INBOX";
    private static final String POP_MAIL_ = "pop3";
    private static final String SMTP_MAIL_ = "smtp";
    private String smtpHost_ = null;
    private String pop3Host_ = null;
    private String user_ = null;
    private String password_ = null;
    private String emailListFile_ = null;
    private String from_ = null;
    private String date_ = null;
     
 
    
    // ACC/GET Methods
    
    // Return the SMTP Host.
    public String getSMTPHost(){
    	return smtpHost_;
    }
    
    // Set SMTP Host.
    public void setSMTPHost(String smtp){
    	smtpHost_ = smtp;
    }
    // Return the POP3 server.
    public String getPop3(){
    	return pop3Host_;
    }
    
    // Set the pop3 junk.
    public void setPop3(String pop3){
    	pop3Host_ = pop3;
    }
    
    // Return the user.
    public String getUser(){
    	return user_;
    }
    
    // Set the user.
    public void setUser(String user){
    	user_ = user;
    }
    
    // Get the person sending to.
    public String getEmailListFile(){
    	return emailListFile_;
    }
    
    // Get who email is from.
    public String getFrom(){
    	return from_;
    }
    
    
    // Updates email.
    public void updateEmail(){
    	try {
			checkNewMessages(smtpHost_, pop3Host_, user_, password_, emailListFile_, from_);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    // Checks for new messages.
    public void checkNewMessages(String smtpHost, String pop3Host, String user, String password, String emailListFile, String from)
           throws Exception
    {
        smtpHost_ = smtpHost;
        pop3Host_ = pop3Host;
        user_ = user;
        password_ = password;
        emailListFile_ = emailListFile;
        from_ = from;
        

        // Get a Session object.
        Properties sysProperties = System.getProperties();
        Session session = Session.getDefaultInstance(sysProperties, null);

        // Connect to host server.
        Store store = session.getStore(POP_MAIL_);
        store.connect(pop3Host, -1, user_, password_);
        

        // Open the default folder containing email.
        Folder folder = store.getDefaultFolder();
        if (folder == null)
            throw new NullPointerException("No default mail folder");

        folder = folder.getFolder(INBOX_);
        if (folder == null)
            throw new NullPointerException("Unable to get folder: " + folder);

        // Get message count (number of messages).
        folder.open(Folder.READ_WRITE);
        int totalMessages = folder.getMessageCount();
        if (totalMessages == 0)
        {
            folder.close(false);
            store.close();
            return;
        }
        
        // Get attributes and flags for all messages.
        Message[] messages = folder.getMessages();
        FetchProfile profile = new FetchProfile();
        profile.add(FetchProfile.Item.ENVELOPE);
        profile.add(FetchProfile.Item.FLAGS);
        profile.add("X-Mailer");
        folder.fetch(messages, profile);

        // Process each message
        for (int i = 0; i < messages.length; i++)
        {
            if (!messages[i].isSet(Flags.Flag.SEEN))    
                processMessage(smtpHost, messages[i]);
            messages[i].setFlag(Flags.Flag.DELETED, true); 
        }
        
        folder.close(true);
        store.close();
    }
    
    
    // Processes message.
    public void processMessage(String smtpHost, Message message)
                 throws Exception
    {
        String replyTo = user_, subject, xMailer, messageText;
        Date sentDate;
        int size;
        Address[] a=null;
        
        
        // Get Headers.
        if ((a = message.getFrom()) != null)
             replyTo = a[0].toString();

        subject  = message.getSubject();
        sentDate = message.getSentDate();
        size     = message.getSize();
        String[] hdrs = message.getHeader("X-Mailer");
        if (hdrs != null)
            xMailer = hdrs[0];
    }
    
    public void getMessage(Part message){
    	try{
    	if (message.getContentType().equals("text/html")) {
    		  String content = (String)message.getContent();
    		  JFrame frame = new JFrame();
    		  JEditorPane text = new JEditorPane("text/html", content);
    		  text.setEditable(false);
    		  JScrollPane pane = new JScrollPane(text);
    		  frame.getContentPane().add(pane);
    		  frame.setSize(300, 300);
    		  frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    		  frame.show();
    		}else{
    			
    		}
    	}catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
    }
    

    // Send message.
    public void sendMessage(String from, Date sentDate, String to, String subject, Message message)throws Exception
    {
        // Set properties up and pass smtpHost_.
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost_);
        Session session = Session.getDefaultInstance(props, null);
        
        // Create message.
        Message newMessage = new MimeMessage(session);
        newMessage.setFrom(new InternetAddress(from));
        newMessage.setRecipient(Message.RecipientType.TO, new InternetAddress (to));
        newMessage.setSubject(subject);
        newMessage.setSentDate(sentDate);

        // Set message contents
        Object content = message.getContent();
        newMessage.setText((String)content);
        
        // Send newMessage.
        Transport transport = session.getTransport(SMTP_MAIL_);
        transport.connect(smtpHost_, user_, password_);
        // CHANGE OUT NULL
        transport.sendMessage(newMessage, null);
    }
    
    // Forward a message.
    public void forwardMessage(String from, Date sentDate, String to, String subject, Message message, Message origionalMessage){
    	try {
    	// Set properties up and pass smtpHost_.
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost_);
        Session session = Session.getDefaultInstance(props, null);
    	// Create the message to forward
    	Message forward = new MimeMessage(session);

    	// Fill in header.
    	forward.setSubject("Fwd: " + message.getSubject());
    	forward.setFrom(new InternetAddress(from));
    	forward.addRecipient(Message.RecipientType.TO, 
    	  new InternetAddress(to));

    	// Create your new message part.
    	BodyPart messageBodyPart = new MimeBodyPart();
    	messageBodyPart.setText(origionalMessage.toString());

    	// Create a multi-part to combine the parts.
    	Multipart multipart = new MimeMultipart();
    	multipart.addBodyPart(messageBodyPart);

    	// Create and fill part for the forwarded content.
    	messageBodyPart = new MimeBodyPart();
    	messageBodyPart.setDataHandler(message.getDataHandler());

    	// Add part to multi part.
    	multipart.addBodyPart(messageBodyPart);

    	// Associate multi-part with message.
    	forward.setContent(multipart);

    	// Send forward.
        Transport transport = session.getTransport(SMTP_MAIL_);
        transport.connect(smtpHost_, user_, password_);
        // CHANGE OUT NULL
        transport.sendMessage(forward, null);
			
		} catch (MessagingException e) {
			e.printStackTrace();
		}	
    }
    
    // Reply to a message.
    public void replyToMessage(String from, Date sentDate, String to, String subject, Message message, Message origionalMessage){
    	try {
    	// Set properties up and pass smtpHost_.
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost_);
        Session session = Session.getDefaultInstance(props, null);
    	// Create the message to reply to.
    	Message forward = new MimeMessage(session);

    	// Fill in header.
    	forward.setSubject("RE: " + message.getSubject());
    	forward.setFrom(new InternetAddress(from));
    	forward.addRecipient(Message.RecipientType.TO, 
    	  new InternetAddress(to));

    	// Create your new message part.
    	BodyPart messageBodyPart = new MimeBodyPart();
    	messageBodyPart.setText(origionalMessage.toString());

    	// Create a multi-part to combine the parts.
    	Multipart multipart = new MimeMultipart();
    	multipart.addBodyPart(messageBodyPart);

    	// Create and fill part for the forwarded content.
    	messageBodyPart = new MimeBodyPart();
    	messageBodyPart.setDataHandler(message.getDataHandler());

    	// Add part to multi part.
    	multipart.addBodyPart(messageBodyPart);

    	// Associate multi-part with message.
    	forward.setContent(multipart);

    	// Send forward.
        Transport transport = session.getTransport(SMTP_MAIL_);
        transport.connect(smtpHost_, user_, password_);
        // CHANGE OUT NULL
        transport.sendMessage(forward, null);
			
		} catch (MessagingException e) {
			e.printStackTrace();
		}	
    }
    
    // Send an attachment.
    public void sendAttachment(String from, String to, String filename){
    	try {
    	// Set properties up and pass smtpHost_.
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost_);
        Session session = Session.getDefaultInstance(props, null);
    	
    	// Define message
    	Message message = new MimeMessage(session);
    	message.setFrom(new InternetAddress(from));
    	message.addRecipient(Message.RecipientType.TO, 
    	  new InternetAddress(to));
    	message.setSubject("Hello JavaMail Attachment");

    	// Create the message part 
    	BodyPart messageBodyPart = new MimeBodyPart();

    	// Fill the message
    	messageBodyPart.setText("Pardon Ideas");

    	Multipart multipart = new MimeMultipart();
    	multipart.addBodyPart(messageBodyPart);

    	// Part two is attachment
    	messageBodyPart = new MimeBodyPart();
    	DataSource source = new FileDataSource(filename);
    	messageBodyPart.setDataHandler(new DataHandler(source));
    	messageBodyPart.setFileName(filename);
    	multipart.addBodyPart(messageBodyPart);

    	// Put parts in message
    	message.setContent(multipart);

    	// Send Message
        Transport transport = session.getTransport(SMTP_MAIL_);
        transport.connect(smtpHost_, user_, password_);
        // CHANGE OUT NULL
        transport.sendMessage(message, null);
		
		} catch (MessagingException e) {
			e.printStackTrace();
		}

    }


	@Override
	public void run() {
		try {
			checkNewMessages(smtpHost_, pop3Host_, user_, password_, emailListFile_, from_);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}   
}

