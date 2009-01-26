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
