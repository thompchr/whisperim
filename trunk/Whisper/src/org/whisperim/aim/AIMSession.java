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

package org.whisperim.aim;

import java.util.ArrayList;
import java.util.LinkedList;

import org.whisperim.client.Buddy;
import org.whisperim.client.Message;

import com.aol.acc.AccAlert;
import com.aol.acc.AccAvManager;
import com.aol.acc.AccAvManagerProp;
import com.aol.acc.AccAvSession;
import com.aol.acc.AccAvStreamType;
import com.aol.acc.AccBartItem;
import com.aol.acc.AccBartItemProp;
import com.aol.acc.AccBuddyList;
import com.aol.acc.AccBuddyListProp;
import com.aol.acc.AccClientInfo;
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
import com.aol.acc.AccImSessionType;
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


/**
 * This class acts as the connection to the AIM service as well as providing
 * the interface to handle events fired from the AIM service.  It runs as an 
 * independent thread and handles all traffic between the client and the AIM
 * servers.
 * @author Chris Thompson
 *
 */
public class AIMSession implements AccEvents, Runnable {

	public static final String SERVICE_UNAVAILABLE = "Service Unavailable";
	public static final String SIGNED_IN = "Signed In";
	public static final String RATE_LIMITED = "Rate Limited";
	public static final String INVALID_USERNAME_OR_PASSWORD = "Invalid Username or Password";
	private AccSession session_;
	private String key_ = "Key:cm13Hg1hwDNm_tow";
	private Thread listenThread_;
	private boolean running_ = true;
	private AIMStrategy strategy_;
	private String localHandle_;
	private String protocol_ = "AIM";

	private LinkedList<AIMOperation> operations_ = new LinkedList<AIMOperation>();


	/**
	 * Constructor.  This method takes the address of its parent strategy in order
	 * to facilitate propagating received messages.
	 * @param strategy
	 */
	public AIMSession(AIMStrategy strategy){
		super();
		strategy_ = strategy;
		listenThread_ = new Thread(this);
		listenThread_.start();	
	}
	
	public void setLocalHandle(String handle){
		localHandle_ = handle;
	}

	@Override
	public void BeforeImReceived(AccSession arg0, AccImSession arg1,
			AccParticipant arg2, AccIm arg3) {
	}

	@Override
	public void BeforeImSend(AccSession arg0, AccImSession arg1,
			AccParticipant arg2, AccIm arg3) {
	}

	@Override
	public void OnAlertReceived(AccSession arg0, AccAlert arg1) {
	}

	@Override
	public void OnAudioLevelChange(AccSession arg0, AccAvSession arg1,
			String arg2, int arg3) {
	}

	@Override
	public void OnAvManagerChange(AccSession arg0, AccAvManager arg1,
			AccAvManagerProp arg2, AccResult arg3) {
	}

	@Override
	public void OnAvStreamStateChange(AccSession arg0, AccAvSession arg1,
			String arg2, AccAvStreamType arg3, AccSecondarySessionState arg4,
			AccResult arg5) {
	}

	@Override
	public void OnBartItemRequestPropertyResult(AccSession arg0,
			AccBartItem arg1, AccBartItemProp arg2, int arg3, AccResult arg4,
			AccVariant arg5) {
	}

	@Override
	public void OnBuddyAdded(AccSession arg0, AccGroup arg1, AccUser arg2,
			int arg3, AccResult arg4) {
		System.out.println("In OnBuddyAdded");
		System.out.println("  " + arg1.toString());
		System.out.println("  " + arg2.toString());
		
		
	}

