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

import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownServiceException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.lang.Object;

import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.Element;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.StructuredTextDocument;
import net.jxta.document.TextElement;
import net.jxta.id.ID;
import net.jxta.id.IDFactory;
import net.jxta.impl.membership.PasswdMembershipService;
import net.jxta.impl.peergroup.StdPeerGroupParamAdv;
import net.jxta.membership.MembershipService;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.ModuleClassID;
import net.jxta.platform.ModuleSpecID;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.protocol.PeerAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.rendezvous.RendezVousService;

// Manages peer groups.
public class PeerGroupManager implements AdvertisementDiscoveryListener {

	// Interface to deal with peer changes.
	public interface PeerListener {
		
		// Called if peer group was changed.
		public void groupChanged ( PeerGroup peergroup );

		// Called if single peer is changed.
		public void peerChanged ( PeerAdvertisement pgAdv );
	}

	// Thread for work.
    protected static WorkerThread worker_ = null;
    
    // The identifiers in the preference file.
    private final static String JOINED = ".JoinedGroups";
    private final static String JOINEDRDV = ".JoinedRdvGroups";
    private final static String SELECTED_PEER = ".SelectedPeer";
    private final static String SELECTED_PEER_GROUP = ".SelectedPeerGroup";
    private final static String EOL = System.getProperty("line.separator");
    
    // Timer for sleep loop.
    private final static int RENDEZVOUS_LOOP_SLEEP = 1000;
    
    // Peer and group members.
    private Vector myGroups_ = new Vector(10);   
    private Vector myGroupAdvs_ = new Vector(10);
    private Vector myGroupRdv_ = new Vector(10);
    private Vector listeners_ = new Vector();
    private Vector structureListeners_ = new Vector();
    private PeerGroup currentGroup_ = null;
    private PeerAdvertisement currentPeer_ = null;
    private DiscoveryClientServer peerDiscover_;
    private DiscoveryClientServer groupDiscover_;
    public PeerGroup rootPeerGroup_;
    
    // Blocking the RendzVous. 
    private boolean blockRendezVousWait_ = false;
    
    // Creates a new PeerGroupSearcher instance
    public PeerGroupManager(PeerGroup group) {
        currentGroup_ = group;
        rootPeerGroup_ = group;
        
        myGroups_.addElement(group);
        myGroupAdvs_.addElement(group.getPeerGroupAdvertisement());
        myGroupRdv_.addElement(new Boolean(false));
        
        // Start the remote discovery.
        peerDiscover_ = new InternalDiscover(group, DiscoveryService.PEER);
        groupDiscover_ = new InternalDiscover(group, DiscoveryService.GROUP);
        peerDiscover_.addListener(this);
        groupDiscover_.addListener(this);
    }
    
    // Returns list of peers found.
    public Enumeration getPeerList() {
        return peerDiscover_.getAdvertisements();
    }
    
    // Returns a list of peer groups this peer is currently a member of
    public Enumeration getJoinedGroups() {
        return myGroupAdvs_.elements();
    }
    
    // Returns a list of currently available peer groups
    public Enumeration getGroups() {
        Enumeration ads = groupDiscover_.getAdvertisements();
        Vector valid = new Vector(10);
        
        while (ads != null && ads.hasMoreElements()) {
            PeerGroupAdvertisement adv = (PeerGroupAdvertisement) ads.nextElement();
            
            // We don't add jxta wire groups
            String nm = adv.getName();
            if (nm == null) nm = "";
            if (nm.indexOf("JxtaWire") == -1) valid.addElement(adv);
        }
        
        return valid.elements();
    }
    
    // Returns a list of currently available peer groups minus joined groups
    public Enumeration getGroupsLeft() {
        
        Vector groupsNotIn = new Vector();
        
        Enumeration all = getGroups();
        
        // Substract the ones that are joined. Can't use removeAll() nor
        // contains() because we need to manually compare advs. Not the
        // same objects !=> different; even if the text of the adv differs
        // slightly they may be the same. Only group ID counts.
        
        while (all.hasMoreElements() ) {
            PeerGroupAdvertisement adv =
            (PeerGroupAdvertisement) all.nextElement();
            
            if( -1 == getIndexOfAdv( myGroupAdvs_, adv ) )
                groupsNotIn.add(adv);
        }
        
        return groupsNotIn.elements();
    }
    
