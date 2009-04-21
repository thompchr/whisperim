package org.whisperim.gtalk;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import javax.speech.AudioException;
import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.swing.text.BadLocationException;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.whisperim.client.Buddy;
import org.whisperim.client.ConnectionManager;
import org.whisperim.client.ConnectionStrategy;
import org.whisperim.plugins.ConnectionPluginAdapter;
import org.whisperim.prefs.Preferences;

public class Gtalk extends ConnectionPluginAdapter implements MessageListener {

	SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
	Date date = new Date();
	
	private XMPPConnection connection_;
	private ConnectionManager manager_;
	private Roster roster_;
	private Presence presence_;
	
	
	private String iconLocation_ = Preferences.getInstance().getGtalkIconSmallLocation();
	private String localHandle_;
	private static String pluginName_ = "Gtalk Connection";
	private static final String protocol_ = "GTALK";	
	
	public final static int STATUS_IDLE = 1;
	public final static int STATUS_INVISIBLE = 2;
	public final static int STATUS_AWAY = 3;
	public final static int STATUS_VISIBLE = 4;
	public final static int SET_STATUS_MESSAGE = 6;
	private int status_ = ConnectionStrategy.OFFLINE;
	
	
	public Gtalk() {
		
	}
	
	
	public void displayBuddyList()
	{
		Collection<RosterEntry> entries = roster_.getEntries();
		System.out.println("\n" + entries.size() + " buddies:");
		for(RosterEntry r:entries)
		{
			System.out.println(r.getUser());
		}
		System.out.println("\n");
	}
	
	
	public String getHandle() {
		return localHandle_;
	}

	
	public String getIdentifier() {
		return "gtalk."+localHandle_;
	}

	
	public String getProtocol() {
		return protocol_;
	}

	
	public String getPluginIconLocation() {
		return iconLocation_;
	}

	
	public String getPluginName() {
		return pluginName_;
	}
	
	public int getStatus() {
		if(this.presence_.getMode().equals(Presence.Mode.available) ||
				presence_.getMode().equals(Presence.Mode.chat)) {
			return STATUS_VISIBLE;
		}
		else if(presence_.getMode().equals(Presence.Mode.away) ||
				presence_.getMode().equals(Presence.Mode.dnd)) {
			return STATUS_AWAY;
		}
		else {
			return STATUS_IDLE;
		}
	}

	
	public void processMessage(Message message) {
		if(message.getType() == Message.Type.chat) {
	        System.out.println(message.getFrom() + " says: " + message.getBody());
	        org.whisperim.client.Message msg = new org.whisperim.client.Message(new Buddy(message.getFrom(), localHandle_, protocol_),
	        		new Buddy(localHandle_, localHandle_, protocol_), message.getBody(),iconLocation_, date);
	        try {
				manager_.messageReceived(msg);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (EngineException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (EngineStateError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AudioException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
	
	public void processMessage(Chat chat, Message message) {
		processMessage(message);
	}
	
	
	public void sendMessage(String message, String to) {
		Chat chat = connection_.getChatManager().createChat(to, this);
		try {
			chat.sendMessage(message);
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setAvailable() {
		presence_.setMode(Presence.Mode.available);
		connection_.sendPacket(presence_);
	}
	
	public void setAway() {
		presence_.setMode(Presence.Mode.away);
		connection_.sendPacket(presence_);
	}
	
	public void setAway(String awayMessage) {
		presence_.setMode(Presence.Mode.away);
		presence_.setStatus(awayMessage);
		connection_.sendPacket(presence_);
	}
	
	
	public void setHandle(String handle) {
		localHandle_ = handle;
	}

	
	public void setIconLocation(String location) {
		iconLocation_ = location;
	}
	
	public void setIdle() {
		presence_.setMode(Presence.Mode.xa);
		connection_.sendPacket(presence_);
	}

	/**
	 * Gtalk doesn't implement invisibility, so don't call this, it does nothing
	 */
	public void setInvisible(boolean visible) {
		//gtalk doesn't allow you to go invisible using desktop clients
	}

	
	public void setStatusMessage(String message) {
		presence_.setStatus(message);
		connection_.sendPacket(presence_);
	}
	
	
	public void setPluginName(String name) {
		pluginName_ = name;
	}
	
	
	public void signOff()
	{
		connection_.disconnect();
		System.out.println("User:"+localHandle_+" signed out of gtalk");
	}
	
	/**
	 * Signs the user on to gtalk using their username and password
	 * Sets the status as available
	 * @param username - username@gmail.com
	 * @param password - password
	 */
	public void signOn(String username, String password) {
		ConnectionConfiguration config = new ConnectionConfiguration("talk.google.com", 5222, "gmail.com");
		
		connection_ = new XMPPConnection(config);
		SASLAuthentication.supportSASLMechanism("PLAIN", 0);
				
		try {
			connection_.connect();
		} catch (XMPPException e) {
			//e.printStackTrace();
			System.out.println("Could not connect to "+connection_.toString());
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("Not a connection error");
		}
		
		try {
		     connection_.login(username, password);
		     System.out.println("User: "+username+" signed into gtalk");
		} catch (XMPPException e) {
			//e.printStackTrace();
			System.out.println("User: "+username+" failed to sign into gtalk");
			System.exit(1);
		}
		
		//set local gtalk variables
		setHandle(username);
		
		presence_ = new Presence(Presence.Type.available);
		
			
		//get buddies
		roster_ = connection_.getRoster();
		
		//automatically accepts other users if they add the user
		//not secure, need to fix
		roster_.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
		System.out.println("Initialized the roster");
		
		roster_.addRosterListener(new RosterListener() {
		    public void entriesAdded(Collection<String> addresses) {
		    	for(String entry : addresses) {
		    		try {
						roster_.createEntry(entry, entry, null);
					} catch (XMPPException e) {
						e.printStackTrace();
					}
		    	}
		    }
		    public void entriesDeleted(Collection<String> addresses) {
		    	for(String entry : addresses) {
		    		try {
						roster_.removeEntry(roster_.getEntry(entry));
					} catch (XMPPException e) {
						e.printStackTrace();
					}
		    	}
		    }
		    public void entriesUpdated(Collection<String> addresses) {
		    	//roster_.removeEntry(entry);
		    }
		    public void presenceChanged(Presence presence) {
		        System.out.println("Presence changed: " + presence.getFrom() + " " + presence);
		    }
		});
		
		//handle incoming messages		
		PacketListener pl = new PacketListener() {
			public void processPacket(Packet p) {
				if(p instanceof Message) {
					Message msg = (Message)p;
					processMessage(msg);
				}
			}
		};
		connection_.addPacketListener(pl, null);
	}
}
