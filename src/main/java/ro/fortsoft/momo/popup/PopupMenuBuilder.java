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

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import ro.fortsoft.momo.HierarchyNode;

/**
 * @author Decebal Suiu
 */
public class PopupMenuBuilder {

	public static JPopupMenu buildPopupMenu(List<HierarchyNode> selectedNodes, List<PopupContributor> contributors) {
		JPopupMenu popupMenu = new JPopupMenu();
		List<JMenu> menus = new ArrayList<JMenu>();
		for (PopupContributor contributor : contributors) {
			Action action = contributor.getPopupAction(selectedNodes);
			if (action == null) {
				continue;
			}

			String menuPath = contributor.getPath();
			if (menuPath == null || PopupContributor.MENU_SEPARATOR.equals(menuPath)) {
	            JMenuItem menuItem = new JMenuItem(action);
	            popupMenu.add(menuItem);
	            continue;
			}

			String[] menuNames = menuPath.split(PopupContributor.MENU_SEPARATOR);
            JMenu menuParent = null;
            String parentMenuName = null;
            int size = menuNames.length;
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    parentMenuName = menuNames[i - 1];
                }
                if (!findMenu(menus, menuNames[i], parentMenuName)) {
                    JMenu menu = new JMenu(menuNames[i]);
                    if (menuParent == null) {
                    	popupMenu.add(menu);
                    } else {
                        menuParent.add(menu);
                    }
                    menus.add(menu);
                    menuParent = menu;
                }
            }

            JMenuItem menuItem = new JMenuItem(action);
            JMenu menu = getMenu(menus, menuNames[size-1], parentMenuName);
            menu.add(menuItem);
            popupMenu.add(menu);
		}

		return popupMenu;
	}

    /**
     * Two menus with the same name may exists if they have different menu parents
     * but a same sequence (parent menu, child menu) in different menus is not taken in
     * consideration because it is unlikely to appear.
     */
    private static boolean findMenu(List<JMenu> menus, String menuName, String menuParentName) {
        for (JMenu menu : menus) {
            if (menu.getText().equals(menuName)) {
                Component parent = menu.getParent();
                if (parent instanceof JMenu) {
                     return ((JMenu)parent).getText().equals(menuParentName);
                } else {
                    return true;
                }
            }
        }

        return false;
    }

    private static JMenu getMenu(List<JMenu> menus, String menuName, String menuParentName) {
        for (JMenu menu : menus) {
             if (menu.getText().equals(menuName)) {
                 Component parent = menu.getParent();
                 if (parent instanceof JMenu) {
                      if (((JMenu) parent).getText().equals(menuParentName)) {
                          return menu;
                      }
                 } else {
                     return menu;
                 }
             }
         }

         return null;
     }

}
