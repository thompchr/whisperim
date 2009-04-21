/**************************************************************************
 * Copyright 2009 Cory Plastek                                             *
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
/**
 * PreferencesWindowAbout.java
 * @author Cory Plastek
 */
package org.whisperim.prefs;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;


public class PreferencesWindowAbout extends JPanel {
	
	private static final long serialVersionUID = 4465197084091323858L;
	private String aboutLocation_ = "..//images//About.txt";
	private String aboutText_;
	private PlainDocument aboutDoc_;
	private JTextArea about_;
	private JScrollPane aboutScrollPane_;
	
	//platform independent newline character
	private String newLine_ = System.getProperty("line.separator");

	
	PreferencesWindowAbout() {
		
		setBackground(Color.white);
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(aboutLocation_));
			StringBuffer strbuf = new StringBuffer();
			for(String line = br.readLine();line != null; line = br.readLine()) {
				strbuf.append(line);
				strbuf.append(newLine_);
			}
			aboutText_ = strbuf.toString();
			aboutDoc_ = new PlainDocument();
			aboutDoc_.insertString(0, aboutText_, null);
		} catch(IOException e) {
			e.printStackTrace();
		}
		catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		about_ = new JTextArea(aboutDoc_);
		about_.setEditable(false);
		about_.setColumns(77);
		about_.setRows(27);
		
		aboutScrollPane_ = new JScrollPane(about_);
		aboutScrollPane_.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		aboutScrollPane_.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		aboutScrollPane_.setBorder(null);
		
		add(aboutScrollPane_);
				
	}
}
