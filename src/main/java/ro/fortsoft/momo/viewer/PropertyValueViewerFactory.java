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

import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.jcr.Property;
import javax.jcr.Value;
import javax.swing.JComponent;

/**
 * @author Decebal Suiu
 */
public class PropertyValueViewerFactory {

	public static JComponent getViewer(Property property) throws Exception {
		if ("jcr:data".equals(property.getName())) {
			String mimeType = property.getParent().getProperty("jcr:mimeType").getValue().getString();
					
			JComponent component = getViewer(mimeType, property.getValue());
			if (component != null) {
				return component;
			}
		}
		
		return getDynamicViewer(property);
	}
	
	protected static JComponent getViewer(String mimeType, Value data) throws Exception {
		if (isXmlMimeType(mimeType)) {
			return new XmlViewer(data.getString());
		} else if (isImageMimeType(mimeType)) {
			return new ImageViewer(ImageIO.read(data.getBinary().getStream()));
		}
 
		return null;
	}
	
	private static boolean isXmlMimeType(String mimeType) {
		if ("text/xml".equals(mimeType)) {
			return true;
		}
		
		return false;
	}

	private static boolean isImageMimeType(String mimeType) {
		if (mimeType.startsWith("image/")) {
			return true;
		}
		
		return false;
	}

	private static JComponent getDynamicViewer(Property property) throws Exception {
		System.out.println("PropertyValueViewerFactory.getDynamicViewer()");
		/*
		Interpreter interpreter = new Interpreter();
		return (JComponent) interpreter.source("dynamicDataHandler.bsh");
		*/
		InputStream inputStream = property.getValue().getBinary().getStream();
	    ObjectInput objectInput = new ObjectInputStream(inputStream);
	    Object value = objectInput.readObject();
		if (value instanceof Object[]) {
			System.out.println(Arrays.asList((Object[]) value));
		} else {
			System.out.println(value);
		}
	    
		return null;
	}
	
}
