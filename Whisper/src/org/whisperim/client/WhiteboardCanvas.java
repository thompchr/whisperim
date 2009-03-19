package org.whisperim.client;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

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
	
	  void clear ()
	  {
		Rectangle r = new Rectangle();
		
	    Graphics2D g2 = (Graphics2D) this.getGraphics();   
	    canvas.setColor (getBackground());
	    canvas.fillRect (r.x, rectangle.y,
	                     rectangle.width, rectangle.height);
	  }
}
