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

import java.util.Vector;

import net.jxta.endpoint.Message;

//  The class that manages the message queue.
public class EndpointReceiveQueue {

	public static final int Max_Messages = 100;

	// Message Queue.
	private Vector queue_ = null;
	private boolean closeFlag_ = false;

	// Constructor.
	public EndpointReceiveQueue() {
		queue_ = new Vector(Max_Messages);
		closeFlag_ = false;
	}

	// Constructor with size input.
	public EndpointReceiveQueue(int size) {
		queue_ = new Vector(size);
		closeFlag_ = false;
	}

	// Push Message into the queue.
	public synchronized void push(Message message) {

		// This queue is closed. No additional messages allowed.
		if (closeFlag_) {
			return;
		}

		if (queue_.size() == queue_.capacity()) {
			// Queue is full. Drop the oldest message
			queue_.removeElementAt(0);
		}

		queue_.addElement(message);

		notifyAll(); // Inform all those waiting.
	}

	// Return next message in queue.
	public synchronized Message next() {

		if (queue_.isEmpty()) {
			return null;
		}

		Message result = (Message) queue_.firstElement();
		queue_.removeElementAt(0);

		return result;
	}

	// Get message out of queue.
	public synchronized Message poll(long timeOut) throws InterruptedException {
		if (timeOut < 0)
			throw new IllegalArgumentException("timeOut must be >= 0");

		long realTimeOut = System.currentTimeMillis() + timeOut;

		do {
			Message result = next();

			if (null != result)
				return result;

			if (!isClosed()) {
				wait(timeOut);

				result = next();

				if (null != result)
					return result;

				if (0 != timeOut) {
					// reduce time because waiting is done.
					timeOut = (realTimeOut - System.currentTimeMillis());

					if (timeOut <= 0) {
						// Time expires, stop.
						break;
					}
				}
			}
		} while (!isClosed());

		return null; // Queue is closed.
	}

	// Wait until there is a Message in the queue, and return it.
	public Message waitForMessage() throws InterruptedException {
		return poll(0);
	}

	// Return if Queue is closed or not.
	public synchronized boolean isClosed() {
		return closeFlag_;
	}

	// Close the queue.
	public synchronized void close() {
		closeFlag_ = true;
		notifyAll(); // make sure anyone waiting knows about it.
	}

	// Max size of queue.
	public int getMaxNbOfMessages() {
		return queue_.capacity();
	}

	// Set storable amount of messages.
	public synchronized void setMaxNbOfMessages(int maxMsgs) {
		queue_.ensureCapacity(maxMsgs);
	}
}