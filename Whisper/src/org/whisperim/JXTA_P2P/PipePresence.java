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


import java.lang.reflect.InvocationTargetException;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Timer;
import java.util.TimerTask;

import net.jxta.document.Advertisement;
import net.jxta.discovery.DiscoveryService;
import net.jxta.exception.PeerGroupException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.endpoint.Message;
import net.jxta.pipe.PipeService;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.OutputPipeListener;
import net.jxta.pipe.OutputPipeEvent;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.protocol.PeerAdvertisement;
import net.jxta.protocol.PipeAdvertisement;

// Creates input and output pipes.
public class PipePresence implements Runnable, OutputPipeListener {

	// Thread dealings.
	private final static int mediumWait_ = 5 * 60 * 1000; // 5 Minutes
	private final static int longWait_ = 15 * 60 * 1000; // 15 Minutes
	
	// For ping.
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

	// Buddy lists
	protected Hashtable onlineBuddies_ = new Hashtable();
	protected Hashtable offlineBuddies_ = new Hashtable();

	// Live peer tracking.
	private PeerGroup group_ = null;
	private PipeService pipe_ = null;
	private DiscoveryService discovery_ = null;
	private Vector listeners_ = new Vector();
	private PipeAdvertisement replyPipe_ = null;
	private Hashtable pipes_ = new Hashtable();

	// Timer.
	private Timer timer_ = new Timer();

	
	// Acssr for online buddies.
	public Hashtable getOnlineBuddiesHash(){
		return onlineBuddies_;
	}
	
	// Resolve input pipes and manage output pipes.
	public PipePresence(PeerGroup group, PipeAdvertisement replyPipe) {
		this.group_ = group;
		this.replyPipe_ = replyPipe;
		this.pipe_ = group.getPipeService();
		this.discovery_ = group.getDiscoveryService();

		// Threading pipes.
		Thread thread = new Thread(this, "P2P Pipe");
		thread.start();
	}

	// Set a reply pipe.
	public void setReplyPipe(PipeAdvertisement adv) {
		replyPipe_ = adv;
	}

	// Get a reply pipe.
	public PipeAdvertisement getReplyPipe() {
		return replyPipe_;
	}

