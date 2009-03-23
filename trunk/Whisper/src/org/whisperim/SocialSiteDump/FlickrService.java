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

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.xml.sax.SAXException;

import com.sun.xml.internal.ws.client.RequestContext;

public class FlickrService {
/*
	// Create instance variables for Flickr
	private String nsid_ = null;
	private Flickr flickr_ = null;
	private AuthStore authStore_ = null;
	private String sharedSecret_ = null;
	
	// Retrieve and store Flickr information.
	flickr_ = new Flickr(apiKey); 
	this.sharedSecret_ = sharedSecret_; 
	this.nsid_ = nsid_; 
	
	// Read from file that stores auths
	File authsDir = new File(System.getProperty("user.home") + File.separatorChar + ".flickrAuth"); 
		
		// Fill in authStore_/
		if (authsDir != null)
		{ 
			authStore_ = new FileAuthStore(authsDir);
		}
	
		
	// Method authorizes connection to flicker.
	private void flickrAuthorization() throws IOException, SAXException, FlickrException 
	{ 
		// Setup interface.
		String frob = flickr_.getAuthInterface().getFrob(); 
		URL authUrl = flickr_.getAuthInterface().buildAuthenticationUrl(Permission.READ, frob);
		
		//  Kick out to URL (change later).
		System.out.println("Authorization Required. Browse to URL: " + authUrl.toExternalForm() + " then, hit enter."); 
		
		// Read in result, set and store.
		System.in.read(); 
		Auth token = flickr_.getAuthInterface().getToken(frob); 
		RequestContext.getRequestContext().setAuth(token); 
		authStore_.store(token); 
	} 
	
	
	// This function allows retrieval of updates.
	private void getFlickrNotifications()
	{
		RequestContext rc = RequestContext.getRequestContext(); 
		rc.setSharedSecret(sharedSecret_); 
		  
		// Begin Retrieval.
		if (authStore_ != null) 
		{ 
		  Auth auth = authStore_.retrieve(this.nsid); 
		  if (auth == null) flickrAuthorization(); 
		  else rc.setAuth(auth); 
		} 
		
		
		ActivityInterface aInterface = flickr_.getActivityInterface(); 
		ItemList list = aInterface.userPhotos(50, 0, "300d"); 
		  
		for (int j = 0; j < list.size(); j++) 
		{ 
		  Item item = (Item) list.get(j); 
		  richText="Item " + (j + 1) + "/" + list.size() + " type: " + item.getType()+"<br>"; 
		  richText="Item-id: " + item.getId() + "<br>"; 
		  ArrayList events = (ArrayList) item.getEvents(); 
		  
		  	for (int i = 0; i < events.size(); i++) 
		  	{ 
		  		richText="Event " + (i + 1) + "/" + events.size() + " of Item " + (j + 1)+"<br>"; 
		  		richText="Event-type: " + ((Event) events.get(i)).getType()+"<br>"; 
		  		if (((Event) events.get(i)).getType().equals("note")) 
		  		{ 
		  			richText="Note-id: " + ((Event) events.get(i)).getId()+"<br>"; 
		  		} else if (((Event) events.get(i)).getType().equals("comment"))
		  			{ 
		  				richText="Comment-id: " + ((Event) events.get(i)).getId()+"<br>"; 
		  			} 
		  
		  		richText="User: " + ((Event) events.get(i)).getUser()+"<br>"; 
		  		richText="Username: " + ((Event) events.get(i)).getUsername()+"<br>"; 
		  		richText="Value: " + ((Event) events.get(i)).getValue()+"<br>"; 
		  		richText="Dateadded: " + ((Event) events.get(i)).getDateadded() +"<br>"; 
		  	}
		}

	}
	
*/
}
