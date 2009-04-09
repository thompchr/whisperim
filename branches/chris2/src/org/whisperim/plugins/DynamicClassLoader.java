package org.whisperim.plugins;

/**************************************************************************
 * Copyright 2009 Jules White                                              *
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


import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.HashMap;
import java.util.Map;



public class DynamicClassLoader extends URLClassLoader {

	private static Map<String, DynamicClassLoader> loaders_ = new HashMap<String, DynamicClassLoader>();

	public static DynamicClassLoader getExtendedClassLoader(ClassLoader parent,
			String jardirectory) {
		return getExtensionClassLoader(parent, jardirectory);
	}

	public static DynamicClassLoader getExtensionClassLoader(
			ClassLoader parent, String jardir) {
		DynamicClassLoader cl = loaders_.get(jardir);
		if (cl == null) {
			URL[] exturls = getExtensionJarURLs(jardir);
			cl = new DynamicClassLoader(exturls, parent);
			loaders_.put(jardir, cl);
		}
		return cl;
	}

	public static URL[] getExtensionJarURLs(String srcdir) {
		URL[] exturls = new URL[0];
		File extdir = new File(srcdir);

		if (extdir.exists() && extdir.isDirectory()) {
			File[] files = extdir.listFiles(new FilenameFilter() {

				public boolean accept(File arg0, String arg1) {
					return arg1.endsWith(".jar");
				}

			});
			exturls = new URL[files.length];
			for (int i = 0; i < files.length; i++) {
				try {
					
					//exturls[i] = new URL("jar", "", files[i].getCanonicalPath());
					exturls[i] = files[i].toURI().toURL();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return exturls;
	}

	private DynamicClassLoader(URL[] arg0, ClassLoader arg1,
			URLStreamHandlerFactory arg2) {
		super(arg0, arg1, arg2);
	}

	private DynamicClassLoader(URL[] arg0, ClassLoader arg1) {
		super(arg0, arg1);
	}

	private DynamicClassLoader(URL[] arg0) {
		super(arg0);
	}

}
