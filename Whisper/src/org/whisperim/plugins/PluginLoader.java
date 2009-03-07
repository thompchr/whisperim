 /**************************************************************************
 * Copyright 2009 Chris Thompson                                           *
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

package org.whisperim.plugins;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.whisperim.client.ConnectionStrategy;
import org.whisperim.client.WhisperClient;

public class PluginLoader {
	
	private WhisperClient client_;
	
	public PluginLoader (WhisperClient client){
		client_ = client;
	}
	
	/**
	 * This method will load all plugins from the plugins directory
	 * that are not currently loaded and running.
	 */
	public void loadPlugins(){
		
	}
	
	/**
	 * This method will load in a plugin from a foreign location.
	 * It will first copy all necessary files (itemized in the xml file)
	 * to the plugin directory and then load the plugin.
	 * 
	 * This method will be exposed to the user through a drop-down menu.
	 * @param url - location (directory) of the plugin
	 * @throws FileNotFoundException - exception thrown when a file necessary
	 * 	to the plugin cannot be found.
	 */
	public void loadPluginFromExtLoc(String url) throws FileNotFoundException{
		File manifest = new File(url);
		if (!manifest.exists()){
			//Plugin is missing the manifest defining the plugin information
			throw new FileNotFoundException("Manifest file could not be found.");
		}
		//We have a manifest file, we now need to parse it to determine the 
		//entry point class and the type of plugin to use (as well as the
		//location of the jar file)
		
		//The type of plugin will dictate the interface that is used to access it
		
		try{
			

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc;

			doc = docBuilder.parse(manifest);

			doc.getDocumentElement().normalize();

			Element manifestElement = (Element)doc.getElementsByTagName("manifest");


			//This is confusing, but we have to cast two objects to Elements
			String type = ((Element)(
					manifestElement)
					.getElementsByTagName("type"))
					.getAttribute("value");

			String location = ((Element)
					((Element)manifestElement.getElementsByTagName("entrypoint"))
					.getElementsByTagName("location"))
					.getAttribute("value");
			
			//Make sure that we are using the correct directory separator for the
			//current OS
			location = location.replace("/", File.separator);
			location = location.replace("\\", File.separator);
			
			
			if (!location.startsWith(File.separator) || !url.endsWith(File.separator)){
				//We need to make it into a valid url
				location = File.separator + location;
			}
			
			if (location.startsWith(File.separator) && url.endsWith(File.separator)){
				location = location.replaceFirst(File.separator, "");
			}

			String entryClass = ((Element)
					((Element)manifestElement.getElementsByTagName("entrypoint"))
					.getElementsByTagName("class"))
					.getAttribute("value");
			
			String name = ((Element)(
					manifestElement)
					.getElementsByTagName("name"))
					.getAttribute("value");
			
			//Load in the class
			
			//This needs to be modified, we need to figure out some way 
			//to have operating-system-independent directory separators
			ClassLoader cl = DynamicClassLoader.getExtendedClassLoader(Thread
					.currentThread().getContextClassLoader(), url + location);
			Class c; 
			try{
				c = cl.loadClass(entryClass);
			}catch(Exception e){
				throw new Exception("Plugin could not be loaded");
			}
			
			if (type.equalsIgnoreCase("connection")){
				Class<ConnectionStrategy> cast = (Class<ConnectionStrategy>) c;
				client_.registerPlugin(name, WhisperClient.CONNECTION, cast);
			}
			
			copyDirectory(new File(url + location), new File(System.getProperty("user.home") + File.separator + "Whisper" 
					+ File.separator + "plugins" + File.separator + name));
			
			
	
		
		}catch(Exception e){
			//Need better exception handling
			e.printStackTrace();
		}
	}
	
	
	/**
	 * This static utility method will be used to generate a plugin registry file
	 * if one does not already exist.
	 */
	public static void generatePluginFile(){
		File pluginFile = new File(System.getProperty("user.home") + File.separator + "Whisper" 
					+ File.separator + "plugins" + File.separator + "plugins.xml");
		
		try {
			
			pluginFile.createNewFile();
			Document dom = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			// Get an instance of builder.
			DocumentBuilder db = dbf.newDocumentBuilder();

			// Create an instance of DOM.
			dom = db.newDocument();
			
			Element root = dom.createElement("Plugins");
			dom.appendChild(root);
			
			Element connectionsRoot = dom.createElement("Connections");
			dom.appendChild(connectionsRoot);
			
			Element lafRoot = dom.createElement("LookAndFeels");
			dom.appendChild(lafRoot);
			
			OutputFormat format = new OutputFormat(dom);
			format.setIndenting(true);

			XMLSerializer serializer = new XMLSerializer(
			new FileOutputStream(pluginFile), format);

			serializer.serialize(dom);


		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * This helper method will be used to copy the files in the external
	 * plugin directory to the Whisper plugin directory.
	 * @param src - The external directory to be copied from
	 * @param dest - The Whisper directory to be copied to
	 * @throws IOException 
	 */
	private void copyDirectory(File src, File dest) throws IOException{


		if (src.isDirectory()) {
			if (!dest.exists()) {
				dest.mkdir();
			}

			String[] children = src.list();
			for (int i=0; i<children.length; i++) {
				copyDirectory(new File(src, children[i]),
						new File(dest, children[i]));
			}
		} else {

			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}


	}

}
