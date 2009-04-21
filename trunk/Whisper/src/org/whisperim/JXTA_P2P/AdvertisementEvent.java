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

import java.util.EventObject;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;

// Deals with event notifications.
public class AdvertisementEvent extends EventObject {

	public static final int ADV_ADDED = 1;
	public static final int ADV_DELETED = 2;
	public static final int ALL_ADV_DELETED = 3;
	public static final int GRP_CHANGED = 4;

	private int type_;

	private List changed_ = null;

	// Creates a new event
	public AdvertisementEvent(Object source, int type, List changed) {
		super(source);
		this.type_ = type;
		this.changed_ = changed;
		if (this.changed_ == null)
			this.changed_ = new ArrayList();
	}

	// Creates a new event
	public AdvertisementEvent(Object source, int type, Enumeration enumeration) {
		super(source);
		this.type_ = type;
		changed_ = new ArrayList();
		while (enumeration.hasMoreElements()) {
			changed_.add(enumeration.nextElement());
		}
	}

	// Returns the event type
	public int getType() {
		return type_;
	}

	// Returns list of updated ads.
	public List getChangedAds() {
		return changed_;
	}
}