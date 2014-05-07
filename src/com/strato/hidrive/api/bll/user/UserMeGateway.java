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
package com.strato.hidrive.api.bll.user;

import com.strato.hidrive.api.connection.gateway.SingleResultGateway;
import com.strato.hidrive.api.connection.httpgateway.request.GetRequest;
import com.strato.hidrive.api.connection.httpgateway.request.Param;
import com.strato.hidrive.api.connection.httpgateway.request.Request;
import com.strato.hidrive.api.dal.UserEntity;
import com.strato.hidrive.api.interfaces.DataReader;

public class UserMeGateway extends SingleResultGateway<UserEntity> {

	@Override
	protected UserEntity prepareObject(DataReader dataReader) {
		return new UserEntity(dataReader);
	}

	@Override
	protected Request prepareRequest() {
		return new GetRequest("user/me", new Param("fields", "alias,account,home,email,descr,is_admin,is_owner,token"));
	}
}
