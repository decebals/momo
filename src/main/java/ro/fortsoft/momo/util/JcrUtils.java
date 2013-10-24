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

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.core.TransientRepository;

/**
 * @author Decebal Suiu
 */
public class JcrUtils {

	private static String repositoryConfigFile;
	private static String repositoryHome;
	private static String username;
	private static char[] password;

	private static Session session;

	static {
		init();
	}
	
	public static Node getRootNode() {
		try {
			return getSession().getRootNode();
		} catch (RepositoryException e) {
			onFatalError(e);
			return null;
		}
	}

	public static Session getSession() {
		if (session == null) {
			try {
				Repository repository = new TransientRepository(repositoryConfigFile, repositoryHome);
				session = repository.login(new SimpleCredentials(username, password));
			} catch (Exception e) {
				onFatalError(e);
			}
		}

		return session;
	}

	public static boolean rename(Node node, String newName) {
        Session session = getSession();
        if (session == null) {
        	return false;
        }
        
    	try {
    		session.move(node.getPath(), node.getParent().getPath() + "/" + newName);
    		session.save();
		} catch (RepositoryException e) {
			onFatalError(e);
			return false;
		}
    	
    	return true;
    }	

	public static boolean remove(List<Node> nodes) {
        Session session = getSession();
        if (session == null) {
        	return false;
        }
        
    	try {
    		for (Node node : nodes) {
    			node.remove();
    		}
    		session.save();
		} catch (RepositoryException e) {
			onFatalError(e);
			return false;
		}
    	
    	return true;
    }	

	private static void init() {
		Properties properties = new Properties();
		try {
			String propertiesFile = System.getProperty("momo.properties", "momo.properties");
			System.out.println("propertiesFile = " + propertiesFile);
			properties.load(new FileInputStream(propertiesFile));
			repositoryConfigFile = properties.getProperty("repository.configFile");
			System.out.println("repositoryConfigFile = " + repositoryConfigFile);
			repositoryHome = properties.getProperty("repository.home");
			System.out.println("repositoryHome = " + repositoryHome);
			username = properties.getProperty("repository.username");
			System.out.println("username = " + username);
			password = properties.getProperty("repository.username").toCharArray();
		} catch (Exception e) {
			onFatalError(e);
		}
	}

	private static void onFatalError(Exception e) {
		e.printStackTrace();
		System.exit(1);
	}

}
