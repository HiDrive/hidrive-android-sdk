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
package com.strato.hidrive.api.bll.free;

import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.strato.hidrive.api.HttpClientManager;
import com.strato.hidrive.api.JSONDataReader;
import com.strato.hidrive.api.connection.gateway.GatewayError;
import com.strato.hidrive.api.connection.gateway.SingleResultGateway;
import com.strato.hidrive.api.connection.httpgateway.HTTPGateway;
import com.strato.hidrive.api.connection.httpgateway.request.Request;
import com.strato.hidrive.api.connection.httpgateway.result.HTTPGatewayResult;
import com.strato.hidrive.api.interfaces.DataReader;

public class HiDriveFreeGateway<T> extends SingleResultGateway<T> {

	public HiDriveFreeGateway() {
		super();
	}

	@Override
	protected T prepareObject(DataReader datareader) {
		return null;
	}

	@Override
	protected Request prepareRequest() {
		return null;
	}

	protected HTTPGateway<String> createHTTPGateway() {
		return new HTTPGateway<String>("", createHTTPGatewayVisitor(), HttpClientManager.getInstance().getHttpClient());
	}

	@Override
	protected GatewayError checkResponseForError(HTTPGatewayResult<String> result) {
		if (result.getResponse() == null || result.getResponse().getResponseData() == null || result.getResponse().getResponseData().equalsIgnoreCase("null")) {
			return new GatewayError(0, "", "");
		}

		String responseData = result.getResponse().getResponseData();
		JsonParser jsonParser = new JsonParser();
		jsonObject = (JsonObject)jsonParser.parse(responseData);
		
		DataReader dataReader = new JSONDataReader(jsonObject).readDataReaderWithName("data");
		if (dataReader == null) {
			return null;
		}

		List<DataReader> errorsList = dataReader.readDataReaderListWithName("errors");
		if (errorsList == null) {
			return null;
		}

		DataReader errorReader = errorsList.get(0);

		return new GatewayError(1, errorReader.readStringWithName("error_msg"), dataReader.readStringWithName("field_name"));

	}

	@Override
	protected String getBaseUri() {
		return "https://www.free-hidrive.com/order/";
	}

}
