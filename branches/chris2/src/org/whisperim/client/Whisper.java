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
import org.whisperim.file.FileStreamCoordinator;
import org.whisperim.keys.KeyContainer;
import org.whisperim.prefs.GlobalPreferences;
import org.whisperim.security.Encryptor;



/**
 * This class handles the keypair generation and sharing between clients.
 * The keys are built and shared as XML.
 * 
 * @author Kirk Banks, Chris Thompson, John Dlugokecki
 */

public class Whisper {  

	private FileStreamCoordinator coord_;

	public Whisper(FileStreamCoordinator coord){
		coord_ = coord;
		GlobalPreferences.getInstance().setFSC(coord_);

	}

	public KeyContainer getKeys(){
		return Encryptor.getKeys();
	}

}

