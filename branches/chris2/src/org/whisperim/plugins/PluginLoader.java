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
import java.util.ArrayList;

import org.whisperim.file.OpenFileSystemCoordinator;
import org.whisperim.prefs.GlobalPreferences;
import org.whisperim.ui.WhisperClient;

import com.thoughtworks.xstream.XStream;

public class PluginLoader {


	private static String PLUGIN_DIR_ = null;

	private WhisperClient client_;

	private PluginRegistry pr_;

	public PluginLoader (WhisperClient client){
		client_ = client;
		if (GlobalPreferences.getInstance() instanceof OpenFileSystemCoordinator){
			PLUGIN_DIR_ = ((OpenFileSystemCoordinator)GlobalPreferences.getInstance()).getHomeDirectory() + "plugins";
			
		}else{
			
			PLUGIN_DIR_ = "";
		}
	}


	public WhisperClient getClient() {
		return client_;
	}
	


	/**
	 * This method will load all plugins from the plugins directory
	 * into the plugins directory.
	 * 
	 * It also will be called when the program initializes.
	 * 
	 * If no registry file exists it create a new, empty registry object
	 * @throws Exception - Thrown if the plugin could not be loaded correctly
	 */
	public void loadPlugins() throws Exception{
		XStream xs = new XStream();
		try{
			pr_ = (PluginRegistry)xs.fromXML(GlobalPreferences.getInstance().getFSC().getInputStream("plugins" + File.separator + "plugins.xml"));
		}catch (Exception e) {
			//Could not read from previous plugin registry
			//Might not exist or a problem occurred.
			
			pr_ = new PluginRegistry();
		}
	}
	
	
	/**
	 * This method is designed to load in all plugins for a given extention point.
	 * It creates an ArrayList of Class objects that the caller can instantiate.
	 * @param extentionPoint - The extention point name.
	 * @return ArrayList<Class>
	 * @throws Exception - Exception thrown when plugin cannot be loaded
	 */
	public ArrayList<Class> loadPlugins(String extentionPoint) throws Exception{
		ArrayList<Class> temp = new ArrayList<Class>();
		for (PluginRegistryEntry p:pr_.getPluginsForExtentionPoint(extentionPoint)){
			ClassLoader cl = DynamicClassLoader.getExtendedClassLoader(Thread
					.currentThread().getContextClassLoader(), p.getLocation());
			
			try{
				temp.add(cl.loadClass(p.getEntryClass()));
			}catch(Exception e){
				throw new Exception("Plugin could not be loaded");
			}
		}
		return temp;
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

		url = url.replace("\\", File.separator);
		url = url.replace("/", File.separator);

		if (url.contains(".")){
			url = url.substring(0, url.lastIndexOf(File.separator));
		}
		//Read the manifest
		XStream xs = new XStream();
		xs.alias("manifest", PluginRegistryEntry.class);
		xs.aliasField("name", PluginRegistryEntry.class, "name_");
		xs.aliasField("entryClass", PluginRegistryEntry.class, "entryClass_");
		xs.aliasField("extentionPoint", PluginRegistryEntry.class, "extentionPoint_");
		xs.aliasField("location", PluginRegistryEntry.class, "location_");

		PluginRegistryEntry p = (PluginRegistryEntry)xs.fromXML(new FileInputStream(manifest));
		String location = p.getLocation();

		//Make sure that we are using the correct directory separator for the
		//current OS
		location.replace("/", File.separator).replace("\\", File.separator);

		if (!location.startsWith(File.separator) && !url.endsWith(File.separator)){
			//We need to make it into a valid url
			location = File.separator + location;
		}
		if (location.startsWith(File.separator) && url.endsWith(File.separator)){
			location = location.replaceFirst(File.separator, "");
		}

		p.setLocation(location);
		
		pr_.registerPlugin(p);

		try {
			copyDirectory(new File(url + location), new File(PLUGIN_DIR_ + File.separator + p.getName() + location));
		} catch (IOException e) {
			//File could not be created
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
