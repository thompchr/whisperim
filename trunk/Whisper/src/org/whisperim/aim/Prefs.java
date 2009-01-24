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
package org.whisperim.aim;


import java.util.HashMap;
import java.util.Vector;

import com.aol.acc.AccPreferencesHook;

/**
 * This class acts as a container for the session preferences for the AIM service.
 * Preferences are added to the HashMap as String objects via the setValue method.
 * This class extends the AccPreferencesHook class which allows it to be passed to
 * and parsed by the AIM service during the connection. 
 * @author Chris Thompson
 *
 */
public class Prefs extends AccPreferencesHook {

	private HashMap<String, String> map_ = new HashMap<String, String>();
	
	
	/**
	 * Method used to retrieve a value from the map_ HashMap.
	 * 
	 * @param specifier
	 * @return String
	 */
	public String getValue(String specifier) {
		return (map_.get(specifier));
	}

	
	/**
	 * Method used to retrieve the default value for a given
	 * preference.  Currently, no default values are supported
	 * by this class.
	 * 
	 * @param specifier
	 * @return String
	 */
	public String getDefaultValue(String specifier){
		//This class does not support default values...should probably
		//fix this at some point
		return null;
	}

	/**
	 * This method adds a value to the map.
	 * 
	 * @param specifier
	 * @param value
	 */
	public void setValue(String specifier, String value) {
		map_.put(specifier, value);
	}

	/**
	 * This method resets the preference associated with the given
	 * specifier to null.
	 * 
	 * @param specifier
	 */
	public void reset(String specifier){
		map_.put(specifier, null);
	}

	/**
	 * This method returns the child preferences associated with the
	 * given specifier.
	 * 
	 * @param specifier
	 * @return String[]
	 */
	public String[] getChildSpecifiers(String specifier) {
		Vector<String> v = new Vector<String>();
		for(String s : map_.keySet())
		{
			if(s.startsWith(specifier) && !s.equals(specifier))
			{
				v.add(s);
			}
		}
		
		if(v.size() > 0)
		{
			return (String[])v.toArray(new String[0]);
		}
		else
		{
			//I'd like to deal with this in a different way
			return null;
			
		}
		
	}
}