    // Returns the advertisement of the currently selected peer,
    public PeerAdvertisement getSelectedPeer() {
        return currentPeer_;
    }
    
    //Returns the advertisement of the currently selected peer group
    public PeerGroup getSelectedPeerGroup() {
        
        if( !myGroups_.contains(currentGroup_) )
            throw new RuntimeException( "Selected group is not one of the joined groups!" );
        
        return currentGroup_;
    }
    
    //  Returns the name of the peer.
    public String getMyPeerName() {
        return rootPeerGroup_.getPeerName();
    }
    
    // WorkerThread is used to deal with blocking issues.
    public WorkerThread getWorkerThread() {
        if (worker_ == null) {
            worker_ = new WorkerThread();
            worker_.setPriority(worker_.getPriority() - 1);
        }
        return worker_;
    }

    // Called if new advertisements are discovered
    public void advertisementsDiscovered(AdvertisementEvent event) {
        if (event.getSource() == groupDiscover_) {
            processGroupStructureChanged(event);
        } else {
            processPeerStructureChanged(event);
        }
    }
    
    // Searches for groups by name.
    public void searchGroups(String groupName) {
        try {
            if (groupName == null || groupName.trim().equals("")) {
                groupDiscover_.searchAdvertisements(null, null);
            } else {
                groupDiscover_.searchAdvertisements("Name", groupName);
            }
        } catch (Exception iox) {
            StringBuffer buffer = new StringBuffer();
            
            buffer.append(
            "Could not retrieve the local peer group advertisements");
            if (iox.getMessage() != null) {
                buffer.append(EOL).append(iox.getMessage());
            }
        }
    }
    
    // Search by Peer name.
    public void searchPeers(String peerName) {
        try {
            if (peerName == null || peerName.trim().equals("")) {
                peerDiscover_.searchAdvertisements(null, null);
            } else {
                peerDiscover_.searchAdvertisements("PeerName", peerName);
            }
        } catch (Exception iox) {
            StringBuffer buffer = new StringBuffer();
            
            buffer.append(
            "Could not retrieve the local peer advertisements");
            if (iox.getMessage() != null) {
                buffer.append(EOL).append(iox.getMessage());
            }
        }
    }
    
    // Add peer listener for changes
    public void addPeerListener(PeerGroupManager.PeerListener listener) {
        listeners_.addElement(listener);
    }
    
    // Removes a peer listener.
    public void removePeerListener(PeerGroupManager.PeerListener listener) {
        listeners_.removeElement(listener);
    }
   
    // Adds listener for peer groups.
    public void addStructureListener(GroupStructureListener listener) {
        structureListeners_.addElement(listener);
    }
    
    // Remove group listener.
    public void removeStructureListener(GroupStructureListener listener) {
        structureListeners_.removeElement(listener);
    }
    
    // Called if selected peer changed.
    public void selectedPeerChanged(PeerAdvertisement adv) {
        PreferenceReader prefs = PreferenceReader.getInstance();
        
        if (adv == currentPeer_) {
            return;
        }
        currentPeer_ = adv;
        for (int i = 0; i < listeners_.size(); i++) {
            ((PeerListener) listeners_.elementAt(i)).peerChanged(adv);
        }
        
        if (adv != null) {
            adv.getPeerGroupID().toString();
        }
    }
    
    // Called if the currently selected group is to be changed.
    public void selectedGroupChanged(PeerGroupAdvertisement adv) {
        if( null == adv )
            return;
        
        int index = getIndexOfAdv( myGroupAdvs_, adv );
        
        if( -1 == index ) {
        	// Bail on joining.
            return;
        }
        
        // Tries to join group twice.
        if ( adv.getPeerGroupID().equals(currentGroup_.getPeerGroupID()) ) {
            return;
        }
        
        currentGroup_ = (PeerGroup) myGroups_.elementAt(index);
        
        peerDiscover_.groupChanged(currentGroup_);
        groupDiscover_.groupChanged(currentGroup_);
        
        Enumeration peerList = getPeerList();
        if (peerList.hasMoreElements()) {
            currentPeer_ = (PeerAdvertisement) peerList.nextElement();
        } else {
            currentPeer_ = null;
        }
        
        for (int i = 0; i < listeners_.size(); i++) {
            PeerListener l = (PeerListener) listeners_.elementAt(i);
            l.groupChanged(currentGroup_);
            l.peerChanged(currentPeer_);
        }
       
        PreferenceReader prefs = PreferenceReader.getInstance();
        
        prefs.setProperty(getClass().getName() + SELECTED_PEER_GROUP,
        adv.getPeerGroupID().toString());
    }
    
