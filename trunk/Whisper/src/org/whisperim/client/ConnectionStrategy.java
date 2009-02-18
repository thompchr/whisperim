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

import java.util.ArrayList;

public interface ConnectionStrategy {

	public void signOn(String username, String password);
	
	public void signOff();
	
	public void sendMessage(Message message);
	
	public void receiveMessage(Message message);
	
	public void receiveBuddies(ArrayList<Buddy> buddies);

    public void statusUpdate(String status);
    
    public void setAwayMessage(String message, boolean away);
    
    public String getProtocol();
    
    public String getIdentifier();
    
}
