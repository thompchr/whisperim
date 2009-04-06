 /**************************************************************************
 * Copyright 2009 Chris Thompson                                           *
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

package org.whisperim.ui;

import java.util.ArrayList;

import org.whisperim.client.Buddy;
import org.whisperim.client.Message;
import org.whisperim.client.MessageProcessor;

public interface UIController {
	
	public void receiveMessage (Message m);
	
	public void addBuddies (ArrayList<Buddy> buddies);

	public void removeBuddies (ArrayList<Buddy> buddies);
	
	public void keyReceived (Buddy b);
	
	public void setMessageProcessor(MessageProcessor mp);

}
