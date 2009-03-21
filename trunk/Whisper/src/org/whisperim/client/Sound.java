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

import java.io.File;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class Sound implements ClientListener {
	
	public void playSound(String name){
		try{
			File soundFile = new File("..\\sounds\\" + name);
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
			audioInputStream = AudioSystem.getAudioInputStream(soundFile);
			AudioFormat audioFormat = audioInputStream.getFormat();
			DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
		
			byte tempBuffer[] = new byte[100000];
		    
			try{
		      SourceDataLine sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLineInfo);
		      sourceDataLine.open();
		      sourceDataLine.start();
		      audioInputStream.read(tempBuffer,0,tempBuffer.length);
		      sourceDataLine.write(tempBuffer, 0,tempBuffer.length);
		      sourceDataLine.drain();
		      sourceDataLine.close();
		    }catch (Exception e) {
		      System.err.println(e);
		    }		  
	    }catch (Exception e) {
		      System.err.println(e);
		}
	}

	@Override
	public void messageRec(Message message, String from) {
		playSound("IM.wav");
	}

	@Override
	public void statusChange() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendMessage() {
		playSound("IM.wav");
	}
}
