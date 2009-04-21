package org.whisperim.client;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import net.dapper.scrender.Scrender;

public class StringFilter {
	private String string_;
	
	public StringFilter(String s)
	{
		if(s !=null && s.length()>0)
			string_ = s;
	}
	
	public String filter()
	{     
        Pattern phone = Pattern.compile("^\\(\\d{3}\\) ?\\d{3}( |-)?\\d{4}|^\\d{3}( |-)?\\d{3}( |-)?\\d{4}");
		Matcher matcher = phone.matcher(string_);

		List<String> num = new ArrayList<String>();
		
		matcher.reset();
		String substr = null;
		
		while (matcher.find()) {
			substr = string_.substring(matcher.start(), matcher.end());
			string_ +=string_.replace(substr, "<a href='mailto://"
					+ string_.substring(matcher.start(), matcher.end()) + "'>(Call via Skype)</a>");
		}

		return string_;
	}
}
