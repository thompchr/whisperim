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

import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;

import com.sun.mail.pop3.POP3SSLStore;

public class GetGmail {
    
    private Session session = null;
    private Store store = null;
    private Folder folder;
    private String user_ = null;
    private String popHost_ = null;
    private int popPort_ = 0;
    private String password_ = null;
    
    public GetGmail(String user, String popHost, String password, int popPort) {
    	user_ = user;
    	popHost_ = popHost;
    	password_ = password;
    	popPort_ = popPort;
        
    }
    
    public void connect() throws Exception {
        
        Properties pop3Props = new Properties();
        
        pop3Props.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        pop3Props.setProperty("mail.pop3.socketFactory.fallback", "false");
		pop3Props.put("mail.debug", "true");
        pop3Props.setProperty("mail.pop3.port", Integer.toString(popPort_));
        pop3Props.setProperty("mail.pop3.socketFactory.port", Integer.toString(popPort_));
        
        URLName url = new URLName("pop3", popHost_, popPort_, "",
                user_, password_);
        
        session = Session.getInstance(pop3Props, null);
        store = new POP3SSLStore(session, url);
        store.connect();
        
    }
    
    public void openFolder(String folderName) throws Exception {
        
        // Open the Folder
        folder = store.getDefaultFolder();
        
        folder = folder.getFolder(folderName);
        
        if (folder == null) {
            throw new Exception("Invalid folder");
        }
        
        // try to open read/write and if that fails try read-only
        try {
            
            folder.open(Folder.READ_WRITE);
            
        } catch (MessagingException ex) {
            
            folder.open(Folder.READ_ONLY);
            
        }
    }
    
    public void closeFolder() throws Exception {
        folder.close(false);
    }
    
    public void disconnect() throws Exception {
        store.close();
    }
    
    public Message[] fetchMessages() throws Exception {
    	
        // Attributes & Flags for all messages ..
        Message[] msgs = folder.getMessages();
        
        // Use a suitable FetchProfile
        FetchProfile fp = new FetchProfile();
        fp.add(FetchProfile.Item.ENVELOPE);        
        folder.fetch(msgs, fp);
        
        for (int i = 0; i < msgs.length; i++) {
            System.out.println("--------------------------");
            System.out.println("MESSAGE #" + (i + 1) + ":");
            if (msgs[i].isMimeType("text/plain")) {
                System.out.println((String)msgs[i].getContent());        
            } 
        }    
        
        
        return msgs;
      }
    
    public int messageCount(){
    	int count = 0;
    	try {
			count = folder.getMessageCount();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
    }
}
