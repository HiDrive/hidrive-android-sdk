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
package com.strato.hidrive.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.strato.hidrive.api.interfaces.DataReader;
import com.strato.hidrive.api.utils.xml.XmlTag;


public class XmlDataReader implements DataReader {

	public XmlTag xmlTag;
	
	public XmlDataReader(XmlTag xmlTag)
	{
		this.xmlTag=xmlTag;
	}
	
	public int readIntWithName(String name) {
		try{	
			return Integer.valueOf(readStringWithName(name)).intValue();
		}catch (Exception e) {
			return 0;
		}
	}
	
	public double readDoubleWithName(String name) {
		try{
			return Double.valueOf(readStringWithName(name)).doubleValue();
		}catch (Exception e) {
			return 0;
		}
	}
	
	public boolean readBooleanWithName(String name) {
		return readStringWithName(name).equalsIgnoreCase("true");
	}

	public String readStringWithName(String name) {
		return xmlTag.findChildTag(name).getValue();
	}

	public Date readDateWithName(String name) {
		return new Date(readStringWithName(name));
	}

	public DataReader readDataReaderWithName(String name) {
		return new XmlDataReader(xmlTag.findChildTag(name));
	}

	public List<DataReader> readDataReaderListWithName(String name) {
		List<DataReader> dataReaderList=new ArrayList<DataReader>();
		for (XmlTag tag:xmlTag.findChildTag(name).getChildrenTags()) 
		{
			dataReaderList.add(new XmlDataReader(tag));
		}
		return dataReaderList;
	}

	public String readStringValue() {
		return xmlTag.getValue();
	}
	
	public DataReader readXmlAttributeDataReader()
	{
		return new XmlAttributeDataReader(xmlTag);
	}

	@Override
	public long readLongWithName(String name) {
		return 0;
	}

	@Override
	public boolean isFieldExists(String name) {
		return xmlTag.isChildTagExists(name);
	}
}
