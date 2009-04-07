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
package org.whisperim.events;

import org.whisperim.client.Buddy;

public class EncryptionEvent implements WhisperEvent {

	private Buddy b_;
	private int encStatus_;
	
	public static int ENCRYPTION_ENABLED = 0;
	public static int ENCRYPTION_DISABLED = 1;

	public EncryptionEvent (Buddy b){
		b_ = b;
		encStatus_ = ENCRYPTION_DISABLED;
	}
	
	public EncryptionEvent(Buddy b, int newStatus){
		b_ = b;
		encStatus_ = newStatus;
	}
	
	public Buddy getAffectedBuddy(){
		return b_;
	}
	
	public int getEncryptionStatus(){
		return encStatus_;
	}
	
	public void setEncryptionStatus(int s){
		encStatus_ = s;
	}
	
	
	
}
