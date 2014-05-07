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

import com.strato.hidrive.api.connection.httpgateway.interfaces.HttpRequestParamsVisitor;
import com.strato.hidrive.api.connection.httpgateway.request.BaseParam;
import com.strato.hidrive.api.connection.httpgateway.request.Method;
import com.strato.hidrive.api.connection.httpgateway.request.Request;
import com.strato.hidrive.api.utils.StringUtils;

public class HttpGetRequestParamsVisitor implements HttpRequestParamsVisitor<String> {

	private StringBuilder httpRequestParams = new StringBuilder("");

	@Override
	public String getHttpRequestParams() {
		return this.httpRequestParams.toString();
	}

	public void visit(BaseParam<?> param) {
		if (!StringUtils.stringEndsWith(getHttpRequestParams(), "?", true)) {
			httpRequestParams.append("&");
		}

		httpRequestParams.append(String.format("%s=%s", param.getName(), StringUtils.encodeUrlQueryValue(param.getValue().toString())));
	}

	public void visit(Method method) {
		// httpRequestParams.append(method.getName());
		if (method.getParams().size() > 0) {
			httpRequestParams.append("?");
		}
		for (BaseParam<?> param : method.getParams()) {
			this.visit(param);
		}
	}

	public void visit(Request request) {
		this.visit(request.getMethod());
	}
}
