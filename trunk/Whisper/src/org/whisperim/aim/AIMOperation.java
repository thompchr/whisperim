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
package org.whisperim.aim;


/**
 * This class acts as a helper class to generate operations to pass to the AIMSession class
 * and it's running thread.  It also contains the enumerations required to designate operations.
 * AIMOperation objects are passed to the AIMSession object and are then placed in a queue to be
 * performed.
 * @author Chris Thompson
 *
 */
public class AIMOperation {
	
	public final static int SIGNIN = 1;
	public final static int SIGNOUT = 2;
	public final static int SEND_MESSAGE = 3;
	
	private Integer operation_;
	private Object[] arguments_;
	
	/**
	 * Returns the operation associated with the object.  Signin, signout, send_message, etc.
	 * @return Integer
	 */
	public Integer getOperation() {
		return operation_;
	}
	
	/**
	 * Set method for the operation_ object.
	 * @param operation
	 */
	public void setOperation(Integer operation) {
		operation_ = operation;
	}
	
	/**
	 * Get method for the arguments_ array.
	 * @return Object[]
	 */
	public Object[] getArguments() {
		return arguments_;
	}
	
	/**
	 * Set method for the arguments_ array.
	 * @param arguments
	 */
	public void setArguments(Object[] arguments) {
		this.arguments_ = arguments;
	}
	
	/**
	 * Constructor for the AIMOperation class.  
	 */
	private AIMOperation() {
		super();
	}
	
	
	/**
	 * This method creates and returns an AIMOperation to sign in to the AIM service.
	 * @param handle
	 * @param password
	 * @return AIMOperation
	 */
	public static AIMOperation createSignIn(String handle, String password) {
		AIMOperation op = new AIMOperation();
		op.operation_ = SIGNIN;
		Object[] args = { handle, password }; 
		op.arguments_ = args;
		return op;
	}

	/**
	 * This method creates and returns an AIMOperation to sign out of the AIM service.
	 * @return AIMOperation
	 */
	public static AIMOperation createSignOut() {
		AIMOperation op = new AIMOperation();
		op.operation_ = SIGNOUT;
		op.arguments_ = new Object[0];
		return op;
	}
	
	/**
	 * This method creates and returns an AIMOperation object to send a message through
	 * the AIM service.
	 * @param handle
	 * @param message
	 * @return AIMOperation
	 */
	public static AIMOperation createSendMessage(String handle, String message) {
		AIMOperation op = new AIMOperation();
		op.operation_ = SEND_MESSAGE;
		Object[] args = { handle, message }; 
		op.arguments_ = args;
		return op;
	}


}

