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
package com.strato.hidrive.api.connection.httpgateway.request;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;

import com.google.gson.JsonObject;
import com.strato.hidrive.api.connection.httpgateway.interfaces.HttpRequestParamsVisitor;
import com.strato.hidrive.api.connection.httpgateway.visitors.HttpPostRequestParamsVisitor;

public class JSONPostRequest extends Request {
	private static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
	private JsonObject jsonObject;
	private JSONArray jsonArray;

	public JSONPostRequest(String methodName, JsonObject jsonObject) {
		super(methodName);
		this.jsonObject = jsonObject;
	}

	/*
	 * public JSONPostRequest(String methodName, JSONObject jsonObject) {
	 * super(methodName); this.jsonObject = jsonObject; }
	 */

	public JSONPostRequest(String methodName, JSONArray jsonArray) {
		super(methodName);
		this.jsonArray = jsonArray;
	}

	protected HttpRequestBase createHttpRequest(String requestUri, HttpRequestParamsVisitor<?> visitor) throws UnsupportedEncodingException {
		HttpPost httpPost = new HttpPost(requestUri);
		String jsonString = "";
		if (jsonObject != null) {
			jsonString = jsonObject.toString();
		} else if (jsonArray != null) {
			jsonString = jsonArray.toString();
		}

		StringEntity stringEntity = new StringEntity(jsonString, "UTF-8");  
		stringEntity.setContentEncoding("UTF-8");
		stringEntity.setContentType(CONTENT_TYPE_APPLICATION_JSON);
		httpPost.setEntity(stringEntity);

		return httpPost;
	}

	protected HttpRequestParamsVisitor<?> createVisitor() {
		return new HttpPostRequestParamsVisitor();
	}
}
