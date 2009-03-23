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
package org.whisperim.models;

import java.util.ArrayList;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.whisperim.client.ConnectionStrategy;

public class ActiveAccountModel implements TableModel {

	private ArrayList<ConnectionStrategy> connections_ = new ArrayList<ConnectionStrategy>();
	private ArrayList<TableModelListener> tml_ = new ArrayList<TableModelListener>();
	
	public void add(ConnectionStrategy cs){
		connections_.add(cs);
		notifyListeners();
	}
	
	public void remove(ConnectionStrategy cs){
		connections_.remove(cs);
		notifyListeners();
	}
	
	public void remove(int i){
		connections_.remove(i);
		notifyListeners();
	}
	
	private void notifyListeners(){
		for (TableModelListener l: tml_){
			l.tableChanged(new TableModelEvent(this));
		}
	}

	@Override
	public void addTableModelListener(TableModelListener arg0) {
		tml_.add(arg0);		
	}

	@Override
	public Class<?> getColumnClass(int arg0) {
		return ConnectionStrategy.class;
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public String getColumnName(int arg0) {
		return "Accounts";
	}

	@Override
	public int getRowCount() {
		return connections_.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		return connections_.get(row);
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	@Override
	public void removeTableModelListener(TableModelListener arg0) {
		tml_.remove(arg0);
	}

	@Override
	public void setValueAt(Object connection, int row, int column) {
		connections_.set(row, (ConnectionStrategy) connection);
	}

}
