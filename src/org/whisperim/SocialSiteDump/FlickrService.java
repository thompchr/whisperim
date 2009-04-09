package org.whisperim.SocialSiteDump;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.activity.ActivityInterface;
import com.aetrion.flickr.activity.Event;
import com.aetrion.flickr.activity.Item;
import com.aetrion.flickr.activity.ItemList;
import com.aetrion.flickr.auth.Auth;
import com.aetrion.flickr.auth.Permission;

public class FlickrService {
	private static String apiKey_ = "c368c2b676805ec3f9dcc5219bb6e982";
	private static String sharedSecretKey_ = "08483e480ce39d70";
	private Flickr flickr_;
	private REST rest_;
	private RequestContext requestContext_;
	private Properties properties_ = null;

	/*
	 * Temp. Until Cory finishes his profile story thingy. After api key and
	 * shared will be dynamic
	 */

	public FlickrService() throws ParserConfigurationException, IOException {
		authorizeFlickrAccount();
		try {
			getNotifications();
		} catch (FlickrException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	public void authorizeFlickrAccount() {
		try {
			// This will be based off account settings when Cory does his story.
			flickr_ = new Flickr(apiKey_, sharedSecretKey_, new REST());
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		requestContext_ = RequestContext.getRequestContext();
		Auth auth = new Auth();
		auth.setPermission(Permission.READ);
		auth.setToken(properties_.getProperty("token"));
		requestContext_.setAuth(auth);
	}

	public void getNotifications() throws FlickrException, IOException,
			SAXException {
		ActivityInterface interface1 = flickr_.getActivityInterface();
		ItemList list = interface1.userComments(10, 0);
		for (int j = 0; j < list.size(); j++) {
			Item item = (Item) list.get(j);
			System.out.println("Item " + (j + 1) + "/" + list.size()
					+ " type: " + item.getType());
			System.out.println("Item-id:       " + item.getId() + "\n");
			ArrayList events = (ArrayList) item.getEvents();
			for (int i = 0; i < events.size(); i++) {
				System.out.println("Event " + (i + 1) + "/" + events.size()
						+ " of Item " + (j + 1));
				System.out.println("Event-type: "
						+ ((Event) events.get(i)).getType());
				System.out.println("User:       "
						+ ((Event) events.get(i)).getUser());
				System.out.println("Username:   "
						+ ((Event) events.get(i)).getUsername());
				System.out.println("Value:      "
						+ ((Event) events.get(i)).getValue() + "\n");
			}
		}
		ActivityInterface interface2 = flickr_.getActivityInterface();
		list = interface2.userPhotos(50, 0, "300d");
		for (int j = 0; j < list.size(); j++) {
			Item item = (Item) list.get(j);
			System.out.println("Item " + (j + 1) + "/" + list.size()
					+ " type: " + item.getType());
			System.out.println("Item-id:       " + item.getId() + "\n");
			ArrayList events = (ArrayList) item.getEvents();
			for (int i = 0; i < events.size(); i++) {
				System.out.println("Event " + (i + 1) + "/" + events.size()
						+ " of Item " + (j + 1));
				System.out.println("Event-type: "
						+ ((Event) events.get(i)).getType());
				if (((Event) events.get(i)).getType().equals("note")) {
					System.out.println("Note-id:    "
							+ ((Event) events.get(i)).getId());
				} else if (((Event) events.get(i)).getType().equals("comment")) {
					System.out.println("Comment-id: "
							+ ((Event) events.get(i)).getId());
				}
				System.out.println("User:       "
						+ ((Event) events.get(i)).getUser());
				System.out.println("Username:   "
						+ ((Event) events.get(i)).getUsername());
				System.out.println("Value:      "
						+ ((Event) events.get(i)).getValue());
				System.out.println("Dateadded:  "
						+ ((Event) events.get(i)).getDateadded() + "\n");
			}
		}
	}
}