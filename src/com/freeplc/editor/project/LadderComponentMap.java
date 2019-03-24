package com.freeplc.editor.project;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.freeplc.editor.project.component.LadderComponent;

public class LadderComponentMap {

	static LadderComponentMap ladderComponentMap = null;
	
	static LadderComponentMap getLadderComponentMap() {
		if (ladderComponentMap == null) {
			ladderComponentMap = new LadderComponentMap();
		}
		
		return ladderComponentMap;
	}

	public void initComponentMap() {
		String classPath = System.getProperty("java.class.path");
		String[] pathElements = classPath.split(System.getProperty("path.separator"));
		
		for (String element : pathElements) {
			File newFile = new File(element);
			if (newFile.isFile() && newFile.getAbsolutePath().endsWith(".jar")) {
				try {
					JarFile f = new JarFile(newFile);
					Enumeration<JarEntry> entry = f.entries();
					while (entry.hasMoreElements()) {
						String name = entry.nextElement().getName();
						if (name.endsWith("laddercomponents.xml")) {
							System.out.println(">>>>>  " + name);
							
							try {
								URL url = this.getClass().getResource(name);
								
								File xmlFile = new File(url.getFile());
								DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
								DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
								Document doc = dBuilder.parse(xmlFile);
								
								doc.getDocumentElement().normalize();
								
								System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
								NodeList nList = doc.getElementsByTagName("component");
								for (int i=0; i<nList.getLength(); i++) {
									Node nNode = nList.item(i);
									Element elem = (Element)nNode;
									System.out.println("ID: " + elem.getAttribute("id") + " className: " + elem.getAttribute("className"));
								}
							}
							catch (ParserConfigurationException | SAXException e) {}
						}
					}
				}catch (IOException e) {}
			}
		}

	}
		
	private Map<String, LadderComponent> map = new HashMap<String, LadderComponent>();
	
	public LadderComponentMap() {
		initComponentMap();
	}
	
	public LadderComponent getComponent(String id) {
		LadderComponent component = map.get(id);
		LadderComponent newComponent = null;
		
		if (component != null) {
			newComponent = component.clone();
		}
		return  newComponent;
	}
}
