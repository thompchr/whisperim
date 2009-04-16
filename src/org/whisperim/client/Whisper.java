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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyRep;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.whisperim.file.FileStreamCoordinator;
import org.whisperim.keys.KeyContainer;
import org.whisperim.prefs.GlobalPreferences;
import org.whisperim.security.Encryptor;

import com.thoughtworks.xstream.XStream;



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
		KeyContainer kc = null;

		// Make sure that a keypair has been generated.

		InputStream istream = null;
		try {
			istream = GlobalPreferences.getInstance().getFSC().getInputStream("keys");
		}catch (FileNotFoundException e){
			generateXML(GlobalPreferences.getInstance().getFSC().getOutputStream("keys"));
		}

		XStream xs = new XStream();
		xs.alias("Keys", KeyContainer.class);
		xs.alias("PublicKey", PublicKey.class);
		xs.alias("PrivateKey", KeyRep.class);
		xs.alias("MyKeys", KeyPair.class);
		try {
			istream = GlobalPreferences.getInstance().getFSC().getInputStream("keys");
		} catch (FileNotFoundException e) {
			//Something went very wrong, we shouldn't be here
			return null;
		}
		kc = (KeyContainer)xs.fromXML(istream);
		return kc;	


	}

	/**
	 * This is a helper method to generate the XML for the keys.
	 * It also handles generating a keypair for the user.  It will
	 * only be called if the key file doesn't exist already.
	 * @param ostream - OutputStream to write keys to
	 */
	private void generateXML(OutputStream ostream){


		KeyContainer kc = new KeyContainer(Encryptor.generateRSAKeyPair());


		// Set output formatting.
		//			OutputFormat format = new OutputFormat(dom);
		//			format.setIndenting(true);
		//
		//			XMLSerializer serializer = new XMLSerializer(
		//					keyFile, format);
		//
		//			serializer.serialize(dom);

		XStream stream = new XStream();
		stream.alias("Keys", KeyContainer.class);
		stream.alias("PublicKey", PublicKey.class);
		stream.alias("PrivateKey", PrivateKey.class);
		stream.alias("MyKeys", KeyPair.class);
		
		try {
			ostream.write(stream.toXML(kc).getBytes());
		} catch (IOException e) {
			//Unable to write to the stream
			e.printStackTrace();
		}


	}
}

