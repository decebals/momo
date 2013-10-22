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

import javax.swing.ListSelectionModel;

import org.jdesktop.swingx.JXTable;

/**
 * @author Decebal Suiu
 */
public class NameValueTable extends JXTable {

	private static final long serialVersionUID = 1L;
	
	public NameValueTable() {
		super(new NameValueTableModel());
		
		getColumnModel().getColumn(0).setPreferredWidth(100);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);		
	}
	
	public void clear() {
		getModel().clear();
	}

	public void add(NameValue row) {
		getModel().add(row);
	}

	public void add(String name, Object value) {
		add(new NameValue(name, value));
	}
	
	public NameValue get(int rowIndex) {
		return getModel().getRow(rowIndex);
	}

	@Override
	public NameValueTableModel getModel() {
		return (NameValueTableModel) super.getModel();
	}
	
}
