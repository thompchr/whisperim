# Build 7 #
  * Super secret and totally awesome user story (Chris)
  * User can receive notifications and integrate with Facebook. (John)
  * User's message will be auto-spell checked as it is typed. (John)
  * User can send SMS texts to cell phones using Whisper client. (Kirk)
  * User will be able to click tab to view email, initially gmail only. (Nick)
  * ~~User can check their buddies statistics.~~ (Already part of whisperbot) (Nick)


# Build 6 #
  * Super secret and totally awesome user story (Chris)
  * User will be able to import GTK themes (Cory) **not feasible**
  * User will be able to pass all traffic through the TOR network (Cory) **not feasible**
  * User can use gtalk (Cory)
  * User can lock session and can only be resumed using a password (Cory)
  * User can 'preview' links and links will be filtered for easy integration into Google Maps and other online services. (John)
  * Users can make themselves 'locatable' and share this information with buddies. (John)
  * User can use a webcam to take a picture of themselves and set it as their buddy icon. (John)
  * User can create a local profile and WYSIWYG edit it. (Kirk)
  * User can receive rich text in IM windows (Kirk)
  * User can drag tabs.  (Logan)
  * User can drag files to chat area to initiate file transfer (pending transfer ability).  (Logan)
  * User will have peer to peer ability with jmdns (thanks javassonne). (Nick)


# Build 5 #
  * User will be displayed an account management window for accounts saved in Whisper program and implemented as part of the Java GUI when starting the program for the first time (Chris)
  * User will be able to save account information such as username, password, buddy icon, logon status, etc for each account that is enabled on Whisper.  This will allow for auto logins (Chris)
  * User will be able to change the theme from preferences (Cory)
  * User will be able to right-click on a buddy to view options to send messages (Cory)
  * User will be able to ... (Cory)
  * Users can connect to digital whiteboard to create drawings collaboratively. (John)
  * User can have their most recently played iTunes song show up in their away message. (John)
  * User can hear various customizable sounds for all actions whisper takes (Kirk)
  * User can view about page (Kirk)
  * User will be able to interface with a 'system tray' icon for Whisper (Kirk)
  * User can change status, hide buddy list, open account manager, blink with new message, hide system tray, mute all sounds from system tray (Kirk)
  * User can close tabs via hotkeys, button on tabs. (Logan)
  * User can choose how to open new conversations (new window, new tab, etc). (Logan)
  * Tabs that aren't focused will flash on receiving message (Logan)
  * Roll over story from previous cycle; Change preferences via gui(Nick)
  * User will have preference states saved by outputing to an XML file. (Nick)
  * User will be able to transfer files with a file transfer bot. (Nick)
  * User will be able to toggle bots with preferences. (Nick)
  * User will be able to transfer the file with the bot as an encrypted file. (Nick)
  * User will be able to check a flickr or similar social networking website with an open API to pull down updates via a Bot. (social site TBD) (Nick)

# Build 4 #
  * User will be able to interface with a 'system tray' icon for Whisper (Kirk, John)
  * User can load custom plug-in modules (including protocols) (Chris)
  * Whisper clients can identify if other client is using Whisper and disable special functionality to prevent bombarding buddy with garbage (Chris)
  * User will be able to access a preferences menu to change program-wide settings (connects in bot from previous build) (Cory, Nick)
  * User is displayed account setup screen when starting program for first time (Delayed, John)

# Build 3 #
  * User can see "enable Bot" for messaging. Bot is simple echo bot. (Nick, John) (Bot/GUI disconnected pending pattern agreement)
  * User can set status. (Nick, John) (On Hold)
  * User can enable/disable logging (Logan)
  * User can receive parsed rich text in conversation with an AIM user (Cory, Kirk)
  * User can view File, etc. menus without problems (Cory)
  * User can identify which network their buddy is on by an icon next to their name in the buddy list and an icon in the chat window (Cory)
  * User will be able to access the 'File' menu and exit the program using 'Quit' from the buddy menu (Cory)
  * User will be able to access the 'Preferences' menu from the buddy menu (Cory)
  * User can select "New IM" and be displayed a new window without selecting a buddy (Chris)
  * Whisper clients can identify if other client is using Whisper and disable special functionality to prevent bombarding buddy with garbage (Chris - Delayed because Chris is an idiot and broke the build)
  * User can view multiple conversations in a tabbed interface (Logan)

# Build 2 #
  * Ability to log conversations.  (Logan)
  * User can set status. (Nick, John) (On Hold)
  * User can receive parsed rich text in conversation with an AIM user(Cory, Kirk) (Delayed)
  * User can utilize the enter key in place of clicking the send button (Cory, Kirk)
  * User can share public key information with other users (Chris)
  * User can view File, etc. menus without problems (Cory) (Delayed)
  * User can view messages in text wrapped window (Cory)

# Build 1 #
  * User can chat securely without error messages (Chris, Cory, Nick)
  * 1/16: Encryptor class has been fixed, code cleanup still in progress
  * User becomes idle after set amount of inactivity (Kirk, John)
  * 1/15: Developed skeleton code for IdleListener class
  * 1/19: Implemented solution to allow user to go idle.
  * Removed listeners to determine idle status
  * Based Idle status on how recent a user has sent a message
    * Idle status is updated in the title bar as: Whisper (Idle)
  * User can utilize the enter key in place of clicking the login button (Cory)
  * User will be notified and redirected when bad password is entered and given some way to correct problem (Chris)

# Future #
  * User can select buddy by left click and then  toggle block status using a button on   toolbar
  * User will see updated buddy list when other users sign in or out
  * User is displayed account setup screen when starting program for first time
  * User will be notified of errors (exceptions) via dialog boxes unless they are fully handled by the software
  * User can send messages when not connected through an ad-hoc network and electing a server
  * User can export private key and use remotely using a web interface
  * User can Whisper as a remote shell client
  * User can sort buddies by status, name, alias, network, or group
  * User can set unique buddy icons to be displayed instead of their icon
  * User can use Whisper on Mac OSX and Linux
  * User can chat using the yahoo protocol
  * User can chat using the msn protocol
  * User can set their status on twitter
  * User can interface with WYSIWYG in IM window