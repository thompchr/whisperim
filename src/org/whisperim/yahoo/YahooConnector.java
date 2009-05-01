package org.whisperim.yahoo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;


import javax.speech.AudioException;
import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;

import org.whisperim.client.Buddy;
import org.whisperim.client.ConnectionManager;
import org.whisperim.client.ConnectionStrategy;
import org.whisperim.plugins.ConnectionPluginAdapter;

import ymsg.network.AccountLockedException;
import ymsg.network.LoginRefusedException;
import ymsg.network.Session;
import ymsg.network.StatusConstants;
import ymsg.network.YahooGroup;
import ymsg.network.YahooUser;
import ymsg.network.event.SessionChatEvent;
import ymsg.network.event.SessionConferenceEvent;
import ymsg.network.event.SessionErrorEvent;
import ymsg.network.event.SessionEvent;
import ymsg.network.event.SessionExceptionEvent;
import ymsg.network.event.SessionFileTransferEvent;
import ymsg.network.event.SessionFriendEvent;
import ymsg.network.event.SessionListener;
import ymsg.network.event.SessionNewMailEvent;
import ymsg.network.event.SessionNotifyEvent;
import ymsg.network.*;

public class YahooConnector extends ConnectionPluginAdapter implements ConnectionStrategy, SessionListener {

	private ConnectionManager manager_;
	
	public ImageIcon serviceIcon_ = null; //= Preferences.getInstance().getYahooIconSmall();
	private String iconLocation_ = null; //= Preferences.getInstance().getYahooIconSmallLocation();
	private String handle_;
	private Buddy to_;
	private ArrayList<Buddy> buddies_ = new ArrayList<Buddy>();
	private static String pluginName_ = "Yahoo Connection";
	private static final String protocol_ = "Yahoo";	
	
	
	private Session yahooSession_;
	
	
	public final static int STATUS_IDLE = 1;
	public final static int STATUS_INVISIBLE = 2;
	public final static int STATUS_AWAY = 3;
	public final static int STATUS_VISIBLE = 4;
	public final static int SET_STATUS_MESSAGE = 6;
	private int status_ = ConnectionStrategy.OFFLINE;
	
	/**
	 * Just and empty function to grab a pointer to use yahoo methods
	 */
	public YahooConnector() {

	}
	
	
	@Override
	public String getHandle() {
		return handle_;
	}

	
	@Override
	public ImageIcon getIcon() {
		return serviceIcon_;
	}
	
	@Override
	public String getIdentifier() {
		return protocol_+":"+handle_;
	}

	@Override
	public String getProtocol() {
		return protocol_;
	}

	@Override
	public ImageIcon getServiceIcon() {
		return serviceIcon_;
	}

	@Override
	public String getPluginIconLocation() {
		return iconLocation_;
	}

	@Override
	public String getPluginName() {
		return pluginName_;
	}
	
	@Override
	public int getStatus() {
		return status_;
	}

	@Override
	public void setIconLocation(String location) {
		iconLocation_ = location;
		
	}

	@Override
	public void setPluginName(String name) {
		pluginName_ = name;
		
	}

	@Override
	public void setHandle(String handle) {
		handle_ = handle;
		
	}

