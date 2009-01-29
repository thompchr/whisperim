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

/**
 * This class is designed to help keep track of which buddy
 * is in each window and each associated service
 * @author Chris Thompson
 *
 */
public class Buddy {

	private String handle_;
	private int managerID_;
	private int windowID_;
	
	//TODO Add an exception for a null value passed to this constructor
	public Buddy(String handle, int managerID, int windowID){
		handle_ = handle;
		managerID_ = managerID;
		windowID_ = windowID;
	}
	
	public String getHandle(){
		return handle_;
	}
	
	public int getManagerID(){
		return managerID_;
	}
	
	public int getWindowID_(){
		return windowID_;
	}
	
	public void setHandle (String handle){
		handle_ = handle;
	}
	
	public void setManagerID(int managerID){
		managerID_ = managerID;
	}
	
	public void setWindowID(int windowID){
		windowID_ = windowID;
	}
}