	@Override
	/**
	 * This event handler is called when the user's buddy list changes.
	 * @param arg0
	 * @param arg1
	 */
	public void OnBuddyListChange(AccSession arg0, AccBuddyList arg1,
			AccBuddyListProp arg2) {
		System.out.println("In OnBuddyListChange");
		ArrayList<Buddy> buddyList = new ArrayList<Buddy>();
		
		try {
			int groups = arg1.getGroupCount();
			for (int i = 0; i < groups; i++){
				AccGroup group = arg1.getGroupByIndex(i);
				int buddies = group.getBuddyCount();
				for (int c = 0; c < buddies; c++) {
					AccUser buddy = group.getBuddyByIndex(c);

					if (buddy.getState() != AccUserState.Offline) {
						//Add the buddy to the list of online users
						try{
							//Throwing an exception when the friendly name isn't set IS AN AWFUL IDEA
							//but AOL did it anyway
							buddyList.add(new Buddy(buddy.getName(), localHandle_, protocol_, buddy.getFriendlyName()));
						}catch(AccException ae){
							//System.err.println(ae.getMessage());
							buddyList.add(new Buddy(buddy.getName(), localHandle_, protocol_));
						}
						
					}
				}
			}
			if (!buddyList.isEmpty()){
				//If the buddy list isn't empty, send it to the UI
				strategy_.receiveBuddies(buddyList);
			}

		} catch (AccException e) {		
			e.printStackTrace();
		}


	}

	@Override
	public void OnBuddyMoved(AccSession arg0, AccUser arg1, AccGroup arg2,
			int arg3, AccGroup arg4, int arg5, AccResult arg6) {
		System.out.println("In OnBuddyMoved");
		System.out.println("  " + arg1.toString());
		System.out.println("  " + arg2.toString());
	}

	@Override
	public void OnBuddyRemoved(AccSession arg0, AccGroup arg1, AccUser arg2,
			AccResult arg3) {
		System.out.println("In OnBuddyRemoved");
		System.out.println("  " + arg1.toString());
		System.out.println("  " + arg2.toString());
	}

	@Override
	public void OnChangesBegin(AccSession arg0) {
	}

	@Override
	public void OnChangesEnd(AccSession arg0) {
	}

	@Override
	public void OnConfirmAccountResult(AccSession arg0, int arg1, AccResult arg2) {
	}

	@Override
	public void OnCustomDataReceived(AccSession arg0, AccCustomSession arg1,
			AccParticipant arg2, AccIm arg3) {
	}

	@Override
	public void OnCustomSendResult(AccSession arg0, AccCustomSession arg1,
			AccParticipant arg2, AccIm arg3, AccResult arg4) {
	}

	@Override
	public void OnDeleteStoredImsResult(AccSession arg0, int arg1,
			AccResult arg2) {
	}

	@Override
	public void OnDeliverStoredImsResult(AccSession arg0, int arg1,
			AccResult arg2) {
	}

	@Override
	public void OnEjectResult(AccSession arg0, AccSecondarySession arg1,
			String arg2, int arg3, AccResult arg4) {
	}

	@Override
	public void OnEmbedDownloadComplete(AccSession arg0, AccImSession arg1,
			AccIm arg2) {
	}

	@Override
	public void OnEmbedDownloadProgress(AccSession arg0, AccImSession arg1,
			AccIm arg2, String arg3, AccStream arg4) {
	}

	@Override
	public void OnEmbedUploadComplete(AccSession arg0, AccImSession arg1,
			AccIm arg2) {
	}

	@Override
	public void OnEmbedUploadProgress(AccSession arg0, AccImSession arg1,
			AccIm arg2, String arg3, AccStream arg4) {
	}

	@Override
	public void OnFileSharingRequestListingResult(AccSession arg0,
			AccFileSharingSession arg1, AccFileSharingItem arg2, int arg3,
			AccResult arg4) {
	}

	@Override
	public void OnFileSharingRequestXferResult(AccSession arg0,
			AccFileSharingSession arg1, AccFileXferSession arg2, int arg3,
			AccFileXfer arg4) {
	}

	@Override
	public void OnFileXferCollision(AccSession arg0, AccFileXferSession arg1,
			AccFileXfer arg2) {
	}

	@Override
	public void OnFileXferComplete(AccSession arg0, AccFileXferSession arg1,
			AccFileXfer arg2, AccResult arg3) {
	}

	@Override
	public void OnFileXferProgress(AccSession arg0, AccFileXferSession arg1,
			AccFileXfer arg2) {
	}

	@Override
	public void OnFileXferSessionComplete(AccSession arg0,
			AccFileXferSession arg1, AccResult arg2) {
		}

	@Override
	public void OnGroupAdded(AccSession arg0, AccGroup arg1, int arg2,
			AccResult arg3) {
		
	}

	@Override
	public void OnGroupChange(AccSession arg0, AccGroup arg1, AccGroupProp arg2) {
	}

