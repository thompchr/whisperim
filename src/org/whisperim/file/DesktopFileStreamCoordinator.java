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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DesktopFileStreamCoordinator implements OpenFileSystemCoordinator {

	private String homeDir_ = System.getProperty("user.home") + File.separator + "Whisper" + File.separator;

	/**
	 * This method assumes the files to be created
	 * or referenced are in Whisper's "home" directory.
	 * @see FileStreamCoordinator
	 */
	@Override
	public InputStream getInputStream(String file){
		File temp = new File (homeDir_ + file);
		if (temp.exists()){
			try {
				return new FileInputStream(temp);
			} catch (FileNotFoundException e) {
				// This should never happen thanks to
				//our check to see if it exists :)
				e.printStackTrace();
				return null;
			}
		}else{
			try {
				temp.createNewFile();
				return new FileInputStream(temp);
			} catch (IOException e) {
				//This could either be a FileNotFoundException
				//which shouldn't happen because it was just
				//created, or a generic exception associated
				//with the creation process.
				e.printStackTrace();
				return null;
			}

		}

	}

	/**
	 * This method assumes the files to be created
	 * or referenced are in Whisper's "home" directory.
	 * @see FileStreamCoordinator
	 */
	@Override
	public OutputStream getOutputStream(String file) {
		File temp = new File (homeDir_ + file);
		if (temp.exists()){
			try {
				return new FileOutputStream(temp);
			} catch (FileNotFoundException e) {
				// This should never happen thanks to
				//our check to see if it exists :)
				e.printStackTrace();
				return null;
			}
		}else{
			try {
				temp.createNewFile();
				return new FileOutputStream(temp);
			} catch (IOException e) {
				//This could either be a FileNotFoundException
				//which shouldn't happen because it was just
				//created, or a generic exception associated
				//with the creation process.
				e.printStackTrace();
				return null;
			}

		}
	}

	@Override
	public String getHomeDirectory() {
		return System.getProperty("user.home") + File.separator + "Whisper" + File.separator;
	}

}
