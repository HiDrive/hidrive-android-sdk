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
package com.strato.hidrive.api.bll.share;

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

public class CreateDirectoryShareGateway extends SingleResultGateway<ShareLinkEntity> {
	private String pid;
	private long timeToLive;
	private String linkPassword;

	public CreateDirectoryShareGateway(String pid, long timeToLive, String linkPassword) {
		super();

		this.pid = pid;
		this.timeToLive = timeToLive;
		this.linkPassword = linkPassword;
	}

	@Override
	protected ShareLinkEntity prepareObject(DataReader datareader) {
		return new ShareLinkEntity(datareader);
	}

	@Override
	protected Request prepareRequest() {
		List<BaseParam<? extends Object>> params = new ArrayList<BaseParam<? extends Object>>();
		params.add(new Param("pid", this.pid));
		params.add(new LongParam("ttl", this.timeToLive));

		if (this.linkPassword != null && this.linkPassword.length() > 0) {
			params.add(new Param("password", this.linkPassword));
		}

		return new PostRequest("share", params);
	}
}