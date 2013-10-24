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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.renderer.DefaultListRenderer;
import org.jdesktop.swingx.renderer.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.fortsoft.momo.util.ImageUtils;
import ro.fortsoft.momo.util.JcrUtils;

import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.Options;

/**
 * @author Decebal Suiu
 */
public class QueryPanel extends JXPanel {
	
	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(QueryPanel.class);
	
	private static final String XPATH_TYPE = Query.XPATH;
	private static final String SQL_TYPE = Query.SQL;
	private static final String SQL2_TYPE = Query.JCR_SQL2;
	private static final String JQOM_TYPE = Query.JCR_JQOM;
	
	private static final String[] TYPES = { SQL2_TYPE, JQOM_TYPE, XPATH_TYPE, SQL_TYPE };
	
	private QueryHistory queryHistory;
	
	private JEditorPane queryEditor;
	private JXList resultList;
	private JComboBox typeComboBox;
	private JButton previouslyButton;
	private JButton nextButton;
	
    public QueryPanel() {
    	super();
    	
        initComponents();
    }

	public QueryHistory getQueryHistory() {
		return queryHistory;
	}

	private void initComponents() {
		setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		add(createHistoryPanel(), gbc);
		
		gbc.insets = new Insets(5, 5, 5, 5);		
		gbc.gridy = 1;
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0.0;
		add(new JLabel("Type"), gbc);

		gbc.gridx = 1;
		typeComboBox = new JComboBox(TYPES);
		typeComboBox.setRenderer(new QueryTypeComboBoxRenderer());
		
		add(typeComboBox, gbc);
		
		gbc.gridx = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		add(new JPanel(), gbc);
		
		gbc.insets = new Insets(0, 5, 5, 5);
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1.0;
		
		queryEditor = new JEditorPane();
//		addPopup(queryEditor);
		add(new JScrollPane(queryEditor), gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		JButton runButton = new JButton("Run", ImageUtils.getImageIcon("run.gif"));
		runButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					runQuery();
				} catch (RepositoryException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			
		});
		add(runButton, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		resultList = new ResultList();
		add(new JScrollPane(resultList), gbc);		
				
		if (queryHistory.size() > 0) {
			QueryHistory.Item item = queryHistory.getCurrentItem();
			typeComboBox.setSelectedItem(item.getType());
			queryEditor.setText(item.getStatement());
			
			if (XPATH_TYPE.equals(item.getType())) {
				queryEditor.setContentType("text/xpath");
			}
		}
	}

	private JComponent createHistoryPanel() {
		queryHistory = QueryHistory.load();
		
		JToolBar toolBar = new JToolBar();
    	toolBar.setFloatable(true);
    	toolBar.putClientProperty("JToolBar.isRollover", Boolean.TRUE); // hide buttons borders
    	toolBar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);

