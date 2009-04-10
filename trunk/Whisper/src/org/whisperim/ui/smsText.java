 /**************************************************************************
 * Copyright 2009                                                          *
 * Kirk Banks   				                                           *
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

import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.JOptionPane;

import java.util.Properties;

public class smsText {
	
	WhisperClient client_;
	private Session mailSession_;
	private Properties props_;
	private MimeMessage message_;

    private static final String SMTP_HOST_NAME_ = "smtp.gmail.com";
    private static final int SMTP_HOST_PORT_ = 465;
    private static final String SMTP_AUTH_USER_ = "whisper.client@gmail.com";
    private static final String SMTP_AUTH_PWD_  = "whisper1";
    
    public smsText(){
    	props_ = new Properties();
    	mailSession_ = Session.getDefaultInstance(props_);
    }

    public void sendText(MimeMessage message) throws Exception{

    	props_.put("mail.transport.protocol", "smtps");
        props_.put("mail.smtps.host", SMTP_HOST_NAME_);
        props_.put("mail.smtps.auth", "true");
        props_.put("mail.smtps.quitwait", "false");

        Transport transport = mailSession_.getTransport();
        transport.connect(SMTP_HOST_NAME_, SMTP_HOST_PORT_, SMTP_AUTH_USER_, SMTP_AUTH_PWD_);
        transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
        transport.close();
    }
    
    public MimeMessage getSMSinfo() throws AddressException, MessagingException{
    	
    	message_ = new MimeMessage(mailSession_);
    	message_.addRecipient(Message.RecipientType.TO, new InternetAddress("2395952944@txt.att.net"));
        message_.setSubject("Subject");
        message_.setContent("This is a test, w00t it worked! YAAAAAAAAAAAAAY!!!!!!!!!", "text/plain");
        
        JOptionPane.showMessageDialog(null, "Your SMS Text has been sent.");
		return message_;
    }
}