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
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import ro.fortsoft.momo.HierarchyNode;
import ro.fortsoft.momo.HierarchyTree;
import ro.fortsoft.momo.HierarchyTreeModel;
import ro.fortsoft.momo.JcrBrowser;
import ro.fortsoft.momo.util.ImageUtils;
import ro.fortsoft.momo.util.JcrUtils;

/**
 * @author Decebal Suiu
 */
public class RenameNodePopupContributor extends AbstractPopupContributor {

	@Override
	public String getPath() {
		return "/";
	}

	@Override
	public Action getPopupAction(List<HierarchyNode> selectedNodes) {
		if ((selectedNodes.size() == 1) && areNodes(selectedNodes))  {
			return new RenameNodeAction(selectedNodes.get(0));
		}

		return null;
	}

	private class RenameNodeAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private HierarchyNode selectedNode;
		
		public RenameNodeAction(HierarchyNode selectedNode) {
			super("Rename");
			
			this.selectedNode = selectedNode;
			
			putValue(Action.SMALL_ICON, ImageUtils.getImageIcon("rename.gif"));
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			Node jcrNode = (Node) selectedNode.getUserObject();
			String oldName;
			try {
				oldName = jcrNode.getName();
			} catch (RepositoryException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(JcrBrowser.getBrowserFrame(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			String newName = JOptionPane.showInputDialog(JcrBrowser.getBrowserFrame(), "Name", oldName);
			System.out.println("newName = " + newName);
			if (newName != null) {
				try {
					JcrUtils.rename(jcrNode, newName);

					// refresh the tree
					HierarchyTree tree = JcrBrowser.getBrowserFrame().getHierarchyPanel().getHierarchyTree();;
					HierarchyTreeModel treeModel = tree.getModel();
					treeModel.nodeChanged(selectedNode);
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(JcrBrowser.getBrowserFrame(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		
	}

}
