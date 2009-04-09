package org.whisperim.client;

import java.util.ArrayList;
import java.util.List;
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
		//System.out.println("in filter" + "\n" + string_);
        //Pattern pattern = Pattern.compile("^(((ht|f)tp(s?))\\://)?((([a-zA-Z0-9_\\-]{2,}\\.)+[a-zA-Z]{2,})|((?:(?:25[0-5]|2[0-4]\\d|[01]\\d\\d|\\d?\\d)(?(\\.?\\d)\\.)){4}))(:[a-zA-Z0-9]+)?(/[a-zA-Z0-9\\-\\._\\?\\,\\'/\\\\\\+&amp;%\\$#\\=~]*)?$");
        Pattern phone = Pattern.compile("^\\(\\d{3}\\) ?\\d{3}( |-)?\\d{4}|^\\d{3}( |-)?\\d{3}( |-)?\\d{4}");
		//Pattern phone = Pattern.compile("^[-+]?\\d*$");
		Matcher matcher = phone.matcher(string_);
       

		List<String> num = new ArrayList<String>();
		int i=0;

		
		while (matcher.find()) {
			num.add("<a href='mailto://"
				+ string_.substring(matcher.start(), matcher.end()) + "'>("+
				string_.substring(matcher.start(), matcher.end())+")</a>");
		}
		
		matcher.reset();
		String substr = null;
		
		while (matcher.find()) {
			substr = string_.substring(matcher.start(), matcher.end());
			string_ +=string_.replace(substr, num.remove(0));
		}

		return string_;
	}
}
