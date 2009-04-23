package org.whisperim.gtalk;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import javax.speech.AudioException;
import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.swing.ImageIcon;
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
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.whisperim.client.Buddy;
import org.whisperim.client.ConnectionManager;
import org.whisperim.client.ConnectionStrategy;
import org.whisperim.plugins.ConnectionPluginAdapter;
import org.whisperim.prefs.Preferences;

public class Gtalk extends ConnectionPluginAdapter implements ConnectionStrategy, MessageListener, PacketListener, RosterListener {
	
	private XMPPConnection connection_;
	private ConnectionManager manager_;
	private Roster roster_;
	private Presence presence_;
	
	public ImageIcon serviceIcon_ = Preferences.getInstance().getGtalkIconSmall();
	private String iconLocation_ = Preferences.getInstance().getGtalkIconSmallLocation();
	private String handle_;
	private String localHandle_; //local handle refers to handle_+"@gmail.com" - use this for everything
	private Buddy to_;
	private ArrayList<Buddy> buddies_ = new ArrayList<Buddy>();
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
	
	/**
	 * Prints out the entire gtalk buddy list to the console.
	 * Only used for debugging purposees.
	 */
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
	
	
	@Override
	public void entriesAdded(Collection<String> addresses) {
    	for(String entry : addresses) {
    		try {
				roster_.createEntry(entry, entry, null);
			} catch (XMPPException e) {
				e.printStackTrace();
			}
    	}
    }
	
	@Override
    public void entriesDeleted(Collection<String> addresses) {
    	for(String entry : addresses) {
    		try {
				roster_.removeEntry(roster_.getEntry(entry));
			} catch (XMPPException e) {
				e.printStackTrace();
			}
    	}
    }
	
