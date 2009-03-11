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

package org.whisperim.client;

public class LookAndFeelContext {
	 private State nativeState_;
	 private State metalState_; 
	 private State state_; 
	 
	public StateContext() 
	{   
		 nativeState_ = new nativeState();   
		 metalState_ = new metalState();     
		 state_ = null; 
	}  
	public String getStatus() 
	{   return state_.getStatus(); 
	} 
	
	public void setState(State state)
	{   
		state_ = state; 
	}
	
	public void setNativeState() 
	{  
		state_ = nativeState; 
	} 
	
	public void setMetalState()
	{
		state_ = metalState;
	}
	public State getNativeState() 
	{  
		return nativeState_; 
	} 
	
	public State getmetalState() 
	{   return metalState_; 
	} 
}