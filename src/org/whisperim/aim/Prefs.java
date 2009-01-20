package org.whisperim.aim;

import com.aol.acc.*;
import java.util.*;

public class Prefs extends AccPreferencesHook {

	HashMap<String, String> map = new HashMap<String, String>();
	public String getValue(String specifier) {
		return (map.get(specifier));
	}

	public String getDefaultValue(String specifier){
		//This class does not support default values...should probably
		//fix this at some point
		return null;
	}

	public void setValue(String specifier, String value) {
		map.put(specifier, value);
	}

	public void reset(String specifier){
		map.put(specifier, null);
	}

	public String[] getChildSpecifiers(String specifier) {
		Vector<String> v = new Vector<String>();
		for(String s : map.keySet())
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

