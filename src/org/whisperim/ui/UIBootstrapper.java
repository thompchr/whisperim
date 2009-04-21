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

import java.awt.EventQueue;

import org.whisperim.client.ConnectionManager;
import org.whisperim.client.MessageProcessor;
import org.whisperim.client.MessageProcessorImpl;
import org.whisperim.client.Whisper;
import org.whisperim.file.DesktopFileStreamCoordinator;

public class UIBootstrapper {
	
	public static void main(String[] args){
		MessageProcessor mp = new MessageProcessorImpl(new Whisper(new DesktopFileStreamCoordinator()).getKeys());
        final ConnectionManager cm = new ConnectionManager(mp);
		EventQueue.invokeLater(new Runnable() {
	           public void run() {
	             new WhisperClient(cm);

	           }
	        });
	}

}