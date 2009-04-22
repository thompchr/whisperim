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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.ImageIcon;

import net.jxta.peergroup.PeerGroup;

import org.whisperim.client.Buddy;
import org.whisperim.client.ConnectionManager;
import org.whisperim.client.Message;
import org.whisperim.plugins.ConnectionPluginAdapter;

public class Peer2PeerPlugIn extends ConnectionPluginAdapter {

	private String name_ = "JXTA Connection";
	private String id_ = "JXTA Connection";
	private String iconPath_;
	private ConnectionManager cm_;
	private String handle_;

	@Override
	public String getIdentifier() {

		return id_ + ":" + handle_.toLowerCase().replace(" ", "");
	}

	@Override
	public String getProtocol() {
		return id_;
	}

	@Override
	public String getPluginIconLocation() {

		return iconPath_;
	}

	@Override
	public String getPluginName() {

		return name_;
	}

	@Override
	public void setIconLocation(String location) {
		iconPath_ = location;

	}

	@Override
	public void setPluginName(String name) {
		name_ = name;
	}

	@Override
	public void signOn(ConnectionManager cm, String username, String password) {

		// Create a peer group manager.
		PeerGroupManager pgm = new PeerGroupManager(null);

		// Create a chat object.
		Chat chat = new Chat(pgm);

		// Login to start server.
		chat.login(username);

		// Create pipe presence instance.
		PipePresence pp = new PipePresence((PeerGroup) pgm.getPeerList(), null);

		// Create Buddy hash.
		Hashtable buddyHash = new Hashtable();

		// Set buddy hash to buddy hash.
		buddyHash = pp.getOnlineBuddiesHash();

		// Push peers into buddy list.
		ArrayList<Buddy> list = new ArrayList<Buddy>();
		// buddyHash.CopyTo(list, 0);

		for (int i = 0; i < buddyHash.size(); ++i) {
			list.add((Buddy) buddyHash.elements().nextElement());
		}

		cm_.receiveBuddies(list);
	}

	@Override
	public void sendMessage(Message m) {
		// Create a chat object.
		PeerGroupManager pgm = new PeerGroupManager(null);

		Chat chat = new Chat(pgm);

		chat.sendMessageToPeers(m.toString());
	}

	private void print(String msg) {
		System.out.println(msg);
	}

	@Override
	public String getHandle() {
		return handle_;
	}

	@Override
	public int getStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setHandle(String handle) {
		handle_ = handle;

	}

	@Override
	public void setIdle() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setInvisible(boolean visible) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setStatusMessage(String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public ImageIcon getSericeIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageIcon getIcon() {
		// peer2peer doesn't have an icon
		return null;
	}

}