    public void savePreferences() {
        PreferenceReader.getInstance().save();
    }
    
    // Called if the user wants to join a  group.
    public void joinGroup( PeerGroup pg, PeerGroupAdvertisement pgAdv,
    boolean isRendezVous) throws Exception {
        
        // Set to join.
        isRendezVous = true;
        try {
            if (getIndexOfAdv( myGroupAdvs_, pgAdv ) != -1) {
                throw new RuntimeException( pgAdv.getName() + " has already been joined." );
            }

            // Update the vectors.
            myGroups_.addElement(pg);
            myGroupAdvs_.addElement(pgAdv);
            myGroupRdv_.addElement(new Boolean(isRendezVous));
            
            // Change the current group.
            selectedGroupChanged(pgAdv);
            
            // Update the prefs file.
            updateJoinedGroups();
            
            processGroupStructureChanged(new AdvertisementEvent(this,AdvertisementEvent.ALL_ADV_DELETED, (List) null));
            
            // At this point we have instantiated the group, but not joined it.
            // Our only identity within the group is "nobody"
            
        } catch (Exception iox) {
                System.out.println( "Group could not be joined" ); 
            }
        }
    
    
    
    // Creates a named new group.
    public void createPeerGroup(String name, boolean isRendezVous, String password ) {
        
        try {
            // Instantiate group from the impl adv. 
            ModuleImplAdvertisement newGroupImpl =
            rootPeerGroup_.getAllPurposePeerGroupImplAdvertisement();
            
            if( null != password ) {
                StdPeerGroupParamAdv params = new StdPeerGroupParamAdv(
                newGroupImpl.getParam() );
                
                boolean replacedMembership = false;
                // Hashtable services = params.getServices();
                Map<ModuleClassID, Object> services = params.getServices();
                
                
                //for( Enumeration eachModule = services.keys();
                for( Enumeration eachModule = (Enumeration) services.keySet();
                eachModule.hasMoreElements(); ) {
                    
                    Object aModuleClassID = eachModule.nextElement();
                    
                    // Find the membership service
                    if ( aModuleClassID.equals(PeerGroup.membershipClassID) ) {
                        ModuleImplAdvertisement aModuleAdv =
                        (ModuleImplAdvertisement) services.get(aModuleClassID);
                        services.remove( aModuleClassID );
                        
                        ModuleImplAdvertisement implAdv = (ModuleImplAdvertisement)
                        AdvertisementFactory.newAdvertisement(
                        ModuleImplAdvertisement.getAdvertisementType());
                        
                        implAdv.setModuleSpecID(PasswdMembershipService.passwordMembershipSpecID);
                        implAdv.setCompat(aModuleAdv.getCompat() );
                        implAdv.setCode( PasswdMembershipService.class.getName() );
                        implAdv.setUri(aModuleAdv.getUri());
                        implAdv.setProvider(aModuleAdv.getProvider());
                        implAdv.setDescription("Password Membership Service");
                        
                        // replace it
                        services.put( PeerGroup.membershipClassID, implAdv );
                        
                        newGroupImpl.setParam(
                        (Element) params.getDocument(
                        new MimeMediaType("text/xml")) );
                        
                        replacedMembership = true;
                        break;
                    }
                }
                
                if ( replacedMembership ) {
                    newGroupImpl.setParam( (Element)
                    params.getDocument( new MimeMediaType("text/xml") ) );
                    
                    if( !newGroupImpl.getModuleSpecID().equals(
                    PeerGroup.allPurposePeerGroupSpecID) ) {
                        newGroupImpl.setModuleSpecID(
                        IDFactory.newModuleSpecID(
                        newGroupImpl.getModuleSpecID().getBaseClass() ) );
                    } else {
                        ID passwdGrpModSpecID = ID.nullID;
                        
                        try {
                            passwdGrpModSpecID = IDFactory.fromURL(
                            new URL( "urn", "",
                            "jxta:uuid-" + "DeadBeefDeafBabaFeedBabe00000001" +
                            "04" +
                            "06" ) );
                        } catch (MalformedURLException absurd) {
                            // Fall through.
                        } catch (UnknownServiceException absurd2) {
                            // Fall through.
                        }
                        
                        newGroupImpl.setModuleSpecID( (ModuleSpecID) passwdGrpModSpecID );
                    }
                }
            }
            
            rootPeerGroup_.getDiscoveryService().publish(newGroupImpl,
            DiscoveryService.ADV,
            PeerGroup.DEFAULT_LIFETIME);
            
            rootPeerGroup_.getDiscoveryService().remotePublish(newGroupImpl,
            DiscoveryService.ADV );
            
            PeerGroupAdvertisement newPGAdv = (PeerGroupAdvertisement)
            AdvertisementFactory.newAdvertisement(
            PeerGroupAdvertisement.getAdvertisementType());
            
            newPGAdv.setPeerGroupID(IDFactory.newPeerGroupID());
            newPGAdv.setModuleSpecID(newGroupImpl.getModuleSpecID());
            newPGAdv.setName(name);
            newPGAdv.setDescription("Created by myJXTA");
            if( null != password ) {
                StructuredTextDocument params = (StructuredTextDocument)
                StructuredDocumentFactory.newStructuredDocument(
                new MimeMediaType( "text/xml" ), "Parm" );
                
                TextElement param =  params.createElement( "Parm" );
                params.appendChild( param );
                
                String passwrdlist = "myjxtauser:" +
                PasswdMembershipService.makePsswd(password) +
                ":";
                
                TextElement login =  params.createElement( "login", passwrdlist );
                param.appendChild( login );
                
                newPGAdv.putServiceParam( PeerGroup.membershipClassID, param );
            }
            
            rootPeerGroup_.getDiscoveryService().publish(newPGAdv,
            DiscoveryService.GROUP,
            PeerGroup.DEFAULT_LIFETIME );
            
            rootPeerGroup_.getDiscoveryService().remotePublish(newPGAdv,
            DiscoveryService.GROUP);
            
            processGroupStructureChanged(
            new AdvertisementEvent(this,
            AdvertisementEvent.ALL_ADV_DELETED, (List) null));
        } catch (Throwable pge) {
            System.out.print("Failed to create/join new group.");
        }
    }
    
