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
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.whisperim.models.ActiveAccountModel;
import org.whisperim.renderers.ActiveAccountRenderer;

public class AccountManagementWindow extends JFrame implements ActionListener {

	/**
	 * I really have no idea what this is
	 */
	private static final long serialVersionUID = 3745620437275002903L;

	/**
	 * Look and feel declaration
	 */
	private static final String LOOK_AND_FEEL_ = UIManager
			.getSystemLookAndFeelClassName();

	private ConnectionManager cm_;
	private ActiveAccountModel aam_ = new ActiveAccountModel();

	private JTable connections_;
	private JMenuBar menuBar_;
	private JScrollPane pluginScroll_;

	// Plugins Menu \\
	// Load Plugin
	private JMenu accountsMenu_;
	private JMenuItem loadPluginMenu_;

	private JButton closeBtn_;

	private static final String NEW_ACCOUNT_ = "New Account";
	private static final String WINDOW_TITLE_ = "Account Manager";
	private static final String CLOSE_ = "Close";
	private static final String ACCOUNTS_ = "Accounts";

	public AccountManagementWindow(ConnectionManager cm){
		cm_ = cm;
		
		closeBtn_ = new JButton(CLOSE_);
		accountsMenu_ = new JMenu(ACCOUNTS_);
		loadPluginMenu_ = new JMenuItem(NEW_ACCOUNT_);
		menuBar_ = new JMenuBar();
		
		setTitle(WINDOW_TITLE_);
		
		connections_ = new JTable(aam_);
		connections_.setDefaultRenderer(ConnectionStrategy.class, new ActiveAccountRenderer());
		//connections_.setCellRenderer(new ActiveAccountRenderer());
		
		pluginScroll_ = new JScrollPane(connections_);
		
		menuBar_.add(accountsMenu_);
		accountsMenu_.add(loadPluginMenu_);
		
		aam_.addTableModelListener(new TableModelListener(){

			@Override
			public void tableChanged(TableModelEvent e) {
				if (e.getSource() instanceof TableModel){
					connections_.setModel((TableModel)e.getSource());
				}

			}
			
		});

		setMinimumSize(new Dimension(400, 600));
		setMaximumSize(new Dimension(1000, 1000));
		setPreferredSize(new Dimension(400, 600));
		
		connections_.setMinimumSize(new Dimension(350, 500));
		connections_.setPreferredSize(new Dimension(350, 500));
		connections_.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		
		
		
		
		closeBtn_.setMinimumSize(new Dimension(75, 26));
		closeBtn_.setMaximumSize(new Dimension(75, 26));
		closeBtn_.setPreferredSize(new Dimension(75,26));
		closeBtn_.setActionCommand(CLOSE_);
		closeBtn_.addActionListener(this);
		
		loadPluginMenu_.setActionCommand(NEW_ACCOUNT_);
		loadPluginMenu_.addActionListener(this);
		
		setJMenuBar(menuBar_);
		
		
		try{
			UIManager.setLookAndFeel(LOOK_AND_FEEL_);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		SpringLayout sl = new SpringLayout();
		Container cp = getContentPane();
		
		
		cp.setLayout(sl);
		
		cp.add(pluginScroll_);
		cp.add(closeBtn_);
		
		//Constraints
		sl.putConstraint(SpringLayout.NORTH, pluginScroll_, 50, SpringLayout.NORTH, cp);
		sl.putConstraint(SpringLayout.WEST, pluginScroll_, 15, SpringLayout.WEST, cp);
		sl.putConstraint(SpringLayout.EAST, pluginScroll_, -15, SpringLayout.EAST, cp);
		sl.putConstraint(SpringLayout.SOUTH, pluginScroll_, -50, SpringLayout.SOUTH, cp);
		
		
		sl.putConstraint(SpringLayout.SOUTH, closeBtn_, -10, SpringLayout.SOUTH, cp);
		sl.putConstraint(SpringLayout.EAST, closeBtn_, -15, SpringLayout.EAST, cp);
		
		
		for(Entry<String, ConnectionStrategy> entry: cm_.getStrategies().entrySet()){
			aam_.add(entry.getValue());
		}
		connections_.setRowHeight(50);
		pack();
		setVisible(true);
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String ac = arg0.getActionCommand();
		if (ac.equalsIgnoreCase(CLOSE_)) {
			this.dispose();
		}
		if (ac.equalsIgnoreCase(NEW_ACCOUNT_)) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					new NewAccountWindow(cm_);
				}

			});
		}

	}

}
