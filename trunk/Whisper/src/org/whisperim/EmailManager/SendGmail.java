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

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendGmail {
	
	private String user_ = null;
	private String password_ = null;
	private int smtpPort_ = 0;
	private String smtpHost_ = null;
	
	
	
	public SendGmail(String user, String password, String smtpHost, int smtpPort){
		user_ = user;
		password_ = password;
		smtpPort_ = smtpPort;
		smtpHost_ = smtpHost;

	}
	
	public void sendSSLMessage(String to, String subject,String message) throws MessagingException {
		
		Properties props = new Properties();
		props.put("mail.smtp.host", smtpHost_);
		props.put("mail.smtp.auth", "true");
		props.put("mail.debug", "true");
		props.put("mail.smtp.port", Integer.toString(smtpPort_));
		props.put("mail.smtp.socketFactory.port", Integer.toString(smtpPort_));
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");

		Session session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {

					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(user_, password_);
					}
				});

		Message msg = new MimeMessage(session);
		// Set From.
		InternetAddress addressFrom = new InternetAddress(user_);
		msg.setFrom(addressFrom);

		// Set to.
		msg.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(to, false));

		// Setting the Subject and Content Type
		msg.setSubject(subject);
		msg.setContent(message, "text/plain");
		Transport.send(msg);
	}
}