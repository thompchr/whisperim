package org.whisperim.EmailManager;

/**************************************************************************
 * Copyright 2009 Nick Krieble                                             *
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

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

public class TableFilter extends JPanel {
	
	private static final long serialVersionUID = 8118246478981217986L;
	private JTable table_;
	private JTextField filterText_;
	private JTextField messageText_;
	private TableRowSorter<EmailTableModel> sorter_;
	EmailTableModel etm_;

	public TableFilter() {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// Create a table with the sorter.
		EmailTableModel model_ = new EmailTableModel();
		sorter_ = new TableRowSorter<EmailTableModel>(model_);
		table_ = new JTable(model_);

		table_.setRowSorter(sorter_);
		table_.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table_.setFillsViewportHeight(true);

		// Set for Single Selection.
		table_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// When selection changes, provide user with location to get
		// information.
		table_.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent event) {
						int viewRow = table_.getSelectedRow();
						if (viewRow < 0) {
							// Selection got filtered away.
							messageText_.setText("");
						} else {
							int modelRow = table_
									.convertRowIndexToModel(viewRow);
							messageText_.setText((String) (etm_.getValueAt(
									viewRow, 3)));
						}
					}
				});

		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table_);

		// Add the scroll pane to this panel.
		add(scrollPane);

		// Create a separate form for filterText and messageText
		JPanel form = new JPanel(new SpringLayout());
		JLabel l1 = new JLabel("Search:", SwingConstants.TRAILING);
		form.add(l1);
		filterText_ = new JTextField();

		// Whenever filterText changes, invoke newFilter.
		filterText_.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				newFilter();
			}

			public void insertUpdate(DocumentEvent e) {
				newFilter();
			}

			public void removeUpdate(DocumentEvent e) {
				newFilter();
			}
		});
		l1.setLabelFor(filterText_);
		form.add(filterText_);
		JLabel l2 = new JLabel("Message:", SwingConstants.TRAILING);
		form.add(l2);
		messageText_ = new JTextField();
		l2.setLabelFor(messageText_);
		form.add(messageText_);
		SpringUtilities.makeCompactGrid(form, 2, 2, 6, 6, 6, 6);
		add(form);
		
	}

	// Update filter.
	private void newFilter() {
		RowFilter<EmailTableModel, Object> rf = null;
		try {
			rf = RowFilter.regexFilter(filterText_.getText(), 0);
		} catch (java.util.regex.PatternSyntaxException e) {
			return;
		}
		sorter_.setRowFilter(rf);
	}
}
