package org.whisperim.EmailManager;

/**************************************************************************
 * Copyright 2009 Nick Krieble                                             *
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

import org.whisperim.prefs.PreferencesWindowEmailAccounts;

// This class handles the 
public class ExpressWhisperMail implements Runnable {
	PreferencesWindowEmailAccounts pwec;

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
	public String getSMTPHost() {
		return smtpHost_;
	}

	// Set SMTP Host.
	public void setSMTPHost() {
		smtpHost_ = pwec.getSMTP();
	}

	// Return the POP3 server.
	public String getPop3() {
		return pop3Host_;
	}

	// Set the pop3 junk.
	public void setPop3() {
		pop3Host_ = pwec.getPOP3();
	}

	// Return the user.
	public String getUser() {
		return user_;
	}

	// Set the user.
	public void setUser() {
		user_ = pwec.getUsername();
	}

	// Get the person sending to.
	public String getEmailListFile() {
		return emailListFile_;
	}

	// Get who email is from.
	public String getFrom() {
		return from_;
	}

	// Updates email.
	public void updateEmail() {
		try {
			checkNewMessages();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Checks for new messages.
	public Message[] checkNewMessages() throws Exception {

		// Get a Session object.
		Properties sysProperties = System.getProperties();
		Session session = Session.getDefaultInstance(sysProperties, null);

		// Connect to host server.
		Store store = session.getStore(POP_MAIL_);
		store.connect(pop3Host_, -1, user_, password_);

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
		if (totalMessages == 0) {
			folder.close(false);
			store.close();
			return null;
		}

		// Get attributes and flags for all messages.
		Message[] messages = folder.getMessages();

		return messages;
		/*
		 * FetchProfile profile = new FetchProfile();
		 * profile.add(FetchProfile.Item.ENVELOPE);
		 * profile.add(FetchProfile.Item.FLAGS); profile.add("X-Mailer");
		 * folder.fetch(messages, profile);
		 * 
		 * // Process each message for (int i = 0; i < messages.length; i++) {
		 * if (!messages[i].isSet(Flags.Flag.SEEN)) processMessage(smtpHost_,
		 * messages[i]); messages[i].setFlag(Flags.Flag.DELETED, true); }
		 * 
		 * folder.close(true); store.close();
		 */
	}

	// Processes message.
	public void processMessage(String smtpHost, Message message)
			throws Exception {
		String replyTo = user_, subject, xMailer, messageText;
		Date sentDate;
		int size;
		Address[] a = null;

		// Get Headers.
		if ((a = message.getFrom()) != null)
			replyTo = a[0].toString();

		subject = message.getSubject();
		sentDate = message.getSentDate();
		size = message.getSize();
		String[] hdrs = message.getHeader("X-Mailer");
		if (hdrs != null)
			xMailer = hdrs[0];
	}

	public void getMessage(Part message) {
		try {
			if (message.getContentType().equals("text/html")) {
				String content = (String) message.getContent();
				JFrame frame = new JFrame();
				JEditorPane text = new JEditorPane("text/html", content);
				text.setEditable(false);
				JScrollPane pane = new JScrollPane(text);
				frame.getContentPane().add(pane);
				frame.setSize(300, 300);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.setVisible(true);
			} else {

			}
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Send message.
	public void sendMessage(Date sentDate, String to, String subject,
			Message message) throws Exception {
		// Set properties up and pass smtpHost_.
		Properties props = new Properties();
		props.put("mail.smtp.host", pwec.getSMTP());
		Session session = Session.getDefaultInstance(props, null);

		// Create message.
		Message newMessage = new MimeMessage(session);
		newMessage.setFrom(new InternetAddress(pwec.getUsername()));
		newMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(
				to));
		newMessage.setSubject(subject);
		newMessage.setSentDate(sentDate);

		// Set message contents
		Object content = message.getContent();
		newMessage.setText((String) content);

		// Send newMessage.
		Transport transport = session.getTransport(SMTP_MAIL_);
		transport.connect(pwec.getSMTP(), pwec.getUsername(), pwec
				.getPassword());
		// CHANGE OUT NULL
		transport.sendMessage(newMessage, null);
	}

	// Forward a message.
	public void forwardMessage(String from, Date sentDate, String to,
			String subject, Message message, Message origionalMessage) {
		try {
			// Set properties up and pass smtpHost_.
			Properties props = new Properties();
			props.put("mail.smtp.host", pwec.getSMTP());
			Session session = Session.getDefaultInstance(props, null);
			// Create the message to forward
			Message forward = new MimeMessage(session);

			// Fill in header.
			forward.setSubject("Fwd: " + message.getSubject());
			forward.setFrom(new InternetAddress(pwec.getUsername()));
			forward.addRecipient(Message.RecipientType.TO, new InternetAddress(
					to));

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
			transport.connect(pwec.getSMTP(), pwec.getUsername(), pwec
					.getPassword());
			// CHANGE OUT NULL
			transport.sendMessage(forward, null);

		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	// Reply to a message.
	public void replyToMessage(String to, String subject, Message message,
			Message origionalMessage) {
		try {
			// Set properties up and pass smtpHost_.
			Properties props = new Properties();
			props.put("mail.smtp.host", pwec.getSMTP());
			Session session = Session.getDefaultInstance(props, null);
			// Create the message to reply to.
			Message forward = new MimeMessage(session);

			// Fill in header.
			forward.setSubject("RE: " + message.getSubject());
			forward.setFrom(new InternetAddress(pwec.getUsername()));
			forward.addRecipient(Message.RecipientType.TO, new InternetAddress(
					to));

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
			transport.connect(pwec.getSMTP(), pwec.getUsername(), pwec
					.getPassword());
			// CHANGE OUT NULL
			transport.sendMessage(forward, null);

		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	// Send an attachment.
	public void sendAttachment(String from, String to, String filename) {
		try {
			// Set properties up and pass smtpHost_.
			Properties props = new Properties();
			props.put("mail.smtp.host", pwec.getSMTP());
			Session session = Session.getDefaultInstance(props, null);

			// Define message
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(pwec.getUsername()));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					to));
			message.setSubject("JavaMail Attachment");

			// Create the message part
			BodyPart messageBodyPart = new MimeBodyPart();

			// Fill the message
			messageBodyPart.setText("bodyparttest");

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
			transport.connect(pwec.getSMTP(), pwec.getUsername(), pwec
					.getPassword());
			// CHANGE OUT NULL
			transport.sendMessage(message, null);

		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		try {
			checkNewMessages();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
