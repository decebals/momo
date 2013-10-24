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

import java.util.Enumeration;

import javax.jcr.Item;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Decebal Suiu
 */
public class HierarchyNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;
	
	private boolean expanded;
	
    public HierarchyNode(Item item) {
    	super(item, true);
    }

    @Override
    public Item getUserObject() {
    	return (Item) super.getUserObject();
    }

    /*
     * Also, this method is called by goToNode() method from QueryPanel file
     * and for this reason is necesary to check children loading.
     */
	@SuppressWarnings("unchecked")
	@Override
	public Enumeration<HierarchyNode> children() {
		if (!expanded) {
			expand();
		}
		
		return super.children();
	}

	@Override
    public boolean isLeaf() {
        return !allowsChildren;
    }

	public boolean isExpanded() {
		return expanded;
	}

	/*
	 * Called by HierarchyTree.TreeLoader
	 */
	void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	private void expand() {
		JcrBrowser.getBrowserFrame().getHierarchyPanel().getHierarchyTree().expandNode(this, false);
	}
	
}
