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
package org.whisperim.android.files;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

import org.whisperim.file.FileStreamCoordinator;

import android.content.Context;

public class AndroidFileStreamCoordinator implements FileStreamCoordinator {
	
	private Context cx_;
	
	public AndroidFileStreamCoordinator(Context cx){
		cx_ = cx;
	}

	@Override
	public InputStream getInputStream(String name) throws FileNotFoundException{
			return cx_.openFileInput(name);
	}

	@Override
	public OutputStream getOutputStream(String name) {
		
		try {
			return cx_.openFileOutput(name, Context.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			return null;
		}
	}

}
