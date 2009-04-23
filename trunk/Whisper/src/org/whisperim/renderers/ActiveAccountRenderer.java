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
package org.whisperim.renderers;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.table.TableCellRenderer;

import org.whisperim.client.ConnectionStrategy;

public class ActiveAccountRenderer implements TableCellRenderer {

	private static final String ACTIVE_ = "Active";
	private static final String INVALID_USERNAME_ = "Offline -- Invalid Username or Password";
	private static final String RATE_LIMITED_ = "Offline -- Rate Limited";
	private static final String DISABLED_ = "Disabled";
	private static final String SERVICE_UNAVAILABLE_ = "Offline -- Service Unavailable";
	private static final String SIGNON_ = "Sign In";
	private static final String SIGNOFF_ = "Sign Out";
	private static final String DELETE_ = "Delete Account";
	private static final String EDIT_ = "Edit Account";

	public ActiveAccountRenderer () {
		
	}


	@Override
	public Component getTableCellRendererComponent(JTable table, Object connection,
			boolean isSelected, boolean hasFocus, int row, int column) {

		final JPanel frame = new JPanel();

		frame.setMinimumSize(new Dimension(100, 100));

		final JPopupMenu rcMenu = new JPopupMenu();
		JMenuItem changeState = new JMenuItem();

		JMenuItem remove = new JMenuItem(DELETE_);
		remove.setActionCommand(DELETE_);

		JMenuItem edit = new JMenuItem(EDIT_);
		edit.setActionCommand(EDIT_);



		SpringLayout sl = new SpringLayout();

		frame.setLayout(sl);

		JLabel handleLbl = new JLabel(((ConnectionStrategy)connection).getHandle());
		handleLbl.setFont(handleLbl.getFont().deriveFont(Font.BOLD));
		//handleLbl.setIcon(new ImageIcon(((ConnectionStrategy)connection).getPluginIconLocation()));
		handleLbl.setIcon(((ConnectionStrategy)connection).getServiceIcon());
		
		JLabel statusLbl= new JLabel();
		statusLbl.setFont(statusLbl.getFont().deriveFont(Font.ITALIC).deriveFont(7));
		JButton changeStateBtn_;

		switch(((ConnectionStrategy)connection).getStatus()){

		case ConnectionStrategy.ACTIVE: {
			statusLbl.setText(ACTIVE_);
			changeState.setText(SIGNOFF_);
			changeState.setActionCommand(SIGNOFF_);
			break;
		}case ConnectionStrategy.INVALID_PASSWORD: {
			statusLbl.setText(INVALID_USERNAME_);
			changeState.setText(SIGNON_);
			changeState.setActionCommand(SIGNON_);
			break;
		}case ConnectionStrategy.RATE_LIMITED:{
			statusLbl.setText(RATE_LIMITED_);
			changeState.setText(SIGNON_);
			changeState.setActionCommand(SIGNON_);
			break;
		}case ConnectionStrategy.OFFLINE:{
			statusLbl.setText(DISABLED_);
			changeState.setText(SIGNON_);
			changeState.setActionCommand(SIGNON_);
			break;
		}case ConnectionStrategy.SERVICE_UNAVAILABLE:{
			statusLbl.setText(SERVICE_UNAVAILABLE_);
			changeState.setText(SIGNON_);
			changeState.setActionCommand(SIGNON_);
			break;
		}default:
			changeState.setText(SIGNON_);
			changeState.setActionCommand(SIGNON_);
			break;

		}
		//JButton deleteBtn = new JButton(DELETE_);


		//Add the components to the frame
		frame.add(handleLbl);
		frame.add(statusLbl);
		//frame.add(changeStateBtn_);
		//frame.add(deleteBtn);

		rcMenu.add(changeState);
		rcMenu.add(edit);
		rcMenu.add(remove);


		//Layout constraints
		sl.putConstraint(SpringLayout.NORTH, handleLbl, 5, SpringLayout.NORTH, frame);
		sl.putConstraint(SpringLayout.WEST, handleLbl, 5, SpringLayout.WEST, frame);

		sl.putConstraint(SpringLayout.NORTH, statusLbl, 5, SpringLayout.SOUTH, handleLbl);
		sl.putConstraint(SpringLayout.WEST, statusLbl, 5, SpringLayout.WEST, frame);

		//sl.putConstraint(SpringLayout.NORTH, deleteBtn, 5, SpringLayout.NORTH, frame);
		//sl.putConstraint(SpringLayout.EAST, deleteBtn, -5, SpringLayout.EAST, frame);

		//sl.putConstraint(SpringLayout.NORTH, changeStateBtn_, 5, SpringLayout.SOUTH, deleteBtn);
		//sl.putConstraint(SpringLayout.EAST, changeStateBtn_, -5, SpringLayout.EAST, frame);

		frame.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					rcMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			} 
		});


		frame.setVisible(true);

		return frame;
	}

}