    // Leave a joined group.
    public synchronized void leaveGroup(PeerGroupAdvertisement group)
    throws Exception {
        if( null == group )
            return;
        
        if( PeerGroupID.defaultNetPeerGroupID.equals(group.getPeerGroupID()) ) {
            StringBuffer buffer = new StringBuffer();
            
            buffer.append("You have tried leaving NetPeerGroup");
            buffer.append(EOL);
            buffer.append("You can't leave this group");
            throw new RuntimeException(buffer.toString());
        }
        
        int index = getIndexOfAdv( myGroupAdvs_, group );
        if ( index == -1) {
            // Not in the group.
            return;
        }
        
        PeerGroup theGroup = (PeerGroup) myGroups_.elementAt(index);
        myGroups_.removeElementAt(index);
        myGroupAdvs_.removeElementAt(index);
        myGroupRdv_.removeElementAt(index);
        
        MembershipService membership = theGroup.getMembershipService();
        membership.resign();
        
        // Terminate associated threads.
        theGroup.stopApp();
        
        // Update the prefs file.
        updateJoinedGroups();
        
        // Select the NetPeerGroup notification.
        selectedGroupChanged(
        (PeerGroupAdvertisement) myGroupAdvs_.elementAt(0));
        
        // Notify process group structure.
        processGroupStructureChanged(
        new AdvertisementEvent(this,
        AdvertisementEvent.ALL_ADV_DELETED, (List) null));
    }
   
   
    // Blocks until a RendezVousService server is found and connection is started.
    public boolean waitForRendezVous() throws Exception {
        RendezVousService rdv = currentGroup_.getRendezVousService();
        
        if (rdv == null) {
            throw new RuntimeException(
            "No Rendezvous Service is not configured");
        }
        
        if (rdv.isConnectedToRendezVous() ) {
            return true;
        }
        
        blockRendezVousWait_ = true;
        while (!rdv.isConnectedToRendezVous() && blockRendezVousWait_) {
            Thread.currentThread().sleep(RENDEZVOUS_LOOP_SLEEP);
        }
        return rdv.isConnectedToRendezVous();
    }
    
