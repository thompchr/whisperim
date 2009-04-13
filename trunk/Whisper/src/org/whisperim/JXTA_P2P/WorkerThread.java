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
package org.whisperim.JXTA_P2P;

import java.util.Vector;

public class WorkerThread extends Thread{

	// Tracks objects to be run.
    private Vector runObjects;

    // Track if thread is running or not.
    private boolean running = true;

   // Create instance.
    public WorkerThread(){
        super("WorkerThread");
	runObjects = new Vector();
        this.start();
    }

    // Run the thread.
    public void run(){
        Runnable runner;
 
        while( running){
	    synchronized(runObjects){
                if(runObjects.size() ==  0){
                    try{
                      runObjects.wait();
                    }   
                    catch(InterruptedException ii){ }
		}
                if( runObjects.size() > 0){
		    runner = (Runnable)runObjects.elementAt(0);
                    runObjects.removeElementAt(0);
		}
                else runner = null;
	    }
            if( runner != null && running)  runner.run();
	}
    }


    // Adds data to the scheduler.
    public void addData(Runnable object){
	synchronized(runObjects){
	    runObjects.addElement(object);
            runObjects.notify();
	}
    }

    //Removes a Runnable object from the stack.
    public boolean removeData(Runnable object){
        boolean result =  false;

        synchronized(runObjects){
 	    result = runObjects.removeElement(object);
	}
        return result;
    }

    // Stops the worker thread.
    public void stopThread(){
        running = false;
        synchronized(runObjects){
            runObjects.notify();    // wake the thread if it is waiting
	}
    } 
}

