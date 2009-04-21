/**************************************************************************
 * Copyright 2009 Nick Krieble                                             *
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
package org.whisperim.JXTA_P2P;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

// Stores Preferences of users.
public class PreferenceReader extends Properties {
	// The instance of the PreferenceReader.
	private static PreferenceReader reader = null;

	// The filename of the properties file.
	private static final String filename = "WhisperJXTAP2P.properties";

	// Constructor.
	private PreferenceReader() {
		super();

		reader = this;
		try {
			InputStream stream = new BufferedInputStream(new FileInputStream(
					filename));
			load(stream);
		} catch (Exception iox) {
			// No preferences found.
		}
	}

	// Gets this instance of this PreferenceReader
	public static PreferenceReader getInstance() {
		if (reader == null) {
			reader = new PreferenceReader();
		}
		return reader;
	}

	// Save data.
	public void save() {
		try {
			OutputStream stream = new BufferedOutputStream(
					new FileOutputStream(filename));
			store(stream, null);
			stream.flush();
			stream.close();
		} catch (Exception iox) {
			// Fails
		}
	}

}
