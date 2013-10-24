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

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXStatusBar;
import org.jdesktop.swingx.border.DropShadowBorder;

import ro.fortsoft.momo.util.ImageUtils;

import com.jgoodies.looks.Options;

/**
 * @author Decebal Suiu
 */
public class JcrBrowserFrame extends JXFrame {

	private static final long serialVersionUID = 1L;
	
	private JLabel statusLabel;
    private JProgressBar progressBar;
	private HierarchyPanel hierarchyPanel;
	private ConsolePanel consolePanel;
	private QueryPanel queryPanel;
	private JTabbedPane tabbedPane;

	public JcrBrowserFrame() {
		super("Jcr Browser");
		
		initComponents();
	}

	public HierarchyPanel getHierarchyPanel() {
		return hierarchyPanel;
	}

	public ConsolePanel getConsolePanel() {
		return consolePanel;
	}

	public QueryPanel getQueryPanel() {
		return queryPanel;
	}

	public String getStatus() {
        return statusLabel.getText();
    }
    
    public void setStatus(String status) {
        statusLabel.setText(status);
    }
    
    public void startProgress() {
        progressBar.setIndeterminate(true);
    }

    public void stopProgress() {
        progressBar.setIndeterminate(false);
    }
    
    public void selectTab(int index) {
    	tabbedPane.setSelectedIndex(index);
    }

	private void initComponents() {
		setLayout(new BorderLayout());
		
		hierarchyPanel = new HierarchyPanel();
		consolePanel = new ConsolePanel();

		tabbedPane = new JTabbedPane();
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, hierarchyPanel, consolePanel);
		tabbedPane.setBorder(new CompoundBorder(new EmptyBorder(3, 3, 3, 3), new DropShadowBorder()));
		tabbedPane.addTab("Hierarchy", splitPane);
		
		queryPanel = new QueryPanel();
		tabbedPane.addTab("Query", queryPanel);

		add(tabbedPane, BorderLayout.CENTER);
		
		// set image for application
		setIconImage(ImageUtils.getImage("eye.png"));

		// do some things on tree node selection
		hierarchyPanel.getHierarchyTree().addTreeSelectionListener(consolePanel);
		
    	getRootPaneExt().setStatusBar(createStatusBar());
    	
		Options.setPopupDropShadowEnabled(true); // add drop shadow to popup menu
	}

    private JXStatusBar createStatusBar() {
        JXStatusBar statusBar = new JXStatusBar();
        statusLabel = new JLabel("Ready");
        statusBar.add(statusLabel, JXStatusBar.Constraint.ResizeBehavior.FILL);
        progressBar = new JProgressBar();
        statusBar.add(progressBar);
        
        return statusBar;
    }

}