		previouslyButton = new JButton(ImageUtils.getImageIcon("previously.png"));
		previouslyButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				onPreviouslyHistory();
			}
			
		});
		toolBar.add(previouslyButton);
		nextButton = new JButton(ImageUtils.getImageIcon("next.png"));
		nextButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				onNextHistory();
			}
			
		});
		toolBar.add(nextButton);
		refreshHistoryStatusButtons();
		
		return toolBar;
	}
	
	private void onPreviouslyHistory() {
		queryHistory.movePreviously();
		onHistoryItemSelected();
	}

	private void onNextHistory() {
		queryHistory.moveNext();
		onHistoryItemSelected();
	}

	private void onHistoryItemSelected() {
		refreshHistoryStatusButtons();
		
		QueryHistory.Item item = queryHistory.getCurrentItem();
		typeComboBox.setSelectedItem(item.getType());
		queryEditor.setText(item.getStatement());
	}
	
	private void refreshHistoryStatusButtons() {
		previouslyButton.setEnabled(true);
		nextButton.setEnabled(true);
		
		if (queryHistory.isEmpty()) {
			previouslyButton.setEnabled(false);
			nextButton.setEnabled(false);
			return;
		}
		
		int currentIndex = queryHistory.getCurrentIndex();
		if (currentIndex == 0) {
			previouslyButton.setEnabled(false);
		}
		if (currentIndex == queryHistory.size() - 1) {
			nextButton.setEnabled(false);
		}
	}
	
	private void runQuery() throws RepositoryException {
		Session session = JcrUtils.getSession();
		Workspace workspace = session.getWorkspace();
		QueryManager queryManager = workspace.getQueryManager();
		String queryType = (String) typeComboBox.getSelectedItem();
		Query query = queryManager.createQuery(queryEditor.getText(), queryType);
		QueryResult queryResult = query.execute();
		long t = System.currentTimeMillis();
		NodeIterator nodeIterator = queryResult.getNodes();
		t = System.currentTimeMillis() - t;
		int resultCounter = 0;
		
		DefaultListModel resultListModel = new DefaultListModel();
		while (nodeIterator.hasNext()) {
			Node node = nodeIterator.nextNode();
			resultListModel.addElement(node);
			resultCounter++;
		}
		resultList.setModel(resultListModel);
		log.info("Found {} nodes for '{}' in {} ms", resultCounter, queryEditor.getText(), t);
		JOptionPane.showMessageDialog(null, resultCounter + " items found", "Info", JOptionPane.INFORMATION_MESSAGE);
		
		queryHistory.addItem(new QueryHistory.Item(queryType, queryEditor.getText()));
		refreshHistoryStatusButtons();
	}
		
	private class ResultList extends JXList {

		private static final long serialVersionUID = 1L;
		
		public ResultList() {
			super();
			
			StringValue stringValue = new StringValue() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public String getString(Object value) {
					try {
						return ((Node) value).getPath();
					} catch (RepositoryException e) {
						e.printStackTrace();
					}
					
					return null;
				}
				
			};
			setCellRenderer(new DefaultListRenderer(stringValue));
			
			addMouseListener(new MouseAdapter() {
				
				@Override
				public void mouseClicked(MouseEvent event) {
					Object[] selectedValues = getSelectedValues();
					if (selectedValues.length > 1) {
						return;
					}

					if (event.getClickCount() == 2) {
						goToNode((Node) selectedValues[0]);
						event.consume();;
					}
				}
				
			});

			addKeyListener(new KeyAdapter() {
				
				@Override
				public void keyReleased(KeyEvent event) {
					Object[] selectedValues = getSelectedValues();
					if (selectedValues.length > 1) {
						return;
					}
					
					if (event.getKeyCode() == KeyEvent.VK_ENTER) {
						goToNode((Node) selectedValues[0]);
						event.consume();
					}
				}
				
			});
		}
		
		private void goToNode(Node node) {
			HierarchyNode searchedNode;
			try {
				searchedNode = searchNode(node);
			} catch (RepositoryException e) {
				e.printStackTrace();
				return;
			}
			
			if (searchedNode != null) {
				// select Hierarchy tab
				JcrBrowserFrame frame = JcrBrowser.getBrowserFrame();				
				frame.selectTab(0);
				
				// make the node visible by scroll to it
				HierarchyTree tree = frame.getHierarchyPanel().getHierarchyTree();;
				HierarchyTreeModel treeModel = tree.getModel();
				TreeNode[] nodes = treeModel.getPathToRoot(searchedNode);
				TreePath path = new TreePath(nodes);
				tree.setExpandsSelectedPaths(true);
				tree.setSelectionPath(path);
				tree.scrollPathToVisible(path);
			} else {
				System.out.println("Node not found");
			}
		}
		
		@SuppressWarnings("unchecked")
		private HierarchyNode searchNode(Node node) throws RepositoryException {
			JcrBrowserFrame frame = JcrBrowser.getBrowserFrame();
			HierarchyTree tree = frame.getHierarchyPanel().getHierarchyTree();;
			HierarchyTreeModel treeModel = tree.getModel();
			HierarchyNode root = treeModel.getRoot();

			HierarchyNode searchedNode = null;
			Enumeration<HierarchyNode> en = root.breadthFirstEnumeration();  
			while (en.hasMoreElements()) {   
				searchedNode = en.nextElement(); 
				Item item = searchedNode.getUserObject();
				if (item instanceof Node) {
					if (node.getIdentifier().equals(((Node) item).getIdentifier())) {
						return searchedNode;
					}
				}  
			}  
			
			return null;  
		}  

	}
	
	private class QueryTypeComboBoxRenderer extends JLabel implements ListCellRenderer {
		
		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList list,
				Object comboItemObject, int comboItemIndex, boolean isSelected,
				boolean cellHasFocus) {
			String text = (String) comboItemObject;
			text = text.toUpperCase();
			if (text.equalsIgnoreCase(XPATH_TYPE) || text.equalsIgnoreCase(SQL2_TYPE)) {
				text = "<html><strike>" + text + "</strike></html>";
			}
			setText(text);

			return this;
		}
		
	}
	
}
