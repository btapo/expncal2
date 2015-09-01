package org.zigmoi.expncal.commons;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlParser {

	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	XPathFactory xpathFactory = XPathFactory.newInstance();
	XPath xpath = xpathFactory.newXPath();
	org.w3c.dom.Document document;
	Map<String, Integer> accessMaintenance = new HashMap<>();
	boolean parseSuccessFul = true;

	public XmlParser(String xml) {
		try {
			InputSource source = new InputSource(new StringReader(xml));
			DocumentBuilder db = dbf.newDocumentBuilder();
			this.document = db.parse(source);
		} catch (ParserConfigurationException | SAXException | IOException ex) {
			this.parseSuccessFul = false;
			Logger.getLogger(XmlParser.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public boolean isParseSuccessful() {
		return parseSuccessFul;
	}

	public String getTagData(String tag) {
		try {
			int pointer = 1;
			if (this.accessMaintenance.containsKey(tag)) {
				pointer = this.accessMaintenance.get(tag);
			}
			String tagVal = xpath.evaluate(tag + "[" + pointer + "]", this.document);
			this.accessMaintenance.put(tag, pointer);
			return tagVal;
		} catch (XPathExpressionException ex) {
			parseSuccessFul = false;
			Logger.getLogger(XmlParser.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	public String getNext(String tag) {
		if (this.accessMaintenance.containsKey(tag)) {
			int pointer = this.accessMaintenance.get(tag);
			this.accessMaintenance.put(tag, ++pointer);
		}
		return this.getTagData(tag);
	}

	public String getPrevious(String tag) {
		if (this.accessMaintenance.containsKey(tag)) {
			int pointer = this.accessMaintenance.get(tag);
			this.accessMaintenance.put(tag, (pointer > 0) ? --pointer : pointer);
		}
		return this.getTagData(tag);
	}

	public int getTagPointer(String tag) {
		if (this.accessMaintenance.containsKey(tag)) {
			return this.accessMaintenance.get(tag);
		}
		return 0;
	}
}
