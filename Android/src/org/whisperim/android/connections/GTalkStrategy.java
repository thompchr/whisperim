/**************************************************************************
 * Copyright 2009 Chris Thompson                                           *
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
package org.whisperim.android.connections;

import java.util.ArrayList;
import java.util.Calendar;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
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

import android.util.Log;

public class GTalkStrategy implements ConnectionStrategy, PacketListener {
	private XMPPConnection connection_;
	private String handle_ = "thompchr@gmail.com";
	private String protocol_ = "gtalk";
	private ConnectionManager cm_;
	private Buddy to_ = new Buddy (handle_, handle_, protocol_);

	@Override
	public String getHandle() {

		return handle_;
	}

	@Override
	public String getIdentifier() {

		return getProtocol() + ":" + getHandle();
	}

	@Override
	public String getProtocol() {

		return protocol_;
	}

	@Override
	public int getStatus() {

		return 0;
	}

	@Override
	public void receiveBuddies(ArrayList<Buddy> buddies) {


	}

	@Override
	public void receiveMessage(org.whisperim.client.Message message) {


	}

	@Override
	public void sendMessage(org.whisperim.client.Message message) {


	}

	@Override
	public void setAwayMessage(String message) {


	}

	@Override
	public void setHandle(String handle) {


	}

	@Override
	public void setIdle() {


	}

	@Override
	public void setInvisible(boolean visible) {


	}

	@Override
	public void setStatusMessage(String message) {


	}

	@Override
	public void signOff() {


	}

	@Override
	public void signOn(ConnectionManager cm, String username, String password) {
		cm_ = cm;
		ConnectionConfiguration config = new ConnectionConfiguration("talk.google.com", 5222, "gmail.com");
		connection_ = new XMPPConnection(config);

		try{
			connection_.connect();
			Log.i("WhisperIM", "[XMPP Protocol] Connected to " + connection_.getHost());
		}catch (XMPPException e) {
			Log.i("WhisperIM", "[XMPP Protocol] Failed to connect to " + connection_.getHost());
		}

		try{
			connection_.login("thompchr@gmail.com", "o%UC3e6S");
			Log.i("WhisperIM", "[XMPP Protocol] Logged in as " + connection_.getUser());
			Presence presence = new Presence(Presence.Type.available);
			connection_.sendPacket(presence);
		}catch (XMPPException e) {
			Log.e("WhisperIM", "[XMPP Protocol] Failed to log in as " + username);
			Log.e("WhisperIM", e.toString());
		}

		connection_.addPacketListener(this, new MessageTypeFilter(Message.Type.chat));

	}

	@Override
	public void statusUpdate(String status) {


	}

	@Override
	public String getPluginIconLocation() {

		return null;
	}

	@Override
	public String getPluginName() {

		return null;
	}

	@Override
	public void setIconLocation(String location) {


	}

	@Override
	public void setPluginName(String name) {


	}

	@Override
	public void processPacket(Packet packet) {
		Message message = (Message) packet;
		if (message.getBody() != null) {
			String fromName = StringUtils.parseBareAddress(message.getFrom());
			Log.i("WhisperIM", "[XMPP Protocol] " + protocol_ + "Got text [" + message.getBody() + "] from [" + fromName + "]");
			Buddy from = new Buddy (fromName, handle_, protocol_);

			org.whisperim.client.Message msg = new org.whisperim.client.Message(from, to_, message.getBody(), protocol_, Calendar.getInstance().getTime());
			cm_.messageReceived(msg);


		}
	}

}
