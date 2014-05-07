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

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.InputStreamEntity;

import com.strato.hidrive.api.connection.httpgateway.interfaces.HttpRequestParamsVisitor;

public class InputStreamPostRequest extends BaseInputStreamRequest {

	public InputStreamPostRequest(Method method) {
		super(method);
	}

	public InputStreamPostRequest(String methodName) {
		super(methodName);
	}

	public InputStreamPostRequest(String methodName, List<BaseParam<?>> params) {
		super(methodName, params);
	}

	public InputStreamPostRequest(String methodName, BaseParam<?>... params) {
		super(methodName, params);
	}

	@Override
	protected HttpRequestBase createHttpRequest(String requestUri, HttpRequestParamsVisitor<?> visitor)
			throws UnsupportedEncodingException {
		HttpPost httpPost = new HttpPost(requestUri + visitor.getHttpRequestParams());
		httpPost.setEntity(new InputStreamEntity(getInputStream(), getStreamLength()));
		return httpPost;
	}
}
