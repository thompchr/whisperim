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
package org.whisperim.plugins;

import java.util.ArrayList;

public class PluginRegistry {
	
	private ArrayList<PluginRegistryEntry> plugins_ = new ArrayList<PluginRegistryEntry>();
	
	public PluginRegistry(){
		
	}
	
	public boolean isEmpty(){
		return plugins_.isEmpty();
	}
	
	public void registerPlugin(PluginRegistryEntry pre){
		plugins_.add(pre);
	}
	
	public void registerPlugin(String name, String location, String extentionPoint, String entryClass, 
			String iconLocation){
		plugins_.add(new PluginRegistryEntry(name, location, extentionPoint, entryClass, iconLocation));
	}
	
	public void registerPlugin(String name, String location, String extentionPoint, 
			String entryClass){
		plugins_.add(new PluginRegistryEntry(name, location, extentionPoint, entryClass));
	}
	
	public ArrayList<PluginRegistryEntry> getPluginsForExtentionPoint(String ep){
		ArrayList<PluginRegistryEntry> temp = new ArrayList<PluginRegistryEntry>();
		for (PluginRegistryEntry p:plugins_){
			if (p.getExtentionPoint().equalsIgnoreCase(ep)){
				temp.add(p);
			}
		}
		return temp;
	}

}
