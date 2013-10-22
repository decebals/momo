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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import jsyntaxpane.DefaultSyntaxKit;
import ro.fortsoft.momo.popup.DeleteFolderPopupContributor;
import ro.fortsoft.momo.popup.RenameFolderPopupContributor;
import ro.fortsoft.momo.util.JcrUtils;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;

/**
 * @author Decebal Suiu
 */
public class JcrBrowser {

	private static JcrBrowserFrame browserFrame;

	public static void main(String[] args) {
		setLookAndFeel();
		
	    // turn anti-aliasing on
        System.setProperty("awt.useSystemAAFontSettings", "on");

        // for jsyntaxpane
		DefaultSyntaxKit.initKit();
		
		browserFrame = new JcrBrowserFrame();
		
		HierarchyTree tree = browserFrame.getHierarchyPanel().getHierarchyTree();
		addPopupContibutors(tree);
				
		browserFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		browserFrame.setSize(800, 600);
		browserFrame.setLocationRelativeTo(null);
		browserFrame.setVisible(true);
		
		browserFrame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent event) {
				try {
					browserFrame.getQueryPanel().getQueryHistory().save();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}

		});
		
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				onShutdown();
			}
			
		});
	}

	public static JcrBrowserFrame getBrowserFrame() {
		return browserFrame;
	}

	private static void setLookAndFeel() {
		PlasticLookAndFeel laf = new Plastic3DLookAndFeel();
		PlasticLookAndFeel.setCurrentTheme(new ExperienceBlue());
		try {
			UIManager.setLookAndFeel(laf);
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	private static void addPopupContibutors(HierarchyTree tree) {
		tree.addPopupContributor(new DeleteFolderPopupContributor());
		tree.addPopupContributor(new RenameFolderPopupContributor());
	}

	private static void onShutdown() {
		JcrUtils.getSession().logout();
	}

}
