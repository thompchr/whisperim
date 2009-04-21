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
import java.io.InputStream;
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
import com.aetrion.flickr.util.IOUtilities;

public class FlickrService implements Runnable {
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

	public ArrayList getNotifications() throws FlickrException, IOException,
	SAXException {

		authorizeFlickrAccount();
		ActivityInterface interface1 = flickr_.getActivityInterface();
		ItemList list = interface1.userComments(10, 0);

		ArrayList events = new ArrayList();
		for (int j = 0; j < list.size(); j++) {
			Item item = (Item) list.get(j);
			events.add((ArrayList) item.getEvents());
		}
		return events;
	}

	@Override
	public void run() {
		try {
			getNotifications();
		} catch (FlickrException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}