    // Stop blocking.
    public void unblockWaitForRendezVous() {
        // setting of a boolean is atomic - do not need to synchronize
        blockRendezVousWait_ = false;
    }
    
   // Get index of advertisement.
    private int getIndexOfAdv(Vector vector, PeerGroupAdvertisement pgAdv) {
        ID mustMatch = pgAdv.getPeerGroupID();
        
        for (int i = 0; i < vector.size(); i++ ) {
            PeerGroupAdvertisement targetAdv =
            (PeerGroupAdvertisement) vector.elementAt(i);
            
            if ( mustMatch.equals(targetAdv.getPeerGroupID()) ) {
                return i;
            }
        }
        
        return -1;
    }
    
    // Called if new peer is found.
    private void processPeerStructureChanged(AdvertisementEvent e) {
        for (int i = 0; i < structureListeners_.size(); i++) {
            GroupStructureListener l = (GroupStructureListener) structureListeners_.elementAt(i);
            l.peerDataChanged(e);
        }
    }
    
    // Called if new group found.
    private void processGroupStructureChanged(AdvertisementEvent advertismentEvent) {
        for (int i = 0; i < structureListeners_.size(); i++) {
            ((GroupStructureListener) structureListeners_.elementAt(i)).
            groupStructureChanged(advertismentEvent);
        }
    } 
    
    // Updates the preference reader for joined groups.
    private void updateJoinedGroups() {
        Enumeration groupAdv = myGroupAdvs_.elements();
        StringBuffer buffer = new StringBuffer();
        StringBuffer bufferRdv = new StringBuffer();
        String id;
        PreferenceReader prefs = PreferenceReader.getInstance();
        
        // The first group is the netgroup - do not save it
        if (groupAdv.hasMoreElements()) {
        	groupAdv.nextElement();
        }
        int index = 1;
        while (groupAdv.hasMoreElements()) {
            id = ((PeerGroupAdvertisement) groupAdv.nextElement()).getPeerGroupID().toString();
            if (((Boolean) myGroupRdv_.elementAt(index)).booleanValue())
                bufferRdv.append(id);
            else
                buffer.append(id);
            
            if (groupAdv.hasMoreElements()) {
                buffer.append(",");
                bufferRdv.append(",");
            }
        }
    }
    
     // Collects the peers and peer groups
    static class InternalDiscover extends DiscoveryClientServer {

        // Type to discover.DiscoveryService.PEER or DiscoveryService.GROUP
        private int type;
        
        // Create a new InternalDiscover instance.
        public InternalDiscover(PeerGroup group, int type) {
            super(group, type);
        }
        
        protected boolean validAdvertisement(Advertisement adv) {
            
            boolean fl = ( adv instanceof PeerGroupAdvertisement);
            return true;
        }
        
        // Compares two advertisement.
        protected boolean compareAds(Advertisement ad1,
        Advertisement ad2) {
            boolean result = false;
            
            if (type == DiscoveryService.PEER) {
                if (ad1 != null && ad2 != null) {
                    PeerAdvertisement p1;
                    PeerAdvertisement p2;
                    
                    try {
                        p1 = (PeerAdvertisement) ad1;
                        p2 = (PeerAdvertisement) ad2;
                        result = p1.getPeerID().equals(p2.getPeerID());
                    } catch (ClassCastException iox) {
                        result = false;
                    }
                }
            } else {
                if (ad1 != null && ad2 != null) {
                    PeerGroupAdvertisement p1;
                    PeerGroupAdvertisement p2;
                    
                    try {
                        p1 = (PeerGroupAdvertisement) ad1;
                        p2 = (PeerGroupAdvertisement) ad2;
                        result = p1.getPeerGroupID().equals(p2.getPeerGroupID());
                    } catch (ClassCastException iox) {
                        result = false;
                    }
                }
            }
            return result;
        }
    }
}