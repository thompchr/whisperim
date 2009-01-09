package org.vanderbilt.client;

import java.util.Date;

public class Message {

	private String from_;
	
	private String to_;
	
	private String message_;
	
	private Date timeSent_;
	
	private String other_ = null;
	
	/**
	 * 
	 * @param from
	 * @param to
	 * @param message
	 * @param date
	 */
	public Message(String from, String to, String message, Date date){
		from_ = from;
		to_ = to;
		message_ = message;
		timeSent_ = date;
	}
	
	/**
	 * 
	 * @param from
	 * @param to
	 * @param message
	 * @param timeSent
	 * @param other
	 */
	public Message(String from, String to, String message, Date timeSent, String other){
		from_ = from;
		to_ = to;
		message_ = message;
		timeSent_ = timeSent;
		other_ = other;
	}
	
	public String getFrom(){
		return from_;
	}
	
	public String getTo(){
		return to_;
	}
	
	public String getMessage(){
		return message_;
	}
	
	public Date getTimeSent(){
		return timeSent_;
	}
	
	public String getOther(){
		return other_;
	}
	
	
	
}
