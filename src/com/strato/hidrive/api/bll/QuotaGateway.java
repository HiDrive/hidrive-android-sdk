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
package com.strato.hidrive.api.bll;

import com.strato.hidrive.api.connection.gateway.SetResultGateway;
import com.strato.hidrive.api.connection.httpgateway.request.GetRequest;
import com.strato.hidrive.api.connection.httpgateway.request.Param;
import com.strato.hidrive.api.connection.httpgateway.request.Request;
import com.strato.hidrive.api.dal.Quota;
import com.strato.hidrive.api.interfaces.DataReader;

public class QuotaGateway extends SetResultGateway<Quota>{

	public QuotaGateway() {
		super();
	}

	@Override
	protected Quota prepareObject(DataReader datareader) {
		return new Quota(datareader);
	}

	@Override
	protected Request prepareRequest() {
		return new GetRequest("zone", new Param("scope", "user"));
	}

}
