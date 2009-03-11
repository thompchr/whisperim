/**************************************************************************
 * Copyright 2009 Cory Plastek                                             *
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

import javax.swing.UIManager;

public class LookAndFeelMetalState implements LookAndFeelState {
	
	public void setState(LookAndFeelStateContext ctx) {
		   System.out.println("Setting metal look and feel");
		   UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
	 }

	public String getState() {
		   return "metalState";
	 }
	
}
