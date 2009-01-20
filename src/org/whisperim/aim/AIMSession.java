package org.whisperim.aim;

import java.util.ArrayList;
import java.util.LinkedList;

import org.whisperim.client.AIMStrategy;
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

public class AIMSession implements AccEvents, Runnable {

	AccSession session;
    String key = "Key:cm13Hg1hwDNm_tow";
    Thread listenThread;
    boolean running = true;
    AIMStrategy strategy_;
    
    LinkedList<AIMOperation> operations = new LinkedList<AIMOperation>();
    
    public AIMSession(AIMStrategy strategy){
    	super();
    	strategy_ = strategy;
    	listenThread = new Thread(this);
    	listenThread.start();
    }
    
	@Override
	public void BeforeImReceived(AccSession arg0, AccImSession arg1,
			AccParticipant arg2, AccIm arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void BeforeImSend(AccSession arg0, AccImSession arg1,
			AccParticipant arg2, AccIm arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnAlertReceived(AccSession arg0, AccAlert arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnAudioLevelChange(AccSession arg0, AccAvSession arg1,
			String arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnAvManagerChange(AccSession arg0, AccAvManager arg1,
			AccAvManagerProp arg2, AccResult arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnAvStreamStateChange(AccSession arg0, AccAvSession arg1,
			String arg2, AccAvStreamType arg3, AccSecondarySessionState arg4,
			AccResult arg5) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnBartItemRequestPropertyResult(AccSession arg0,
			AccBartItem arg1, AccBartItemProp arg2, int arg3, AccResult arg4,
			AccVariant arg5) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnBuddyAdded(AccSession arg0, AccGroup arg1, AccUser arg2,
			int arg3, AccResult arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnBuddyListChange(AccSession arg0, AccBuddyList arg1,
			AccBuddyListProp arg2) {
		// TODO Auto-generated method stub
		ArrayList<String> buddyList = new ArrayList<String>();
		try {
	        int groups = arg1.getGroupCount();
	        for (int i = 0; i < groups; i++){
	            AccGroup group = arg1.getGroupByIndex(i);
	            int buddies = group.getBuddyCount();
	            for (int c = 0; c < buddies; c++) {
	                AccUser buddy = group.getBuddyByIndex(c);
	                
	                if (buddy.getState() != AccUserState.Offline) {
	                    //Add the buddy to the list of online users
	                	buddyList.add(buddy.getName());
	                }
	            }
	        }
	        if (!buddyList.isEmpty()){
	        	//If the buddy list isn't empty, send it to the UI
	        	strategy_.receiveBuddies(buddyList);
	        }
	        
	    } catch (AccException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }


	}

	@Override
	public void OnBuddyMoved(AccSession arg0, AccUser arg1, AccGroup arg2,
			int arg3, AccGroup arg4, int arg5, AccResult arg6) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnBuddyRemoved(AccSession arg0, AccGroup arg1, AccUser arg2,
			AccResult arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnChangesBegin(AccSession arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnChangesEnd(AccSession arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnConfirmAccountResult(AccSession arg0, int arg1, AccResult arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnCustomDataReceived(AccSession arg0, AccCustomSession arg1,
			AccParticipant arg2, AccIm arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnCustomSendResult(AccSession arg0, AccCustomSession arg1,
			AccParticipant arg2, AccIm arg3, AccResult arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnDeleteStoredImsResult(AccSession arg0, int arg1,
			AccResult arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnDeliverStoredImsResult(AccSession arg0, int arg1,
			AccResult arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnEjectResult(AccSession arg0, AccSecondarySession arg1,
			String arg2, int arg3, AccResult arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnEmbedDownloadComplete(AccSession arg0, AccImSession arg1,
			AccIm arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnEmbedDownloadProgress(AccSession arg0, AccImSession arg1,
			AccIm arg2, String arg3, AccStream arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnEmbedUploadComplete(AccSession arg0, AccImSession arg1,
			AccIm arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnEmbedUploadProgress(AccSession arg0, AccImSession arg1,
			AccIm arg2, String arg3, AccStream arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnFileSharingRequestListingResult(AccSession arg0,
			AccFileSharingSession arg1, AccFileSharingItem arg2, int arg3,
			AccResult arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnFileSharingRequestXferResult(AccSession arg0,
			AccFileSharingSession arg1, AccFileXferSession arg2, int arg3,
			AccFileXfer arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnFileXferCollision(AccSession arg0, AccFileXferSession arg1,
			AccFileXfer arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnFileXferComplete(AccSession arg0, AccFileXferSession arg1,
			AccFileXfer arg2, AccResult arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnFileXferProgress(AccSession arg0, AccFileXferSession arg1,
			AccFileXfer arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnFileXferSessionComplete(AccSession arg0,
			AccFileXferSession arg1, AccResult arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnGroupAdded(AccSession arg0, AccGroup arg1, int arg2,
			AccResult arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnGroupChange(AccSession arg0, AccGroup arg1, AccGroupProp arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnGroupMoved(AccSession arg0, AccGroup arg1, int arg2,
			int arg3, AccResult arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnGroupRemoved(AccSession arg0, AccGroup arg1, AccResult arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnIdleStateChange(AccSession arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnImReceived(AccSession arg0, AccImSession arg1,
			AccParticipant arg2, AccIm arg3) {
		try {
			Message message = new Message(arg2.getName(), arg0.getIdentity(), arg3.getText(), arg3.getTimestamp());
			strategy_.receiveMessage(message);
		} catch (AccException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void OnImSendResult(AccSession arg0, AccImSession arg1,
			AccParticipant arg2, AccIm arg3, AccResult arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnImSent(AccSession arg0, AccImSession arg1,
			AccParticipant arg2, AccIm arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnInputStateChange(AccSession arg0, AccImSession arg1,
			String arg2, AccImInputState arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnInstanceChange(AccSession arg0, AccInstance arg1,
			AccInstance arg2, AccInstanceProp arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnInviteResult(AccSession arg0, AccSecondarySession arg1,
			String arg2, int arg3, AccResult arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnLocalImReceived(AccSession arg0, AccImSession arg1, AccIm arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnLookupUsersResult(AccSession arg0, String[] arg1, int arg2,
			AccResult arg3, AccUser[] arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnNewFileXfer(AccSession arg0, AccFileXferSession arg1,
			AccFileXfer arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnNewSecondarySession(AccSession arg0,
			AccSecondarySession arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnParticipantChange(AccSession arg0, AccSecondarySession arg1,
			AccParticipant arg2, AccParticipant arg3, AccParticipantProp arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnParticipantJoined(AccSession arg0, AccSecondarySession arg1,
			AccParticipant arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnParticipantLeft(AccSession arg0, AccSecondarySession arg1,
			AccParticipant arg2, AccResult arg3, String arg4, String arg5) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnPluginChange(AccSession arg0, AccPluginInfo arg1,
			AccPluginInfoProp arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnPluginUninstall(AccSession arg0, AccPluginInfo arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnPreferenceChange(AccSession arg0, String arg1, AccResult arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnPreferenceInvalid(AccSession arg0, String arg1, AccResult arg2) {
		// TODO Auto-generated method stub

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
	 * OnStateChange called to report change in system state from online to offline and 
	 * vice versa 
	 */
	@Override
	public void OnStateChange(AccSession arg0, AccSessionState arg1,
			AccResult arg2) {
		System.out.println("In OnStateChange..." + arg2);
		if (arg1 == AccSessionState.Offline) {
			//Kill the thread
			System.out.print("offline...");
	        running = false;
	        if (arg2 == AccResult.ACC_E_INVALID_KEY) {
	            //Bad client key
	        	System.out.println("Bad client key");
	        }
	        if (arg2 == AccResult.ACC_E_RATE_LIMITED) {
	            //Rate limited
	        }
	        if (arg2 == AccResult.ACC_E_RATE_LIMITED_KEY) {
	           //Key rate limited
	        }
	     System.out.print(arg2.toString());
	        //Signout
	        return;
	    }
        if (arg1 == AccSessionState.Connecting){
            strategy_.statusUpdate("Connecting...");
            return;
        }
        if (arg1 == AccSessionState.Negotiating){
            strategy_.statusUpdate("Negotiating...");
            return;
        }
        if (arg1 == AccSessionState.Validating){
            strategy_.statusUpdate("Verifying Username and Password...");
            return;
        }
	    if (arg1 == AccSessionState.Online) {
            strategy_.statusUpdate("Signed In");
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
	public void run() {
		// TODO Auto-generated method stub
		try {
	        session = new AccSession();
	        
	        // Add event listener
	        session.setEventListener(this);
	        
	        // set key
	        AccClientInfo info = session.getClientInfo();
	        info.setDescription(key);
	        
	        // setup prefs so anyone can IM us, but not chats or DIMs
	        session.setPrefsHook(new Prefs());
	        AccPreferences prefs = session.getPrefs();
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
	        
	    while( running ) 
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
	private void processOperation(AIMOperation operation) throws AccException {
	    switch (operation.getOperation()) {
	    case AIMOperation.SIGNIN:
	    {
	        Class[] types = {String.class, String.class};
	        this.checkArgs(operation, types);
	        String handle = (String) operation.arguments[0];
	        String password = (String) operation.arguments[1];
	        // set screen name
	        session.setIdentity(handle);
	        session.signOn(password);
	        break;
	        }
	    case AIMOperation.SIGNOUT:
	    {
	        Class[] types = new Class[0];
	        this.checkArgs(operation, types);
	        session.signOff();
	        break;
	        }           
	    case AIMOperation.SEND_MESSAGE:
	    {
	        Class[] types = {String.class, String.class};
	        this.checkArgs(operation, types);
	        String userName = (String) operation.arguments[0];
	        String message = (String) operation.arguments[1];
	        AccIm im = session.createIm(message, null);
	        AccImSession imSession = session.createImSession(userName, AccImSessionType.Im);
	        imSession.sendIm(im);
	        
	        break;
	        }
	    }
	}
	
	/**
	 * Helper method for checking the argument types passed to the operation
	 * @param operation
	 * @param types
	 */
	private void checkArgs(AIMOperation operation, Class[] types) {
		if (operation.arguments.length != types.length) {
			throw new IllegalArgumentException("Wrong number of arguments");
		}
		int l = types.length;
		for (int i = 0; i < l; i++) {
			if (!types[i].isInstance(operation.arguments[i])) {
				throw new IllegalArgumentException("Argument is wrong type, should be " +
						types[i].getName() +", is " +
						operation.arguments[i].getClass().getName());
			}
		}

	}
	
	public void addOperation(AIMOperation op) {
	    synchronized (operations) {
	        operations.add(op);		
	    }
	}
		
	public AIMOperation getOperation() {
	    synchronized (operations) {
	        if (operations.size() > 0) {
	            return operations.remove();
	        }
	    }
	    return null;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

}
