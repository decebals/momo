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
package ro.fortsoft.momo.viewer;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.ImagePainter;

/**
 * @author Decebal Suiu
 */
public class ImageViewer extends JXPanel {

	private static final long serialVersionUID = 1L;

	public ImageViewer(BufferedImage image) {
		super();

		ImagePainter painter = new ImagePainter();
		painter.setImage(image);
		setBackgroundPainter(painter);
		setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
	}

}
