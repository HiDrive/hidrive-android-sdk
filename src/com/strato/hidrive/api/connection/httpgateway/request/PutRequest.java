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
import java.util.List;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;

import com.strato.hidrive.api.connection.httpgateway.interfaces.HttpRequestParamsVisitor;

public class PutRequest extends Request {

	public PutRequest(String methodName) {
		super(methodName);
	}

	public PutRequest(String methodName, BaseParam<?>... params) {
		super(methodName, params);
	}

	public PutRequest(String methodName, List<BaseParam<?>> params) {
		super(methodName, params);
	}

	protected HttpRequestBase createHttpRequest(String requestUri, HttpRequestParamsVisitor<?> visitor) throws UnsupportedEncodingException {
		return new HttpPut(requestUri + visitor.getHttpRequestParams());
	}
}