	// Send output pipe message.
	public void probe(String pipeID) {

		try {
			// get advertisement in pipe.
			Enumeration locAds = discovery_.getLocalAdvertisements(
					DiscoveryService.ADV, PipeAdvertisement.IdTag, pipeID);
			// Create output pipe with listener.
			if (locAds.hasMoreElements()) {
				PipeAdvertisement pipeAdv = (PipeAdvertisement) locAds
						.nextElement();
				if (pipeAdv != null) {
					pipe_.createOutputPipe(pipeAdv, this);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Probes a for an existence of pipes on network.
	public void probe(String buddy, Enumeration advertisements) {

		try {
			// Create output pipe with listener
			while (advertisements.hasMoreElements()) {
				PipeAdvertisement pipeAdv = (PipeAdvertisement) advertisements
						.nextElement();

				if (pipeAdv != null
						&& (!(pipeAdv.getType())
								.equals(PipeService.PropagateType))) {
					if (buddy != null) {
						// Store it in our offline list
						offlineBuddies_.put(buddy, pipeAdv.getPipeID()
								.toString());
					}
					// Create the output pipe
					pipe_.createOutputPipe(pipeAdv, this);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Return found pipes.
	public Enumeration getPipes() {

		return pipes_.keys();
	}

	// Send output pipe resolution notification.
	public void probe(String buddy, PipeAdvertisement pipeAdv) {
		if (buddy != null) {
			// Store it in our offline list.
			offlineBuddies_.put(buddy, pipeAdv.getPipeID().toString());
		}

		try {
			pipe_.createOutputPipe(pipeAdv, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Run the thread.
	public void run() {

		// Schedule the various tasks.
		timer_.scheduleAtFixedRate(new MediumNapTask(this), 0, // Now
				mediumWait_);

		timer_.scheduleAtFixedRate(new LongNapTask(this), 0, // Now
				longWait_);

	}

	public OutputPipe getOutputPipe(String id) {
		return (OutputPipe) pipes_.get(id);
	}

	public void removeOutputPipe(String id) {
		pipes_.remove(id);
	}

	// Output pipe objects.
	public void outputPipeEvent(OutputPipeEvent event) {

		OutputPipe op = event.getOutputPipe();
		String pipeId = event.getPipeID();

		// Update the OutputPipe
		OutputPipe old = (OutputPipe) pipes_.put(pipeId, op);
		if (old != null) {
			old.close();
		}
		boolean foundInOffline = false;
		// Check if this pipe is in the offLine list
		Enumeration offLineBuddies = offlineBuddies_.keys();
		try {
			String buddy = null;
			String pid = null;

			while (offLineBuddies.hasMoreElements()) {
				buddy = (String) offLineBuddies.nextElement();
				pid = (String) offlineBuddies_.get(buddy);
				if ((pid != null) && (pid.equals(pipeId))) {

					// Put in
					offlineBuddies_.remove(buddy);
					onlineBuddies_.put(buddy, new BuddyListBuilder(buddy, pipeId,
							group_, this, replyPipe_));
					foundInOffline = true;
					break;
				}
			}

		} catch (Exception ez1) {

		}

		// Update the time on the online buddy element
		if (!foundInOffline) {
			Enumeration buddies = onlineBuddies_.keys();
			try {
				String buddy = null;
				while (buddies.hasMoreElements()) {
					buddy = (String) buddies.nextElement();

					BuddyListBuilder onlineBuddy = (BuddyListBuilder) onlineBuddies_
							.get(buddy);
					if ((onlineBuddy != null)
							&& (onlineBuddy.pipeID_.equals(pipeId))) {
						// That's the pipe we are receiving an event
						// Update the last checked time
						onlineBuddy.lastChecked_ = System.currentTimeMillis();
						break;
					}
				}
			} catch (Exception ez1) {

			}
		}

		if (listeners_.size() > 0) {
			PresenceEvent newevent = new PresenceEvent(this, event.getPipeID(),
					getName(event.getPipeID()), true);

			for (int i = 0; i < listeners_.size(); i++) {
				Listener pl = (Listener) listeners_.elementAt(i);
				pl.presenceEvent(newevent);
			}
		}
	}

	// Resolve names.
	public Enumeration getNames() {
		Vector names = new Vector();

		Enumeration thePipes = pipes_.keys();
		while (thePipes.hasMoreElements()) {
			names.addElement(getName((String) thePipes.nextElement()));
		}
		return names.elements();
	}

	public void processAck(String buddy) {
		BuddyListBuilder onlineBuddy = (BuddyListBuilder) onlineBuddies_.get(buddy);
		if (onlineBuddy != null) {
			// Update the last checked time
			onlineBuddy.lastChecked_ = System.currentTimeMillis();
		}
	}

	// Add a pipe listener
	public synchronized void addListener(Listener listener) {

		listeners_.addElement(listener);
	}

	// Remove a listener
	public synchronized boolean removeListener(Listener listener) {

		return (listeners_.removeElement(listener));
	}

	// Adds an online buddy to the list.
	public void addOnlineBuddy(String buddy, PipeAdvertisement buddyAdv) {

		if (buddyAdv == null) {
			return;
		}

		// Probe it
		try {
			probe(buddy, buddyAdv);
		} catch (Exception ez1) {

		}
	}

	public BuddyListBuilder getOnlineBuddy(String name) {
		return (BuddyListBuilder) onlineBuddies_.get(name);
	}

	// Get a name of the pipe.
	public String getName(String id) {

                Enumeration pipeName = null;
                try {
                	pipeName = discovery_.getLocalAdvertisements(DiscoveryService.ADV,
                                        PipeAdvertisement.IdTag,
                                        id);
                        if (pipeName == null) {
                                return null;
                        }
                } catch (IOException e) {
                }
                if (pipeName.hasMoreElements()) {
                        PipeAdvertisement pipeAdv = (PipeAdvertisement) pipeName.nextElement();
                        String name = pipeAdv.getName();
                        if (name != null) {
                               System.out.println(name);
                        }
                }

                return null;
        }

	public class PresenceEvent extends EventObject {

		private String pipeID = null;
		private String name = null;
		private boolean status = false;

		// Constructor for the PresenceEvent object
		public PresenceEvent(Object source, String id, String name,
				boolean status) {
			super(source);
			this.pipeID = id;
			this.name = name;
			this.status = status;
		}

		// Get pipe ID.
		public String getPipeID() {
			return pipeID;
		}

		// Get pipe name.
		public String getName() {
			return name;
		}

		// Returns if connected or not.
		public boolean getStatus() {
			return status;
		}
	}

	// Presence Event Interface
	public interface Listener extends EventListener {

		void presenceEvent(PresenceEvent event);
	}

	protected void notifyOfflineBuddy(BuddyListBuilder buddy) {
		if (listeners_.size() > 0) {
			PresenceEvent newevent = new PresenceEvent(this, buddy.pipeID_,
					buddy.name_, false);

			for (int i = 0; i < listeners_.size(); i++) {
				Listener pl = (Listener) listeners_.elementAt(i);
				pl.presenceEvent(newevent);
			}
		}
	}

	protected void ping(BuddyListBuilder buddy) {
		Chat.ping(buddy);
	}

	protected class MediumNapTask extends TimerTask {
		private PipePresence presence = null;

		public MediumNapTask(PipePresence presence) {
			this.presence = presence;
		}

		public void run() {

			Enumeration buddys = null;
			BuddyListBuilder buddy = null;
			String buddyName = null;

			Hashtable onlineBuddies = presence.onlineBuddies_;
			Hashtable offlineBuddies = presence.offlineBuddies_;

			// We check the online users on the ShortNap period.
			if (onlineBuddies.size() > 0) {
				// Check if we consider that the output pipe we have for the
				// user is expired.
				buddys = onlineBuddies.keys();

				while (buddys.hasMoreElements()) {
					buddyName = (String) buddys.nextElement();
					buddy = (BuddyListBuilder) onlineBuddies.get(buddyName);

					long currentTime = System.currentTimeMillis();
					long lastChecked = currentTime - buddy.lastChecked_;
					if (lastChecked > mediumWait_) {
						// We declare this user off line.
						removeOutputPipe(buddy.pipeID_);
						onlineBuddies.remove(buddyName);
						presence.notifyOfflineBuddy(buddy);

						// Store it in our offline list
						offlineBuddies.put(buddyName, buddy.pipeID_);
					} else {
						// This user is still online, but it is time to check
						presence.ping(buddy);
					}
				}
			}
		}
	}

	protected class LongNapTask extends TimerTask {
		private PipePresence presence = null;

		public LongNapTask(PipePresence presence) {
			this.presence = presence;
		}

		public void run() {

			Enumeration buddys = null;

			Hashtable offlineBuddies = presence.offlineBuddies_;

			// Try to probe the offlist on the LongNapPeriod, except for the
			// first time
			buddys = offlineBuddies.elements();
			while (buddys.hasMoreElements()) {
				try {
					probe((String) buddys.nextElement());
				} catch (Exception ez1) {
					continue;
				}
			}
		}
	}
}
