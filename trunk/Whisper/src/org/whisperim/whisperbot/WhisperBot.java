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

public class WhisperBot implements AccEvents {

	AccSession session;
	boolean running = true;
	long start = new Date().getTime();
	private FileEncryptor fileEncryptor_;

	public WhisperBot() throws AccException {
		// Create main session object
		session = new AccSession();

		// Add event listener
		session.setEventListener(this);

		// Setup prefs so only buddies can access.
		session.setPrefsHook(new Prefs());
		AccPreferences prefs = session.getPrefs();
		prefs.setValue("aimcc.im.chat.permissions.buddies",
				AccPermissions.RejectAll);
		prefs.setValue("aimcc.im.chat.permissions.nonBuddies",
				AccPermissions.RejectAll);
		prefs.setValue("aimcc.im.direct.permissions.buddies",
				AccPermissions.RejectAll);
		prefs.setValue("aimcc.im.direct.permissions.nonBuddies",
				AccPermissions.RejectAll);
		prefs.setValue("aimcc.im.standard.permissions.buddies",
				AccPermissions.AcceptAll);
		prefs.setValue("aimcc.im.standard.permissions.nonBuddies",
				AccPermissions.RejectAll);

		// msg pump
		while (running) {
			try {
				AccSession.pump(50);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		session = null;
		prefs = null;

		System.gc();
		System.runFinalization();
	}

	public void OnImReceived(AccSession session, AccImSession imSession,
			AccParticipant participant, AccIm im) {
		try {
			// Get incoming IM and cut it down.
			String richText = im.getConvertedText("application/xhtml+xml");
			String msg = im.getConvertedText("text/plain");
			msg = msg.trim();

			if (msg.equals("$stats")) {
				long diff = (new Date()).getTime() - start;
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

	public void OnStateChange(AccSession arg0, AccSessionState arg1,
			AccResult arg2) {
		System.out.println(arg1 + "" + arg2);

		if (arg1 == AccSessionState.Offline) {
			running = false;
		}

	}

	/** Unimplemented stubs **/

	public void OnSessionChange(AccSession arg0, AccSessionProp arg1) {
	}

	public void OnIdleStateChange(AccSession arg0, int arg1) {
	}

	public void OnInstanceChange(AccSession arg0, AccInstance arg1,
			AccInstance arg2, AccInstanceProp arg3) {
	}

	public void OnLookupUsersResult(AccSession arg0, String[] arg1, int arg2,
			AccResult arg3, AccUser[] arg4) {
	}

	public void OnSearchDirectoryResult(AccSession arg0, int arg1,
			AccResult arg2, AccDirEntry arg3) {
	}

	public void OnSendInviteMailResult(AccSession arg0, int arg1, AccResult arg2) {
	}

	public void OnRequestServiceResult(AccSession arg0, int arg1,
			AccResult arg2, String arg3, int arg4, byte[] arg5) {
	}

	public void OnConfirmAccountResult(AccSession arg0, int arg1, AccResult arg2) {
	}

	public void OnReportUserResult(AccSession arg0, AccUser arg1, int arg2,
			AccResult arg3, int arg4, int arg5) {
	}

	public void OnAlertReceived(AccSession arg0, AccAlert arg1) {
	}

	public void OnPreferenceResult(AccSession arg0, String arg1, int arg2,
			String arg3, AccResult arg4) {
	}

	public void OnPreferenceChange(AccSession arg0, String arg1, AccResult arg2) {
	}

	public void OnPreferenceInvalid(AccSession arg0, String arg1, AccResult arg2) {
	}

	public void OnPluginChange(AccSession arg0, AccPluginInfo arg1,
			AccPluginInfoProp arg2) {
	}

	public void OnBartItemRequestPropertyResult(AccSession arg0,
			AccBartItem arg1, AccBartItemProp arg2, int arg3, AccResult arg4,
			AccVariant arg5) {
	}

	public void OnUserRequestPropertyResult(AccSession arg0, AccUser arg1,
			AccUserProp arg2, int arg3, AccResult arg4, AccVariant arg5) {
	}

	public void OnGroupAdded(AccSession arg0, AccGroup arg1, int arg2,
			AccResult arg3) {
	}

	public void OnGroupRemoved(AccSession arg0, AccGroup arg1, AccResult arg2) {
	}

	public void OnGroupMoved(AccSession arg0, AccGroup arg1, int arg2,
			int arg3, AccResult arg4) {
	}

	public void OnBuddyAdded(AccSession arg0, AccGroup arg1, AccUser arg2,
			int arg3, AccResult arg4) {
	}

	public void OnBuddyRemoved(AccSession arg0, AccGroup arg1, AccUser arg2,
			AccResult arg3) {
	}

	public void OnBuddyMoved(AccSession arg0, AccUser arg1, AccGroup arg2,
			int arg3, AccGroup arg4, int arg5, AccResult arg6) {
	}

	public void OnBuddyListChange(AccSession arg0, AccBuddyList arg1,
			AccBuddyListProp arg2) {
	}

	public void OnGroupChange(AccSession arg0, AccGroup arg1, AccGroupProp arg2) {
	}

	public void OnUserChange(AccSession arg0, AccUser arg1, AccUser arg2,
			AccUserProp arg3, AccResult arg4) {
		try {
			AccUserState newState = arg2.getState();
			if (arg1.getState() != newState) {
				if (newState == AccUserState.Away) {
					System.out.println(arg2.getName() + " is now away");
				}
				if (newState == AccUserState.Idle) {
					System.out.println(arg2.getName() + " is now idle");
				}
				if (newState == AccUserState.Online) {
					if (arg1.getState() == AccUserState.Offline) {
						System.out.println(arg2.getName() + " has come online");
					} else if (arg1.getState() == AccUserState.Unknown) {
						System.out.println(arg2.getName()
								+ " was already online");
					} else {
						System.out.println(arg2.getName() + " has returned");
					}
				}
				if (newState == AccUserState.Offline) {
					System.out.println(arg2.getName() + " has gone offline");
				}
			}
		} catch (AccException e) {
			e.printStackTrace();
		}

	}

	public void OnChangesBegin(AccSession arg0) {
	}

	public void OnChangesEnd(AccSession arg0) {
	}

	public void OnNewSecondarySession(AccSession arg0,
			AccSecondarySession arg1, int arg2) {
	}

	public void OnSecondarySessionStateChange(AccSession arg0,
			AccSecondarySession arg1, AccSecondarySessionState arg2,
			AccResult arg3) {
	}

	public void OnSecondarySessionChange(AccSession arg0,
			AccSecondarySession arg1, int arg2) {
	}

	public void OnParticipantJoined(AccSession arg0, AccSecondarySession arg1,
			AccParticipant arg2) {
	}

	public void OnParticipantChange(AccSession arg0, AccSecondarySession arg1,
			AccParticipant arg2, AccParticipant arg3, AccParticipantProp arg4) {
	}

	public void OnParticipantLeft(AccSession arg0, AccSecondarySession arg1,
			AccParticipant arg2, AccResult arg3, String arg4, String arg5) {
	}

	public void OnInviteResult(AccSession arg0, AccSecondarySession arg1,
			String arg2, int arg3, AccResult arg4) {
	}

	public void OnEjectResult(AccSession arg0, AccSecondarySession arg1,
			String arg2, int arg3, AccResult arg4) {
	}

	public void BeforeImSend(AccSession arg0, AccImSession arg1,
			AccParticipant arg2, AccIm arg3) {
	}

	public void OnImSent(AccSession arg0, AccImSession arg1,
			AccParticipant arg2, AccIm arg3) {
	}

	public void OnImSendResult(AccSession arg0, AccImSession arg1,
			AccParticipant arg2, AccIm arg3, AccResult arg4) {
	}

	public void BeforeImReceived(AccSession arg0, AccImSession arg1,
			AccParticipant arg2, AccIm arg3) {
	}

	public void OnInputStateChange(AccSession arg0, AccImSession arg1,
			String arg2, AccImInputState arg3) {
	}

	public void OnEmbedDownloadComplete(AccSession arg0, AccImSession arg1,
			AccIm arg2) {
	}

	public void OnEmbedUploadComplete(AccSession arg0, AccImSession arg1,
			AccIm arg2) {
	}

	public void OnRateLimitStateChange(AccSession arg0, AccImSession arg1,
			AccRateState arg2) {
	}

	public void OnNewFileXfer(AccSession arg0, AccFileXferSession arg1,
			AccFileXfer arg2) {
	}

	public void OnFileXferProgress(AccSession arg0, AccFileXferSession arg1,
			AccFileXfer arg2) {
	}

	public void OnFileXferCollision(AccSession arg0, AccFileXferSession arg1,
			AccFileXfer arg2) {
	}

	public void OnFileXferComplete(AccSession arg0, AccFileXferSession arg1,
			AccFileXfer arg2, AccResult arg3) {
	}

	public void OnFileXferSessionComplete(AccSession arg0,
			AccFileXferSession arg1, AccResult arg2) {
	}

	public void OnFileSharingRequestListingResult(AccSession arg0,
			AccFileSharingSession arg1, AccFileSharingItem arg2, int arg3,
			AccResult arg4) {
	}

	public void OnFileSharingRequestXferResult(AccSession arg0,
			AccFileSharingSession arg1, AccFileXferSession arg2, int arg3,
			AccFileXfer arg4) {
	}

	public void OnAvStreamStateChange(AccSession arg0, AccAvSession arg1,
			String arg2, AccAvStreamType arg3, AccSecondarySessionState arg4,
			AccResult arg5) {
	}

	public void OnAudioLevelChange(AccSession arg0, AccAvSession arg1,
			String arg2, int arg3) {
	}

	public void OnSoundEffectReceived(AccSession arg0, AccAvSession arg1,
			String arg2, String arg3) {
	}

	public void OnCustomDataReceived(AccSession arg0, AccCustomSession arg1,
			AccParticipant arg2, AccIm arg3) {
	}

	public void OnCustomSendResult(AccSession arg0, AccCustomSession arg1,
			AccParticipant arg2, AccIm arg3, AccResult arg4) {
	}

	public void OnEmbedUploadProgress(AccSession arg0, AccImSession arg1,
			AccIm arg2, String arg3, AccStream arg4) {
	}

	public void OnEmbedDownloadProgress(AccSession arg0, AccImSession arg1,
			AccIm arg2, String arg3, AccStream arg4) {
	}

	public void OnDeleteStoredImsResult(AccSession arg0, int arg1,
			AccResult arg2) {
	}

	public void OnDeliverStoredImsResult(AccSession arg0, int arg1,
			AccResult arg2) {
	}

	public void OnRequestSummariesResult(AccSession arg0, int arg1,
			AccResult arg2, AccVariant arg3) {
	}

	public void OnAvManagerChange(AccSession arg0, AccAvManager arg1,
			AccAvManagerProp arg2, AccResult arg3) {
	}

	public void OnLocalImReceived(AccSession arg0, AccImSession arg1, AccIm arg2) {
	}

	public void OnPluginUninstall(AccSession arg0, AccPluginInfo arg1) {
	}

	public void OnPushBuddyFeedResult(AccSession arg0, int arg1,
			AccResult arg2, String arg3) {
	}

	public void OnRequestServiceResult(AccSession arg0, int arg1,
			AccResult arg2, String arg3, int arg4, AccVariant arg5) {
	}

}