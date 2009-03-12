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

package org.whisperim.prefs;

public class LookAndFeelStateContext {
	 private LookAndFeelState nativeState_;
	 private LookAndFeelState metalState_; 
	 private LookAndFeelState state_; 
	 
	//Shouldn't this be a constructor?
	 public void StateContext() 
	{   
		 //nativeState_ = new nativeState();   
		 //metalState_ = new metalState();     
		 state_ = null; 
	}  
	public String getState() 
	{   return state_.getState(); 
	} 
	
	public void setState(LookAndFeelState state)
	{   
		state_ = state; 
	}
	
	public void setNativeState() 
	{  
		state_ = nativeState_; 
	} 
	
	public void setMetalState()
	{
		state_ = metalState_;
	}
	public LookAndFeelState  getNativeState() 
	{  
		return nativeState_; 
	} 
	
	public LookAndFeelState getmetalState() 
	{   return metalState_; 
	} 
}