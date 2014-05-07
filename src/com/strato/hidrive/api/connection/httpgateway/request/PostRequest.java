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

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import com.strato.hidrive.api.connection.httpgateway.interfaces.HttpRequestParamsVisitor;
import com.strato.hidrive.api.connection.httpgateway.visitors.HttpPostRequestParamsVisitor;

public class PostRequest extends Request {

	public PostRequest(String methodName) {
		super(methodName);
	}

	public PostRequest(String methodName, BaseParam<?>... params) {
		super(methodName, params);
	}

	public PostRequest(String methodName, List<BaseParam<?>> params) {
		super(methodName, params);
	}

	@SuppressWarnings("unchecked")
	protected HttpRequestBase createHttpRequest(String requestUri, HttpRequestParamsVisitor<?> visitor) throws UnsupportedEncodingException {
		HttpPost httpPost = new HttpPost(requestUri);
		//There used StringEntity and some string manipulations instead of UrlEncodedFormEntity due to problem in url encoding ("+" instead "%20")
		//details: http://stackoverflow.com/questions/7915029/how-to-encode-space-as-20-in-urlencodedformentity-while-executing-apache-httppo
		String entityValue = URLEncodedUtils.format((List<NameValuePair>)visitor.getHttpRequestParams(), HTTP.UTF_8);
		entityValue = entityValue.replaceAll("\\+","%20");
		StringEntity entity = new StringEntity(entityValue, HTTP.UTF_8);
		entity.setContentType(URLEncodedUtils.CONTENT_TYPE);
		httpPost.setEntity(entity);
		//original code:
		//httpPost.setEntity(new UrlEncodedFormEntity((List<? extends NameValuePair>) visitor.getHttpRequestParams(), HTTP.UTF_8));
		return httpPost;
	}

	protected HttpRequestParamsVisitor<?> createVisitor() {
		return new HttpPostRequestParamsVisitor();
	}
}
