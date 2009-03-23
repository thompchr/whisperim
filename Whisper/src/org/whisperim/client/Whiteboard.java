package org.whisperim.client;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.jdesktop.layout.GroupLayout;
import org.whisperim.filters.WhiteboardFileFilter;

public class Whiteboard extends JFrame{
    private JButton clear;
    private JButton save;
    private WhiteboardCanvas canvas;
    private Container pane;
    private FlowLayout layout;
    
	public Whiteboard(String buddy,int width, int height)
	{
		super("Whiteboard with " + buddy);
		this.setSize(width, height);
		
		this.setResizable(false);
		
		layout = new FlowLayout();
		
		pane = getContentPane();
		pane.setLayout(layout);
		
		save = new JButton("Save");
		clear = new JButton("Clear");
		canvas = new WhiteboardCanvas();
	    
	    clear.addActionListener(new ClearHandler());
	    save.addActionListener(new SaveHandler());
	   
	    pane.add(clear);
	    pane.add(save);
	    pane.add(canvas);
	    
	    setVisible(true);
	  }
	  // add inner class event handler for each button here
	  class ClearHandler implements ActionListener
	  {
	    public void actionPerformed(ActionEvent e)
	    {
	        canvas.clear();
	    }
	  }
	  
	  class SaveHandler implements ActionListener
	  {
	    public void actionPerformed(ActionEvent e)
	    {
	         File file = null;
	         JFileChooser fileChooser = new JFileChooser ();
	         WhiteboardFileFilter whiteboardFileFilter = new WhiteboardFileFilter();
	         
	         fileChooser.setCurrentDirectory (new File ("."));
	         fileChooser.setFileFilter(whiteboardFileFilter);
	               
	         int result = fileChooser.showSaveDialog(null);
	         
	         if (result == JFileChooser.APPROVE_OPTION) 
	         {
	             file = fileChooser.getSelectedFile();
	             		
	             if (file.exists ()) 
	             {
	                 int response = JOptionPane.showConfirmDialog (null,
	                   "Overwrite existing file?","Confirm Overwrite",
	                    JOptionPane.OK_CANCEL_OPTION,
	                    JOptionPane.QUESTION_MESSAGE);
	             
		             if(response == JOptionPane.OK_OPTION)
		            	 canvas.save(file);
	             }
	             else
	            	 canvas.save(file);
	         }
	    }
	  }
	  
	  public void handleCommand(String cmd)
	  {
		  canvas.paintString(cmd);
	  }
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
}
