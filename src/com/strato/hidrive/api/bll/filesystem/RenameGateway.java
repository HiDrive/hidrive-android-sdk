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

import java.util.Arrays;
import java.util.List;

import com.strato.hidrive.api.connection.gateway.SingleResultGateway;
import com.strato.hidrive.api.connection.httpgateway.request.BaseParam;
import com.strato.hidrive.api.connection.httpgateway.request.Param;
import com.strato.hidrive.api.connection.httpgateway.request.PostRequest;
import com.strato.hidrive.api.connection.httpgateway.request.Request;
import com.strato.hidrive.api.dal.FileInfo;
import com.strato.hidrive.api.dal.RemoteFileInfo;
import com.strato.hidrive.api.interfaces.DataReader;

public class RenameGateway extends SingleResultGateway<FileInfo> {

	private final FileInfo source;
	private final String newName;

	public RenameGateway(FileInfo source, String newName) {
		this.source = source;
		this.newName = newName;
	}

	@Override
	protected FileInfo prepareObject(DataReader datareader) {
		return new RemoteFileInfo(datareader);
	}

	@Override
	protected Request prepareRequest() {
		String method = this.source.isDirectory() ? "dir/rename" : "file/rename";

		List<BaseParam<?>> params = (List)Arrays.asList(new Param("path", this.source.getPath()), new Param("name", this.newName));

		return new PostRequest(method, params);
	}
}
