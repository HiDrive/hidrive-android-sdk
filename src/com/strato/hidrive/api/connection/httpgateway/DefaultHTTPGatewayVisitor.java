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
package com.strato.hidrive.api.connection.httpgateway;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.strato.hidrive.api.connection.httpgateway.interfaces.HTTPGatewayVisitor;
import com.strato.hidrive.api.connection.httpgateway.request.BaseParam;
import com.strato.hidrive.api.connection.httpgateway.request.Method;
import com.strato.hidrive.api.connection.httpgateway.request.Request;

public class DefaultHTTPGatewayVisitor implements HTTPGatewayVisitor {

	private StringBuffer httpGetRequest;
	private List<NameValuePair> httpPostRequest;

	public DefaultHTTPGatewayVisitor() {
		httpGetRequest = new StringBuffer(getUrl());
		httpPostRequest = new ArrayList<NameValuePair>();
	}

	protected String getUrl() {
		return "http://api.alpha.stg.rzone.de/2.0/";
	}

	public String getHttpGetRequest() {
		return httpGetRequest.toString();
	}

	public List<NameValuePair> getHttpPostRequest() {
		return httpPostRequest;
	}

	public void visit(BaseParam<?> param) {
		httpPostRequest.add(new BasicNameValuePair(param.getName(), (String) param.getValue()));
	}

	public void visit(Method method) {
		httpGetRequest.append(method.getName());
		for (BaseParam<?> param : method.getParams()) {
			this.visit(param);
		}
	}

	public void visit(Request request) {
		this.visit(request.getMethod());
	}
}
