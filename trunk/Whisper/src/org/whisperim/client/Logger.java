package org.whisperim.client;


/*
 *  This is a simple buffered logger.  
 * 
 *  Each IM Session gets its own logger. 
 * 
 *  Currently, the log is stored in Whisper/lib, as it is the working directory set in the
 *  run configuration.
 *  @author: logan buchanan
 */



import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger{
	 
	private BufferedWriter writer_;
	
	
	Date date = new Date();
	SimpleDateFormat filename_ = new SimpleDateFormat("MM-dd-yy");
	
	//Default ctor, creates the file with the date as filename
	public Logger(){
		
		try{				
			//Appends to the file if it exists
			writer_ = new BufferedWriter(new FileWriter(filename_.format(date) + ".txt",true));
			
		}		
		catch (IOException e){
			System.err.println("Failed to create log file: " + e.getMessage());
		}
	}
	
	//Ctor that creates log with user defined filename.
	//Currently not used, but could be used in the future to give users the option to specify
	//the filename.
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