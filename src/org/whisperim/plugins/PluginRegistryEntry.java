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

public class PluginRegistryEntry {
	private String name_;
	private String location_;
	private String extentionPoint_;
	private String iconLocation_;
	private String entryClass_;
	
	public PluginRegistryEntry(String name, String location, String extentionPoint, String entryClass, 
			String iconLocation){
		name_ = name;
		extentionPoint_ = extentionPoint;
		iconLocation_ = iconLocation;
		entryClass_ = entryClass;
	}
	
	public PluginRegistryEntry(){
		
	}
	
	public PluginRegistryEntry(String name, String location, String extentionPoint,
			String entryClass){
		name_ = name;
		extentionPoint_ = extentionPoint;
		iconLocation_ = null;
		entryClass_ = entryClass;
	}
	
	public String getName() {
		return name_;
	}
	public void setName(String name) {
		name_ = name;
	}
	public String getLocation() {
		return location_;
	}
	public void setLocation(String location) {
		location_ = location;
	}
	public String getExtentionPoint() {
		return extentionPoint_;
	}
	public void setExtentionPoint(String extentionPoint) {
		extentionPoint_ = extentionPoint;
	}
	public String getIconLocation() {
		return iconLocation_;
	}
	public void setIconLocation(String iconLocation) {
		iconLocation_ = iconLocation;
	}
	public String getEntryClass() {
		return entryClass_;
	}
	public void setEntryClass(String entryClass) {
		entryClass_ = entryClass;
	}
	
	
	

}
