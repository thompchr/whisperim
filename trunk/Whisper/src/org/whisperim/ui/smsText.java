package org.whisperim.ui;

import javax.mail.*;
import javax.mail.internet.*;

import java.util.Properties;


public class smsText {

    private static final String SMTP_HOST_NAME_ = "smtp.gmail.com";
    private static final int SMTP_HOST_PORT_ = 465;
    private static final String SMTP_AUTH_USER_ = "whisper.client@gmail.com";
    private static final String SMTP_AUTH_PWD_  = "whisper1";

    public void sendText(long number, MimeMessage message) throws Exception{
        Properties props = new Properties();

        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtps.host", SMTP_HOST_NAME_);
        props.put("mail.smtps.auth", "true");
        props.put("mail.smtps.quitwait", "false");

        Session mailSession = Session.getDefaultInstance(props);
        //mailSession.setDebug(true);
        Transport transport = mailSession.getTransport();

        //MimeMessage message = new MimeMessage(mailSession);
        //message.setSubject("Testing SMTP-SSL");
        //message.setContent("This is a test", "text/plain");

        message.addRecipient(Message.RecipientType.TO, new InternetAddress("2395952944@txt.att.net"));

        transport.connect(SMTP_HOST_NAME_, SMTP_HOST_PORT_, SMTP_AUTH_USER_, SMTP_AUTH_PWD_);

        transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
        transport.close();
    }
}