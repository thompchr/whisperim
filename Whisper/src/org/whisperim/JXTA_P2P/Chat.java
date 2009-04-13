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
package org.whisperim.JXTA_P2P;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.Vector;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredTextDocument;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.endpoint.Message.ElementIterator;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PeerAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.protocol.PipeAdvertisement;

// Implements the actual chat.
public class Chat extends DiscoveryClientServer implements 
        DiscoveryListener,
        PipeMsgListener,
        Runnable,
        PipePresence.Listener,
        AdvertisementDiscoveryListener {

    /** The eol to use */
    private static String EOL = System.getProperty("line.separator");
    public final static String CHATNAMETAG_ = "JxtaTalkUserName";
    public final static String SENDERNAME_ = "JxtaTalkSenderName";
    public final static String SENDERGROUPNAME_ = "GrpName";
    public final static String SENDERMESSAGE_ = "JxtaTalkSenderMessage";
    public final static String SRCPEERADV_ = "JxtaTalkSrcPeerAdv";
    public final static String SRCPIPEADV_ = "JxtaTalkSrcPipeAdv";
    public final static String GROUPID_ = "JxtaTalkGroupId";
    public final static String COMMAND_ = "JxtaTalkCommand";
    public final static String PING_ = "Ping";
    public final static String ACK_ = "Ack";

    protected final static int PipeTimeout_ = 50000;
    private final static int WaitingTime_ = 2000;
    private final static int MAXRETRIES_ = 10;
    private final static int MAX_MESSAGES_ = 5;
    private boolean groupChanged_ = false;
    
    private EndpointReceiveQueue queue_ = new EndpointReceiveQueue(MAX_MESSAGES_);

    // The mime type of the Advertisements we send out
    private final static MimeMediaType XMLMIMETYPE_ = new MimeMediaType("text/xml");

    // Pipes
    protected PipeAdvertisement myPipeAdvt_ = null;
    protected PeerAdvertisement myPeerAdvt_ = null;
    private String myPipeAdvString_ = null;
    private static String myPeerAdvString_ = null;
    protected InputPipe inputPipe_ = null;
    protected PipeService pipes_ = null;
    protected PeerGroup currentGroup_ = null;
    protected Vector structureListeners_ = new Vector();
    protected Vector pipePListener_ = new Vector();
    protected Vector inputPipes_ = new Vector();

    // Peer Group Manager.
    protected PeerGroupManager manager_ = null;

    // Buddy List user being messaged
    protected BuddyListBuilder currentUser_ = null;

    // DiscoveryService service
    protected DiscoveryService discovery_ = null;

    // Chat stuff.
    protected boolean chatInProgress_ = false;
    protected String toName_;
    protected static String myName_;
    private static Vector chatSession_ = new Vector(10);
    private static Hashtable pipePTable_ = new Hashtable();
    private static Hashtable groups_ = new Hashtable();
    private static Vector messages_ = new Vector(10);
    
    // Initialized.
    private boolean initialized_ = false;
	  
    
    public interface Listener extends EventListener {

       void userEvent( PipePresence.PresenceEvent event );
       void refreshEvent(Enumeration event);
    }


    // Chat constructor.
    public Chat(PeerGroupManager manager) {

        this(manager, PipeAdvertisement.NameTag, CHATNAMETAG_ +".*");
    }

    // Chat constructor.
    protected Chat(PeerGroupManager manager,
                   String attr,
                   String value) {

        super(manager.getSelectedPeerGroup(),
                DiscoveryService.ADV,
                attr,
                value);

        this.manager_ = manager;

        // We use a thread just to let this happen in background
        Thread thread = new Thread(this, "Chat Thread");
        thread.start();
    }

    // Run chat.
    public void run() {

        manager_.addPeerListener(this);
        addListener(this);
        currentGroup_ = manager_.getSelectedPeerGroup();
        pipes_ = currentGroup_.getPipeService();
        recordGroup (currentGroup_);

        discovery_ = currentGroup_.getDiscoveryService();
        myName_ = manager_.getMyPeerName();
        chatSession_.addElement(currentGroup_.getPeerGroupID());
        login(myName_);

        getPipePresence (currentGroup_, myPipeAdvt_);
        initialized_ = true;
        
          while (true) {
            try {
             Message msg = queue_.waitForMessage();
             boolean res = currentUser_.sendMessage(msg);
            } catch (InterruptedException e) {
               // we should never get this
               // so printStackTrace();
               e.printStackTrace();
            }
            
          }        
    }

    private PipePresence getPipePresence (PeerGroup g, PipeAdvertisement localPipeAdv) {

        PipePresence tmpPresence = (PipePresence) pipePTable_.get (g.getPeerGroupID().toString());
        if (tmpPresence == null) {
            // No PipePresence has been created yet.
            tmpPresence = new PipePresence (g, localPipeAdv);
            pipePTable_.put(manager_.getSelectedPeerGroup().getPeerGroupID().toString(), tmpPresence);
            tmpPresence.addListener(this);
        }
        tmpPresence.setReplyPipe (localPipeAdv);
        return tmpPresence;
    }

    // Convert a doc to string
    private static String advToString(Advertisement adv) {
        
        StringWriter out = new StringWriter();
        MimeMediaType displayAs = new MimeMediaType("text/xml");
        
        try {
            StructuredTextDocument doc = (StructuredTextDocument) adv.getDocument(displayAs);
            doc.sendToWriter(out);
            return out.toString();
        } catch (Exception all) {
            return null;
        }
    }    
    
    // Get name of the user sending the ad.
    public String getName(PipeAdvertisement pipeAdv) {
        if (pipeAdv == null) 
                return null;
        String name = pipeAdv.getName();
        if (name != null) {
            return name.substring( CHATNAMETAG_.length() + 1 /* the dot */ );
        }
        return null;
    }

   // Get groups.
    private void recordGroup (PeerGroup group) {

        groups_.put (group.getPeerGroupID().toString(), group);
    }

    private PeerGroup getGroup (MessageElement groupId) {
	return (PeerGroup) groups_.get (groupId);
    }

    // Update groups if peer groups change.
    public synchronized void groupChanged(PeerGroup group) {

        super.groupChanged(group);
        currentGroup_= group;
        pipes_ = group.getPipeService();
        discovery_ = group.getDiscoveryService();
        try {
         Enumeration localAds = discovery_.getLocalAdvertisements(
                             DiscoveryService.ADV,
                             PipeAdvertisement.NameTag,
                             CHATNAMETAG_ +".*");

        java.util.Vector result = new  java.util.Vector();

        while (localAds != null && localAds.hasMoreElements() ) {
            PipeAdvertisement adv  = (PipeAdvertisement) localAds.nextElement();
            if (validAdvertisement(adv)) {
                result.add(adv);
            }
        }
        processPeerStructureChanged(new AdvertisementEvent( this,
                                                AdvertisementEvent.GRP_CHANGED,
                                                result.elements() ) );
        } catch (IOException e) {
          System.out.println("Error when groups updated.");
        }
        if (!initialized_) return;

        //Check if need to create a new chat session when groups change.
        if (!chatSession_.contains(group.getPeerGroupID())) {
          myName_ = manager_.getMyPeerName();
          login(myName_);
          chatSession_.addElement(group.getPeerGroupID());
        }

        recordGroup (group);
        PipePresence tmpp = getPipePresence(group, myPipeAdvt_);
        refreshNotify(null);
    }

    // Peer changed stub.
    public void peerChanged(PeerAdvertisement pgAdv) { }

    // Starts the chat - the old output pipe is closed, and/
    public void startChat(PipeAdvertisement toWhom) throws Exception {

        int index;
        PipeAdvertisement pipeAd = null;
        
        if (toWhom != null) {
         PeerGroup group = manager_.getSelectedPeerGroup();
         currentUser_ = getPipePresence (group, myPipeAdvt_).getOnlineBuddy (getName (toWhom));
            if (currentUser_ != null) {
                // This user is already an active user.
                toName_ = getName (toWhom);
                return;
            } 
            // This is not an online user. We cannot chat with this user.
            // However, we can probe it.
            probeUser (toWhom);
        }
    }

    // Ping Buddy to see if they are still out there.
    public static void pingBuddy (BuddyListBuilder buddy) {

        Message msg = null;//= pipes_.createMessage();
        /*
        msg.setString (SRCPIPEADV_, advToString (buddy.getReplyPipe()));
        msg.setString (SRCPEERADV_, myPeerAdvString_);
        msg.setString (GROUPID_, buddy.getGroup().getPeerGroupID().toString());
        msg.setString (COMMAND_, PING_);
        msg.setString (SENDERNAME_, myName_);
        */
        
        msg.setMessageProperty(SRCPIPEADV_, advToString (buddy.getReplyPipe()));
        msg.setMessageProperty(SRCPEERADV_, myPeerAdvString_);
        msg.setMessageProperty(GROUPID_, buddy.getGroup().getPeerGroupID().toString());
        msg.setMessageProperty(COMMAND_, PING_);
        msg.setMessageProperty(SENDERNAME_, myName_);
        // Send the message.
        buddy.sendMessage (msg);
    }

    
    // Sends back ad in the message.
    public static void sendAdInMessage (BuddyListBuilder buddy, Message msg) {
    	/*
        msg.setString (SRCPIPEADV_, advToString (buddy.getReplyPipe()));
        msg.setString (SRCPEERADV_, myPeerAdvString_);
        msg.setString (GROUPID_, buddy.getGroup().getPeerGroupID().toString());
        msg.setString (SENDERNAME_, myName_);
        */
    	
        msg.setMessageProperty(SRCPIPEADV_, advToString (buddy.getReplyPipe()));
        msg.setMessageProperty(SRCPEERADV_, myPeerAdvString_);
        msg.setMessageProperty(GROUPID_, buddy.getGroup().getPeerGroupID().toString());
        msg.setMessageProperty(SENDERNAME_, myName_);
    }

    // Probe users and add to peer list if exists.
    public void probeUser (PipeAdvertisement toWhom) {
        PeerGroup group = manager_.getSelectedPeerGroup();
        getPipePresence (group, myPipeAdvt_).addOnlineBuddy (getName (toWhom), toWhom);
    }

    // Log in and start server.
    void login(String loginName) 
    {
        myName_       = loginName;
        // start the server here
        runChatServer();
    }
  
    // Add a listener for user and message events.
    public synchronized void addListener(Listener listener) {
           if (!pipePListener_.contains(listener) ) {
                   pipePListener_.addElement(listener);
           }
    }

    // Remove listener for a user and message event set.
    public synchronized boolean removeListener(Listener listener) {
 
        return (pipePListener_.removeElement(listener));
    }
    

    // Attempts to find a user locally first, then search outside.
    public synchronized PipeAdvertisement findUser(
                 String name, 
                 DiscoveryService discovery )
    {
        int i = 0;

        PipeAdvertisement adv = findLocalUser (name, discovery);
        if (adv != null ) {
                return adv;
        }
        discovery.getRemoteAdvertisements(null,
                                        DiscoveryService.ADV,
                                        PipeAdvertisement.NameTag,
                                        "*." + name,
                                          1,
                                        null);
        while (true) {
            try {
                if (i > MAXRETRIES_) {
                    break;
                }
                // Start looking remotely.
                if (i > MAXRETRIES_/2 ) {
                          discovery.getRemoteAdvertisements(null,
                                        DiscoveryService.ADV,
                                        PipeAdvertisement.NameTag,
                                        "*." + name,
                                          1,
                                        null);
                }
                Thread.sleep(WaitingTime_);
                adv = findLocalUser (name, discovery);
                if (adv != null ) {
                        return adv;
                }
                i++;
            } catch (Exception e) {
            }
        }
        return findLocalUser(name, discovery);
    }

    // Look in the local cache for the user.
    private synchronized PipeAdvertisement findLocalUser(String name,
                           DiscoveryService discovery)
    {
        int i = 0;
        try {
        	Enumeration localAds = discovery.getLocalAdvertisements(DiscoveryService.ADV,
                                                    PipeAdvertisement.NameTag,
                                                    "*." + name);

            if ((localAds != null) && (localAds.hasMoreElements())) {
                PipeAdvertisement adv = null;
                while (localAds.hasMoreElements()) {
                    try {
                        adv = (PipeAdvertisement) localAds.nextElement();
                        return adv;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
        // Get all local users.
        private Enumeration findLocalUsers() {

        int i = 0;        
        try {
            return discovery_.getLocalAdvertisements(DiscoveryService.ADV,
                                                    PipeAdvertisement.NameTag,
                                                    CHATNAMETAG_+".*");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // sendMessageToPeers - sends message to listening peer(s).
    public void sendMessageToPeers(String message) {

        if (currentUser_ != null) {
            Message msg = null;
            ByteArrayInputStream ip = null;

            try {
                if (message == null) {
                    return;
                }
                //msg = pipes_.createMessage();
                ip = new ByteArrayInputStream(message.getBytes());
               msg.addMessageElement(SENDERNAME_, null);

                // push my Name
                //msg.setString (SENDERNAME_, myName_);
                msg.setMessageProperty(SENDERNAME_, myName_);
                
                // push my GroupName
                //msg.setString(
                msg.setMessageProperty(
                        SENDERGROUPNAME_,
                        manager_.getSelectedPeerGroup().getPeerGroupName());

                queue_.push(msg);

            } catch (Exception e) {
            	System.out.println("Failed to post message.");
            }
        }
    }

    // Runs chat server until message arrives on input pipe.
    public synchronized void runChatServer() {
        
    	// Runs chat.
        PeerGroup group = manager_.getSelectedPeerGroup();
        DiscoveryService tmpDiscovery = group.getDiscoveryService();
        PipeService tmpPipe = group.getPipeService();
        PeerGroupAdvertisement pgAdv;
        myPeerAdvt_ = group.getPeerAdvertisement();
        myPeerAdvString_ = advToString (myPeerAdvt_);

        myPipeAdvt_ = findUser(myName_, tmpDiscovery);

        if (myPipeAdvt_ == null) {
            // Create a pipe advertisement for this pipe.
            myPipeAdvt_ = (PipeAdvertisement)
                    AdvertisementFactory.newAdvertisement(
                    PipeAdvertisement.getAdvertisementType());

            myPipeAdvt_.setPipeID(IDFactory.newPipeID((PeerGroupID) group.getPeerGroupID()));
            myPipeAdvt_.setName(CHATNAMETAG_ + "." + myName_);
            myPipeAdvt_.setType(PipeService.UnicastSecureType);
        }
        // Convert prev registered Unicast type to secure
        myPipeAdvt_.setType(PipeService.UnicastSecureType);
        myPipeAdvString_ = advToString (myPipeAdvt_);

        try {
         publishAdvertisement(myPipeAdvt_, tmpDiscovery);
         inputPipe_ = tmpPipe.createInputPipe(myPipeAdvt_, this);
         
         //Store user input pipe information.
         inputPipes_.addElement(inputPipe_);
         probeUser (myPipeAdvt_);
        } catch (IOException e)  {
             System.out.println("error logging in.");
        }
    }

    // Publisher for ads.
    public synchronized void publishAdvertisement(Advertisement adv,
              DiscoveryService discovery ) throws IOException {

         discovery.publish(adv);
         discovery.publish(adv, 
                discovery.ADV,
                discovery.DEFAULT_LIFETIME);
                
         discovery.remotePublish(adv, 
                discovery.ADV);
    }
    
    // Ends chat server on logout.
    public synchronized void logout() {
        // Not implemented yet.
    }

    // Advertisement discovery method stub.
    public void advertisementsDiscovered(AdvertisementEvent e) {
        processPeerStructureChanged(e);
    }

    // Adds listener for peer groups structure listeners.
    public void addStructureListener(GroupStructureListener listener) {
        structureListeners_.addElement(listener);
    }

    // Removes listeners for Group and Peer  structure
    public void removeStructureListener(GroupStructureListener listener) {
        structureListeners_.removeElement(listener);
    }

    // Finds the PipeAdvertisement for the selected name.
    public PipeAdvertisement getPipeAdvertisement(String name) {

        Enumeration ads = getAdvertisements();
        if ( ads == null ) {
            return null;
        }
        PipeAdvertisement pipeAd = null;
        PipeAdvertisement tmp;
        while (ads.hasMoreElements()) {
            tmp = (PipeAdvertisement) ads.nextElement();
            if (tmp.getName().endsWith(name)) {
                pipeAd = tmp;
                break;
            }
        }
        return pipeAd;
    }

    // Searches ads.
    public void searchAdvertisements()
             throws IOException {
        discovery_.getRemoteAdvertisements(
                        null, 
                        DiscoveryService.ADV,
                        PipeAdvertisement.NameTag, 
                        CHATNAMETAG_ +".*", 
                        DiscoveryClientServer.THRESHOLD_,
                        null);

        // Get local  values.
        Enumeration localAds = discovery_.getLocalAdvertisements(
                             DiscoveryService.ADV,
                             PipeAdvertisement.NameTag,
                             CHATNAMETAG_ +".*");

        java.util.ArrayList result = new  java.util.ArrayList();

        while (localAds != null && localAds.hasMoreElements() ) {
                PipeAdvertisement adv  = (PipeAdvertisement) localAds.nextElement();
                if (validAdvertisement(adv)) {
                    addAdToList(adv,result);
                }
        }

        // Inform registered listeners.
        processAdDiscovered( new AdvertisementEvent(this,
                            AdvertisementEvent.ADV_ADDED,
                            result) );
    }

    // Retrieves the content of the message labeled by tag
    protected byte[] getTagValue(Message msg, String tag) throws Exception {
        byte[] buffer = null;
        MessageElement elem = msg.getMessageElement (tag);
        // Remove the element from the message
        if ( elem != null ) {
        msg.removeMessageElement (elem);
        InputStream ip = elem.getStream();
        int res;

        if (ip != null) {
            buffer = new byte[ip.available()];
            ip.read(buffer);
        }
        return buffer; 
        } else {
                return null;
        }
    }

    // Retrieves the content of the message labeled by tag.
    protected String getTagString(Message msg, String tag, String defaultValue)
             throws Exception {
        byte[] buffer = getTagValue(msg, tag);
        String result;

        if (buffer != null) {
            result = new String(buffer);
        } else {
            result = defaultValue;
        }

        return result;
    }


    // Compares two advertisements for equality.
    protected boolean compareAds(Advertisement ad1,
            Advertisement ad2) {
        
        if ( validAdvertisement(ad1) ) {
         if (ad1 != null && ad2 != null) {
            PipeAdvertisement p1 = (PipeAdvertisement) ad1;
            PipeAdvertisement p2 = (PipeAdvertisement) ad2;
            if (p1.getPipeID().equals(p2.getPipeID())) {
              return true;
            }
         }
        }
        return false;
    }

    // Valid advertisement checker.
    public boolean validAdvertisement(Advertisement adv) {
        PipeAdvertisement pipeAdv;

        if (adv instanceof PipeAdvertisement) {
            pipeAdv = (PipeAdvertisement) adv;
            String str = pipeAdv.getName();
            String type = pipeAdv.getType();
            if ( str.startsWith(CHATNAMETAG_+".") &&
               !( type.equals(PipeService.PropagateType)) ) {
                return true;
            }
        }
        return false;
    }

   

    public void presenceEvent( PipePresence.PresenceEvent event ) {

        // We filter out disconnect even from the local user
        if (event.getName().equals (myName_) &&
            !event.getStatus()) {
            // Ignore the event
            return;
        }
        if (pipePListener_ != null) {
            // this can happen as the method may be called from the
            // constructor in the super class
            for (int i = 0; i < pipePListener_.size(); i++) {
              Listener l = (Listener) pipePListener_.elementAt(i);
              l.userEvent( event );
            }
        }
    }

       public void refreshNotify( Enumeration notifications ) {

        if (pipePListener_ != null) {
            for (int i = 0; i < pipePListener_.size(); i++) {
              Listener l = (Listener) pipePListener_.elementAt(i);
              l.refreshEvent( notifications );
            }
        }
    }

    // Called if the peer structure changes.
    private void processPeerStructureChanged(AdvertisementEvent e) {
        if (structureListeners_ != null) {
            // this can happen as the method may be called from the
            // constructor in the super class
            for (int i = 0; i < structureListeners_.size(); i++) {
              GroupStructureListener l = (GroupStructureListener) structureListeners_.elementAt(i);
              l.peerDataChanged(e);
            }
        }
    }


	@Override
	public void discoveryEvent(DiscoveryEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pipeMsgEvent(PipeMsgEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}


