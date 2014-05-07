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
package com.strato.hidrive.api.connection.httpgateway.interfaces;

import java.util.List;

import org.apache.http.NameValuePair;

import com.strato.hidrive.api.connection.httpgateway.request.BaseParam;
import com.strato.hidrive.api.connection.httpgateway.request.Method;
import com.strato.hidrive.api.connection.httpgateway.request.Request;

public interface HTTPGatewayVisitor {
	String getHttpGetRequest();
	List<NameValuePair> getHttpPostRequest();
	
	void visit(BaseParam<?> param);
	void visit(Method method);
	void visit(Request request);
}
