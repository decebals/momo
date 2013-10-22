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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

/**
 * @author Decebal Suiu
 */
public class PopupTriggerMouseListener extends MouseAdapter {
	
	private JPopupMenu popup;
	private JComponent component;

	public PopupTriggerMouseListener(JPopupMenu popup, JComponent component) {
		this.popup = popup;
		this.component = component;
	}

	@Override
	public void mousePressed(MouseEvent event) {
		showMenuIfPopupTrigger(event);
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		showMenuIfPopupTrigger(event);
	}

	// according to the javadocs on isPopupTrigger, checking for popup trigger
	// on mousePressed and mouseReleased
	// should be all that is required
	// public void mouseClicked(MouseEvent event)
	// {
	// showMenuIfPopupTrigger(event);
	// }

	// some systems trigger popup on mouse press, others on mouse release, we
	// want to cater for both
	private void showMenuIfPopupTrigger(MouseEvent event) {
		if (event.isPopupTrigger()) {
			popup.show(component, event.getX() + 3, event.getY() + 3);
		}
	}

}
