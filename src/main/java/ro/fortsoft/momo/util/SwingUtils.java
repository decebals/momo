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
package ro.fortsoft.momo.util;

import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;

import ro.fortsoft.momo.PopupTriggerMouseListener;

/**
 * @author Decebal Suiu
 */
public class SwingUtils {

	/*
	public static void addPopup(JTextComponent textComponent) {
		JPopupMenu menu = new JPopupMenu();
        menu.add(getActionByName(textComponent, DefaultEditorKit.copyAction, "Copy"));
        menu.add(getActionByName(textComponent, DefaultEditorKit.cutAction, "Cut"));
        menu.add(getActionByName(textComponent, DefaultEditorKit.pasteAction, "Paste"));
        menu.add(new JSeparator());
        menu.add(getActionByName(textComponent, DefaultEditorKit.selectAllAction, "Select All"));
        
        textComponent.add(menu);
        textComponent.addMouseListener(new PopupTriggerMouseListener(menu, textComponent));
	}
	*/

	private static Action getActionByName(JTextComponent textComponent, String name, String description) {
        Action action = textComponent.getActionMap().get(name);
        action.putValue(Action.NAME, description);
        
        return action;
    }

}
