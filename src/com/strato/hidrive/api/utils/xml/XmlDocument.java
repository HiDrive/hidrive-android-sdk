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


public class XmlDocument {
	private XmlTag rootTag;
	private XmlTag currentTag;

	public static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	public XmlDocument(String rootTagName) {
		this.rootTag = new XmlTag(rootTagName);
		this.currentTag = rootTag;
	}
	
	public XmlTag getRootTag()
	{
		return rootTag;	
	}
	
	public void addChaildTag(String chaildTagName) {
		addTo(currentTag, chaildTagName);
	}

	public void addChaildTag(String chaildTagName, String chaildTagValue) {
		addTo(currentTag, chaildTagName, chaildTagValue);
	}

	public void addSiblingTag(String siblingTagName) {
		addSiblingTag(siblingTagName, "");
	}

	public void addSiblingTag(String siblingTagName, String siblingTagValue) {
		addTo((currentTag.getParentTag() != null) ? currentTag.getParentTag()
				: currentTag, siblingTagName, siblingTagValue);
	}

	public void addToParent(String parentTagName, String childTagName)
			throws Exception {
		this.addToParent(parentTagName, childTagName, "");
	}

	public void addToParent(String parentTagName, String childTagName,
			String childTagValue) throws Exception {
		XmlTag parentTag = findParentTagByName(parentTagName, currentTag);
		if (parentTag == null)
			throw new Exception("Missing parent tag:" + parentTagName);
		addTo(parentTag, childTagName, childTagValue);
	}

	private XmlTag findParentTagByName(String parentTagName) {
		XmlTag parentTag = currentTag;
		while (parentTag != null) {
			if (parentTag.getName().equalsIgnoreCase(parentTagName))
				return parentTag;
			parentTag = parentTag.getParentTag();
		}
		return null;
	}

	private XmlTag findParentTagByName(String parentTagName, XmlTag currentTag) {

		if (currentTag != null)
			return (currentTag.getName().equalsIgnoreCase(parentTagName)) ? currentTag
					: findParentTagByName(parentTagName, currentTag
							.getParentTag());
		return null;
	}

	public XmlTag findTagByName(String tagName) {
		return findTagByName(tagName, this.rootTag);
	}

	private XmlTag findTagByName(String tagName, XmlTag rootTag) {
		if (rootTag != null)
			if (rootTag.getName().equalsIgnoreCase(tagName))
				return rootTag;
			else
				for (XmlTag tag : rootTag.getChildrenTags()) {
					XmlTag currentTag = findTagByName(tagName, tag);
					if (currentTag != null)
						return currentTag;
				}
		return null;
	}

	private void addTo(XmlTag parentTag, String tagName, String tagValue) {
		addTo(parentTag, new XmlTag(tagName, tagValue));
	}

	private void addTo(XmlTag parentTag, String tagName) {
		addTo(parentTag, tagName, "");
	}

	private void addTo(XmlTag parentTag, XmlTag tag) {
		currentTag = tag;
		parentTag.addChildTag(currentTag);
	}

	public void addAttribute(String name, String value) {
		currentTag.addAttribute(name, value);
	}

	public void addTagValue(String value) {
		currentTag.setValue(value);
	}

	public void backToParentTag() {
		if (this.currentTag.getParentTag() != null)
			this.currentTag = this.currentTag.getParentTag();
	}

	public String toXml() {
		return XML_DECLARATION + rootTag.toString();
	}
}
