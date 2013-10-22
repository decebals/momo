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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author Decebal Suiu
 */
public class QueryHistory {

	public static final int DEFAULT_LIMIT = 50;
	public static final String DEFAULT_FILE = "query-history.xml";
	
	private int limit = DEFAULT_LIMIT;
	private LinkedList<Item> items = new LinkedList<Item>();
	private int currentIndex;
	
	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public LinkedList<Item> getItems() {
		return items;
	}

	public boolean isEmpty() {
		return items.isEmpty();
	}
	
	public int size() {
		return items.size();
	}
	
	public void addItem(Item item) {
		 if (items.size() == limit) {
	         items.removeFirst();
		 }

		items.addLast(item);
		currentIndex = size() - 1;
	}
	
	public int getCurrentIndex() {
		return currentIndex;
	}

	public Item getCurrentItem() {
		if (items.isEmpty()) {
			return null;
		}
		
		return items.get(currentIndex);
	}
	
	public boolean moveNext() {
		if (currentIndex + 1 > items.size()) {
			return false;
		}
		
		currentIndex++;
		
		return true;
	}
	
	public boolean movePreviously() {	
		if (currentIndex == 0) {
			return false;
		}
		
		currentIndex--;
		
		return true;		
	}
	
	public static QueryHistory load() {
		try {
			return QueryHistory.load(new FileInputStream(DEFAULT_FILE));
		} catch (FileNotFoundException e) {
			return new QueryHistory();
		}
	}
	
	public static QueryHistory load(InputStream input) {
		QueryHistory queryHistory = (QueryHistory) createXStream().fromXML(input);
		if (queryHistory.size() > 1) {
			queryHistory.currentIndex = queryHistory.size() - 1;
		}
		
		return queryHistory;
	}
	
	public void save() throws IOException {
		File file = new File(DEFAULT_FILE);
		if (!file.exists()) {
				file.createNewFile();
		}
		save(new FileOutputStream(file));
	}
	
	public void save(OutputStream output) {
		createXStream().toXML(this, output);
	}
	
    @Override
	public String toString() {
		return "limit: " + limit + "; items: " + items;
	}

	private static XStream createXStream() {
        XStream xstream = new XStream(new DomDriver("UTF-8"));
        xstream.setMode(XStream.NO_REFERENCES);

        xstream.alias("query-history", QueryHistory.class);
        xstream.alias("item", Item.class);
        xstream.omitField(QueryHistory.class, "currentIndex");

        return xstream;
    }
	
	public static class Item {
		
		private String type;
		private String statement;
		
		public Item() {
		}
		
		public Item(String type, String statement) {
			this.type = type;
			this.statement = statement;
		}
		
		public String getType() {
			return type;
		}
				
		public String getStatement() {
			return statement;
		}

		@Override
		public String toString() {
			return "type: " + type + " > statement : " + statement;
		}
				
	}
	
	public static void main(String[] args) {
		/*
		QueryHistory queryHistory = new QueryHistory();
		queryHistory.setLimit(3);
		queryHistory.addItem(new QueryHistory.Item("xpath", "/jcr:root/nexus/reports//*[@className='com.asf.evrika.domain.Report']"));
		queryHistory.addItem(new QueryHistory.Item("xpath", "/jcr:root/nexus/reports//*[@className='com.asf.evrika.domain.RunReportJobHistory']"));
		queryHistory.addItem(new QueryHistory.Item("xpath", "/jcr:root/nexus/reports//*[runnerId='f2706869-8975-4a4a-b8ad-78d8968dcf60']"));
		queryHistory.addItem(new QueryHistory.Item("xpath", "/jcr:root/nexus/reports//*[runnerId='f2706869-8975-4a4a-b8ad-000000000000']"));
		try {
			queryHistory.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		QueryHistory queryHistory = QueryHistory.load();
		System.out.println(queryHistory);
	}
	
}
