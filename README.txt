How to run WhisperIM

In Eclipse:
-Checkout from SVN
-Select project
-From menu bar goto Run->Run Configurations
-Select the Arguments Tab
-In the VM Arguments text box type this:  -Djava.library.path= $YOUR_PATH_TO_WHISPER_LIB
	-For example, on Vista this would look something like: -Djava.library.path=C:\Users\dlugokja\workspace\Whisper\lib
-In the same Arguments Tab, change the working directory to 'other'
-Paste this into the 'other' text field: ${workspace_loc:Whisper/lib}
