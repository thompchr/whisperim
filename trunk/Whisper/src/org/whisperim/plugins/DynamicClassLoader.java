package org.whisperim.plugins;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.HashMap;
import java.util.Map;

/*******************************************************************************
 * Copyright (c) 2005, 2006 Jules White. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Jules White - initial API and implementation
 ******************************************************************************/

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
					exturls[i] = files[i].toURL();
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
