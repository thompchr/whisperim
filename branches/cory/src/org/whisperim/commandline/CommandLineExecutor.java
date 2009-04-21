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
package org.whisperim.commandline;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CommandLineExecutor {

	public CommandLineExecutor(){

	}

	/**
	 * This method is used to execute system commands.  If the application
	 * is running on a Windows system, it will add the appropriate command
	 * to the front of the supplied command.
	 * @param command - The command to be executed
	 * @param workingDir - The directory to execute the command in
	 * @return InputStream[] - An array of input streams, the input stream 
	 * from the executed command and the error stream respectively.
	 */
	public InputStream[] execute(String command, String workingDir){
		workingDir = workingDir.replace("/", File.separator).replace("\\", File.separator);
		if (System.getProperty("os.name").contains("Windows")){
			command = "cmd /c " + command;
		}

		try {
			Runtime rt = Runtime.getRuntime();
			InputStream[] is = new InputStream[2];
			Process p = rt.exec(command, null, new File(workingDir));
			is[0] = p.getInputStream();
			is[1] = p.getErrorStream();
			return is;

		}catch (Exception e) {
			ByteArrayInputStream err = new ByteArrayInputStream(e.toString().getBytes());
			return new InputStream[] {err , err };
		}
	}

	public static void main (String[] args){
		CommandLineExecutor cle = new CommandLineExecutor();
		BufferedReader input = new BufferedReader(new InputStreamReader(cle.execute("dir", "C:\\Users\\Chris Thompson")[0]));

		String line=null;

		try {
			while((line=input.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
}
