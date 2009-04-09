package org.whisperim.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dapper.scrender.Scrender;

public class StringFilter {
	private String string_;
	
	public StringFilter(String s)
	{
		if(s !=null && s.length()>0)
			string_ = s;
	}
	
	public String filter()
	{
		//Scrender scrender = new Scrender();

        String patternStr = "";
        
        //Pattern pattern = Pattern.compile("^(((ht|f)tp(s?))\\://)?((([a-zA-Z0-9_\\-]{2,}\\.)+[a-zA-Z]{2,})|((?:(?:25[0-5]|2[0-4]\\d|[01]\\d\\d|\\d?\\d)(?(\\.?\\d)\\.)){4}))(:[a-zA-Z0-9]+)?(/[a-zA-Z0-9\\-\\._\\?\\,\\'/\\\\\\+&amp;%\\$#\\=~]*)?$");
        Pattern phone = Pattern.compile("^\\d{1,9}$");
        Matcher matcher = phone.matcher(string_);
       
        //String[] x = phone.split(string_);
        //String output = matcher.replaceAll(replacementStr);

		return string_;
	}
	
	
}
