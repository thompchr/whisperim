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
import org.w3c.dom.NodeList;
import org.whisperim.client.ConnectionStrategy;
import org.whisperim.client.WhisperClient;
import org.xml.sax.SAXException;

public class PluginLoader {

	private static final String REGISTRY_ = System.getProperty("user.home") + File.separator + "Whisper" 
						+ File.separator + "plugins" + File.separator + "plugins.xml";
	
	private static final String PLUGIN_DIR_ = System.getProperty("user.home") + File.separator + "Whisper" 
						+ File.separator + "plugins";

	private WhisperClient client_;
	

	public PluginLoader (WhisperClient client){
		client_ = client;
	}


	public WhisperClient getClient() {
		return client_;
	}


	/**
	 * This method will load all plugins from the plugins directory
	 * that are not currently loaded and running.
	 * 
	 * It also will be called when the program initializes.
	 * 
	 * If no registry file exists
	 * @throws Exception - Thrown if the plugin could not be loaded correctly
	 */
	public void loadPlugins() throws Exception{
		try {
			File registry = new File(REGISTRY_);
			
			if (!registry.exists()){
				generatePluginFile();
				return;
			}


			Document dom = DocumentBuilderFactory
							.newInstance()
							.newDocumentBuilder()
							.parse(registry);
			
			NodeList connections = ((Element)dom.getElementsByTagName("connection")).getElementsByTagName("plugin");
			
			for (int i = 0; i < connections.getLength(); ++i){
				
				Element curElement = (Element)connections.item(i);
				String name = ((Element)curElement.getElementsByTagName("name")).getAttribute("value");
				String location = ((Element)curElement.getElementsByTagName("location")).getAttribute("value");
				String iconLocation = ((Element)curElement.getElementsByTagName("iconLocation")).getAttribute("value");
				String entryClass = ((Element)curElement.getElementsByTagName("class")).getAttribute("value");
				
				
				
				ClassLoader cl = DynamicClassLoader.getExtendedClassLoader(Thread
						.currentThread().getContextClassLoader(), PLUGIN_DIR_ + location);
				Class c; 
				try{
					c = cl.loadClass(entryClass);
				}catch(Exception e){
					throw new Exception("Plugin could not be loaded");
				}
				Plugin p = (Plugin) c.newInstance();
				p.setIconLocation(iconLocation);
				p.setPluginName(name);
				
				client_.registerPlugin(name, WhisperClient.CONNECTION, p);
				
			}
			
			NodeList lookAndFeel = ((Element)dom.getElementsByTagName("lookandfeel")).getElementsByTagName("plugin");
			
			for (int i = 0; i < lookAndFeel.getLength(); ++i){
				
				Element curElement = (Element)lookAndFeel.item(i);
				String name = ((Element)curElement.getElementsByTagName("name")).getAttribute("value");
				String location = ((Element)curElement.getElementsByTagName("location")).getAttribute("value");
				String entryClass = ((Element)curElement.getElementsByTagName("class")).getAttribute("value");
				
				ClassLoader cl = DynamicClassLoader.getExtendedClassLoader(Thread
						.currentThread().getContextClassLoader(), PLUGIN_DIR_ + location);
				Class c; 
				try{
					c = cl.loadClass(entryClass);
				}catch(Exception e){
					throw new Exception("Plugin could not be loaded");
				}
				Plugin p = (Plugin)c.newInstance();
				client_.registerPlugin(p.getPluginName(), WhisperClient.LOOK_AND_FEEL, p);
				
			}
			
			
		} catch (SAXException e) {

			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			
			e.printStackTrace();
		}


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
			
			String iconLocation = ((Element)(
					manifestElement)
					.getElementsByTagName("iconLocation"))
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
			
			Plugin p = (Plugin) c.newInstance();
			p.setIconLocation(iconLocation);
			p.setPluginName(name);

			if (type.equalsIgnoreCase("connection")){
				client_.registerPlugin(name, WhisperClient.CONNECTION, p);
			}

			copyDirectory(new File(url + location), new File(System.getProperty("user.home") + File.separator + "Whisper" 
					+ File.separator + "plugins" + File.separator + name));

			//We then need to add the plugin information to the local registry file
			File registry = new File(REGISTRY_);

			Document dom = DocumentBuilderFactory
			.newInstance()
			.newDocumentBuilder()
			.parse(registry);

			NodeList types = dom.getElementsByTagName(type);
			Element curPlugin = dom.createElement("plugin");
			Element nameElement = dom.createElement("name");
			Element locationElement = dom.createElement("location");
			Element classElement = dom.createElement("class");

			locationElement.setAttribute("value", File.separator + name + location);
			classElement.setAttribute("value", entryClass);
			nameElement.setAttribute("value", name);

			curPlugin.appendChild(nameElement);
			curPlugin.appendChild(locationElement);
			curPlugin.appendChild(classElement);

			((Element)types.item(0)).appendChild(curPlugin);


			OutputFormat format = new OutputFormat(dom);
			format.setIndenting(true);

			XMLSerializer serializer = new XMLSerializer(
					new FileOutputStream(registry), format);

			serializer.serialize(dom);


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
		File pluginFile = new File(REGISTRY_);

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
