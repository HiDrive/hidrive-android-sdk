/**
* Copyright 2014 STRATO AG
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
* http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.strato.hidrive.api.utils.xml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class XmlParser  extends DefaultHandler{
	
	XMLReader xr;
	private XmlDocument xmlDocument;
	private String tagValue = "";
		
	public XmlDocument parse(InputStream inputStream) {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			xr.setContentHandler(this);	
			xr.parse(new InputSource(inputStream));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xmlDocument;
	}
	
	public XmlDocument parse(String inputString) {
		//return parse(new StringBufferInputStream(inputString));
		try {
			return parse(new ByteArrayInputStream(inputString.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		if (xmlDocument == null) {
			xmlDocument=new XmlDocument(localName);
		}else
		{
			xmlDocument.addChaildTag(localName);
		}
		tagValue = "";
		if (attributes != null)
			for (int i = 0; i < attributes.getLength(); i++)
				xmlDocument.addAttribute(attributes.getLocalName(i), attributes
						.getValue(i));
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName) {
		xmlDocument.backToParentTag();
	}

	@Override
	public void characters(char ch[], int start, int length) {
		tagValue=tagValue.concat(new String(ch, start, length));
		xmlDocument.addTagValue(tagValue.trim());
	}
}
