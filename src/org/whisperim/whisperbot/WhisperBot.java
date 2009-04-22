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
package org.whisperim.whisperbot;

import java.io.File;
import java.util.Date;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;

import org.whisperim.client.ConnectionManager;
import org.whisperim.plugins.ConnectionPluginAdapter;
import org.whisperim.security.FileEncryptor;

import com.aol.acc.AccAlert;
import com.aol.acc.AccAvManager;
import com.aol.acc.AccAvManagerProp;
import com.aol.acc.AccAvSession;
import com.aol.acc.AccAvStreamType;
import com.aol.acc.AccBartItem;
import com.aol.acc.AccBartItemProp;
import com.aol.acc.AccBuddyList;
import com.aol.acc.AccBuddyListProp;
import com.aol.acc.AccCustomSession;
import com.aol.acc.AccDirEntry;
import com.aol.acc.AccEvents;
import com.aol.acc.AccException;
import com.aol.acc.AccFileSharingItem;
import com.aol.acc.AccFileSharingSession;
import com.aol.acc.AccFileXfer;
import com.aol.acc.AccFileXferSession;
import com.aol.acc.AccGroup;
import com.aol.acc.AccGroupProp;
import com.aol.acc.AccIm;
import com.aol.acc.AccImInputState;
import com.aol.acc.AccImSession;
import com.aol.acc.AccInstance;
import com.aol.acc.AccInstanceProp;
import com.aol.acc.AccParticipant;
import com.aol.acc.AccParticipantProp;
import com.aol.acc.AccPermissions;
import com.aol.acc.AccPluginInfo;
import com.aol.acc.AccPluginInfoProp;
import com.aol.acc.AccPreferences;
import com.aol.acc.AccRateState;
import com.aol.acc.AccResult;
import com.aol.acc.AccSecondarySession;
import com.aol.acc.AccSecondarySessionState;
import com.aol.acc.AccSession;
import com.aol.acc.AccSessionProp;
import com.aol.acc.AccSessionState;
import com.aol.acc.AccStream;
import com.aol.acc.AccUser;
import com.aol.acc.AccUserProp;
import com.aol.acc.AccUserState;
import com.aol.acc.AccVariant;

public class WhisperBot extends ConnectionPluginAdapter{

	boolean running_ = true;
	long start_ = new Date().getTime();
	private FileEncryptor fileEncryptor_;
	private String name_ = "WhisperBot";
	private String id_ = "WhisperBotS";
	private String iconPath_;
	private ConnectionManager cm_;
	private String handle_;

	
	
	public void OnImReceived(AccSession session, AccImSession imSession,
			AccParticipant participant, AccIm im) {
		try {
			// Get incoming IM and cut it down.
			String richText = im.getConvertedText("application/xhtml+xml");
			String msg = im.getConvertedText("text/plain");
			msg = msg.trim();

			if (msg.equals("$stats")) {
				long diff = (new Date()).getTime() - start_;
				diff /= 1000; // convert to seconds
				long days = diff / 86400;
				diff = diff - 86400 * days;
				long hours = diff / 3600;
				diff = diff - 3600 * hours;
				long minutes = diff / 60;

				richText = "Running for " + days + " day"
						+ ((days != 1) ? "s" : "") + ", " + hours + " hour"
						+ ((hours != 1) ? "s" : "") + ", " + minutes
						+ " minute" + ((minutes != 1) ? "s" : "") + ".";
				
				session.createIm(richText, null);
			} else if (msg.startsWith("$encryptedSendFile")) {

				// Separate out $encryptedSendFile from file name to be
				// transfered and format
				int i = msg.length();
				char encryptedFileNameChar[] = new char[i];
				msg.getChars(18, msg.length(), encryptedFileNameChar, 0);
				String encryptedFileName = encryptedFileNameChar.toString();
				encryptedFileName = encryptedFileName.trim();

				File input = new File(encryptedFileName);
				
				// Encrypt file
				  fileEncryptor_.generateCipherFile(input, encryptedFileName);

				// Send File
				AccFileXferSession fileXferSession = session
						.getFileXferManager().send(participant.getName(),
								encryptedFileName, "Transfering file...", null);
			} else if (msg.startsWith("$sendFile")) {

				// Separate out $sendFile from file name to be transfered and
				// Format
				int i = msg.length();
				char fileNameChar[] = new char[i];
				msg.getChars(8, msg.length(), fileNameChar, 0);
				String fileName = fileNameChar.toString();
				fileName = fileName.trim();

				// Send File
				AccFileXferSession fileXferSession = session
						.getFileXferManager().send(participant.getName(),
								fileName, "Transfering file...", null);
			} else if (msg.equals("$directory")) {
				// Pull up all files in directory.
				File file = new File("C:\\files");
				String[] filesArray = file.list();

				String fileList = "";
				int i = 0;

				while (i <= filesArray.length) {
					fileList = fileList + " " + i + ")" + "" + filesArray[i];
				}

				// IMs the array as a string.
				session.createIm(fileList, null);

			} else if (msg.startsWith("help")) {
				richText = "$stats<br>$directory<br>$sendFile<br>$encryptedSendFile<br>$help<br>";
				session.createIm(richText, null);
			}

		} catch (AccException e) {
			System.out.println("Someone through an AccException with the HR: "
					+ e.errorCode);
			e.printStackTrace();
		}
	}

	@Override
	public String getIdentifier() {
		
		return id_ + ":" + handle_.toLowerCase().replace(" ", "");
	}

	@Override
	public String getProtocol() {
		return id_;
	}

	@Override
	public String getPluginIconLocation() {
		
		return iconPath_;
	}

	@Override
	public String getPluginName() {
		
		return name_;
	}

	@Override
	public void setIconLocation(String location) {
		iconPath_ = location;

	}

	@Override
	public void setPluginName(String name) {
		name_ = name;
	}
	
	private void print(String msg){
		System.out.println(msg);
	}

	@Override
	public String getHandle() {
		return handle_;
	}

	@Override
	public int getStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setHandle(String handle) {
		handle_ = handle;
		
	}

	@Override
	public void setIdle() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setInvisible(boolean visible) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStatusMessage(String message) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public ImageIcon getIcon() {
		return null;
	}

	@Override
	public ImageIcon getServiceIcon() {
		return null;
	}
}