	@Override
    public void entriesUpdated(Collection<String> addresses) {
    	//roster_.removeEntry(entry);
    }
	
	
	/**
	 * Returns the full gmail chat handle, username@gmail.com. Use this method instead of getHandle()
	 * if you want to send a message. Gtalk only recognizes username@gmail.com NOT username.
	 */
	public String getGmailHandle() {
		return localHandle_;
	}
	
	
	/**
	 * ONLY ACCOUNT XML SHOULD USE THIS - USE getGmailHandle INSTEAD
	 * Returns the handle of the user WITHOUT @gmail.com
	 * Referred to program-wide as handle_, gtalk needs the @gmail.com extension to be recognized
	 * If you're using it to send a message, HAVE to append @gmail.com
	 */
	public String getHandle() {
		return handle_;
	}

	
	public String getIdentifier() {
		return getProtocol() + ":" + getGmailHandle();
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
	
	
	@Override
	/**
	 * Same as getServiceIcon() for gtalk.
	 * 
	 * Returns the gtalk icon (usually a small, 16x16 image icon)
	 * To use as an icon, need to cast getIcon() to Icon.
	 */
	public ImageIcon getIcon() {
		return serviceIcon_;
	}


	@Override
	/**
	 * Same as getIcon() for gtalk.
	 * 
	 * Returns the gtalk service icon (usually a small, 16x16 image icon)
	 * To use as an icon, need to cast getIcon() to Icon.
	 * 
	 */
	public ImageIcon getServiceIcon() {
		return serviceIcon_;
	}
	
	
	public int getStatus() {
		return status_;
	}

	
	public void processMessage(Message message) {
		if(message.getType() == Message.Type.chat) {
	        System.out.println(message.getFrom() + " says: " + message.getBody());
	        org.whisperim.client.Message msg = new org.whisperim.client.Message(
	        		new Buddy(message.getFrom(),localHandle_, protocol_),
	        		new Buddy(localHandle_, localHandle_, protocol_), 
	        		message.getBody(),iconLocation_, Calendar.getInstance().getTime());
	        try {
				manager_.messageReceived(msg);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (EngineException e) {
				e.printStackTrace();
			} catch (EngineStateError e) {
				e.printStackTrace();
			} catch (AudioException e) {
				e.printStackTrace();
			} catch (BadLocationException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    }
	}
	
	
	@Override
    public void presenceChanged(Presence presence) {
        System.out.println("Presence changed: " + presence.getFrom() + " " + presence);
    }	
	

	@Override
	public void processPacket(Packet p) {
		Message message = (Message)p;
		if (message.getBody() != null) {
			String fromName = StringUtils.parseBareAddress(message.getFrom());
			Buddy from = new Buddy (fromName, localHandle_, protocol_);
			
			System.out.println(fromName);
			System.out.println(localHandle_);
			System.out.println(protocol_);
			
			System.out.println(manager_.getStrategies().entrySet().toString());
			

			org.whisperim.client.Message msg = new org.whisperim.client.Message(from, 
					to_, message.getBody(), protocol_, Calendar.getInstance().getTime());
			
			System.out.println("--------------------------");
			System.out.println("From:"+msg.getFrom());
			System.out.println("To:"+msg.getTo());
			System.out.println("Other:"+msg.getOther());
			System.out.println("Protocol:"+msg.getProtocol());
			System.out.println("At time:"+msg.getTimeSent());
			System.out.println("Message:"+msg.getMessage());
			System.out.println("--------------------------");
			
			try {
				manager_.messageReceived(msg);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (EngineException e) {
				e.printStackTrace();
			} catch (EngineStateError e) {
				e.printStackTrace();
			} catch (AudioException e) {
				e.printStackTrace();
			} catch (BadLocationException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}	
	
	
	public void processMessage(Chat chat, Message message) {
		processMessage(message);
	}
	
	
	public void sendMessage(org.whisperim.client.Message message) {
		System.out.println("--------------------------");
		System.out.println("From:"+message.getFrom());
		System.out.println("To:"+message.getTo());
		System.out.println("Other:"+message.getOther());
		System.out.println("Protocol:"+message.getProtocol());
		System.out.println("At time:"+message.getTimeSent());
		System.out.println("Message:"+message.getMessage());
		System.out.println("--------------------------");
		Message m = new Message (message.getTo(), Message.Type.chat);
		//en defines the language that gtalk requires
		m.addBody("en", message.getMessage());
		connection_.sendPacket(m);
	}
	
	
	public void setAvailable() {
		presence_.setMode(Presence.Mode.available);
		connection_.sendPacket(presence_);
	}
	
	
	public void setAway() {
		presence_.setMode(Presence.Mode.away);
		connection_.sendPacket(presence_);
	}
	
	
	/**
	 * Sets the current user's status message _and_ sets the user away.
	 * Combination of the setAway and setStatus methods.
	 * @param awayMessage
	 */
	public void setAway(String awayMessage) {
		presence_.setMode(Presence.Mode.away);
		presence_.setStatus(awayMessage);
		connection_.sendPacket(presence_);
	}
	
	
	/**
	 * Sets the handle of the user. Need to set this _before_ any connections or logins occur.
	 * Probably isn't a good idea to set the handle in the middle of a current connection.
	 * If you're going to setup a new connection (and Whisper still doesn't allow multiple
	 * connections using the same protocol) then make the current user signOff, then change the
	 * handle and call signOn for the new handle.
	 * @param handle - needs to be @gmail.com
	 */
	public void setHandle(String handle) {
		localHandle_ = handle;
	}

	
	/**
	 * Sets location of Gtalk icon. Don't use this - it is never checked, and should never be checked.
	 */
	public void setIconLocation(String location) {
		iconLocation_ = location;
	}
	
	
	/**
	 * Sets the user's presence to idle.
	 * Doesn't change user's status message.
	 */
	public void setIdle() {
		presence_.setMode(Presence.Mode.xa);
		connection_.sendPacket(presence_);
	}

	
	/**
	 * Gtalk doesn't implement invisibility(except through web interface)
	 * so don't call this, it does nothing
	 */
	public void setInvisible(boolean visible) {
		//gtalk doesn't allow you to go invisible using desktop clients
	}

	
	/**
	 * Sets the current status message. Does NOT make user available/away/busy/donotdisturb
	 * Doesn't change user's mode or presence type
	 */
	public void setStatusMessage(String statusMessage) {
		presence_.setStatus(statusMessage);
		connection_.sendPacket(presence_);
	}
	
	
	/**
	 * Sets the plugin name. Really don't touch this unless you know its ok to set.
	 */
	public void setPluginName(String name) {
		pluginName_ = name;
	}
	
	
	/**
	 * Signs the user off of gtalk. Must be called after the user has been logged in, 
	 * or throws NullPointerException
	 */
	public void signOff()
	{
		try {
			connection_.disconnect();
		} catch (NullPointerException e) {
			System.out.println("User:"+localHandle_+" couldn't log off because they were never logged in");
		}
		status_ = ConnectionStrategy.OFFLINE;
		System.out.println(localHandle_+"/"+connection_.getUser()+" signed out of "+connection_.getServiceName());
		
		manager_ = null;
		connection_ = null;
		handle_ = null;
		localHandle_ = null;
		to_ = null;
		buddies_ = null;
	}
	
	
	/**
	 * Signs the user on to gtalk using their username and password
	 * Sets the status as available
	 * @param username - username - DO NOT INCLUDE @gmail.com
	 * @param password - password
	 */
	public void signOn(ConnectionManager connectionManager, String username, String password) {
		manager_ = connectionManager;
		handle_ = username;
		localHandle_ = username.concat("@gmail.com");
		to_ = new Buddy(localHandle_, localHandle_, protocol_);
		
		
		
		ConnectionConfiguration config = new ConnectionConfiguration("talk.google.com", 5222, "gmail.com");
		connection_ = new XMPPConnection(config);
		SASLAuthentication.supportSASLMechanism("PLAIN", 0);
		
		
		try {
			connection_.connect();
			if(connection_ == null) {
				System.out.println("Could not connect to "+connection_.getHost());
				status_ = ConnectionStrategy.SERVICE_UNAVAILABLE;
				return;
			}
		} catch (XMPPException e) {
			System.out.println("Could not connect to "+connection_.getHost());
			status_ = ConnectionStrategy.SERVICE_UNAVAILABLE;
			return;
		}
		
		try {
		     connection_.login(localHandle_, password);
		     System.out.println(localHandle_+"/"+connection_.getUser()+" signed into "+connection_.getServiceName());
		     sendMessage(new org.whisperim.client.Message(
		    		new Buddy(localHandle_, localHandle_, protocol_, "whisperimtest1"),
		    		new Buddy("jplastek@gmail.com", "jplastek@gmail.com", protocol_, "jplastek"),
		    		"User:"+localHandle_+"/"+connection_.getUser()+" should be whisperimtest1@gmail.com says hello", 
		    		Calendar.getInstance().getTime(),
		    		Gtalk.protocol_,
		    		"other"));
		     status_ = ConnectionStrategy.ACTIVE;
		} catch (XMPPException e) {
			System.out.println(connection_.getUser()+" failed signing into "+connection_.getServiceName());
			status_ = ConnectionStrategy.INVALID_PASSWORD;
			return;
		}
		
		//set user as available
		presence_ = new Presence(Presence.Type.available);
		
		//listen for incoming messages, filter only to get chat messages
		//ignore/dont display other requests
		connection_.addPacketListener(this, new MessageTypeFilter(Message.Type.chat));
		
		//get buddies
		roster_ = connection_.getRoster();
		//ArrayList<Buddy> buddies = new ArrayList<Buddy>();
		
		//automatically accepts other users if they add the user
		//not secure, need to fix
		roster_.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
		
		System.out.println("Found all buddies for "+localHandle_+"/"+connection_.getUser());
		
		for(RosterEntry r : roster_.getEntries()) {
			Buddy tmp;
			if (r.getName() == null){
				tmp = new Buddy(r.getUser(), localHandle_, protocol_);
			}else{
				 tmp = new Buddy(r.getUser(), localHandle_, protocol_, r.getName());
			}
			
			//buddies.add(tmp);
			buddies_.add(tmp);
		}
		
		manager_.receiveBuddies(buddies_);
	}
	
	
	/**
	 * Returns GTALK:username@gmail.com
	 * Needed for strategy.toString() to identify in a user-friendly way
	 */
	@Override
	public String toString() {
		return protocol_+":"+localHandle_;
	}
	
	
}
