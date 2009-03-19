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

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.UIManager;

import org.jdesktop.layout.GroupLayout;

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
	    
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    
	    setVisible(true);
	  }
	  // add inner class event handler for each button here
	  class ClearHandler implements ActionListener
	  {
	    public void actionPerformed(ActionEvent e)
	    {
	        //clear canvas
	    }
	  }
	  
	  class SaveHandler implements ActionListener
	  {
	    public void actionPerformed(ActionEvent e)
	    {
	        System.exit(0);
	    }
	  }
	  
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
}
