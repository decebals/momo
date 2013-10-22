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
import java.awt.Dimension;

import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXPanel;

/**
 * @author Decebal Suiu
 */
public class HierarchyPanel extends JXPanel {

	private static final long serialVersionUID = 1L;
	
	private HierarchyTree hierarchyTree;

    public HierarchyPanel() {
    	super();
        initComponents();
    }

	public HierarchyTree getHierarchyTree() {
		return hierarchyTree;
	}

	private void initComponents() {
		setLayout(new BorderLayout());
        hierarchyTree = new HierarchyTree();
        JScrollPane scroll = new JScrollPane(hierarchyTree);
        scroll.setPreferredSize(new Dimension(250, 200));
        add(scroll, BorderLayout.CENTER);
	}

}
