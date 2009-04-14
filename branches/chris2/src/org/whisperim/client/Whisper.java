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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.whisperim.file.FileStreamCoordinator;
import org.whisperim.prefs.GlobalPreferences;
import org.whisperim.security.Encryptor;
import org.xml.sax.SAXException;



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
	/**
	 * I take a xml element and the tag name, look for the tag and get
	 * the text content
	 * i.e for <employee><name>John</name></employee> xml snippet if
	 * the Element points to employee node and tagName is 'name' I will return John
	 * 
	 * Take from http://www.totheriver.com/learn/xml/xmltutorial.html#6.1.2
	 */
	private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}

	public KeyPair getKeys(){

		// Make sure that a keypair has been generated.
		
		InputStream istream = null;
		try {
			istream = GlobalPreferences.getInstance().getFSC().getInputStream("keys");
		}catch (FileNotFoundException e){
			generateXML(GlobalPreferences.getInstance().getFSC().getOutputStream("keys"));
		}
		try { 	
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc;
			istream = GlobalPreferences.getInstance().getFSC().getInputStream("keys");
			try {
				doc = docBuilder.parse(istream);
			} catch(SAXException e){
				generateXML(GlobalPreferences.getInstance().getFSC().getOutputStream("keys"));
				return getKeys();
			}

			// Normalize text representation.
			doc.getDocumentElement ().normalize ();


			NodeList myKeys = doc.getElementsByTagName("MyKeys");


			if (myKeys.getLength() == 0){
				/*KeyPair hasn't been generated,
				 *File was probably just created
				 */
				generateXML(GlobalPreferences.getInstance().getFSC().getOutputStream("keys"));
				doc = docBuilder.parse(GlobalPreferences.getInstance().getFSC().getInputStream("keys"));
			}

			try {

				// KeyPair has been generated.
				Element myKeysElement = (Element) myKeys.item(0);
				String keys[] = new String[2];
				keys[0] = getTextValue(myKeysElement, "PublicKey");
				keys[1] = getTextValue(myKeysElement, "PrivateKey");
				return Encryptor.generateRSAKeyPairFromString(keys);


			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (SAXException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		return null;


	}

	/**
	 * This is a helper method to generate the XML for the keys.
	 * It also handles generating a keypair for the user.  It will
	 * only be called if the key file doesn't exist already.
	 * @param keyFile
	 */
	private void generateXML(OutputStream keyFile){
		Document dom = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			// Get an instance of builder.
			DocumentBuilder db = dbf.newDocumentBuilder();

			// Create an instance of DOM.
			dom = db.newDocument();

		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
			return;
		}

		// Write the xml header.
		Element root = dom.createElement("Keys");
		dom.appendChild(root);

		KeyPair newKeyPair = Encryptor.generateRSAKeyPair();

		Base64 b64 = new Base64();

		String publicKeyString = new String(b64.encode(newKeyPair.getPublic().getEncoded()));

		String privateKeyString = new String (b64.encode(newKeyPair.getPrivate().getEncoded()));

		Element myKeys = dom.createElement("MyKeys");
		root.appendChild(myKeys);

		Element myPublic = dom.createElement("PublicKey");
		myPublic.appendChild(dom.createTextNode(publicKeyString));



		myKeys.appendChild(myPublic);

		Element myPrivate = dom.createElement("PrivateKey");

		myPrivate.appendChild(dom.createTextNode(privateKeyString));

		myKeys.appendChild(myPrivate);

		try{
			// Set output formatting.
			OutputFormat format = new OutputFormat(dom);
			format.setIndenting(true);

			XMLSerializer serializer = new XMLSerializer(
					keyFile, format);

			serializer.serialize(dom);

		} catch(IOException ie) {
			ie.printStackTrace();
		}
	}
}

