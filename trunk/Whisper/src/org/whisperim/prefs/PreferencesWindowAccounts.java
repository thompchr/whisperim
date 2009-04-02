/**************************************************************************
 * Copyright 2009 Cory Plastek                                             *
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

/*
 * PreferencesWindowAccounts.java
 */

package org.whisperim.prefs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.whisperim.client.ConnectionManager;
import org.whisperim.client.ConnectionStrategy;
import org.whisperim.client.NewAccountWindow;
import org.whisperim.menus.AccountsRightClickMenu;
import org.whisperim.models.ActiveAccountModel;
import org.whisperim.renderers.ActiveAccountRenderer;

public class PreferencesWindowAccounts extends JPanel implements ActionListener {

	private static final long serialVersionUID = -8510819567207609301L;

	private ConnectionManager connectionManager_;
	private ActiveAccountModel aam_;

	private static final String ADD_ACCOUNT_ = "Add Account";
	private static final String REMOVE_ACCOUNT_ = "Remove Account";
	private static final String ACCOUNTS_ = "Accounts";
	private static final String EDIT_ = "Edit";
	private static final String SIGN_IN_ = "Sign in";
	private static final String SIGN_OUT_ = "Sign out";

	private JTable accounts_;
	private JScrollPane accountsScroll_;
	private JPanel accountsButtons_;
	private JButton addAccountButton_;
	private JButton removeAccountButton_;


	PreferencesWindowAccounts(ConnectionManager connectionManager) {
		connectionManager_ = connectionManager;


		setBackground(Color.white);
		//this.setLayout(new SpringLayout());

		aam_ = new ActiveAccountModel();
		for(Entry<String, ConnectionStrategy> entry: connectionManager_.getStrategies().entrySet()){
			aam_.add(entry.getValue());
		}
		aam_.addTableModelListener(new TableModelListener(){
			public void tableChanged(TableModelEvent e) {
				if (e.getSource() instanceof TableModel){
					accounts_.setModel((TableModel)e.getSource());
				}

			}

		});

		accounts_ = new JTable(aam_);
		accounts_.setBackground(Color.white);
		//accounts_.setBorder(null);
		accounts_.setDefaultRenderer(ConnectionStrategy.class, new ActiveAccountRenderer());
		//connections_.setCellRenderer(new ActiveAccountRenderer());
		accounts_.setMinimumSize(new Dimension(350, 400));
		accounts_.setPreferredSize(new Dimension(350, 400));
		accounts_.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		accounts_.setRowHeight(50);

		accounts_.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				if (e.getButton() == MouseEvent.BUTTON3){
				// TODO
					//if(e.isPopupTrigger()) {
					//right click
					final JMenuItem changeState = new JMenuItem();
					final JMenuItem edit = new JMenuItem();
					final JMenuItem remove = new JMenuItem();

					Point p = e.getPoint();

					final ConnectionStrategy cs = (ConnectionStrategy) aam_.getValueAt(accounts_.rowAtPoint(p),0);
					AccountsRightClickMenu menu = new AccountsRightClickMenu(cs);
					if (cs.getStatus() == ConnectionStrategy.ACTIVE){
						changeState.setText(SIGN_OUT_);
						changeState.setActionCommand(SIGN_OUT_);
					}else{
						changeState.setText(SIGN_IN_);
						changeState.setActionCommand(SIGN_IN_);
					}



					edit.setText(EDIT_);
					edit.setActionCommand(EDIT_);

					remove.setText(REMOVE_ACCOUNT_);
					remove.setActionCommand(REMOVE_ACCOUNT_);

					menu.add(changeState);
					menu.add(edit);
					menu.add(remove);

					changeState.addMouseListener(new MouseAdapter(){
						@Override
						public void mousePressed(MouseEvent e){
							if (e.getSource() == changeState){
								connectionManager_.signOff(cs.getHandle(), cs.getProtocol());
							}
						}
					});

					remove.addMouseListener(new MouseAdapter(){
						@Override
						public void mousePressed(MouseEvent e){

						}
					});

					edit.addMouseListener(new MouseAdapter(){
						@Override
						public void mousePressed(MouseEvent e){

						}
					});

					menu.show(accounts_, e.getX(), e.getY());

				}
			}

		});

		accountsScroll_ = new JScrollPane(accounts_);

		add(accountsScroll_);

		accountsButtons_ = new JPanel();
		accountsButtons_.setBackground(Color.white);
		add(accountsButtons_);

		addAccountButton_ = new JButton(ADD_ACCOUNT_);
		addAccountButton_.addActionListener(this);
		accountsButtons_.add(addAccountButton_);

		
		removeAccountButton_ = new JButton(REMOVE_ACCOUNT_);
		removeAccountButton_.setEnabled(false);
		removeAccountButton_.addActionListener(this);
		accountsButtons_.add(removeAccountButton_);

	}

	public void actionPerformed(ActionEvent arg0) {
		String ac = arg0.getActionCommand();

		//if user hits "Add Account" start the new account window
		if (ac.equalsIgnoreCase(ADD_ACCOUNT_)) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					new NewAccountWindow(connectionManager_);
				}

			});
		}
		else if (ac.equalsIgnoreCase(REMOVE_ACCOUNT_)) {
			//remove from accounts jtable
			//update accounts file

		}else if (ac.equalsIgnoreCase(SIGN_IN_)){
			if (arg0.getSource() instanceof Component){
				ConnectionStrategy cs = ((AccountsRightClickMenu)((JMenuItem)arg0.getSource()).getParent()).getClicked();
				connectionManager_.loadConnection(cs.getProtocol(), cs.getHandle(), "");
			}
		}
	}

}
