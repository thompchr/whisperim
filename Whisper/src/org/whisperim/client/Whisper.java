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
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.SwingUtilities;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.PrivateKey;

//XML Imports
import java.io.File;
import org.w3c.dom.*;
import org.whisperim.keys.*;
import org.whisperim.security.Encryptor;
import org.apache.xml.serialize.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException; 


import com.sun.org.apache.xml.internal.security.utils.Base64;

/**
 * This class handles the keypair generation and sharing between clients.
 * The keys are built and shared as XML.
 * 
 * @author Kirk Banks, Chris Thompson, John Dlugokecki
 */

public class Whisper {  

	private ArrayList<Buddy> buddies_ = new ArrayList<Buddy>();
	
	private PrivateKey myPrivateKey_;
	private PublicKey myPublicKey_;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Whisper main = new Whisper();

	}
	
	public Whisper(){
		
		File dir = new File(System.getProperty("user.home") + File.separator + "Whisper");
		File keyFile = new File(System.getProperty("user.home") + File.separator + "Whisper" + File.separator + "keys");
		
		
		if (dir.exists()){
			//Directory exists, check to see if file exists
			if (!keyFile.exists()){
				//Key store does not exist, create it
				try {
					keyFile.createNewFile();
					
					generateXML(keyFile);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//Determine if the file has been properly constructed as an XML file
			
			
		}else{
			//Directory does not exist, create it and the file
			try{
				dir.mkdir();
				keyFile.createNewFile();
				
				generateXML(keyFile);

			}catch (IOException e){
				e.printStackTrace();
			}
		}

		//Make sure that a keypair has been generated
		try { 	
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc;

			doc = docBuilder.parse (keyFile);

			// normalize text representation
			doc.getDocumentElement ().normalize ();
			

			NodeList myKeys = doc.getElementsByTagName("MyKeys");
			
							
			if (myKeys.getLength() == 0){
				//KeyPair hasn't been generated
				//Something is wrong with the file
				

			}else{
				try {
					
					//KeyPair has been generated
					Element myKeysElement = (Element) myKeys.item(0);
					String keys[] = new String[2];
					keys[0] = getTextValue(myKeysElement, "PublicKey");
					keys[1] = getTextValue(myKeysElement, "PrivateKey");
					KeyPair keyPair = Encryptor
							.generateRSAKeyPairFromString(keys);
					myPrivateKey_ = keyPair.getPrivate();
					myPublicKey_ = keyPair.getPublic();
				
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		} catch (SAXException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		java.awt.EventQueue.invokeLater(new Runnable() {
	           public void run() {
	               Login client = new Login(myPublicKey_, myPrivateKey_);
	               client.setVisible(true);
	           }
	        });
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
	
	public void sendMessage(Message message){
		
	}

	/**
	 * This is a helper method to generate the XML for the keys.
	 * It also handles generating a keypair for the user.  It will
	 * only be called if the key file doesn't exist already.
	 * @param keyFile
	 */
	private void generateXML(File keyFile){
		Document dom = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
		//get an instance of builder
		DocumentBuilder db = dbf.newDocumentBuilder();

		//create an instance of DOM
		dom = db.newDocument();

		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
			return;
		}
		
		//Write the xml header
		Element root = dom.createElement("Keys");
		dom.appendChild(root);
		
		KeyPair newKeyPair = Encryptor.generateRSAKeyPair();

		String publicKeyString = new String(Base64.encode(newKeyPair.getPublic().getEncoded()));

		String privateKeyString = new String (Base64.encode(newKeyPair.getPrivate().getEncoded()));
		
		Element myKeys = dom.createElement("MyKeys");
		root.appendChild(myKeys);
		
		Element myPublic = dom.createElement("PublicKey");
		
		myPublic.setTextContent(publicKeyString);
		
		myKeys.appendChild(myPublic);
		
		Element myPrivate = dom.createElement("PrivateKey");
		
		myPrivate.setTextContent(privateKeyString);
		
		myKeys.appendChild(myPrivate);
		
		try
		{
			//Set output formatting
			OutputFormat format = new OutputFormat(dom);
			format.setIndenting(true);

			XMLSerializer serializer = new XMLSerializer(
			new FileOutputStream(keyFile), format);

			serializer.serialize(dom);

		} catch(IOException ie) {
		    ie.printStackTrace();
		}
	}
}

