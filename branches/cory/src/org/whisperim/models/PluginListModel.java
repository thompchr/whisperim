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

package org.whisperim.models;

import java.util.ArrayList;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.whisperim.plugins.Plugin;


/**
 * This class represents the model used to display the list
 * of active plugins.
 * @author Chris Thompson
 *
 */
public class PluginListModel implements ListModel {
	
	private ArrayList<ListDataListener> ld_ = new ArrayList<ListDataListener>();
	private ArrayList<Plugin> plugins_ = new ArrayList<Plugin>();
	
	
	/**
	 * Constructor
	 */
	public PluginListModel(){
		
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		ld_.add(l);
	}
	
	public void addPlugin(Plugin p){
		plugins_.add(p);
		notifyListeners();
	}
	
	public void removePlugin(Plugin p){
		plugins_.remove(p);
		notifyListeners();
	}

	@Override
	public Object getElementAt(int arg0) {
		return plugins_.get(arg0);
	}

	@Override
	public int getSize() {
		return plugins_.size();
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		ld_.remove(l);

	}
	
	private void notifyListeners(){
		for (ListDataListener l:ld_){
			l.contentsChanged(new ListDataEvent(plugins_, ListDataEvent.CONTENTS_CHANGED, 0, plugins_.size()));
			
		}
	}
	
	public boolean contains(Plugin plugin){
		if (plugins_.contains(plugin)){
			return true;
		}else{
			return false;
		}
	}

}
