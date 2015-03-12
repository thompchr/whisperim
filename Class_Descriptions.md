## AIM Operation ##

This class acts as a helper class to generate operations to pass to the AIMSession class and it's running thread.  It also contains the enumerations required to designate operations.  AIMOperation objects are passed to the AIMSession object and are then placed in a queue to be performed.

## AIM Session ##

This class acts as the connection to the AIM service as well as providing the interface to handle events fired from the AIM service.  It runs as an independent thread and handles all traffic between the client and the AIM servers.

## Prefs ##

This class acts as a container for the session preferences for the AIM service. Preferences are added to the HashMap as String objects via the setValue method. This class extends the AccPreferencesHook class which allows it to be passed to and parsed by the AIM service during the connection.

## Encryptor ##

This class forms the backbone of the security functionality of the whisper client. It contains methods for generating RSA keypairs as well as performing the encryption/decryption operations on the messages.  It also reads the key file to import a given buddy's public key.

## Key Container ##
This class is currently unused but is designed to hold local and foreign keys in memory.

## Encryptor Test ##

This method runs a random number of encryption/decryption cycles that each use a string of random characters and random size. This functions to test the stability of the encryption/decryption process and assure that all strings will be encrypted and decrypted without error.  Further, this test verifies that the product of the encryption is not equal to the original plain text.  At the end, it verifies that the output of the decryption function is the same as the original plain text. Currently, this test outputs its results and information for each iteration to the command line.

## Connection Manager Test ##

This class is designed to provided a set of tests for the ConnectionManager class.