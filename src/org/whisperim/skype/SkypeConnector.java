 /**************************************************************************
 * Copyright 2009 Cory Plastek                                             *
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

package org.whisperim.skype;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import org.jivesoftware.smack.XMPPConnection;
import org.whisperim.client.Buddy;
import org.whisperim.client.ConnectionManager;
import org.whisperim.client.ConnectionStrategy;
import org.whisperim.plugins.ConnectionPluginAdapter;
import org.whisperim.prefs.Preferences;

import com.skype.ContactList;
import com.skype.Friend;
import com.skype.Skype;
import com.skype.Call;
import com.skype.Application;
import com.skype.SkypeException;
import com.skype.Stream;
import com.skype.connector.Connector;
import com.skype.connector.win32.Win32Connector;
import com.skype.connector.ConnectorException;
import com.skype.connector.windows.WindowsConnector;


/**
 * Skype
 * 
 * Provides methods to interface with the Skype protocol: signon, signoff, send messages,
 * get buddies, retrieve icons, etc.
 * Should eventually be implemented as a dynamic plugin instead of
 * static (everytime Whisper is loaded, the Skype plugin is loaded - doesn't matter if there
 * are any Skype accounts or not)
 * @author Cory Plastek
 *
 */
public class SkypeConnector extends ConnectionPluginAdapter implements ConnectionStrategy {

	private XMPPConnection connection_;
	private ConnectionManager manager_;
	
	public ImageIcon serviceIcon_ = null;//Preferences.getInstance().getSkypeIconSmall();
	private String iconLocation_ = null;//Preferences.getInstance().getSkypeIconSmallLocation();
	private String handle_;
	private Buddy to_;
	private ArrayList<Buddy> buddies_ = new ArrayList<Buddy>();
	private static String pluginName_ = "Skype Connection";
	private static final String protocol_ = "SKYPE";	
	
	public final static int STATUS_IDLE = 1;
	public final static int STATUS_INVISIBLE = 2;
	public final static int STATUS_AWAY = 3;
	public final static int STATUS_VISIBLE = 4;
	public final static int SET_STATUS_MESSAGE = 6;
	private int status_ = ConnectionStrategy.OFFLINE;
	
	/**
	 * Get an instance of SkypeConnector
	 * Doesn't initialize anything.
	 */
	public SkypeConnector() {
		
	}
	
	@Override
	/**
	 * Returns the user's handle (usually firstname.lastname)
	 * @return handle - String
	 */
	public String getHandle() {
		return handle_;
	}
	
	@Override
	/**
	 * Returns the icon associated with Skype. Same as getServiceIcon
	 * @return icon - ImageIcon
	 */
	public ImageIcon getIcon() {
		return serviceIcon_;
	}

	@Override
	/**
	 * Returns the unique identifier associated with this user
	 * SKYPE:username
	 * @return SKYPE:username - String
	 */
	public String getIdentifier() {
		return getProtocol() + ":" + getHandle();
	}

	@Override
	/**
	 * Returns a string to the location of the service icon.
	 * @return stringToIconLocation - String
	 */
	public String getPluginIconLocation() {
		return iconLocation_;
	}

	
	@Override
	/**
	 * returns String "Skype Connection"
	 * @return pluginName - String
	 */
	public String getPluginName() {
		return pluginName_;
	}
	
	
	@Override
	/**
	 * Returns the skype protocol name
	 * @return "SKYPE" - String
	 */
	public String getProtocol() {
		return protocol_;
	}

	@Override
	/**
	 * Returns the small Skype service icon
	 * @return skypeIcon - ImageIcon
	 */
	public ImageIcon getServiceIcon() {
		return serviceIcon_;
	}

	@Override
	/**
	 * Returns the status ONLINE, OFFLINE, etc.
	 * @return status - int
	 */
	public int getStatus() {
		return status_;
	}

	public void sendMessage(org.whisperim.client.Message message) {
		//sends a message
		//TODO
	}
	
	public void setAvailable() {
		//TODO
	}
	
	public void setAway() {
		//TODO
	}
	
	
	@Override
	/**
	 * Sets the username's handle. Don't use this. If you need to use a different
	 * handle, register a new account.
	 * @param handle - String
	 */
	public void setHandle(String handle) {
		handle_ = handle;	
	}
	
	@Override
	/**
	 * Sets the location for the plugin's icon
	 * @param locationToPluginIcon - String
	 */
	public void setIconLocation(String location) {
		iconLocation_ = location;
		
	}
	
	@Override
	/**
	 * Sets the user's presence to idle
	 */
	public void setIdle() {
		// TODO
	}

	@Override
	/**
	 * Sets the user invisible or not depending on boolean visible
	 * @param visible - boolean
	 */
	public void setInvisible(boolean visible) {
		// TODO Auto-generated method stub
		
	}

	@Override
	/**
	 * Sets the plugin's name
	 * @param pluginName - String
	 */
	public void setPluginName(String pluginName) {
		pluginName_ = pluginName;
	}
	
	@Override
	/**
	 * Sets the current status message. Doesn't change user's status
	 * @param message - String
	 */
	public void setStatusMessage(String message) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Sign the user off the connection.
	 */
	public void signOff() {
		//TODO
		//need to also make sure end all conversations		
		
		
		
		
		status_ = ConnectionStrategy.OFFLINE;
		System.out.println(handle_+"/"+connection_.getUser()+" signed out of "+connection_.getServiceName());
		
		manager_ = null;
		connection_ = null;
		handle_ = null;
		to_ = null;
		buddies_ = null;
	}
	
	
	public void signOn(ConnectionManager connectionManager, String username, String password) {
		manager_ = connectionManager;
		handle_ = username;
		to_ = new Buddy(handle_, handle_, protocol_);
		
		try {
			if(!Skype.isRunning()){
				//start skype if not running
				System.out.println("Skype must be running - restart this application after Skype is installed");
				System.exit(1);
				//Process p = Runtime.getRuntime().exec(Skype.getInstalledPath());
			}
		} catch (SkypeException e) {
			// TODO Auto-generated catch block
			System.out.println("Skype exception");
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Other exception");
			e.printStackTrace();
		}
		
		//TODO	
	}
	
	@Override
	/**
	 * Returns SKYPE:username
	 * Needed for strategy.toString to identify in a user-friendly way
	 * @return SKPYE:username - String
	 */
	public String toString() {
		return protocol_+":"+handle_;
	}
	
}
