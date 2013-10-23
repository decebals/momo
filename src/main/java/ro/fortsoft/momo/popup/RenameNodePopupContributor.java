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
import javax.jcr.Session;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import ro.fortsoft.momo.JcrBrowser;
import ro.fortsoft.momo.HierarchyNode;
import ro.fortsoft.momo.HierarchyTree;
import ro.fortsoft.momo.HierarchyTreeModel;
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
			return new RenameFolderAction(selectedNodes.get(0));
		}

		return null;
	}

	private class RenameFolderAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private HierarchyNode selectedNode;
		
		public RenameFolderAction(HierarchyNode selectedNode) {
			super("Rename");
			
			this.selectedNode = selectedNode;
			
			putValue(Action.SMALL_ICON, ImageUtils.getImageIcon("rename.gif"));
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			String newName = JOptionPane.showInputDialog(JcrBrowser.getBrowserFrame(), "Name", selectedNode);
			System.out.println("newName = " + newName);
			if (newName != null) {
				try {
					Session session = JcrUtils.getSession();
					Node jcrNode = (Node) selectedNode.getUserObject();
					session.move(jcrNode.getPath(), jcrNode.getParent().getPath() + "/" + newName);
					session.save();

					// refresh the tree
					HierarchyTree tree = JcrBrowser.getBrowserFrame().getHierarchyPanel().getHierarchyTree();;
					HierarchyTreeModel treeModel = tree.getModel();
					selectedNode.setName(newName);
					treeModel.nodeChanged(selectedNode);
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(JcrBrowser.getBrowserFrame(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}

	}

}
