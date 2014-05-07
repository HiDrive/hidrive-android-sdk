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
package com.strato.hidrive.api.bll;

import java.util.ArrayList;
import java.util.List;

import com.strato.hidrive.api.XmlDataReader;
import com.strato.hidrive.api.connection.gateway.GatewayError;
import com.strato.hidrive.api.connection.gateway.SetResultGateway;
import com.strato.hidrive.api.connection.httpgateway.CustomersWhitelistHTTPGatewayVisitor;
import com.strato.hidrive.api.connection.httpgateway.interfaces.HTTPGatewayVisitor;
import com.strato.hidrive.api.connection.httpgateway.request.Request;
import com.strato.hidrive.api.connection.httpgateway.response.Response;
import com.strato.hidrive.api.dal.CustomerPreferences;
import com.strato.hidrive.api.interfaces.DataReader;
import com.strato.hidrive.api.utils.xml.XmlParser;
import com.strato.hidrive.api.utils.xml.XmlTag;

public class CustomersWhitelistGateway extends SetResultGateway<CustomerPreferences> {

	public CustomersWhitelistGateway() {
		super();
	}

	@Override
	protected CustomerPreferences prepareObject(DataReader dataReader) {
		return new CustomerPreferences(dataReader);
	}
	
	@Override
	protected String getBaseUri() {
		return "https://www.hidrive.strato.com/apps";
	}

	@Override
	protected Request prepareRequest() {
		return new Request("config.xml");
	}

	@Override
	protected HTTPGatewayVisitor createHTTPGatewayVisitor() {
		return new CustomersWhitelistHTTPGatewayVisitor();
	}

	protected List<CustomerPreferences> prepareResult(Response<String> response) {
		List<CustomerPreferences> setResult = new ArrayList<CustomerPreferences>();
		if (response != null && response.getResponseData() != null) {
			for (XmlTag tag : parentTag(response).getChildrenTags()) {
				setResult.add(prepareObject(new XmlDataReader(tag)));
			}
		}
		return setResult;
	}

	protected XmlTag parentTag(Response<String> response) {
		return new XmlParser().parse(response.getResponseData()).getRootTag();
	}

	protected GatewayError checkResponseForError(Response<String> response) {
		String responseData = response.getResponseData();
		if (responseData == null || responseData.equalsIgnoreCase("null")) {
			return new GatewayError(0, "", "");
		}

		return null;
	}
}
