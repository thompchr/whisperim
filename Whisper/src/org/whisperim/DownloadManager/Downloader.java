	/**************************************************************************
	 * Copyright 2009 Nick Krieble							                   *
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
package org.whisperim.DownloadManager;

import java.io.*;
import java.net.*;
import java.util.*;

public class Downloader extends Observable implements Runnable {

	 // Max size of the download buffer.
    private static final int MAX_BUFFER_SIZE = 1024;
    
    // Status names.
    public static final String STATUSES[] = {"Downloading",
    "Paused", "Complete", "Cancelled", "Error"};
    
    // These are the status codes.
    public static final int DOWNLOADING = 0;
    public static final int PAUSED = 1;
    public static final int COMPLETE = 2;
    public static final int CANCELLED = 3;
    public static final int ERROR = 4;
    
    private URL url_; // Download URL.
    private int size_; // Size of download in bytes.
    private int downloaded_; // Number of bytes downloaded.
    private int status_; // Current status of download.
    
    // Ctr.
    public Downloader(URL url) {
        this.url_ = url;
        size_ = -1;
        downloaded_ = 0;
        status_ = DOWNLOADING;
        
        // Begin the download.
        download();
    }
    
    // Get this download's URL.
    public String getUrl() {
        return url_.toString();
    }
    
    // Get this download's size.
    public int getSize() {
        return size_;
    }
    
    // Get this download's progress.
    public float getProgress() {
        return ((float) downloaded_ / size_) * 100;
    }
    
    // Get this download's status.
    public int getStatus() {
        return status_;
    }
    
    // Pause this download.
    public void pause() {
        status_ = PAUSED;
        stateChanged();
    }
    
    // Resume this download.
    public void resume() {
        status_ = DOWNLOADING;
        stateChanged();
        download();
    }
    
    // Cancel this download.
    public void cancel() {
        status_ = CANCELLED;
        stateChanged();
    }
    
    // Mark this download as having an error.
    private void error() {
        status_ = ERROR;
        stateChanged();
    }
    
    // Start or resume downloading.
    private void download() {
        Thread thread = new Thread(this);
        thread.start();
    }
    
    // Get file name portion of URL.
    private String getFileName(URL url) {
        String fileName = url.getFile();
        return fileName.substring(fileName.lastIndexOf('/') + 1);
    }
    
    // Download file.
    public void run() {
        RandomAccessFile file = null;
        InputStream stream = null;
        
        try {
            // Open connection to URL.
            HttpURLConnection connection =
                    (HttpURLConnection) url_.openConnection();
            
            // Specify what portion of file to download.
            connection.setRequestProperty("Range",
                    "bytes=" + downloaded_ + "-");
            
            // Connect to server.
            connection.connect();
            
            // Make sure response code is in the 200 range.
            if (connection.getResponseCode() / 100 != 2) {
                error();
            }
            
            // Check for valid content length.
            int contentLength = connection.getContentLength();
            if (contentLength < 1) {
                error();
            }
            
      /* Set the size for this download if it
         hasn't been already set. */
            if (size_ == -1) {
                size_ = contentLength;
                stateChanged();
            }
            
            // Open file and seek to the end of it.
            file = new RandomAccessFile(getFileName(url_), "rw");
            file.seek(downloaded_);
            
            stream = connection.getInputStream();
            while (status_ == DOWNLOADING) {
        /* Size buffer according to how much of the
           file is left to download. */
                byte buffer[];
                if (size_ - downloaded_ > MAX_BUFFER_SIZE) {
                    buffer = new byte[MAX_BUFFER_SIZE];
                } else {
                    buffer = new byte[size_ - downloaded_];
                }
                
                // Read from server into buffer.
                int read = stream.read(buffer);
                if (read == -1)
                    break;
                
                // Write buffer to file.
                file.write(buffer, 0, read);
                downloaded_ += read;
                stateChanged();
            }
            
      /* Change status to complete if this point was
         reached because downloading has finished. */
            if (status_ == DOWNLOADING) {
                status_ = COMPLETE;
                stateChanged();
            }
        } catch (Exception e) {
            error();
        } finally {
            // Close file.
            if (file != null) {
                try {
                    file.close();
                } catch (Exception e) {}
            }
            
            // Close connection to server.
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {}
            }
        }
    }
    
    // Notify observers that this download's status has changed.
    private void stateChanged() {
        setChanged();
        notifyObservers();
    }
} 


