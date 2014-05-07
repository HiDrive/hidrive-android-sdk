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
package com.strato.hidrive.api.bll.news;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import com.strato.hidrive.api.connection.gateway.HiDriveDomainGateway;
import com.strato.hidrive.api.connection.httpgateway.request.GetRequest;
import com.strato.hidrive.api.connection.httpgateway.request.Request;
import com.strato.hidrive.api.connection.httpgateway.response.Response;
import com.strato.hidrive.api.utils.StringUtils;

public class ChangelogLanguagesGateway extends HiDriveDomainGateway<List<String>> {

	@Override
	protected List<String> prepareResult(Response<String> response) {
		List<String> languages = new ArrayList<String>();
		
		if (response.getResponseData() == null) {
			return languages;
		}
		try {
			String responseString = StringUtils.unescapeHiDriveCoding(response.getResponseData());
			JSONArray array = new JSONArray(responseString);
			for (int i = 0; i < array.length(); i++) {
				languages.add((String)array.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return languages;
	}

	@Override
	protected Request prepareRequest() {
		return new GetRequest("changelog/languages");
	}
}
