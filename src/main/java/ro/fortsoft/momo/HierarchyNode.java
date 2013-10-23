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

import javax.jcr.Item;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Decebal Suiu
 */
public class HierarchyNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;
	
    public HierarchyNode(Item item) {
    	super(item, true);
    }

    @Override
    public Item getUserObject() {
    	return (Item) super.getUserObject();
    }

    @Override
    public boolean isLeaf() {
        return !allowsChildren;
    }

    public void setName(String name) {
    	// TODO
//        dbObject.xxx(name);
        this.setUserObject(name);
    }

}
