package org.whisperim.client;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ThreadServices {

	private static ThreadServices instance_ = new ThreadServices();
	
	public static ThreadServices get(){
		return instance_;
	}
	
	private Executor executor_ = Executors.newFixedThreadPool(5);
	
	private ThreadServices(){}
	
	public void runInThread(Runnable r){
		executor_.execute(r);		
	}
	
}
