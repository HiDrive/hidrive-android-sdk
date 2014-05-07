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

import com.strato.hidrive.api.connection.httpgateway.interfaces.HTTPGatewayVisitor;
import com.strato.hidrive.api.connection.httpgateway.request.BaseParam;
import com.strato.hidrive.api.connection.httpgateway.request.Method;
import com.strato.hidrive.api.connection.httpgateway.request.Request;
import com.strato.hidrive.api.utils.StringUtils;

public class GetHTTPGatewayVisitor implements HTTPGatewayVisitor {

	protected StringBuffer httpGetRequest;
	protected List<NameValuePair>  httpPostRequest;
	
	public GetHTTPGatewayVisitor() {
		httpGetRequest=new StringBuffer("https://api.hidrive.strato.com/1.0/");
		httpPostRequest=new ArrayList<NameValuePair>();
	}
	
	public String getHttpGetRequest() {
		return httpGetRequest.toString();
	}

	public List<NameValuePair> getHttpPostRequest() {
		return httpPostRequest;
	}

	public void visit(BaseParam<?> param) {
		if (!StringUtils.stringEndsWith(httpGetRequest.toString(), "?", true))
	    {
	        httpGetRequest.append("&");
	    }	 
	    httpGetRequest.append(String.format("%s=%s",param.getName(),param.getValue()));
	}

	
	public void visit(Method method) {
		httpGetRequest.append(method.getName());
		if(method.getParams().size()>0){
			httpGetRequest.append("?");
		}
		for (BaseParam<?> param:method.getParams()) {
			this.visit(param);
		}
	}

	public void visit(Request request) {
		this.visit(request.getMethod());
	}
}
