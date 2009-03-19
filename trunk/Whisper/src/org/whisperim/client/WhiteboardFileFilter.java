package org.whisperim.client;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class WhiteboardFileFilter extends FileFilter{
	public WhiteboardFileFilter()
	{
		
	}

	public boolean accept(File f){     
    	if(f != null) {
    	    if(f.isDirectory()) 
    	    {
    	    	return true;
    	    }
    	    
    	    String extension = getExtension(f);
    	    
    	    if(extension != null && extension.equalsIgnoreCase("png")) 
    	    {
    	    	return true;
    	    }
    	}
    	return false;
	}
    
	public String getDescription(){
        return "PNG (Portable Network Graphics) *.png";
     }
     
    public String getExtension(File f) {
    		if(f != null) {
    		    String filename = f.getName();
    		    int i = filename.lastIndexOf('.');
    		    if(i>0 && i<filename.length()-1) {
    			return filename.substring(i+1).toLowerCase();
    		    };
    		}
    	return null;
    }

}
