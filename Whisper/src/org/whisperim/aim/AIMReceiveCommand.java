/**
 * 
 */
package org.whisperim.aim;

import org.whisperim.client.Command;
import org.whisperim.client.ConnectionManager;
import org.whisperim.client.Message;

/**
 * @author Chris Thompson
 * This class is used by the AIM session to communicate with the main program 
 */
public class AIMReceiveCommand implements Command {
	
	private ConnectionManager connection_;
	private Message message_;

	@Override
	public void execute() {
		connection_.messageReceived(message_);

	}
	
	public AIMReceiveCommand(ConnectionManager connection){
		connection_ = connection;
	}

	public void setMessage_(Message message_) {
		this.message_ = message_;
	}

	public Message getMessage_() {
		return message_;
	}

}
