package org.whisperim.whisperbot;

import com.aol.acc.*;

import java.util.*;
import java.util.regex.*;


public class WhisperBot  implements AccEvents {

	AccSession session;
	boolean running = true;
	String key = "WhisperBot (Key:mo1hC594Jyv8-wX2)";
	String signOffPassword = "password";
	Vector<String> regexps = new Vector<String>();
	
	public static void main(String[] args) {
		
		if(args.length != 2) {
			System.out.print("usage: java WhisperBot username password");
			return;
		}
			
		try {
			new WhisperBot(args[0], args[1]);
		} catch (AccException e) {
			System.out.println("Main AccException, hr: "+e.errorCode);
			e.printStackTrace();
		}
	}
	
	public WhisperBot(String username, String password) throws AccException
	{
		// Create main session object
		session = new AccSession();
		
		// Add event listener
		session.setEventListener(this);
		
		// Set key
		AccClientInfo info = session.getClientInfo();
		info.setDescription(key);
		
		// Set screen name
		session.setIdentity(username);
		
		// Setup prefs so anyone can IM us, but not chats or DIMs
		session.setPrefsHook(new Prefs());
		AccPreferences prefs = session.getPrefs();
		prefs.setValue("aimcc.im.chat.permissions.buddies", AccPermissions.RejectAll);
		prefs.setValue("aimcc.im.chat.permissions.nonBuddies", AccPermissions.RejectAll);
		prefs.setValue("aimcc.im.direct.permissions.buddies", AccPermissions.RejectAll);
		prefs.setValue("aimcc.im.direct.permissions.nonBuddies", AccPermissions.RejectAll);
		prefs.setValue("aimcc.im.standard.permissions.buddies", AccPermissions.AcceptAll);
		prefs.setValue("aimcc.im.standard.permissions.nonBuddies", AccPermissions.AcceptAll);
	
		session.signOn(password);

		// Message ("msg") pump
	    while( running ) 
	    {
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
	    
            info = null;
            session = null;
            prefs = null;
        
	    System.gc();
	    System.runFinalization();
	}

	
	public void OnImReceived(AccSession session, AccImSession imSession, AccParticipant participant, AccIm im) 
	{
		try { 
			
			String richText = im.getConvertedText("application/xhtml+xml");
			String msg = im.getConvertedText("text/plain");

			System.out.println("You said: " + msg );
			
			if(running)
			{
				im.setText(richText);
				imSession.sendIm(im);
			}
		
		} catch (AccException e) {
			System.out.println("AccException with the HR: "+e.errorCode);
			e.printStackTrace();
		}
	}

	
	public void OnStateChange(AccSession arg0, AccSessionState arg1, AccResult arg2) {
		System.out.println(arg1+""+arg2);
		
		if(arg1 == AccSessionState.Offline)
		{
			running = false;
		}
		
	}
	
	/** Unimplemented stubs **/
	
	public void OnSessionChange(AccSession arg0, AccSessionProp arg1) {}
	
	public void OnIdleStateChange(AccSession arg0, int arg1) {}
	
	public void OnInstanceChange(AccSession arg0, AccInstance arg1, AccInstance arg2, AccInstanceProp arg3) {}
	
	public void OnLookupUsersResult(AccSession arg0, String[] arg1, int arg2, AccResult arg3, AccUser[] arg4) {}
	
	public void OnSearchDirectoryResult(AccSession arg0, int arg1, AccResult arg2, AccDirEntry arg3) {}
	
	public void OnSendInviteMailResult(AccSession arg0, int arg1, AccResult arg2) {}
	
	public void OnRequestServiceResult(AccSession arg0, int arg1, AccResult arg2, String arg3, int arg4, byte[] arg5) {}
	
	public void OnConfirmAccountResult(AccSession arg0, int arg1, AccResult arg2) {}
	
	public void OnReportUserResult(AccSession arg0, AccUser arg1, int arg2, AccResult arg3, int arg4, int arg5) {}
	
	public void OnAlertReceived(AccSession arg0, AccAlert arg1) {}
	
	public void OnPreferenceResult(AccSession arg0, String arg1, int arg2, String arg3, AccResult arg4) {}

	public void OnPreferenceChange(AccSession arg0, String arg1, AccResult arg2) {}

	public void OnPreferenceInvalid(AccSession arg0, String arg1, AccResult arg2) {}

	public void OnPluginChange(AccSession arg0, AccPluginInfo arg1, AccPluginInfoProp arg2) {}

	public void OnBartItemRequestPropertyResult(AccSession arg0, AccBartItem arg1, AccBartItemProp arg2, int arg3, AccResult arg4, AccVariant arg5) {}

	public void OnUserRequestPropertyResult(AccSession arg0, AccUser arg1, AccUserProp arg2, int arg3, AccResult arg4, AccVariant arg5) {}

	public void OnGroupAdded(AccSession arg0, AccGroup arg1, int arg2, AccResult arg3) {}

	public void OnGroupRemoved(AccSession arg0, AccGroup arg1, AccResult arg2) {}

	public void OnGroupMoved(AccSession arg0, AccGroup arg1, int arg2, int arg3, AccResult arg4) {}

	public void OnBuddyAdded(AccSession arg0, AccGroup arg1, AccUser arg2, int arg3, AccResult arg4) {}

	public void OnBuddyRemoved(AccSession arg0, AccGroup arg1, AccUser arg2, AccResult arg3) {}

	public void OnBuddyMoved(AccSession arg0, AccUser arg1, AccGroup arg2, int arg3, AccGroup arg4, int arg5, AccResult arg6) {}

	public void OnBuddyListChange(AccSession arg0, AccBuddyList arg1, AccBuddyListProp arg2) {}

	public void OnGroupChange(AccSession arg0, AccGroup arg1, AccGroupProp arg2) {}

	public void OnUserChange(AccSession arg0, AccUser arg1, AccUser arg2, AccUserProp arg3, AccResult arg4) {}

	public void OnChangesBegin(AccSession arg0) {}

	public void OnChangesEnd(AccSession arg0) {}

	public void OnNewSecondarySession(AccSession arg0, AccSecondarySession arg1, int arg2) {}

	public void OnSecondarySessionStateChange(AccSession arg0, AccSecondarySession arg1, AccSecondarySessionState arg2, AccResult arg3) {}

	public void OnSecondarySessionChange(AccSession arg0, AccSecondarySession arg1, int arg2) {}

	public void OnParticipantJoined(AccSession arg0, AccSecondarySession arg1, AccParticipant arg2) {}

	public void OnParticipantChange(AccSession arg0, AccSecondarySession arg1, AccParticipant arg2, AccParticipant arg3, AccParticipantProp arg4) {}

	public void OnParticipantLeft(AccSession arg0, AccSecondarySession arg1, AccParticipant arg2, AccResult arg3, String arg4, String arg5) {}

	public void OnInviteResult(AccSession arg0, AccSecondarySession arg1, String arg2, int arg3, AccResult arg4) {}

	public void OnEjectResult(AccSession arg0, AccSecondarySession arg1, String arg2, int arg3, AccResult arg4) {}

	public void BeforeImSend(AccSession arg0, AccImSession arg1, AccParticipant arg2, AccIm arg3) {}

	public void OnImSent(AccSession arg0, AccImSession arg1, AccParticipant arg2, AccIm arg3) {}

	public void OnImSendResult(AccSession arg0, AccImSession arg1, AccParticipant arg2, AccIm arg3, AccResult arg4) {}

	public void BeforeImReceived(AccSession arg0, AccImSession arg1, AccParticipant arg2, AccIm arg3) {}

	public void OnInputStateChange(AccSession arg0, AccImSession arg1, String arg2, AccImInputState arg3) {}

	public void OnEmbedDownloadComplete(AccSession arg0, AccImSession arg1, AccIm arg2) {}

	public void OnEmbedUploadComplete(AccSession arg0, AccImSession arg1, AccIm arg2) {}

	public void OnRateLimitStateChange(AccSession arg0, AccImSession arg1, AccRateState arg2) {}

	public void OnNewFileXfer(AccSession arg0, AccFileXferSession arg1, AccFileXfer arg2) {}

	public void OnFileXferProgress(AccSession arg0, AccFileXferSession arg1, AccFileXfer arg2) {}

	public void OnFileXferCollision(AccSession arg0, AccFileXferSession arg1, AccFileXfer arg2) {}

	public void OnFileXferComplete(AccSession arg0, AccFileXferSession arg1, AccFileXfer arg2, AccResult arg3) {}

	public void OnFileXferSessionComplete(AccSession arg0, AccFileXferSession arg1, AccResult arg2) {}

	public void OnFileSharingRequestListingResult(AccSession arg0, AccFileSharingSession arg1, AccFileSharingItem arg2, int arg3, AccResult arg4) {}

	public void OnFileSharingRequestXferResult(AccSession arg0, AccFileSharingSession arg1, AccFileXferSession arg2, int arg3, AccFileXfer arg4) {}

	public void OnAvStreamStateChange(AccSession arg0, AccAvSession arg1, String arg2, AccAvStreamType arg3, AccSecondarySessionState arg4, AccResult arg5){}

	public void OnAudioLevelChange(AccSession arg0, AccAvSession arg1, String arg2, int arg3){}

	public void OnSoundEffectReceived(AccSession arg0, AccAvSession arg1, String arg2, String arg3){}

	public void OnCustomDataReceived(AccSession arg0, AccCustomSession  arg1,  AccParticipant arg2, AccIm arg3){}
	
	public void OnCustomSendResult(AccSession arg0, AccCustomSession arg1, AccParticipant arg2, AccIm arg3, AccResult arg4){}

	public void OnEmbedUploadProgress(AccSession arg0, AccImSession arg1, AccIm arg2, String arg3, AccStream arg4){}
	   
	public void OnEmbedDownloadProgress(AccSession arg0, AccImSession arg1, AccIm arg2, String arg3, AccStream arg4){}

	public void OnDeleteStoredImsResult(AccSession arg0, int arg1, AccResult arg2){}

	public void OnDeliverStoredImsResult(AccSession arg0,int arg1,AccResult arg2){}

	public void OnRequestSummariesResult(AccSession arg0,int arg1, AccResult arg2,AccVariant arg3){}
	
	public void OnAvManagerChange(AccSession arg0, AccAvManager arg1, AccAvManagerProp arg2, AccResult arg3){}
	
	public void OnLocalImReceived(AccSession arg0, AccImSession arg1, AccIm arg2){}

	public void OnPluginUninstall(AccSession arg0, AccPluginInfo arg1) {}

	public void OnPushBuddyFeedResult(AccSession arg0, int arg1,
			AccResult arg2, String arg3) {}

	public void OnRequestServiceResult(AccSession arg0, int arg1,
			AccResult arg2, String arg3, int arg4, AccVariant arg5) {}
}
