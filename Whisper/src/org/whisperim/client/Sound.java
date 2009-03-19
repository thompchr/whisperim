 /**************************************************************************
 * Copyright 2009                                                          *
 * Kirk Banks   				                                           *
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

import sun.applet.Main;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound implements ClientListener {

	//This may not work, still working on implementing this
	  public static synchronized void playSound(final String url) {
		    new Thread(new Runnable() {
		      public void run() {
		        try {
		          Clip clip = AudioSystem.getClip();
		          AudioInputStream inputStream = AudioSystem.getAudioInputStream(Main.class.getResourceAsStream("..\\images\\" + url));
		          clip.open(inputStream);
		          clip.start(); 
		        } catch (Exception e) {
		          System.err.println(e.getMessage());
		        }
		      }
		    }).start();
		  }	
	
	@Override
	public void messageRec(Message message, String from) {
		Sound.playSound("receiveIM.mp3");	
	}

	@Override
	public void statusChange() {
		// TODO Auto-generated method stub
		
	} 
}
