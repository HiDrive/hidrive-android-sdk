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
package com.strato.hidrive.api.connection.httpgateway.visitors;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.strato.hidrive.api.connection.httpgateway.interfaces.HttpRequestParamsVisitor;
import com.strato.hidrive.api.connection.httpgateway.request.BaseParam;
import com.strato.hidrive.api.connection.httpgateway.request.Method;
import com.strato.hidrive.api.connection.httpgateway.request.Request;

public class HttpPostRequestParamsVisitor implements HttpRequestParamsVisitor<List<NameValuePair>> {

	private List<NameValuePair> httpPostRequestParams = new ArrayList<NameValuePair>();

	@Override
	public List<NameValuePair> getHttpRequestParams() {
		return httpPostRequestParams;
	}

	public void visit(BaseParam<?> param) {
		httpPostRequestParams.add(new BasicNameValuePair(param.getName(), param.getValue().toString()));
	}

	public void visit(Method method) {
		// httpGetRequest.append(method.getName());
		for (BaseParam<?> param : method.getParams()) {
			this.visit(param);
		}
	}

	public void visit(Request request) {
		this.visit(request.getMethod());
	}

}
