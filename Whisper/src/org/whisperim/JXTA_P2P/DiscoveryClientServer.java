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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.MimeMediaType;
import net.jxta.peergroup.PeerGroup;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.PeerAdvertisement;
import net.jxta.protocol.PipeAdvertisement;

/*
 * This class discovers peers and peer-groups.
 */
public abstract class DiscoveryClientServer implements DiscoveryListener,
		PeerGroupManager.PeerListener {

	// Threshold value for discovery service.
	protected final static int THRESHOLD_ = 50;

	// MIME advertisement.
	private final static MimeMediaType XMLMIMETYPE_ = new MimeMediaType(
			"text/xml");

	private Vector listeners_ = new Vector();
	protected DiscoveryService discover_ = null;
	protected int adType_ = -1;
	private String attribute_ = null;
	private String value_ = null;

	// Create a new discovery instance.
	public DiscoveryClientServer(PeerGroup group, int adType) {
		this(group, adType, null, null);
	}

	// Create a new discovery instance.
	public DiscoveryClientServer(PeerGroup group, int adType, String attr,
			String value) {
		// this will start the discovery
		this.adType_ = adType;
		this.attribute_ = attr;
		this.value_ = value;
		groupChanged(group);
	}

	// Returns discovered advertisements.
	public Enumeration getAdvertisements() {
		try {
			return discover_
					.getLocalAdvertisements(adType_, attribute_, value_);
		} catch (IOException e) {
			return null;
		}
	}

	// Call if peer group is updated.
	public void groupChanged(PeerGroup group) {
		if (group != null) {
			if (discover_ != null) {
				discover_.removeDiscoveryListener(this);
			}
			discover_ = group.getDiscoveryService();
			discover_.addDiscoveryListener(this);
		}
		try {

			// Inform the listners, then search for new ads.
			processAdDiscovered(new AdvertisementEvent(this,
					AdvertisementEvent.ALL_ADV_DELETED, (List) null));
			searchAdvertisements(attribute_, value_);
		} catch (Exception iox) {
			System.out.println(iox.getMessage());
		}
	}

	// Called if if peer changes (stub method).
	public void peerChanged(PeerAdvertisement pgAdv) {
	}

	// Search through advertisements.
	public void searchAdvertisements() throws IOException {
		searchAdvertisements(attribute_, value_);
	}

	// Search through advertisements with params. and store results.
	public void searchAdvertisements(String attr, String value)
			throws IOException {
		discover_.getRemoteAdvertisements(null, adType_, attr, value,
				THRESHOLD_, null);

		// Get local values.
		Enumeration locAds = discover_.getLocalAdvertisements(adType_, attr,
				value);

		ArrayList result = new ArrayList();

		while (locAds != null && locAds.hasMoreElements()) {
			Advertisement adv = (Advertisement) locAds.nextElement();
			addAdToList(adv, result);
		}
		// Notify listeners.
		processAdDiscovered(new AdvertisementEvent(this,
				AdvertisementEvent.ADV_ADDED, result));
	}

	// Discovery service event handler.
	public void discoveryEvent(DiscoveryEvent event) {
		DiscoveryResponseMsg res = event.getResponse();
		Enumeration enumResponse;
		String str = null;
		Advertisement adv = null;
		// String advStr = res.getPeerAdv();
		PeerAdvertisement advStr = res.getPeerAdvertisement();
		boolean exception = false;
		ArrayList result = null;

		if (res.getDiscoveryType() == adType_) {
			enumResponse = res.getResponses();
			if (enumResponse == null || !enumResponse.hasMoreElements()) {
				return;
			}
			while (enumResponse.hasMoreElements()) {
				try {
					str = (String) enumResponse.nextElement();

					// Create Peer(Group)Advertisement from response.
					adv = (Advertisement) AdvertisementFactory
							.newAdvertisement(XMLMIMETYPE_,
									new ByteArrayInputStream(str.getBytes()));

					if (isInvalidPipeAdv(adv)) {
						continue;
					}

					if (result == null) {
						result = new ArrayList();
					}
					// adv is good add it
					addAdToList(adv, result);
				} catch (Exception ex) {
					ex.printStackTrace();
					exception = true;
				}
			}
			if (result != null) {
				processAdDiscovered(new AdvertisementEvent(this,
						AdvertisementEvent.ADV_ADDED, result));
			}
		}
	}

	private boolean isInvalidPipeAdv(Advertisement adv) {

		if (adType_ == DiscoveryService.ADV) {
			if (adv instanceof PipeAdvertisement) {
				PipeAdvertisement pipeAdv = (PipeAdvertisement) adv;
				if (pipeAdv.getName().startsWith(value_)) {
					return false;
				} else {
					return true;
				}
			}
		}
		return false;
	}

	// publish advertisements.
	public void publishAdvertisement(Advertisement adv) throws IOException {
		discover_.publish(adv, adType_, adType_);
		discover_.remotePublish(adv, adType_);
	}

	// Adds an AdvertisementDiscoveryListener listener.
	public void addListener(AdvertisementDiscoveryListener listener) {
		listeners_.addElement(listener);
	}

	// Removes an AdvertisementDiscoveryListener listener.
	public void removeListener(AdvertisementDiscoveryListener listener) {
		listeners_.removeElement(listener);
	}

	// Process newly discovered ads.
	protected void processAdDiscovered(AdvertisementEvent event) {
		for (int i = 0; i < listeners_.size(); i++) {
			((AdvertisementDiscoveryListener) listeners_.elementAt(i))
					.advertisementsDiscovered(event);
		}
	}

	// Compares two ad
	protected abstract boolean compareAds(Advertisement ad1, Advertisement ad2);

	/*
	 * Adds a newly discovered advertisement to the list of advertisements if
	 * the list does not already contain this peer
	 */
	protected synchronized void addAdToList(Advertisement adv, List list) {
		int index = -1;
		boolean flag = false;

		for (int i = 0; i < list.size(); i++) {
			if (compareAds(adv, (Advertisement) list.get(i))) {
				index = i;
				flag = true;
				break;
			}
		}

		if (index != -1) {
			list.remove(index);
		}
		list.add(adv);

	}

}