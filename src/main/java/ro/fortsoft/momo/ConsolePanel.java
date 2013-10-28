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

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.nodetype.PropertyDefinition;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import ro.fortsoft.momo.viewer.PropertyValueViewerFactory;

/**
 * @author Decebal Suiu
 */
public class ConsolePanel extends JXPanel implements TreeSelectionListener {

	private static final long serialVersionUID = 1L;
	
	private NameValueTable table;
	private Item currentItem;

	public ConsolePanel() {
		super();
		
		initComponents();
	}

	@Override
	public void valueChanged(TreeSelectionEvent event) {
		JTree tree = (JTree) event.getSource();
		HierarchyNode treeNode = (HierarchyNode) tree.getLastSelectedPathComponent();
		if (treeNode == null) {
			return;
		}
		
		currentItem = treeNode.getUserObject();
				
		try {
			table.clear();
			
			table.add("Name", currentItem.getName());
			table.add("Path", currentItem.getPath());
			table.add("Depth", currentItem.getDepth());
			if (currentItem.isNode()) { // it's a node
				Node node = (Node) currentItem;
				table.add("Identifier", node.getIdentifier());
				table.add("Locked", node.isLocked());
				table.add("Nodes", node.getNodes().getSize());
				table.add("Properties", node.getProperties().getSize());
			} else { // it's a property
				Property property = (Property) currentItem;
				table.add("Type", PropertyType.nameFromValue(property.getType()));
				PropertyDefinition propertyDefinition = property.getDefinition();
				table.add("Mandatory", propertyDefinition.isMandatory());
				table.add("Protected", propertyDefinition.isProtected());
				table.add("Multiple", propertyDefinition.isMultiple());
				if (!propertyDefinition.isMultiple()) {
					table.add("Size", humanReadableByteCount(property.getLength()));
					Value value = property.getValue();
					if (value.getType() == PropertyType.BINARY) {
						table.add("Value", "...");
					} else {
						table.add("Value", value.getString());
					}
				} else {
					Value[] values = property.getValues();
					for (Value value : values) {
						if (value.getType() == PropertyType.BINARY) {
							table.add("Value", "...");
						} else {
							table.add("Value", value.getString());
						}
					}
				}
			}
			
			table.tableChanged(new TableModelEvent(table.getModel()));
			table.packColumn(1, -1);;
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}

	private void initComponents() {
		setLayout(new BorderLayout());
		
		table = new NameValueTable();
		Highlighter alternateHighlighter = HighlighterFactory.createAlternateStriping();
		table.setHighlighters(alternateHighlighter);
		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent event) {
				if (event.getClickCount() != 2) {
					return;
				}
				
				Point point = event.getPoint();
				int columnIndex = table.convertColumnIndexToModel(table.columnAtPoint(point));
				if (columnIndex != 1) {
					return;
				}
				
				int rowIndex = table.convertRowIndexToModel(table.rowAtPoint(point));
				NameValue row = table.get(rowIndex);
				if (!"Value".equals(row.getName())) {
					return;
				}
				
				Property property;
				try {
					property = (Property) currentItem;
				} catch (ClassCastException e) {
					return;
				}
								
				int propertyType = -1;
				try {
					propertyType = property.getType();
				} catch (RepositoryException e) {
					e.printStackTrace();
					return;					
				}
				
				if (propertyType == PropertyType.BINARY) {
					try {
						JComponent propertyValueViewer = PropertyValueViewerFactory.getViewer(property);
						if (propertyValueViewer != null) {
							viewValue(propertyValueViewer);
						}
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
				}
			}
			
		});
        JScrollPane scroll = new JScrollPane(table);
		add(scroll, BorderLayout.CENTER);
	}
	
	private void viewValue(final JComponent propertyValueViewer) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JDialog dialog = new JDialog(JcrBrowser.getBrowserFrame(), propertyValueViewer.getClass().getSimpleName());
				dialog.setModal(true);
				dialog.setLayout(new BorderLayout());
				dialog.add(propertyValueViewer);
				dialog.pack();
				dialog.setLocationRelativeTo(JcrBrowser.getBrowserFrame());
				dialog.setVisible(true);
			}
			
		});
		
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
