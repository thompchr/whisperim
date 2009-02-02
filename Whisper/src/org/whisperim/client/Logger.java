package org.whisperim.client;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
/*
 * This is a simple buffered logger.  
 * 
 * Each IM Session gets its own logger.  
 */


public class Logger{
	 
	private BufferedWriter writer_;
	
	
	Date date = new Date();
	SimpleDateFormat filename_ = new SimpleDateFormat("MM-dd-yy");
	
	//Default ctor, creates the file with the date and buddy name.
	public Logger(){
		
		try{				
			writer_ = new BufferedWriter(new FileWriter(filename_.format(date) + ".txt",true));
		}		
		catch (IOException e){
			System.err.println("Failed to create log file: " + e.getMessage());
		}
	}
	
	//Ctor that creates log with user defined filename, for future use?
	public Logger(String filename){
		
		try{
			writer_ = new BufferedWriter(new FileWriter(filename + ".txt",true));
		}
		catch (IOException e){
			System.err.println("Failed to create log file: " + e.getMessage());
		}
	}
	
	
	public void write(Message message, String user){
		
		try{
			writer_.write(message.getTimeSent()+ " " + user + ": " + message.getMessage());
			writer_.newLine();
		}
		catch (IOException e){
			System.err.println("Failed to write to log file: " + e.getMessage());
		}
		
	}
	
	
	public void close(){
		
		try{
			writer_.close();
		}
		catch (IOException e){
			System.err.println("Failed to close file: " + e.getMessage());
		}
	}
	
}