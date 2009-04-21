package org.whisperim.webcam;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.media.Buffer;
import javax.media.CannotRealizeException;
import javax.media.CaptureDeviceInfo;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.control.FrameGrabbingControl;
import javax.media.format.VideoFormat;
import javax.media.util.BufferToImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class camagain extends JPanel implements ActionListener{
	public static Player player = null;
	public static CaptureDeviceInfo di = null;
	public static MediaLocator ml = null;
	public static Buffer buf = null;
	public static Image img = null;
	public static BufferToImage btoi = null;
	private JFrame f;
	private Container contentPane;
	private JLabel label;
	
	public static void main(String[] args) throws IOException,
			NoPlayerException, CannotRealizeException {
		camagain c = new camagain();
	}

	public camagain()
	{
        f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        contentPane = f.getContentPane();
        contentPane.setLayout(new FlowLayout());

        
        JButton takePic = new JButton("Take Picture");
        takePic.addActionListener(this);
        takePic.setActionCommand("takePic");
        takePic.setPreferredSize(new Dimension(120,20));
        
        JButton setPic = new JButton("Set Picture");
        setPic.addActionListener(this);
        setPic.setActionCommand("setPic");
        setPic.setPreferredSize(new Dimension(120,20));
        
        contentPane.add(takePic);
        contentPane.add(setPic);
        
        f.setBounds(10, 10, 300, 175);
        f.setLocation(200,250);
        f.setVisible(true);
	}
	
    public void actionPerformed(ActionEvent e) {
        if ("setPic".equals(e.getActionCommand())) {
        	System.out.println("Set Pic");
        } else if ("takePic".equals(e.getActionCommand())){
        	takePic();
        	System.out.println("Take Pic");
        }
        else
        {
        	System.out.println("Clicked");
        }
    }
	
	public void takePic(){	    
		CaptureDeviceInfo device = new CaptureDeviceInfo("vfw://0",
				new MediaLocator("vfw:Microsoft WDM Image Capture (Win32):0"),
				null);

		ml = device.getLocator();

		if (ml == null) {
			System.out.println("ml is null...");
		} else {

		}

		try {
			player = Manager.createRealizedPlayer(ml);
		} catch (NoPlayerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (CannotRealizeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		player.start();
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		FrameGrabbingControl frameGrabber = (FrameGrabbingControl)player.getControl("javax.media.control.FrameGrabbingControl");
		Buffer buf = frameGrabber.grabFrame();
		// Convert frame to an buffered image so it can be processed and saved
		Image img = (new BufferToImage((VideoFormat)buf.getFormat()).createImage(buf));
		BufferedImage buffImg = new BufferedImage(40, 40, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = buffImg.createGraphics();
		
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(img, 0, 0, 40, 40, null);
		
		//g.drawImage(img, null, null);
		
		// Save image to disk as JPG
		try {
			ImageIO.write(buffImg, "jpg", new File("C:\\Users\\dlugokja\\Pictures\\cam.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		drawPic();
		
		player.close();
		player.deallocate();
	}
	
	public void drawPic(){
		URL url = null;
		try {
			url = new File("C:\\Users\\dlugokja\\Pictures\\cam.jpg").toURL();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        BufferedImage image = null;
		try {
			image = ImageIO.read(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if(label == null)
        	label = new JLabel(new ImageIcon(image));
        else
        {
        	contentPane.remove(label);
        	label = new JLabel(new ImageIcon(image));
        }
        contentPane.add(label);
        contentPane.repaint();
	}
}