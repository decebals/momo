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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jdesktop.swingx.JXTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.fortsoft.momo.popup.PopupContributor;
import ro.fortsoft.momo.popup.PopupMenuBuilder;
import ro.fortsoft.momo.util.ImageUtils;
import ro.fortsoft.momo.util.JcrUtils;

/**
 * @author Decebal Suiu
 */
public class HierarchyTree extends JXTree {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(HierarchyTree.class); 
		
	private List<PopupContributor> popupContributors;

    public HierarchyTree() {
    	super(new HierarchyTreeModel(new HierarchyNode(JcrUtils.getRootNode())));
    	
		popupContributors = new ArrayList<PopupContributor>();
		
		initComponents();
    }

    @Override
	public HierarchyTreeModel getModel() {
		return (HierarchyTreeModel) super.getModel();
	}

	public List<PopupContributor> getPopupContributors() {
		return popupContributors;
	}

	public void addPopupContributor(PopupContributor popupContributor) {
		popupContributors.add(popupContributor);
	}

    private void initComponents() {
        addTreeExpansionListener(new NodeExpansionListener());
        setShowsRootHandles(true);
        
        HierarchyNode root = getModel().getRoot();        
        expandNode(root, false);

		getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        setCellRenderer(new TreeRenderer());
        addMouseListener(new TreePopup());
    }

    void expandNode(HierarchyNode node, boolean selectNode) {
        if (node == null) {
            throw new IllegalArgumentException("HierarchyNode is null");
        }

        if (node.getChildCount() == 0) {
            TreeLoader loader = new TreeLoader(node, selectNode);
            loader.execute();
        }        
    }

    private class NodeExpansionListener implements TreeExpansionListener {

    	@Override
        public void treeExpanded(TreeExpansionEvent event) {
            final TreePath path = event.getPath();
            final Object parentObject = path.getLastPathComponent();
            if (parentObject instanceof HierarchyNode) {
            	expandNode((HierarchyNode) parentObject, false);
            }
        }

        @Override
        public void treeCollapsed(TreeExpansionEvent event) {
        }

    }

    private class TreeLoader {

        private HierarchyNode node;
        private boolean selectNode;

        TreeLoader(HierarchyNode node, boolean selectNode) {
            super();
            
            this.node = node;
            this.selectNode = selectNode;
        }

        void execute() {
            try {
                try {
                    loadChildren();
                } finally {
                    fireStructureChanged(node);
                    if (selectNode) {
                        clearSelection();
                        setSelectionPath(new TreePath(node.getPath()));
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            
            node.setExpanded(true);
        }

        /**
         * This expands the parent node and shows all its children.
         */
        private void loadChildren() throws Exception {
        	Item item = node.getUserObject();
        	if (item instanceof Node) {
        		long t = System.currentTimeMillis(); 
        		loadNodes((Node) item);
        		loadProperties((Node) item);
        		t = System.currentTimeMillis() - t;
        		
        		log.info("Load {}  items for '{}' in {} ms",  node.getChildCount(), item.getPath(), t); 
        	}
        }

        private void loadNodes(Node pNode) throws Exception {
        	NodeIterator nit = pNode.getNodes();
        	while (nit.hasNext()) {
        		Node item = nit.nextNode();
        		HierarchyNode childNode = new HierarchyNode(item);
        		childNode.setAllowsChildren(item.hasNodes() || item.hasProperties());
        		node.add(childNode);
        	}
        }

        private void loadProperties(Node pNode)  throws Exception {
        	PropertyIterator pit = pNode.getProperties();
        	while (pit.hasNext()) {
        		Property item = pit.nextProperty();
        		HierarchyNode childNode = new HierarchyNode(item);
        		childNode.setAllowsChildren(false);
        		node.add(childNode);
        	}
        }

        /**
         * Let the object tree model know that its structure has changed.
         */
        private void fireStructureChanged(final HierarchyNode node) {
            HierarchyTree.this.getModel().nodeStructureChanged(node);
        }

    }

    private class TreeRenderer extends DefaultTreeCellRenderer {

		private static final long serialVersionUID = 1L;

		@Override
        public Component getTreeCellRendererComponent(JTree tree,
        		Object value, boolean selected, boolean expanded,
        		boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, selected,
                    expanded, leaf, row, hasFocus);

            HierarchyNode node = (HierarchyNode) value;
            Item item = node.getUserObject();

            try {
                setText(item.getName());
                
	            if (item.getDepth() == 0) {
	            	setIcon(ImageUtils.getImageIcon("root.png"));
	            } else if (item instanceof Node) {
	            	setIcon(ImageUtils.getImageIcon("node.png"));
	            } else {
	            	setIcon(ImageUtils.getImageIcon("property.png"));
	            }
            } catch (Exception e) {
            	e.printStackTrace();
			}

            return this;
        }

    }

    private class TreePopup extends MouseAdapter {

    	@Override
        public void mousePressed(MouseEvent event) {
    		if (event.isPopupTrigger() && (event.getClickCount() == 1)) {
				doPopup(event.getX(), event.getY());
			}
        }

    	@Override
        public void mouseReleased(MouseEvent event) {
    		if (event.isPopupTrigger() && (event.getClickCount() == 1)) {
				doPopup(event.getX(), event.getY());
			}
        }

    	private void doPopup(int x, int y)  {
			//  Get the tree element under the mouse.
			TreePath clickedElement = getPathForLocation(x, y);
			if (clickedElement == null) {
				return;
			}
			//  Update the selection if necessary.
			updateSelection(clickedElement);

			//  Get selection paths
			TreePath[] selectionPaths = getSelectionPaths();
			if (selectionPaths == null) {
				return;
			}

			//  Get the desired context menu and show it
			List<HierarchyNode> selectedNodes = new ArrayList<HierarchyNode>();
			for (int i = 0; i < selectionPaths.length; i++) {
					selectedNodes.add((HierarchyNode) selectionPaths[i].getLastPathComponent());
			}
			JPopupMenu popupMenu = PopupMenuBuilder.buildPopupMenu(selectedNodes, popupContributors);
			if (popupMenu.getComponentCount() > 0) {
				popupMenu.show(HierarchyTree.this, x, y);
			}
    	}

        private void updateSelection(TreePath clickedElement)  {
			//  Find out if the clicked on element is already selected
			boolean clickedElementSelected = false;
			TreePath[] selection = getSelectionPaths();
			if (clickedElement != null && selection != null) {
				// Determine if it one of the selected paths
				for (int index = 0; index < selection.length; ++index) {
					if (clickedElement.equals(selection[index])) {
						clickedElementSelected = true;
						break;
					}
				}
			}

			// Select the clicked on element or clear all selections
			if (!clickedElementSelected) {
				if (clickedElement != null) {
					// Clicked on unselected item - make it the selection
					setSelectionPath(clickedElement);
				} else {
					// Clicked over nothing clear the selection
					clearSelection();
				}
			}
        }

	}

}