	@Override
	public void OnGroupMoved(AccSession arg0, AccGroup arg1, int arg2,
			int arg3, AccResult arg4) {
	}

	@Override
	public void OnGroupRemoved(AccSession arg0, AccGroup arg1, AccResult arg2) {
	}

	@Override
	public void OnIdleStateChange(AccSession arg0, int arg1) {
	}

	@Override

	/**
	 * This event handler is called when an IM is received from the AIM service.
	 * @param accSession
	 * @param accIMSession
	 * @param participant
	 * @param im
	 */
	public void OnImReceived(AccSession accSession, AccImSession accIMSession,
			AccParticipant participant, AccIm im) {
		try {
			Message message = new Message(
					new Buddy(participant.getName(), localHandle_, protocol_, participant.getUser().getFriendlyName()), new Buddy(accSession.getIdentity(), localHandle_, protocol_), im.getText(), protocol_, im.getTimestamp());
			message.setProtocol(protocol_);
			strategy_.receiveMessage(message);
		} catch (AccException e) {
			Message message;
			
			try {
				message = new Message(
						new Buddy(participant.getName(), localHandle_, protocol_, participant.getUser().getName()), new Buddy(accSession.getIdentity(), localHandle_, protocol_), im.getText(), protocol_, im.getTimestamp());
				message.setProtocol(protocol_);
				strategy_.receiveMessage(message);
			} catch (AccException e1) {
				
				e1.printStackTrace();
			}

		}


	}

	@Override
	public void OnImSendResult(AccSession arg0, AccImSession arg1,
			AccParticipant arg2, AccIm arg3, AccResult arg4) {
	}

	@Override
	public void OnImSent(AccSession arg0, AccImSession arg1,
			AccParticipant arg2, AccIm arg3) {
	}

	@Override
	public void OnInputStateChange(AccSession arg0, AccImSession arg1,
			String arg2, AccImInputState arg3) {
	}

	@Override
	public void OnInstanceChange(AccSession arg0, AccInstance arg1,
			AccInstance arg2, AccInstanceProp arg3) {
	}

	@Override
	public void OnInviteResult(AccSession arg0, AccSecondarySession arg1,
			String arg2, int arg3, AccResult arg4) {
	}

	@Override
	public void OnLocalImReceived(AccSession arg0, AccImSession arg1, AccIm arg2) {
	}

	@Override
	public void OnLookupUsersResult(AccSession arg0, String[] arg1, int arg2,
			AccResult arg3, AccUser[] arg4) {
	}

	@Override
	public void OnNewFileXfer(AccSession arg0, AccFileXferSession arg1,
			AccFileXfer arg2) {
	}

	@Override
	public void OnNewSecondarySession(AccSession arg0,
			AccSecondarySession arg1, int arg2) {
	}

	@Override
	public void OnParticipantChange(AccSession arg0, AccSecondarySession arg1,
			AccParticipant arg2, AccParticipant arg3, AccParticipantProp arg4) {
	}

	@Override
	public void OnParticipantJoined(AccSession arg0, AccSecondarySession arg1,
			AccParticipant arg2) {
	}

	@Override
	public void OnParticipantLeft(AccSession arg0, AccSecondarySession arg1,
			AccParticipant arg2, AccResult arg3, String arg4, String arg5) {
	}

	@Override
	public void OnPluginChange(AccSession arg0, AccPluginInfo arg1,
			AccPluginInfoProp arg2) {
	}

	@Override
	public void OnPluginUninstall(AccSession arg0, AccPluginInfo arg1) {
	}

	@Override
	public void OnPreferenceChange(AccSession arg0, String arg1, AccResult arg2) {
	}

	@Override
	public void OnPreferenceInvalid(AccSession arg0, String arg1, AccResult arg2) {
	}

