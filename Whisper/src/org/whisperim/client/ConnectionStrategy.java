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

import javax.speech.AudioException;
import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.swing.ImageIcon;

import org.whisperim.plugins.Plugin;

public interface ConnectionStrategy extends Plugin {
	
	public static final int OFFLINE = 0;
	public static final int INVALID_PASSWORD = 1;
	public static final int ACTIVE = 3;
	public static final int RATE_LIMITED = 4;
	public static final int SERVICE_UNAVAILABLE = 5;

	public void signOn(ConnectionManager cm, String username, String password);
	
	public void signOff();
	
	public void sendMessage(Message message);
	
	public void receiveMessage(Message message) throws IllegalArgumentException, EngineException, EngineStateError, AudioException, InterruptedException;
	
	public void receiveBuddies(ArrayList<Buddy> buddies);

    public void statusUpdate(String status);
    
    public void setAwayMessage(String message);
    
    public void setIdle();
    
    public void setInvisible(boolean visible);
    
    public String getProtocol();
    
    public String getIdentifier();
    
    public void setHandle(String handle);
    
    public String getHandle();
    
    public String toString();
    
    public int getStatus();
    
    public void setStatusMessage(String message);

	public ImageIcon getServiceIcon();
}
