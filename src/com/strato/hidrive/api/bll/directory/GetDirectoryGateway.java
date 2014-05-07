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
package com.strato.hidrive.api.bll.directory;

import com.strato.hidrive.api.connection.gateway.SingleResultGateway;
import com.strato.hidrive.api.connection.httpgateway.request.GetRequest;
import com.strato.hidrive.api.connection.httpgateway.request.Param;
import com.strato.hidrive.api.connection.httpgateway.request.Request;
import com.strato.hidrive.api.dal.RemoteFileInfo;
import com.strato.hidrive.api.interfaces.DataReader;

public class GetDirectoryGateway extends SingleResultGateway<RemoteFileInfo> {

	private String path;
	private boolean isNeedChilds = true;

	public GetDirectoryGateway(String path, boolean isNeedChilds) {
		super();
		this.path = path;
		this.isNeedChilds = isNeedChilds;
	}

	@Override
	protected RemoteFileInfo prepareObject(DataReader datareader) {
		return new RemoteFileInfo(datareader, null);
	}

	@Override
	protected Request prepareRequest() {
		String fields = "id,type,size,writable,name,path,mtime,ctime,rshare";
		if (isNeedChilds) {
			fields += ",members.id,members.name,members.type,members.size,members.mime_type,members.mtime,members.ctime,members.writable,members.rshare";
		}
		return new GetRequest("dir", new Param("path", path), new Param("fields", fields));
	}

}
