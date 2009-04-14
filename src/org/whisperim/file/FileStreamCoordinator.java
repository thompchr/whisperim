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
package org.whisperim.file;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

public interface FileStreamCoordinator {
	
	/**
	 * This method is used to get an output stream and 
	 * must be defined by the specific UI.  This allows
	 * for system independent file-system interactions.
	 * If the file/object does not exist, it should be created.
	 * @param name - Name of the object to get the stream.
	 * For example, this could be the filename associated
	 * with this output stream.
	 * @return OutputStream
	 */
	public OutputStream getOutputStream(String name);
	
	
	/**
	 * This method is used to get an input stream
	 * and must be defined by the specific UI.  This
	 * allows for system independent file-system interactions.
	 * If the file/object does not exist, it should be 
	 * created.
	 * @param name - Name of the object associated with the stream.
	 * @return InputStream
	 */
	public InputStream getInputStream(String name) throws FileNotFoundException;

}