	@Override
	public void setIdle() {
		try {
			yahooSession_.setStatus(StatusConstants.STATUS_IDLE);
			System.out.println("Now idle");
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void setInvisible(boolean visible) {
		try {
			yahooSession_.setStatus(StatusConstants.STATUS_INVISIBLE);
			System.out.println("Now invisible");
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void setStatusMessage(String message) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String toString() {
		return protocol_+":"+handle_;
	}
	
	public void signOn(ConnectionManager connectionManager, String username, String password) {
		handle_ = username;
		
		yahooSession_ = new Session();
		
		try {
			yahooSession_.login(handle_, password);
			
			System.out.println("Logged into Yahoo w/ user:"+handle_);
			
			status_ = ConnectionStrategy.ACTIVE;
		} catch (AccountLockedException e) {
			System.out.println("Account Locked - Contact Yahoo");
			status_ = ConnectionStrategy.SERVICE_UNAVAILABLE;
			e.printStackTrace();
		} catch (IllegalStateException e) {
			System.out.println("Illegal State Exception");
			status_ = ConnectionStrategy.OFFLINE;
			e.printStackTrace();
		} catch (LoginRefusedException e) {
			System.out.println("Password Incorrect");
			status_ = ConnectionStrategy.INVALID_PASSWORD;
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			status_ = ConnectionStrategy.OFFLINE;
		}
		
		//get buddies
		YahooGroup[] groups = yahooSession_.getGroups();
		System.out.println(groups.length);
		for(int i = 0; i <= groups.length; i++) {
			
			Vector members = groups[i].getMembers();
			
			System.out.println(members.size());
			System.out.println(members.toString());
			
			for(int j = 0; j <= members.size(); j++) {
				
				YahooUser u = (YahooUser) members.elementAt(j);
				
				if(u.isLoggedIn()) {
					Buddy tmp;
					tmp = new Buddy(u.getId(), handle_, protocol_);
					buddies_.add(tmp);
				}
			}
		}
		
		manager_.receiveBuddies(buddies_);
		
		
	}
	
	public void signOff() {
		try {
			yahooSession_.logout();
			
			System.out.println("Logged out of Yahoo w/ username:"+handle_);
			
			status_ = ConnectionStrategy.OFFLINE;
			
			manager_ = null;
			handle_ = null;
			buddies_ = null;
			yahooSession_ = null;
		} catch (IllegalStateException e) {
			System.out.println("Not signed in to Yahoo");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException in signOff");
			e.printStackTrace();
		}
		
		
	}
	
	public void sendMessage(org.whisperim.client.Message message) {
		try {
			yahooSession_.sendMessage(message.getTo(), message.getMessage());
		} catch (IllegalStateException e) {
			System.out.println("Can't send messages when not logged into Yahoo");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	//public void receiveMessage() {
		//TODO
		
	//}
	
	public void setAway() {
		try {
			yahooSession_.setStatus(StatusConstants.STATUS_BRB_STR, true);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void setAway(String awayMessage) {
		try {
			yahooSession_.setStatus(awayMessage, true);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	public void buzzReceived(SessionEvent ev) {
		System.out.println("BUZZZZZ received");
		
	}


	@Override
	public void chatCaptchaReceived(SessionChatEvent ev) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void chatConnectionClosed(SessionEvent ev) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void chatLogoffReceived(SessionChatEvent ev) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void chatLogonReceived(SessionChatEvent ev) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void chatMessageReceived(SessionChatEvent ev) {
		System.out.println("Chat Message Received");
		
	}


	@Override
	public void chatUserUpdateReceived(SessionChatEvent ev) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void conferenceInviteDeclinedReceived(SessionConferenceEvent ev) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void conferenceInviteReceived(SessionConferenceEvent ev) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void conferenceLogoffReceived(SessionConferenceEvent ev) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void conferenceLogonReceived(SessionConferenceEvent ev) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void conferenceMessageReceived(SessionConferenceEvent ev) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void connectionClosed(SessionEvent ev) {
		System.out.println("Connection Closed");
		
	}


	@Override
	public void contactRejectionReceived(SessionEvent ev) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void contactRequestReceived(SessionEvent ev) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void errorPacketReceived(SessionErrorEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void fileTransferReceived(SessionFileTransferEvent ev) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void friendAddedReceived(SessionFriendEvent ev) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void friendRemovedReceived(SessionFriendEvent ev) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void friendsUpdateReceived(SessionFriendEvent ev) {
		YahooUser[] u = ev.getFriends();
		for(int i = 0; i <= ev.getFriends().length; i++) {
			if(u[i].getStatus() == StatusConstants.STATUS_AVAILABLE ) {
				buddies_.add(new Buddy(u[i].getId(), handle_, protocol_));
			}
			else if(u[i].getStatus() ==  StatusConstants.STATUS_OFFLINE) {
				buddies_.remove(new Buddy(u[i].getId(), handle_, protocol_));
			}
			else {
				//do nothing
			}
		}
		
	}


	@Override
	public void inputExceptionThrown(SessionExceptionEvent ev) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void listReceived(SessionEvent ev) {
		System.out.println("List received");
		
	}


	@Override
	public void messageReceived(SessionEvent ev) {
		Buddy from = new Buddy (ev.getFrom(), handle_, protocol_);
		
		System.out.println("From: "+ev.getFrom());
		System.out.println("Message: "+ev.getMessage());

		org.whisperim.client.Message msg = new org.whisperim.client.Message(from, 
				to_, ev.getTo(), protocol_, ev.getTimestamp());
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


	@Override
	public void newMailReceived(SessionNewMailEvent ev) {
		System.out.println("Mail received");
		
	}


	@Override
	public void notifyReceived(SessionNotifyEvent ev) {
		System.out.println("Notify received");
		
	}


	@Override
	public void offlineMessageReceived(SessionEvent ev) {
		System.out.println("Offline message received");
		
	}

}
