package org.whisperim.client;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

import java.awt.image.BufferedImage;

public class WhiteboardCanvas extends Canvas{
	public WhiteboardCanvas()
	{
		super();
		this.setSize(470, 430);
		this.setBackground(Color.WHITE);
	}
	
	public void paint (Graphics g) {
	    Graphics2D g2;
	    int Height;
	    g2 = (Graphics2D) g;
	    Height = getHeight();
	    g2.drawString ("CANVAS!!!", 10, Height/2);
	 }
	
	  public void clear()
	  {		
	    Graphics2D g2 = (Graphics2D) this.getGraphics();   
	    g2.setColor (Color.WHITE);
	    g2.fillRect (0,0,470,430);
	  }
	  
	  public void save(File f)
	  {
		  try
		  {
			  	BufferedImage bi = canvasToImage(this);
		        ImageIO.write(bi,"PNG",f);
		  } 
		  catch (IOException e) 
		  {
			  
		  }
	  }
	  
	  private BufferedImage canvasToImage(Canvas canvas) {
	        BufferedImage image = new BufferedImage(canvas.getWidth(),
	        		canvas.getHeight(),
	        		BufferedImage.TYPE_BYTE_INDEXED);
	        
	        Graphics2D g2 = image.createGraphics();
	        canvas.paint(g2);
	        g2.dispose();
	        return image;
	    }
}
