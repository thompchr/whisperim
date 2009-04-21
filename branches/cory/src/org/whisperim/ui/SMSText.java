/**************************************************************************
* Copyright 2009                                                          *
* Kirk Banks                                                              *
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

public class SMSText {
       
   private Session mailSession_;
   private Properties props_;
   private MimeMessage message_;

   private static final String SMTP_HOST_NAME_ = "smtp.gmail.com";
   private static final int SMTP_HOST_PORT_ = 465;
   private static final String SMTP_AUTH_USER_ = "whisper.client@gmail.com";
   private static final String SMTP_AUTH_PWD_  = "whisper1";
   private static String carrier_;
   private static String number_;
   private static String messageString_;
   private static String user_;
   
   public SMSText(){
       props_ = new Properties();
       mailSession_ = Session.getDefaultInstance(props_);
   }

   public void sendText() throws Exception{
       
       MimeMessage message = getSMSinfo();
       
       if(message.getDataHandler().getContent().toString() != "cancel"){
               props_.put("mail.transport.protocol", "smtps");
               props_.put("mail.smtps.host", SMTP_HOST_NAME_);
               props_.put("mail.smtps.auth", "true");
               props_.put("mail.smtps.quitwait", "false");
       
               Transport transport = mailSession_.getTransport();
               transport.connect(SMTP_HOST_NAME_, SMTP_HOST_PORT_, SMTP_AUTH_USER_, SMTP_AUTH_PWD_);
               transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
               transport.close();
               JOptionPane.showMessageDialog(null, "Your SMS Text has been sent.");
       }
       else{
               JOptionPane.showMessageDialog(null, "Your SMS Text was canceled and not sent.");
       }
   }
   
   public String getCarrier(){
       Object[] cellPhoneCompanys = { "Alltel", "AT&T", "Boost Mobile", "Nextel", "Sprint", "T-Mobile", "Verizon", "Virgin Mobile"};
       Object userInput = JOptionPane.showInputDialog(null, "Select: ", "Cell Phone Carrier", 
                                                                JOptionPane.QUESTION_MESSAGE, null,cellPhoneCompanys, cellPhoneCompanys[0]);
       //Check is user canceled SMS Texting process
       if(userInput == null){
           return null;
       }
       
       String input = userInput.toString(); 
       if(input == "Alltel"){
           return "message.alltel.com";
       }
       else if(input == "AT&T"){
           return "txt.att.net";
       }
       else if(input == "Boost Mobile"){
           return "myboostmobile.com";
       }
       else if(input == "Nextel"){
           return "messaging.nextel.com";
       }
       else if(input == "Sprint"){
           return "messaging.sprintpcs.com";
       }
       else if(input == "T-Mobile"){
           return "tmomail.net";
       }
       else if(input == "Verizon"){
           return "vtext.com";
       }
       else if(input == "Virgin Mobile"){
           return "vmobl.com";
       }
       else{
           //Should never get here
           return null;
       }
   }
   
   public String getNumber(){
         Object userInput = JOptionPane.showInputDialog(null, "What 10-Digit cell phone number would you like to SMS text?", 
                                                                      "Cell Phone Number", JOptionPane.QUESTION_MESSAGE);
         if(userInput != null){
                 if(userInput.toString().length() != 10){
                        JOptionPane.showMessageDialog(null, "You did not enter a 10-Digit Number.  Please try again.");
                        getNumber();
                 }
                 return userInput.toString();
         }
         return null;
   }
   
   public String getMessage(){
           Object userInput = JOptionPane.showInputDialog(null, "Enter a message under 150 characters.                                ", 
                                                                                                                    "Enter your message", JOptionPane.QUESTION_MESSAGE);
           if(userInput != null){
                   if(userInput.toString().length() > 150){
                           JOptionPane.showMessageDialog(null, "Your message was too long.  Please try again.");
                           getMessage();
                   }
                   return userInput.toString();
           }
           return null;
   }

   public String getUserName(){
	   Object userInput = JOptionPane.showInputDialog(null, "Enter your name:", 
			   											"Enter your name", JOptionPane.QUESTION_MESSAGE); 
       if(userInput != null){
           if(userInput.toString().length() > 20){
                   JOptionPane.showMessageDialog(null, "Your name was too long.  Please try again.");
                   getUserName();
           }
           return userInput.toString();
       }
       return null;
   }
   
   public MimeMessage getSMSinfo() throws AddressException, MessagingException{
       //Get user inputs
       carrier_ = getCarrier();
       if(carrier_ != null){
               number_ = getNumber();
               if(number_ != null){
            	   	user_ = getUserName();
            	   	if(user_ != null){
                 	   messageString_ = getMessage();
            	   	}
               }
       }
       
       //Forming message to be sent
       message_ = new MimeMessage(mailSession_);
       if(carrier_ != null && number_ != null && messageString_ != null && user_ != null){
               message_.addRecipient(Message.RecipientType.TO, new InternetAddress(number_ + "@" + carrier_));
               message_.setSubject(user_);
               message_.setContent(messageString_, "text/plain");
               return message_;
       }
       
       //User has decided to cancel SMS Texting process - notifying sendText() method of this
       message_.setContent("cancel", "text/plain");
       return message_;
   }
}

