package org.whisperim.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

//import net.dapper.scrender.RenderingException;
//import net.dapper.scrender.Scrender;

public class HyperlinkWindow extends JFrame{
	
	public HyperlinkWindow(String url){
		super();
		File f = new File("C:\\mvc.png");
	        setSize(400, 400);
	        setTitle(url);
	        setDefaultCloseOperation(EXIT_ON_CLOSE);
	        //Scrender s = new Scrender();
	        try {
				//s.init();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        //s.render(url, f);
			//this.getContentPane().add(comp)
			BufferedImage image;
			try {
				image = ImageIO.read(f);
			    ImageRenderComponent irc = new ImageRenderComponent(image);
			    irc.setOpaque(true);
			    this.setContentPane(irc);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
}

class ImageRenderComponent extends JPanel {
    BufferedImage image;
    Dimension size;
 
    public ImageRenderComponent(BufferedImage image) {
        this.image = image;
        size = new Dimension(image.getWidth(), image.getHeight());
    }
 
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int x = (getWidth() - size.width)/2;
        int y = (getHeight() - size.height)/2;
        g.drawImage(image, x, y, this);
    }
 
    public Dimension getPreferredSize() {
        return size;
    }
}