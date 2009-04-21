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

import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.peergroup.PeerGroup;

// Interface implemented by class.
public interface GroupStructureListener {

	// Called if the peer group structure was changed
	public void groupStructureChanged(AdvertisementEvent event);

	// Called if more peers for a given group where discovered
	public void peerDataChanged(AdvertisementEvent event);
}