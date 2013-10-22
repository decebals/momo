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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import org.jdesktop.swingx.JXPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.fortsoft.momo.util.ImageUtils;
import ro.fortsoft.momo.util.JcrUtils;
import ro.fortsoft.momo.util.SwingUtils;

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
	
	private static final String[] TYPES = { XPATH_TYPE, SQL_TYPE };
	
	private QueryHistory queryHistory;
	
	private JEditorPane queryEditor;
	private JTextArea resultTextArea;
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
		queryEditor.setContentType("text/xpath");		
		if (queryHistory.size() > 0) {
			queryEditor.setText(queryHistory.getCurrentItem().getStatement());
		}
		
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
		resultTextArea = new JTextArea();
		SwingUtils.addPopup(resultTextArea);
		add(new JScrollPane(resultTextArea), gbc);		
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
		onHistoryMove();
	}

	private void onNextHistory() {
		queryHistory.moveNext();
		onHistoryMove();
	}

	// TODO better name
	private void onHistoryMove() {
		refreshHistoryStatusButtons();
		QueryHistory.Item item = queryHistory.getCurrentItem();
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
		resultTextArea.setText("");
		while (nodeIterator.hasNext()) {
			Node node = nodeIterator.nextNode();
			resultTextArea.append(node.getPath() + "\n\r");
			resultCounter++;
		}
		log.info("Found {} nodes for '{}' in {} ms", resultCounter, queryEditor.getText(), t);
		JOptionPane.showMessageDialog(null, resultCounter + " items found", "Info", JOptionPane.INFORMATION_MESSAGE);
		
		queryHistory.addItem(new QueryHistory.Item(queryType, queryEditor.getText()));
		refreshHistoryStatusButtons();
	}
		
}
