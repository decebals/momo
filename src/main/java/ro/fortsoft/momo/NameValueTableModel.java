/*
 * Copyright 2013 Decebal Suiu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with
 * the License. You may obtain a copy of the License in the LICENSE file, or at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ro.fortsoft.momo;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * @author Decebal Suiu
 */
public class NameValueTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	
	private static final String[] COLUMN_NAMES = { "Name", "Value" };
	private static final Class<?>[] COLUMN_TYPES = new Class[] { String.class, Object.class };

	private List<NameValue> rows;
	
	public NameValueTableModel() {
		super();

		rows = new ArrayList<NameValue>();
	}

	public void add(NameValue row) {
		rows.add(row);
	}
	
	public NameValue getRow(int rowIndex) {
		return rows.get(rowIndex);
	}
	
	public void clear() {
		rows.clear();
	}
	
	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public String getColumnName(int columnIndex) {
		return COLUMN_NAMES[columnIndex];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return COLUMN_TYPES[columnIndex];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if ((rowIndex >= getRowCount()) || (rowIndex >= getRowCount())) {
			return null;
		}

		NameValue row = rows.get(rowIndex);
		if (columnIndex == 0) {
			return row.getName();
		}
		
		return row.getValue();
	}

}
