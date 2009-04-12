package org.whisperim.JXTA_P2P;

import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.Vector;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import net.jxta.pipe.*;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.document.Advertisement;
import net.jxta.discovery.DiscoveryService;
import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.exception.*;
import net.jxta.peergroup.*;
import net.jxta.protocol.*;
import net.jxta.document.*;
import net.jxta.id.ID;
import net.jxta.id.IDFactory;
import net.jxta.endpoint.*;
import net.jxta.impl.endpoint.EndpointReceiveQueue;

// Implements the actual chat.
public class Chat extends DiscoveryClientServer implements 
        DiscoveryListener,
        PipeMsgListener,
        Runnable,
        PipePresence.Listener,
        AdvertisementDiscoveryListener {

    /** The eol to use */
    private static String EOL = System.getProperty("line.separator");
    public final static String CHATNAMETAG = "JxtaTalkUserName";
    public final static String SENDERNAME = "JxtaTalkSenderName";
    public final static String SENDERGROUPNAME = "GrpName";
    public final static String SENDERMESSAGE = "JxtaTalkSenderMessage";
    public final static String SRCPEERADV = "JxtaTalkSrcPeerAdv";
    public final static String SRCPIPEADV = "JxtaTalkSrcPipeAdv";
    public final static String GROUPID = "JxtaTalkGroupId";
    public final static String COMMAND = "JxtaTalkCommand";
    public final static String PING = "Ping";
    public final static String ACK = "Ack";

    protected final static int PipeTimeout = 50000;
    private final static int WaitingTime = 2000;
    private final static int MAXRETRIES = 10;
    private boolean groupChanged = false;
    private EndpointReceiveQueue queue = new EndpointReceiveQueue();

    // The mime type of the Advertisements we send out
    private final static MimeMediaType XMLMIMETYPE = new MimeMediaType("text/xml");

    // Pipes
    protected PipeAdvertisement myPipeAdvt = null;
    protected PeerAdvertisement myPeerAdvt = null;
    private String myPipeAdvString = null;
    private String myPeerAdvString = null;
    protected InputPipe inputPipe = null;
    protected PipeService pipes = null;
    protected PeerGroup currentGroup = null;
    protected Vector structureListeners = new Vector();
    protected Vector pipePListener = new Vector();
    protected Vector inputPipes = new Vector();

    // Peer Group Manager.
    protected PeerGroupManager manager = null;

    // Buddy List user being messaged
    protected BuddyListBuilder currentUser = null;

    // DiscoveryService service
    protected DiscoveryService discovery = null;

    // Chat stuff.
    protected boolean chatInProgress = false;
    protected String toName;
    protected String myName;
    private static Vector chatSession = new Vector(10);
    private static Hashtable pipePTable = new Hashtable();
    private static Hashtable groups = new Hashtable();
    private static Vector messages = new Vector(10);
    
    // Initialized.
    private boolean initialized = false;
	  
    
    public interface Listener extends EventListener {

       void userEvent( PipePresence.PresenceEvent event );
       void refreshEvent(Enumeration event);
    }


    // Chat constructor.
    public Chat(PeerGroupManager manager) {

        this(manager, PipeAdvertisement.NameTag, CHATNAMETAG +".*");
    }

    // Chat constructor.
    protected Chat(PeerGroupManager manager,
                   String attr,
                   String value) {

        super(manager.getSelectedPeerGroup(),
                DiscoveryService.ADV,
                attr,
                value);

        this.manager = manager;

        // We use a thread just to let this happen in background
        Thread thread = new Thread(this, "Chat Thread");
        thread.start();
    }

    // Run chat.
    public void run() {

        manager.addPeerListener(this);
        addListener(this);
        currentGroup = manager.getSelectedPeerGroup();
        pipes = currentGroup.getPipeService();
        recordGroup (currentGroup);

        discovery = currentGroup.getDiscoveryService();
        myName = manager.getMyPeerName();
        chatSession.addElement(currentGroup.getPeerGroupID());
        login(myName);

        getPipePresence (currentGroup, myPipeAdvt);
        initialized = true;
        
          while (true) {
            try {
             Message msg = queue.waitForMessage();
             boolean res = currentUser.sendMessage(msg);
            } catch (InterruptedException e) {
               // we should never get this
               // so printStackTrace();
               e.printStackTrace();
            }
            
          }        
    }

    private PipePresence getPipePresence (PeerGroup g, PipeAdvertisement localPipeAdv) {

        PipePresence tmpPresence = (PipePresence) pipePTable.get (g.getPeerGroupID().toString());
        if (tmpPresence == null) {
            // No PipePresence has been created yet.
            tmpPresence = new PipePresence (g, localPipeAdv);
            pipePTable.put(manager.getSelectedPeerGroup().getPeerGroupID().toString(), tmpPresence);
            tmpPresence.addListener(this);
        }
        tmpPresence.setReplyPipe (localPipeAdv);
        return tmpPresence;
    }

    // Convert a doc to string
    private String advToString(Advertisement adv) {
        
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
            return name.substring( CHATNAMETAG.length() + 1 /* the dot */ );
        }
        return null;
    }

   // Get groups.
    private void recordGroup (PeerGroup group) {

        groups.put (group.getPeerGroupID().toString(), group);
    }

    private PeerGroup getGroup (String pgid) {
	return (PeerGroup) groups.get (pgid);
    }

    // Update groups if peer groups change.
    public synchronized void groupChanged(PeerGroup group) {

        super.groupChanged(group);
        currentGroup= group;
        pipes = group.getPipeService();
        discovery = group.getDiscoveryService();
        try {
         Enumeration localAds = discovery.getLocalAdvertisements(
                             DiscoveryService.ADV,
                             PipeAdvertisement.NameTag,
                             CHATNAMETAG +".*");

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
        if (!initialized) return;

        //Check if need to create a new chat session when groups change.
        if (!chatSession.contains(group.getPeerGroupID())) {
          myName = manager.getMyPeerName();
          login(myName);
          chatSession.addElement(group.getPeerGroupID());
        }

        recordGroup (group);
        PipePresence tmpp = getPipePresence(group, myPipeAdvt);
        refreshNotify(null);
    }

    // Peer changed stub.
    public void peerChanged(PeerAdvertisement pgAdv) { }

    // Starts the chat - the old output pipe is closed, and/
    public void startChat(PipeAdvertisement toWhom) throws Exception {

        int index;
        PipeAdvertisement pipeAd = null;
        
        if (toWhom != null) {
         PeerGroup group = manager.getSelectedPeerGroup();
         currentUser = getPipePresence (group, myPipeAdvt).getOnlineBuddy (getName (toWhom));
            if (currentUser != null) {
                // This user is already an active user.
                toName = getName (toWhom);
                return;
            } 
            // This is not an online user. We cannot chat with this user.
            // However, we can probe it.
            probeUser (toWhom);
        }
    }

    // Ping Buddy to see if they are still out there.
    public void pingBuddy (BuddyListBuilder buddy) {

        Message msg = pipes.createMessage();

        msg.setString (SRCPIPEADV, advToString (buddy.getReplyPipe()));
        msg.setString (SRCPEERADV, myPeerAdvString);
        msg.setString (GROUPID, buddy.getGroup().getPeerGroupID().toString());
        msg.setString (COMMAND, PING);
        msg.setString (SENDERNAME, myName);
        // Send the message.
        buddy.sendMessage (msg);
    }

    
    // Sends back ad in the message.
    public void sendAdInMessage (BuddyListBuilder buddy, Message msg) {

        msg.setString (SRCPIPEADV, advToString (buddy.getReplyPipe()));
        msg.setString (SRCPEERADV, myPeerAdvString);
        msg.setString (GROUPID, buddy.getGroup().getPeerGroupID().toString());
        msg.setString (SENDERNAME, myName);
    }

    // Probe users and add to peer list if exists.
    public void probeUser (PipeAdvertisement toWhom) {
        PeerGroup group = manager.getSelectedPeerGroup();
        getPipePresence (group, myPipeAdvt).addOnlineBuddy (getName (toWhom), toWhom);
    }

    // Log in and start server.
    void login(String loginName) 
    {
        myName       = loginName;
        // start the server here
        runChatServer();
    }
  
    // Add a listener for user and message events.
    public synchronized void addListener(Listener listener) {
           if (!pipePListener.contains(listener) ) {
                   pipePListener.addElement(listener);
           }
    }

    // Remove listener for a user and message event set.
    public synchronized boolean removeListener(Listener listener) {
 
        return (pipePListener.removeElement(listener));
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
                if (i > MAXRETRIES) {
                    break;
                }
                // Start looking remotely.
                if (i > MAXRETRIES/2 ) {
                          discovery.getRemoteAdvertisements(null,
                                        DiscoveryService.ADV,
                                        PipeAdvertisement.NameTag,
                                        "*." + name,
                                          1,
                                        null);
                }
                Thread.sleep(WaitingTime);
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
            return discovery.getLocalAdvertisements(DiscoveryService.ADV,
                                                    PipeAdvertisement.NameTag,
                                                    CHATNAMETAG+".*");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // sendMessageToPeers - sends message to listening peer(s).
    public void sendMessageToPeers(String message) {

        if (currentUser != null) {
            Message msg = null;
            InputStream ip = null;

            try {
                if (message == null) {
                    return;
                }
                msg = pipes.createMessage();
                ip = new ByteArrayInputStream(message.getBytes());
                msg.addElement(msg.newMessageElement (
                                        SENDERMESSAGE,
                                        null,
                                        ip));

                // push my Name
                msg.setString (SENDERNAME, myName);

                // push my GroupName
                msg.setString(
                        SENDERGROUPNAME,
                        manager.getSelectedPeerGroup().getPeerGroupName());

                queue.push(msg);

            } catch (Exception e) {
            	System.out.println("Failed to post message.");
            }
        }
    }

    // Runs chat server until message arrives on input pipe.
    public synchronized void runChatServer() {
        
    	// Runs chat.
        PeerGroup group = manager.getSelectedPeerGroup();
        DiscoveryService tmpDiscovery = group.getDiscoveryService();
        PipeService tmpPipe = group.getPipeService();
        PeerGroupAdvertisement pgAdv;
        myPeerAdvt = group.getPeerAdvertisement();
        myPeerAdvString = advToString (myPeerAdvt);

        myPipeAdvt = findUser(myName, tmpDiscovery);

        if (myPipeAdvt == null) {
            // Create a pipe advertisement for this pipe.
            myPipeAdvt = (PipeAdvertisement)
                    AdvertisementFactory.newAdvertisement(
                    PipeAdvertisement.getAdvertisementType());

            myPipeAdvt.setPipeID(IDFactory.newPipeID((PeerGroupID) group.getPeerGroupID()));
            myPipeAdvt.setName(CHATNAMETAG + "." + myName);
            myPipeAdvt.setType(PipeService.UnicastSecureType);
        }
        // Convert prev registered Unicast type to secure
        myPipeAdvt.setType(PipeService.UnicastSecureType);
        myPipeAdvString = advToString (myPipeAdvt);

        try {
         publishAdvertisement(myPipeAdvt, tmpDiscovery);
         inputPipe = tmpPipe.createInputPipe(myPipeAdvt, this);
         
         //Store user input pipe information.
         inputPipes.addElement(inputPipe);
         probeUser (myPipeAdvt);
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
    
    public void pipeMsgEvent(PipeMsgEvent event) {

            Message msg = event.getMessage();
            processMessage(msg);
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
        structureListeners.addElement(listener);
    }

    // Removes listeners for Group and Peer  structure
    public void removeStructureListener(GroupStructureListener listener) {
        structureListeners.removeElement(listener);
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
        discovery.getRemoteAdvertisements(
                        null, 
                        DiscoveryService.ADV,
                        PipeAdvertisement.NameTag, 
                        CHATNAMETAG +".*", 
                        DiscoveryClientServer.THRESHOLD,
                        null);

        // Get local  values.
        Enumeration localAds = discovery.getLocalAdvertisements(
                             DiscoveryService.ADV,
                             PipeAdvertisement.NameTag,
                             CHATNAMETAG +".*");

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
            if ( str.startsWith(CHATNAMETAG+".") &&
               !( type.equals(PipeService.PropagateType)) ) {
                return true;
            }
        }
        return false;
    }

    // Processes a message
    protected void processMessage(Message msg) {
        String messageID;
        byte[] buffer = null;

        String srcPeerAdvWireFormat = msg.getString (SRCPEERADV);
        PeerAdvertisement srcPeerAdv = null;
        try {
            if (srcPeerAdvWireFormat != null) {
                srcPeerAdv = (PeerAdvertisement) AdvertisementFactory.newAdvertisement(
                                              new MimeMediaType("text/xml"),
                                              new ByteArrayInputStream(srcPeerAdvWireFormat.getBytes()));
                
                discovery.publish(srcPeerAdv, DiscoveryService.PEER);
            }
        } catch (Exception e) {
        }

        String srcPipeAdvWireFormat = msg.getString (SRCPIPEADV);
        PipeAdvertisement srcPipeAdv = null;
        try {
            if (srcPipeAdvWireFormat != null) {
                srcPipeAdv = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(
                                new MimeMediaType("text/xml"),
                                new ByteArrayInputStream(srcPipeAdvWireFormat.getBytes()));
                
                discovery.publish(srcPipeAdv, DiscoveryService.ADV);
            }
        } catch (Exception e) {
        }

        String groupId = msg.getString (GROUPID);
        PeerGroup group = null;
        if (groupId != null) {
            group = getGroup (groupId);
        }

        String        sender = null;
        String     groupname = null;
        String senderMessage = null;
        // Get sender information
        try {
                sender = getTagString(msg, SENDERNAME, "anonymous");
                groupname = getTagString(msg, SENDERGROUPNAME, "unknown");
                senderMessage = getTagString(msg, SENDERMESSAGE, null);

                String msgstr;
                if (groupname.equals(manager.getSelectedPeerGroup().getPeerGroupName()) ) {
                     //message is from this group
                     msgstr = sender + "> " + senderMessage +EOL ;   
                } else {
                     msgstr = sender + "@" + groupname + "> " + senderMessage +EOL;   
                }                
                
                if (senderMessage != null) {
                }
                // If there is a PipeAdvertisement piggy backed into the message
                // create a new buddy.
                if ((srcPipeAdv != null) && (group != null)) {
                    PipePresence p  =  getPipePresence (group, myPipeAdvt);
                    if (p != null) {
                        p.addOnlineBuddy (sender, srcPipeAdv);
                    }
                }
        } catch (Exception e) {
           System.out.println("Error processing message.");
        }
        // Process any Chat commands

        String cmd = msg.getString (COMMAND);
        if (cmd == null) {
            // Nothing to do
            return;
        }
        if (cmd.equals (PING) && (group != null)) {
            // This is a PING request. We need to reply ACK
            OutputPipe op = null;
            Vector dstPeers = new Vector (1);
            dstPeers.add (srcPeerAdv.getPeerID());
            try {
                op = group.getPipeService().createOutputPipe (srcPipeAdv,
                                     PipeTimeout);
                if (op != null) {
                    // Send the ACK
                    Message rep = pipes.createMessage();
                    rep.setString (COMMAND, ACK);
                    rep.setString (GROUPID, groupId);
                    rep.setString (SENDERNAME, myName);
                    op.send (rep);
                } else {
                }
            } catch (Exception ez1) {
                // We can't reply. Too bad...
            }
        }

        if (cmd.equals (ACK) && (group != null)) {
            // This is a ACK reply. Get the appropriate PipePresence
            PipePresence p  =  getPipePresence (group, myPipeAdvt);
            if (p != null) {
                p.processAck (sender);
            }
        }
    }

    public void presenceEvent( PipePresence.PresenceEvent event ) {

        // We filter out disconnect even from the local user
        if (event.getName().equals (myName) &&
            !event.getStatus()) {
            // Ignore the event
            return;
        }
        if (pipePListener != null) {
            // this can happen as the method may be called from the
            // constructor in the super class
            for (int i = 0; i < pipePListener.size(); i++) {
              Listener l = (Listener) pipePListener.elementAt(i);
              l.userEvent( event );
            }
        }
    }

       public void refreshNotify( Enumeration notifications ) {

        if (pipePListener != null) {
            for (int i = 0; i < pipePListener.size(); i++) {
              Listener l = (Listener) pipePListener.elementAt(i);
              l.refreshEvent( notifications );
            }
        }
    }

    // Called if the peer structure changes.
    private void processPeerStructureChanged(AdvertisementEvent e) {
        if (structureListeners != null) {
            // this can happen as the method may be called from the
            // constructor in the super class
            for (int i = 0; i < structureListeners.size(); i++) {
              GroupStructureListener l = (GroupStructureListener) structureListeners.elementAt(i);
              l.peerDataChanged(e);
            }
        }
    }


	@Override
	public void discoveryEvent(DiscoveryEvent arg0) {
		// TODO Auto-generated method stub
		
	}


}


