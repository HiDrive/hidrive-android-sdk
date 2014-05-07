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

import com.strato.hidrive.api.connection.gateway.SetResultGateway;
import com.strato.hidrive.api.connection.httpgateway.request.BaseParam;
import com.strato.hidrive.api.connection.httpgateway.request.GetRequest;
import com.strato.hidrive.api.connection.httpgateway.request.Param;
import com.strato.hidrive.api.connection.httpgateway.request.Request;
import com.strato.hidrive.api.dal.ShareLinkEntity;
import com.strato.hidrive.api.interfaces.DataReader;

public class ReadListShareGateway extends SetResultGateway<ShareLinkEntity> {
	private static final String FIELDS_TO_RETRIEVE = "file_type,has_password,id,path,pid,status,ttl,uri,valid_until,created";

	public ReadListShareGateway() {
		super();
	}

	@Override
	protected ShareLinkEntity prepareObject(DataReader datareader) {
		return new ShareLinkEntity(datareader);
	}

	@Override
	protected Request prepareRequest() {
		List<BaseParam<?>> params = new ArrayList<BaseParam<?>>();
		params.add(new Param("fields", FIELDS_TO_RETRIEVE));

		return new GetRequest("share", params);
	}
}
