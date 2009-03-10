 /**************************************************************************
 * Copyright 2009 Chris Thompson                                           *
 *                                                                         *
 * Licensed under the Apache License, Version 2.0 (the "License");         *
 * you may not use this file except in compliance with the License.        *
 * You may obtain a copy of the License at                                 *
 *                                                                         *
 * http://www.apache.org/licenses/LICENSE-2.0                              *
 *                                                                         *
 * Unless required by applicable law or agreed to in writing, software     *
 * distributed under the License is distributed on an "AS IS" BASIS,       *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.*
 * See the License for the specific language governing permissions and     *
 * limitations under the License.                                          *
 **************************************************************************/

package org.whisperim.client;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.UIManager;

import org.whisperim.plugins.PluginLoader;

public class WhisperPluginManagerWindow extends JFrame implements
		ActionListener {
	/**
	 * Look and feel declaration
	 */
	private static final String LOOK_AND_FEEL_ = UIManager.getSystemLookAndFeelClassName();
	
	private PluginLoader pm_;
	
	
	private JList activePlugins_;
	private JMenuBar menuBar_;
	private JScrollPane pluginScroll_;
	
	// Plugins Menu \\
		//Load Plugin
	private JMenu pluginsMenu_;
	private JMenuItem loadPluginMenu_;
	
	private JButton closeBtn_;
	
	

	/**
	 * I really have no idea what this is
	 */
	private static final long serialVersionUID = -28004543656411969L;
	
	private static final String LOAD_NEW_PLUGIN_ = "Load Plugin";
	private static final String WINDOW_TITLE_ = "Plugin Manager";
	private static final String CLOSE_ = "Close";
	private static final String PLUGIN_ = "Plugins";
	
	
	
	
	
	/**
	 * Constructor
	 */
	public WhisperPluginManagerWindow(PluginLoader loader){
		
		pm_ = loader;
		
		closeBtn_ = new JButton(CLOSE_);
		pluginsMenu_ = new JMenu(PLUGIN_);
		loadPluginMenu_ = new JMenuItem(LOAD_NEW_PLUGIN_);
		menuBar_ = new JMenuBar();
		
		setTitle(WINDOW_TITLE_);
		
		activePlugins_ = new JList();
		pluginScroll_ = new JScrollPane(activePlugins_);
		
		menuBar_.add(pluginsMenu_);
		pluginsMenu_.add(loadPluginMenu_);
		
		
		setMinimumSize(new Dimension(400, 600));
		setMaximumSize(new Dimension(1000, 1000));
		setPreferredSize(new Dimension(400, 600));
		
		activePlugins_.setMinimumSize(new Dimension(350, 500));
		activePlugins_.setPreferredSize(new Dimension(350, 500));
		
		closeBtn_.setMinimumSize(new Dimension(75, 26));
		closeBtn_.setMaximumSize(new Dimension(75, 26));
		closeBtn_.setPreferredSize(new Dimension(75,26));
		closeBtn_.setActionCommand(CLOSE_);
		closeBtn_.addActionListener(this);
		
		loadPluginMenu_.setActionCommand(LOAD_NEW_PLUGIN_);
		loadPluginMenu_.addActionListener(this);
		
		setJMenuBar(menuBar_);
		
		try{
			UIManager.setLookAndFeel(LOOK_AND_FEEL_);
		}catch(Exception e){
			//totally hosed...
		}
		
		SpringLayout sl = new SpringLayout();
		Container cp = getContentPane();
		cp.setLayout(sl);
		
		cp.add(activePlugins_);
		cp.add(closeBtn_);
		
		//Constraints
		sl.putConstraint(SpringLayout.NORTH, activePlugins_, 50, SpringLayout.NORTH, cp);
		sl.putConstraint(SpringLayout.WEST, activePlugins_, 15, SpringLayout.WEST, cp);
		sl.putConstraint(SpringLayout.EAST, activePlugins_, -15, SpringLayout.EAST, cp);
		sl.putConstraint(SpringLayout.SOUTH, activePlugins_, -50, SpringLayout.SOUTH, cp);
		
		
		sl.putConstraint(SpringLayout.SOUTH, closeBtn_, -10, SpringLayout.SOUTH, cp);
		sl.putConstraint(SpringLayout.EAST, closeBtn_, -15, SpringLayout.EAST, cp);
		
		
		
		
		pack();
		setVisible(true);
		
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String ac = arg0.getActionCommand();
		if(ac.equalsIgnoreCase(CLOSE_)){
			this.dispose();
		}
		
		if(ac.equalsIgnoreCase(LOAD_NEW_PLUGIN_)){
			//Open the load plugin window
			
			EventQueue.invokeLater(new Runnable(){

				@Override
				public void run() {
					final PluginLoader temp = pm_;
					new LoadPluginWindow(temp);
					
				}
				
			});
			
		}

	}
	
	public static void main(String[] args){
		EventQueue.invokeLater(new Runnable(){

			@Override
			public void run() {
				new WhisperPluginManagerWindow(new PluginLoader(null));
				
			}
			
		});
	}
	

}
