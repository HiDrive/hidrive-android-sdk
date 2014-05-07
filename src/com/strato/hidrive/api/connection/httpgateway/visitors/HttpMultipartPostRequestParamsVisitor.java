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
import com.strato.hidrive.api.connection.httpgateway.request.InputStreamParam;
import com.strato.hidrive.api.connection.httpgateway.request.Method;
import com.strato.hidrive.api.connection.httpgateway.request.Request;
import com.strato.hidrive.api.utils.multipart.MultipartEntity;
import com.strato.hidrive.api.utils.multipart.StreamPart;
import com.strato.hidrive.api.utils.multipart.StringPart;

public class HttpMultipartPostRequestParamsVisitor implements HttpRequestParamsVisitor<MultipartEntity> {

	private MultipartEntity entity = new MultipartEntity();

	public HttpMultipartPostRequestParamsVisitor() {
		super();
	}

	@Override
	public MultipartEntity getHttpRequestParams() {
		return entity;
	}

	@Override
	public void visit(BaseParam<?> param) {
		if (param instanceof InputStreamParam) {
			InputStreamParam streamParam = (InputStreamParam) param;
			entity.addPart(new StreamPart(param.getName(), streamParam.getValue(), streamParam.getStreamLength(), streamParam.getFileName(), null));
		} else {
			entity.addPart(new StringPart(param.getName(), param.getValue().toString()));
		}
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
