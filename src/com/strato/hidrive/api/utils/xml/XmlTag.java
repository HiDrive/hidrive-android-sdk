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

import java.util.ArrayList;
import java.util.List;

public class XmlTag {
	private String name="";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private String value="";

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private XmlTag parentTag;

	public XmlTag getParentTag() {
		return parentTag;
	}

	public void setParentTag(XmlTag parentTag) {
		this.parentTag = parentTag;
	}

	private List<XmlAttribute> attributes;
	
	public List<XmlAttribute> getAttributes() {
		return attributes;
	}

	private List<XmlTag> childrenTags;

	public List<XmlTag> getChildrenTags() {
		return childrenTags;
	}

	public XmlTag() {
		attributes = new ArrayList<XmlAttribute>();
		childrenTags = new ArrayList<XmlTag>();
	}

	public XmlTag(String name) {
		this.setName(name);
		this.attributes = new ArrayList<XmlAttribute>();
		this.childrenTags = new ArrayList<XmlTag>();
	}

	public XmlTag(String name, String value) {
		this.setName(name);
		this.setValue(value);
		this.attributes = new ArrayList<XmlAttribute>();
		this.childrenTags = new ArrayList<XmlTag>();
	}

	public void addChildTag(XmlTag childTag) {
		childTag.setParentTag(this);
		this.childrenTags.add(childTag);
	}

	public void addChildTag(String name, String value) {
		this.addChildTag(new XmlTag(name, value));
	}

	public void addChaildTag(String name) {
		this.addChildTag(name, "");
	}

	public void addAttribute(XmlAttribute attribute) {
		this.attributes.add(attribute);
	}

	public void addAttribute(String name, String value) {
		addAttribute(new XmlAttribute(name, value));
	}

	@Override
	public String toString() {
		return String.format("<%s%s>%s%s</%s>", this.getName(), this
				.collectionToString(this.attributes), this.getValue(),
				collectionToString(this.childrenTags), this.getName());
	}

	private <T> String collectionToString(List<T> collection) {
		String result = "";
		for (T var : collection) {
			result += var.toString();
		}
		return result;
	}

	public XmlTag findChildTag(String tagName) {
		for (XmlTag tag : this.childrenTags)
			if (tag.getName().equalsIgnoreCase(tagName))
				return tag;
		return new XmlTag();
	}

	public List<XmlTag> findChildrenTags(String tagName) {
		List<XmlTag> tags = new ArrayList<XmlTag>();
		for (XmlTag tag : this.childrenTags)
			if (tag.getName().equalsIgnoreCase(tagName))
				tags.add(tag);
		return tags;
	}
	
	public boolean isChildTagExists(String tagName) {
		for (XmlTag tag : this.childrenTags)
			if (tag.getName().equalsIgnoreCase(tagName))
				return true;
		return false;
	}
}
