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
package org.whisperim.android.buddylist;

import java.util.ArrayList;

import org.whisperim.client.Buddy;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

public class BuddyListModel implements ListAdapter {
	
	private ArrayList<Buddy> buddies_ = new ArrayList<Buddy>();
	private ArrayList<DataSetObserver> observers_ = new ArrayList<DataSetObserver>();
	
	public void add(Buddy b){
		buddies_.add(b);
		notifyListeners();
	}

	@Override
	public boolean areAllItemsEnabled() {
		
		return true;
	}

	@Override
	public boolean isEnabled(int arg0) {
		
		return true;
	}

	@Override
	public int getCount() {
		return buddies_.size();
	}

	@Override
	public Object getItem(int arg0) {
		return buddies_.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		
		return 1;
	}

	@Override
	public int getItemViewType(int position) {
		
		return 1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView row = new TextView(parent.getContext());
		row.setText(buddies_.get(position).getHandle());
		
		return row;
	}

	@Override
	public int getViewTypeCount() {

		return 1;
	}

	@Override
	public boolean hasStableIds() {

		return false;
	}

	@Override
	public boolean isEmpty() {

		return buddies_.isEmpty();
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		observers_.add(observer);

	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		observers_.remove(observer);

	}
	
	private void notifyListeners(){
		for (DataSetObserver dso: observers_){
			dso.onChanged();
		}
	}

}
