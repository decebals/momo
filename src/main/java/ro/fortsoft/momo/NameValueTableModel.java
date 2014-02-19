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

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.nodetype.PropertyDefinition;
import javax.swing.table.AbstractTableModel;

import ro.fortsoft.momo.util.JcrUtils;

/**
 * @author Decebal Suiu
 */
public class NameValueTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	
	private static final String[] COLUMN_NAMES = { "Name", "Value" };
	private static final Class<?>[] COLUMN_TYPES = new Class[] { String.class, Object.class };
	
	private List<NameValue> rows;

	private Item currentItem;
	
	public NameValueTableModel() {
		super();

		rows = new ArrayList<NameValue>();
	}

	public void add(String name, Object value) {
		rows.add(new NameValue(name, value));
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

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {		
		// TODO it's hardcoded
		if (columnIndex == 0) {
			return false;
		}
		NameValue row = rows.get(rowIndex);			
		if (!"Value".equals(row.getName())) {
			return false;
		}
				
		Property property = (Property) currentItem;
		
		boolean multiple;
		try {
			multiple = property.isMultiple();
		} catch (RepositoryException e) {
			// TODO any good idea?
			throw new RuntimeException(e);			
		}
		
		if (multiple) {
			return false;
		}
		
		boolean locked;
		try {
			locked = property.getDefinition().isProtected();
		} catch (RepositoryException e) {
			// TODO any good idea?
			throw new RuntimeException(e);			
		}
		
		if (locked) {
			return false;
		}
		
		int valueType;
		try {
			valueType = property.getValue().getType();
		} catch (RepositoryException e) {
			// TODO any good idea?
			throw new RuntimeException(e);			
		}
		
		// TODO add more types
		if (valueType != PropertyType.STRING) {
			return false;
		}
		
		return true;
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		Property property = (Property) currentItem;
		try {
			property.setValue((String) value);
			JcrUtils.getSession().save();
			
			// update cache
			rows.get(rowIndex).setValue(value);
		} catch (RepositoryException e) {
			// TODO any good idea?
			throw new RuntimeException(e);
		}
	}

	public void setCurrentItem(Item currentItem) {
		this.currentItem = currentItem;
		
		try {
			initRows();
		} catch (RepositoryException e) {
			// TODO any good idea?
			throw new RuntimeException(e);
		}
	}

	private void initRows() throws RepositoryException {
		rows.clear();
		
		add("Name", currentItem.getName());
		add("Path", currentItem.getPath());
		add("Depth", currentItem.getDepth());
		if (currentItem.isNode()) { // it's a node
			Node node = (Node) currentItem;
			add("Identifier", node.getIdentifier());
			add("Locked", node.isLocked());
			add("Nodes", node.getNodes().getSize());
			add("Properties", node.getProperties().getSize());
		} else { // it's a property
			Property property = (Property) currentItem;
			add("Type", PropertyType.nameFromValue(property.getType()));
			PropertyDefinition propertyDefinition = property.getDefinition();
			add("Mandatory", propertyDefinition.isMandatory());
			add("Protected", propertyDefinition.isProtected());
			add("Multiple", propertyDefinition.isMultiple());
			if (!propertyDefinition.isMultiple()) {
				add("Size", humanReadableByteCount(property.getLength()));
				Value value = property.getValue();
				if (value.getType() == PropertyType.BINARY) {
					add("Value", "...");
				} else {
					add("Value", value.getString());
				}
			} else {
				Value[] values = property.getValues();
				for (Value value : values) {
					if (value.getType() == PropertyType.BINARY) {
						add("Value", "...");
					} else {
						add("Value", value.getString());
					}
				}
			}
		}
	}
	
	private String humanReadableByteCount(long bytes) {
		int unit = 1024;
	    if (bytes < unit) {
	    	return bytes + " B";
	    }
	    
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = "KMGTPE".charAt(exp - 1) + "";
	    
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

}
