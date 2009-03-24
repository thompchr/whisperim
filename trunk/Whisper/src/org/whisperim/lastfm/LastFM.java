package org.whisperim.lastfm;

import java.text.DateFormat;
import java.util.Collection;

import net.roarsoftware.lastfm.Artist;
import net.roarsoftware.lastfm.Chart;
import net.roarsoftware.lastfm.Track;
import net.roarsoftware.lastfm.User;


public class LastFM {

	private String username_;
	
	public LastFM(String username)
	{
		username_ = username;
	}
	
	public String getLastSong()
	{
		String song = null;
		String key = "4e5b94a79e7b8edc70f9875e191386ef"; //this is the key used in the last.fm API examples online.
		Collection<Track> tracks = User.getRecentTracks(username_, 1, key);
				
		for (Track track:tracks) {
			song = track.getName();
		}
		
		return song;
	}
}
