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
package ro.fortsoft.momo.popup;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.tree.TreeNode;

import ro.fortsoft.momo.HierarchyNode;
import ro.fortsoft.momo.HierarchyTree;
import ro.fortsoft.momo.HierarchyTreeModel;
import ro.fortsoft.momo.JcrBrowser;
import ro.fortsoft.momo.util.ImageUtils;
import ro.fortsoft.momo.util.JcrUtils;

/**
 * @author Decebal Suiu
 */
public class DeleteNodePopupContributor extends AbstractPopupContributor {

	@Override
	public String getPath() {
		return "/";
	}

	@Override
	public Action getPopupAction(List<HierarchyNode> selectedNodes) {
		if (areNodes(selectedNodes)) {
			return new DeleteNodeAction(selectedNodes);
		}

		return null;
	}
	
	private class DeleteNodeAction extends AbstractAction {

		private static final long serialVersionUID = 1L;
		
		private List<HierarchyNode> selectedNodes;

		public DeleteNodeAction(List<HierarchyNode> selectedNodes) {
			super("Delete");
			
			this.selectedNodes = selectedNodes;
			
			putValue(Action.SMALL_ICON, ImageUtils.getImageIcon("delete.gif"));			
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			int confirm = JOptionPane.showConfirmDialog(JcrBrowser.getBrowserFrame(), "Delete folder '" + selectedNodes.size() + "'?");
			if (confirm == 0) {
				try {
					List<Node> jcrNodes = new ArrayList<Node>();
					for (HierarchyNode node : selectedNodes) {
						jcrNodes.add((Node) node.getUserObject());
					}
					JcrUtils.remove(jcrNodes);
					
					// refresh the tree
					HierarchyTree tree = JcrBrowser.getBrowserFrame().getHierarchyPanel().getHierarchyTree();;
					HierarchyTreeModel treeModel = tree.getModel();
//					TreeNode parentNode = selectedNode.getParent();
//					selectedNode.removeFromParent();
//					treeModel.nodeStructureChanged(parentNode);
					treeModel.nodeStructureChanged((TreeNode) treeModel.getRoot());
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(JcrBrowser.getBrowserFrame(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}

	}

}
