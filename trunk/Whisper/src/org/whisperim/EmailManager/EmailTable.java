package org.whisperim.EmailManager;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;



public class EmailTable extends JPanel{

	private static final long serialVersionUID = 8529323638775301702L;

		public EmailTable() {
	        super(new GridLayout(1,0));

	        JTable table = new JTable(new EmailTableModel());
	        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
	        table.setFillsViewportHeight(true);

	        //Create the scroll pane and add the table to it.
	        JScrollPane scrollPane = new JScrollPane(table);

	        //Add the scroll pane to this panel.
	        add(scrollPane);
	    }
}
