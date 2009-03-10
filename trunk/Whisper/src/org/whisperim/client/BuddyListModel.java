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
package org.whisperim.client;

import java.util.ArrayList;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * This class is an implementation of the ListModel interface.
 * It is designed to provide a data structure for storing
 * buddy list information.
 * @author Chris Thompson
 *
 */
public class BuddyListModel implements ListModel {

	private ArrayList<Buddy> buddies_ = new ArrayList<Buddy>();
	private ArrayList<ListDataListener> ld_ = new ArrayList<ListDataListener>();
	
	/**
	 * Constructor
	 */
	public BuddyListModel(){
		
	}
	
	@Override
	public void addListDataListener(ListDataListener l) {
		ld_.add(l);
	}

	/**
	 * This method returns the item at the given index in the model
	 * 
	 * @param
	 * 		index - location of the item
	 */
	@Override
	public Object getElementAt(int index) {
		return buddies_.get(index);
	}

	/**
	 * This method returns the number of objects in the model
	 */
	@Override
	public int getSize() {
		
		return buddies_.size();
	}

	/**
	 * This method removes a ListDataListener
	 * 
	 * @param
	 * 		l - The ListDataListener to be removed
	 */
	@Override
	public void removeListDataListener(ListDataListener l) {
		ld_.remove(l);
	}
	
	public void addBuddies(ArrayList<Buddy> list){
		for (Buddy buddy:list){
			buddies_.add(buddy);
		}
		notifyListeners();
	}
	
	/**
	 * This method adds a single buddy to the model.
	 * Used when a buddy signs on.
	 * @param 
	 * 		buddy - The Buddy object to be added
	 */
	public void addSingleBuddy (Buddy buddy){
		buddies_.add(buddy);
		notifyListeners();
	}
	
	
	/**
	 * This method removes a single buddy from the list.
	 * Used when a buddy signs out.
	 * @param 
	 * 		buddy - The Buddy object to be removed
	 */
	public void removeSingleBuddy (Buddy buddy) {
		buddies_.remove(buddy);
		notifyListeners();
	}
	
	/**
	 * This method is used to clear the buddy list if a given session ends, etc.
	 * @param 
	 * 		protocol - The protocol id associated with that session
	 * @param 
	 * 		handle - The local handle associated with that session
	 * 
	 */
	public void removeBuddies(String protocol, String handle){
		for (Buddy buddy:buddies_){
			if (buddy.getProtocolID().equalsIgnoreCase(protocol) && buddy.getAssociatedLocalHandle().equalsIgnoreCase(handle)){
				buddies_.remove(buddy);
			}
		}
		notifyListeners();
	}
	
	/**
	 * This method is used to clear the buddy list if a given session ends, etc.
	 * @param 
	 * 		protocol - The protocol id associated with that session
	 */
	public void removeBuddies(String protocol){
		for (Buddy buddy:buddies_){
			if (buddy.getProtocolID().equalsIgnoreCase(protocol)){
				buddies_.remove(buddy);
			}
		}
	}
	
	/**
	 * This method is designed to check if the list contains a given element.
	 * Method uses the equals method of the Buddy class.
	 * @param 
	 * 		buddy - The element we are looking for
	 */
	public boolean contains(Buddy buddy){
		if (buddies_.contains(buddy)){
			return true;
		}else {
			return false;
		}
	}
	
	private void notifyListeners(){
		for (ListDataListener ldl:ld_){
			ldl.contentsChanged(new ListDataEvent(buddies_, ListDataEvent.CONTENTS_CHANGED, 0, buddies_.size()));
		}
	}

}
