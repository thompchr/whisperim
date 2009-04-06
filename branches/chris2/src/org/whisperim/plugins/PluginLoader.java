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

import org.whisperim.ui.WhisperClient;
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
	 * If no registry file exists it will create one.
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

			NodeList connections = ((Element)dom.getElementsByTagName("connection").item(0)).getElementsByTagName("plugin");

			for (int i = 0; i < connections.getLength(); ++i){

				Element curElement = (Element)connections.item(i);
				String name;
				try{
					name = ((Element)curElement.getElementsByTagName("name").item(0)).getAttribute("value");
				}catch(NullPointerException e){
					System.err.println("Name value was null and has not been set");
					name = "";
				}
				
				String location;
				try{
					location = ((Element)curElement.getElementsByTagName("location").item(0)).getAttribute("value");
				}catch (NullPointerException e){
					System.err.println("Location value was null, plugin cannot be loaded");
					break;
				}
				location = location.replace("/", File.separator);
				location = location.replace("\\", File.separator);

				
				String iconLocation;
				try{
					iconLocation = ((Element)curElement.getElementsByTagName("iconLocation").item(0)).getAttribute("value");
				}catch (NullPointerException e){
					System.err.println("IconLocation was null and has not been set");
					iconLocation = "";
				}
				
				String entryClass;
				try{
					entryClass = ((Element)curElement.getElementsByTagName("class").item(0)).getAttribute("value");
				}catch(NullPointerException e){
					System.err.println("Entry class was null, plugin cannot be loaded");
					break;
				}
				
				File dir = new File(location);
				if (!dir.exists()){
					System.err.println("File in registry doesn't exist");
				}
				if (dir.isDirectory()){
					System.out.println("File is a directory");
				}else{
					System.out.println("File is not a directory");
				}

				ClassLoader cl = DynamicClassLoader.getExtendedClassLoader(Thread
						.currentThread().getContextClassLoader(), location);
				 
				try{
					
					Plugin p = (Plugin) cl.loadClass(entryClass).newInstance();
					p.setIconLocation(iconLocation);
					p.setPluginName(name);

					client_.registerPlugin(name, WhisperClient.CONNECTION, p);
				}catch(Exception e){
					System.err.println(name + " could not be loaded.");
				}
				

			}

			NodeList lookAndFeel = ((Element)dom.getElementsByTagName("lookandfeel").item(0)).getElementsByTagName("plugin");

			for (int i = 0; i < lookAndFeel.getLength(); ++i){

				Element curElement = (Element)lookAndFeel.item(i);
				String name = ((Element)curElement.getElementsByTagName("name").item(0)).getAttribute("value");
				String location = ((Element)curElement.getElementsByTagName("location").item(0)).getAttribute("value");
				String entryClass = ((Element)curElement.getElementsByTagName("class").item(0)).getAttribute("value");

				ClassLoader cl = DynamicClassLoader.getExtendedClassLoader(Thread
						.currentThread().getContextClassLoader(), location);
				
				try{

					Plugin p = (Plugin)cl.loadClass(entryClass).newInstance();
					p.setPluginName(name);
					p.setIconLocation(location);
					client_.registerPlugin(p.getPluginName(), WhisperClient.LOOK_AND_FEEL, p);
				}catch(Exception e){
					System.err.println(name + " could not be loaded.");

				}
				

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

			url = url.replace("\\", File.separator);
			url = url.replace("/", File.separator);

			if (url.contains(".")){
				url = url.substring(0, url.lastIndexOf(File.separator));
			}

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc;

			doc = docBuilder.parse(manifest);

			doc.getDocumentElement().normalize();

			Element manifestElement = (Element)doc.getElementsByTagName("manifest").item(0);


			String type;
			try{
				type = ((Element)(manifestElement
						.getElementsByTagName("type")).item(0))
						.getAttribute("value");
			}catch(NullPointerException e){
				System.out.println("Type not defined");
				type = "";
			}

			String iconLocation;

			try{
				iconLocation = ((Element)(
						manifestElement
						.getElementsByTagName("iconLocation")).item(0))
						.getAttribute("value");
			}catch(NullPointerException e){
				System.out.println("Icon location not defined");
				iconLocation = "";

			}

			String location;
			try{
				location = ((Element)
						((Element)manifestElement.getElementsByTagName("entrypoint").item(0))
						.getElementsByTagName("location").item(0))
						.getAttribute("value");
			}catch(NullPointerException e){
				System.out.println("Location not defined");
				location = "";
			}




			//Make sure that we are using the correct directory separator for the
			//current OS
			location = location.replace("/", File.separator);
			location = location.replace("\\", File.separator);


			if (!location.startsWith(File.separator) && !url.endsWith(File.separator)){
				//We need to make it into a valid url
				location = File.separator + location;
			}

			if (location.startsWith(File.separator) && url.endsWith(File.separator)){
				location = location.replaceFirst(File.separator, "");
			}

			String entryClass;
			try{
				entryClass = ((Element)
						((Element)manifestElement.getElementsByTagName("entrypoint").item(0))
						.getElementsByTagName("class").item(0))
						.getAttribute("value");
			}catch (NullPointerException e){
				System.out.println("Entry class not defined");
				entryClass = "";
			}

			String name;
			try{
				name = ((Element)(
						manifestElement)
						.getElementsByTagName("name").item(0))
						.getAttribute("value");
			}catch (NullPointerException e){
				System.out.println("Name not defined");
				name = "";
			}


			//Load in the class
			
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
			}else if (type.equalsIgnoreCase("lookandfeel")){
				client_.registerPlugin(name, WhisperClient.LOOK_AND_FEEL, p);
			}else {
				client_.registerPlugin(name, 0, p);
			}

			copyDirectory(new File(url + location), new File(PLUGIN_DIR_ + File.separator + name + location));

			//We then need to add the plugin information to the local registry file
			File registry = new File(REGISTRY_);
			Document dom;
			if (!registry.exists()){
				generatePluginFile();
			}
			
			dom = DocumentBuilderFactory
			.newInstance()
			.newDocumentBuilder()
			.parse(registry);


			NodeList types = dom.getElementsByTagName(type);
			Element curPlugin = dom.createElement("plugin");
			Element nameElement = dom.createElement("name");
			Element locationElement = dom.createElement("location");
			Element classElement = dom.createElement("class");

			locationElement.setAttribute("value", PLUGIN_DIR_ + File.separator + name + location);
			classElement.setAttribute("value", entryClass);
			nameElement.setAttribute("value", name);

			curPlugin.appendChild(nameElement);
			curPlugin.appendChild(locationElement);
			curPlugin.appendChild(classElement);

			if (types == null){
				Element typeNode = dom.createElement(type);
				typeNode.appendChild(curPlugin);

			}
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
		File dir = new File(PLUGIN_DIR_);
		if (!dir.exists()){
			dir.mkdirs();
		}
		if (!pluginFile.exists()){
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

				Element connectionsRoot = dom.createElement("connection");
				root.appendChild(connectionsRoot);

				Element lafRoot = dom.createElement("lookandfeel");
				root.appendChild(lafRoot);

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
				if(!dest.mkdirs()){
					System.err.println("Directories could not be created");
				}
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