	@Override
	public void OnPreferenceResult(AccSession arg0, String arg1, int arg2,
			String arg3, AccResult arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnPushBuddyFeedResult(AccSession arg0, int arg1,
			AccResult arg2, String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnRateLimitStateChange(AccSession arg0, AccImSession arg1,
			AccRateState arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnReportUserResult(AccSession arg0, AccUser arg1, int arg2,
			AccResult arg3, int arg4, int arg5) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnRequestServiceResult(AccSession arg0, int arg1,
			AccResult arg2, String arg3, int arg4, AccVariant arg5) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnRequestSummariesResult(AccSession arg0, int arg1,
			AccResult arg2, AccVariant arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnSearchDirectoryResult(AccSession arg0, int arg1,
			AccResult arg2, AccDirEntry arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnSecondarySessionChange(AccSession arg0,
			AccSecondarySession arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnSecondarySessionStateChange(AccSession arg0,
			AccSecondarySession arg1, AccSecondarySessionState arg2,
			AccResult arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnSendInviteMailResult(AccSession arg0, int arg1, AccResult arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnSessionChange(AccSession arg0, AccSessionProp arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnSoundEffectReceived(AccSession arg0, AccAvSession arg1,
			String arg2, String arg3) {
		// TODO Auto-generated method stub

	}

	/**
	 * This event handler is called to report change in system state.  
	 * Examples include online, offline, etc.
	 * 
	 * @param accSession
	 * @param accSessionState
	 * @param result
	 */
	@Override
	public void OnStateChange(AccSession accSession, AccSessionState accSessionState,
			AccResult result) {
		System.out.println("In OnStateChange..." + result);
		if (result == AccResult.ACC_E_FAIL){
			strategy_.statusUpdate(SERVICE_UNAVAILABLE);
		}
		if (accSessionState == AccSessionState.Offline) {
			//Kill the thread
			//System.out.print("offline...");
			running_ = false;
			if (result == AccResult.ACC_E_INVALID_KEY) {
				//Bad client key
				//System.out.println("Bad client key");
			}
			if (result == AccResult.ACC_E_RATE_LIMITED) {
				//Rate limited
				strategy_.statusUpdate(RATE_LIMITED);
			}
			if (result == AccResult.ACC_E_RATE_LIMITED_KEY) {
				//Key rate limited
				
			}
			if (result == AccResult.ACC_E_INVALID_PASSWORD){
				strategy_.statusUpdate(INVALID_USERNAME_OR_PASSWORD);
				return;
			}
			//System.out.print(result.toString());
			//Signout
			return;
		}
		if (accSessionState == AccSessionState.Connecting){
			strategy_.statusUpdate("Connecting...");
			return;
		}
		if (accSessionState == AccSessionState.Negotiating){
			strategy_.statusUpdate("Negotiating...");
			return;
		}
		if (accSessionState == AccSessionState.Validating){
			strategy_.statusUpdate("Verifying Username and Password...");
			return;
		}
		if (accSessionState == AccSessionState.Online) {
			strategy_.statusUpdate(SIGNED_IN);
			return;
		}




	}

	@Override
	public void OnUserChange(AccSession arg0, AccUser arg1, AccUser arg2,
			AccUserProp arg3, AccResult arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnUserRequestPropertyResult(AccSession arg0, AccUser arg1,
			AccUserProp arg2, int arg3, AccResult arg4, AccVariant arg5) {
		// TODO Auto-generated method stub

	}

	@Override
	/**
	 * This method serves as the actual connection to the AIM service.
	 * It establishes the session preferences via the preferences class, 
	 * adds itself as the event listener for the session and makes the connection.
	 * The thread also loops infinitely until told to sign off and checks the operations
	 * queue, performing any it comes across.
	 */
	public void run() {
		
		try {
			session_ = new AccSession();

			// Add event listener
			session_.setEventListener(this);

			// set key
			AccClientInfo info = session_.getClientInfo();
			info.setDescription(key_);

			// setup prefs so anyone can IM us, but not chats or DIMs
			session_.setPrefsHook(new Prefs());
			AccPreferences prefs = session_.getPrefs();
			prefs.setValue("aimcc.im.chat.permissions.buddies", 
					AccPermissions.AcceptAll);
			prefs.setValue("aimcc.im.chat.permissions.nonBuddies", 
					AccPermissions.AcceptAll);
			prefs.setValue("aimcc.im.direct.permissions.buddies", 
					AccPermissions.AcceptAll);
			prefs.setValue("aimcc.im.direct.permissions.nonBuddies", 
					AccPermissions.AcceptAll);
			prefs.setValue("aimcc.im.standard.permissions.buddies", 
					AccPermissions.AcceptAll);
			prefs.setValue("aimcc.im.standard.permissions.nonBuddies", 
					AccPermissions.AcceptAll);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		while( running_ ) 
		{
			try {
				AccSession.pump(50);
				AIMOperation operation = this.getOperation();
				if (operation != null) {
					this.processOperation(operation);
				}				
				Thread.sleep(50);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}


	}
	
	/**
	 * This method is a helper method designed to parse operations placed in
	 * the operations queue.
	 * @param operation
	 * @throws AccException
	 */
	private void processOperation(AIMOperation operation) throws AccException {
		switch (operation.getOperation()) {
		case AIMOperation.SIGNIN:
		{
			Class[] types = {String.class, String.class};
			this.checkArgs(operation, types);
			String handle = (String) operation.getArguments()[0];
			String password = (String) operation.getArguments()[1];
			// set screen name
			session_.setIdentity(handle);
			session_.signOn(password);
			break;
		}
		case AIMOperation.SIGNOUT:
		{
			Class[] types = new Class[0];
			this.checkArgs(operation, types);
			session_.signOff();
			break;
		}           
		case AIMOperation.SEND_MESSAGE:
		{
			Class[] types = {String.class, String.class};
			this.checkArgs(operation, types);
			String userName = (String) operation.getArguments()[0];
			String message = (String) operation.getArguments()[1];
			AccIm im = session_.createIm(message, null);
			session_.createImSession(userName, AccImSessionType.Im).sendIm(im);
			

			break;
		}
		case AIMOperation.SET_STATUS:
		{
			Class[] types = {Integer.class, String.class };
			this.checkArgs(operation, types);
			switch (((int)(Integer)operation.getArguments()[0])){
			
			case AIMOperation.STATUS_AWAY:{
				session_.setAwayMessage(session_.createIm((String) operation.getArguments()[1], null));

				break;
			}
			
			case AIMOperation.STATUS_IDLE:{
				session_.setSecondsRemainingUntilIdleState(0);
				break;
			}
			
			case AIMOperation.STATUS_INVISIBLE:{
				session_.setAppearOffline(true);
				break;
			}
			
			case AIMOperation.STATUS_VISIBLE:{
				session_.setAppearOffline(false);
			}
			}
			
		}
		case AIMOperation.CREATE_VIDEO_SESSION:{
			AccAvManager av = session_.getAudioVideoManager();
			//av.setVideoInputDevice(av.getWi)
			
		}
		
		case AIMOperation.SET_STATUS_MESSAGE:{
			session_.setProperty(AccSessionProp.StatusText, new AccVariant((String)operation.getArguments()[0]));
			System.out.println((String)operation.getArguments()[0]);
		}
		
		}
	}

	/**
	 * Helper method for checking the argument types passed to the operation.  Asserts that
	 * the parameters passed to the operation are of the types required by that operation.
	 * @param operation
	 * @param types
	 */
	private void checkArgs(AIMOperation operation, Class[] types) {
		if (operation.getArguments().length != types.length) {
			throw new IllegalArgumentException("Wrong number of arguments");
		}
		int l = types.length;
		for (int i = 0; i < l; i++) {
			if (!types[i].isInstance(operation.getArguments()[i])) {
				throw new IllegalArgumentException("Argument is wrong type, should be " +
						types[i].getName() +", is " +
						operation.getArguments()[i].getClass().getName());
			}
		}

	}

	/**
	 * Adds an operation to the queue of operations.
	 * @param op
	 */
	public void addOperation(AIMOperation op) {
		synchronized (operations_) {
			operations_.add(op);		
		}
	}

	/**
	 * Retrieves an operation from the operations queue.
	 * @return AIMOperation
	 */
	public AIMOperation getOperation() {
		synchronized (operations_) {
			if (operations_.size() > 0) {
				return operations_.remove();
			}
		}
		return null;
	}

	/**
	 * Determines whether the session thread is running or not.
	 * @return boolean
	 */
	public boolean isRunning() {
		return running_;
	}

	/**
	 * Sets the running_ member variable in order to instruct the session
	 * thread to terminate. 
	 * @param running
	 */
	public void setRunning(boolean running) {
		running_ = running;
	}
	
	 

}
