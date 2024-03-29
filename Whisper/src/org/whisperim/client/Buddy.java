 /**************************************************************************
 * Copyright 2009 John Dlugokecki                                         *
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

package org.whisperim.client;

/**
 * This class is designed to help keep track of which buddy
 * is in each window and each associated service
 * @author Chris Thompson
 *
 */
public class Buddy {

	private String handle_;
	private String protocolID_;
	private String buddyIconLoc_;
	private String serviceIconLoc_;
	private String associatedLocalHandle_;
	private String alias_;
		
	public Buddy(String handle, String localHandle, String protocol){
		handle_ = handle;
		protocolID_ = protocol;
		associatedLocalHandle_ = localHandle;
		alias_ = handle;
	}
	
	public Buddy(String handle, String localHandle, String protocol, String alias){
		handle_ = handle;
		protocolID_ = protocol;
		alias_ = alias;
		associatedLocalHandle_ = localHandle;
	}
	
	public String getHandle(){
		return handle_;
	}
	
	public void setHandle (String handle){
		handle_ = handle;
	}
	
	public String getAlias(){
		return alias_;
	}
	
	public void setAlias(String alias){
		alias_ = alias; 
	}

	public String getProtocolID() {
		return protocolID_;
	}

	public void setProtocolID(String protocol) {
		protocolID_ = protocol;
	}
	
	@Override
	public String toString(){
		return alias_;
	}

	public String getAssociatedLocalHandle() {
		return associatedLocalHandle_;
	}

	public void setAssociatedLocalHandle(String associatedLocalHandle) {
		associatedLocalHandle_ = associatedLocalHandle;
	}
	
	public void setServiceIconLoc(String loc){
		serviceIconLoc_ = loc;
	}
	
	public String getServiceIconLoc(){
		return serviceIconLoc_;
	}
	
	public void setBuddyIconLoc(String loc){
		buddyIconLoc_ = loc;
	}
	
	public String getBuddyIconLoc(){
		return buddyIconLoc_;
	}
	
	@Override
	public boolean equals(Object rhs){
		if (rhs instanceof Buddy){
			Buddy otherBuddy = (Buddy) rhs;
			if (otherBuddy.getAssociatedLocalHandle().equalsIgnoreCase(associatedLocalHandle_) &&
					otherBuddy.getHandle().equalsIgnoreCase(handle_) &&
					otherBuddy.getProtocolID().equalsIgnoreCase(protocolID_)){
				return true;				
			}else{
				return false;
			}
		}else{
			return false;
		}			
		
	}
	
	
	
}
