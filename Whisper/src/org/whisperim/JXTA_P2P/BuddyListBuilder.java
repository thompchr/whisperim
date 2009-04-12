/**************************************************************************
 * Copyright 2009 Nick Krieble                                             *
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
package org.whisperim.JXTA_P2P;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import net.jxta.document.Advertisement;
import net.jxta.endpoint.Message;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.OutputPipeEvent;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.protocol.PipeAdvertisement;

/**
 *  Description of the Class
 */
public class BuddyListBuilder {

        public long lastChecked_ = 0;
        public String name_ = null;
        public String pipeID_ = null;
        private PeerGroup group_ = null;
        private PipeAdvertisement replyPipe_ = null;
        private PipePresence presence_ = null;

        // Constructor for the OnlineBuddy object. This will be put into WhisperBuddy List.
        public BuddyListBuilder( String name,
                        String pid,
                        PeerGroup group,
                        PipePresence presence,
                        PipeAdvertisement replyPipe ) {

                this.lastChecked_ = System.currentTimeMillis();
                this.pipeID_ = pid;
                this.name_ = name;
                this.group_ = group;
                this.replyPipe_ = replyPipe;
                this.presence_ = presence;
        }

        // Returns the group associated with the object.
        public PeerGroup getGroup() {
                return group_;
        }

        // Gets the replyPipe 
        public PipeAdvertisement getReplyPipe() {
                return replyPipe_;
        }

		public boolean sendMessage(Message msg) {
			// TODO Auto-generated method stub
			return false;
		}
}