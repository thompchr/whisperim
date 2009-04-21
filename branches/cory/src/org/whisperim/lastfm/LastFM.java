package org.whisperim.lastfm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;


public class LastFM {

	private String username_;
	
	public LastFM(String username)
	{
		username_ = username;
	}

		
	public String getLastSong()
	{
		String song = null;
		
		 try {

			 HttpURLConnection con = (HttpURLConnection) 
			 	new URL("http://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&user="+
			 			username_
			 			+"&api_key=4e5b94a79e7b8edc70f9875e191386ef&limit=1").openConnection();
			 
			 con.setRequestMethod("POST");
			 con.setDoOutput(true);
	         con.setReadTimeout(10000);
			 
			 con.connect();
			 
			 BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
			 StringBuilder sb = new StringBuilder();
			 
	         while ((song = rd.readLine()) != null)
	             sb.append(song+"\n");
	         
	         String artist = sb.toString().substring(
	        		 sb.toString().indexOf(">", sb.toString().indexOf("<artist"))+1, 
	        		 sb.toString().indexOf("</artist>")
	        		 );
	         
	         String track = sb.toString().substring(
	        		 sb.toString().indexOf(">", sb.toString().indexOf("<name"))+1, 
	        		 sb.toString().indexOf("</name>")
	        		 );        
	         
	         song = artist + " - " + track;
	         
	         con.disconnect();

		   } catch (Exception exception) {
		
		   }
		   return song;
	}
}
