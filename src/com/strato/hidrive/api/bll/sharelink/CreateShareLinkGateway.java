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
package com.strato.hidrive.api.bll.sharelink;

import java.util.ArrayList;
import java.util.List;

import com.strato.hidrive.api.connection.gateway.SingleResultGateway;
import com.strato.hidrive.api.connection.httpgateway.request.BaseParam;
import com.strato.hidrive.api.connection.httpgateway.request.LongParam;
import com.strato.hidrive.api.connection.httpgateway.request.Param;
import com.strato.hidrive.api.connection.httpgateway.request.PostRequest;
import com.strato.hidrive.api.connection.httpgateway.request.Request;
import com.strato.hidrive.api.dal.ShareLinkEntity;
import com.strato.hidrive.api.interfaces.DataReader;

public class CreateShareLinkGateway extends SingleResultGateway<ShareLinkEntity> {
	private String path;
	private long timeToLive;
	private int downloadMaxCount;
	private String linkPassword;
	private static final String TYPE_FILE = "file";

	public CreateShareLinkGateway(String path, long timeToLive, int downloadMaxCount, String linkPassword) {
		super();

		this.path = path;
		this.timeToLive = timeToLive;
		this.downloadMaxCount = downloadMaxCount;
		this.linkPassword = linkPassword;
	}

	@Override
	protected ShareLinkEntity prepareObject(DataReader datareader) {
		return new ShareLinkEntity(datareader);
	}

	@Override
	protected Request prepareRequest() {
		List<BaseParam<? extends Object>> params = new ArrayList<BaseParam<? extends Object>>();
		params.add(new Param("path", this.path));
		params.add(new Param("type", TYPE_FILE));
		params.add(new LongParam("ttl", this.timeToLive));
		params.add(new LongParam("maxcount", this.downloadMaxCount));

		if (this.linkPassword != null && this.linkPassword.length() > 0) {
			params.add(new Param("password", this.linkPassword));
		}

		return new PostRequest("sharelink", params);
	}
}
