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
package com.strato.hidrive.api.bll.filesystem;

import java.util.ArrayList;
import java.util.List;

import com.strato.hidrive.api.connection.gateway.SetResultGateway;
import com.strato.hidrive.api.connection.httpgateway.request.BaseParam;
import com.strato.hidrive.api.connection.httpgateway.request.BooleanParam;
import com.strato.hidrive.api.connection.httpgateway.request.DeleteRequest;
import com.strato.hidrive.api.connection.httpgateway.request.Param;
import com.strato.hidrive.api.connection.httpgateway.request.Request;
import com.strato.hidrive.api.dal.FileInfo;
import com.strato.hidrive.api.interfaces.DataReader;

public class DeleteGateway extends SetResultGateway<FileInfo> {

	private final FileInfo source;

	public DeleteGateway(FileInfo source) {
		this.source = source;
	}

	@Override
	protected FileInfo prepareObject(DataReader datareader) {
		return source;
	}

	@Override
	protected Request prepareRequest() {
		List<BaseParam<?>> params = new ArrayList<BaseParam<?>>();
		params.add(new Param("path", this.source.getPath()));

		String method = "";
		if (this.source.isDirectory()) {
			method = "dir";
			params.add(new BooleanParam("recursive", true));
		} else {
			method = "file";
		}

		return new DeleteRequest(method, params);
	}
}
