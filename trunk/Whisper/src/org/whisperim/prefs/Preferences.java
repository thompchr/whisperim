/**************************************************************************
 * Copyright 2009 Cory Plastek                                             *
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


/*
 * Preferences.java
 */

package org.whisperim.prefs;

/**
 * @author Cory Plastek
 * 
 */
class Preferences {
	
	
	private static Preferences instance = null;
	
	protected Preferences() {
	    
		//load prefs from xml file
		//if prefs doesn't exist, initialize prefs variables to defaults and store to xml file
		
		//on close of prefswindow should save all prefs to xml
		
	}
	
	public static Preferences getInstance() {
		if(instance == null) {
			instance = new Preferences();
		}
		return instance;
	}
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

}
