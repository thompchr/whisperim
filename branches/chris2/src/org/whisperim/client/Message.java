 /**************************************************************************
 * Copyright 2009 John Dlugokecki                                         *
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

package org.whisperim.client;
import java.util.Date;

/**
 * This class contains the content and meta data about messages sent
 * between clients.
 * 
 * @author Kirk Banks, Chris Thompson, John Dlugokecki
 */

public class Message {

	private Buddy from_;
	
	private Buddy to_;
	
	private String message_;
	
	private Date timeSent_;
	
	//Protocol identifier specified in the
	//ConnectionStategy object.
	private String protocolID_;
	
	private String other_ = null;
	
	/**
	 * 
	 * @param from
	 * @param to
	 * @param message
	 * @param date
	 */
	public Message(Buddy from, Buddy to, String message, String protocol, Date date){
		from_ = from;
		to_ = to;
		message_ = message;
		timeSent_ = date;
		protocolID_ = protocol;
	}
	
	/**
	 * 
	 * @param from
	 * @param to
	 * @param message
	 * @param timeSent
	 * @param other
	 */
	public Message(Buddy from, Buddy to, String message, Date timeSent, String protocol, String other){
		from_ = from;
		to_ = to;
		message_ = message;
		timeSent_ = timeSent;
		other_ = other;
	}
	
	public void setMessage(String message){
		message_ = message;
	}
	
	public String getFrom(){
		return from_.getHandle();
	}
	
	public String getTo(){
		return to_.getHandle();
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
	
	public String getProtocol(){
		return protocolID_;
	}
	
	public void setProtocol(String protocol){
		protocolID_ = protocol;
	}
	
	public Buddy getFromBuddy(){
		return from_;
	}
	
	
	
}
