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

import com.strato.hidrive.api.connection.gateway.SingleResultGateway;
import com.strato.hidrive.api.connection.httpgateway.request.GetRequest;
import com.strato.hidrive.api.connection.httpgateway.request.Param;
import com.strato.hidrive.api.connection.httpgateway.request.Request;
import com.strato.hidrive.api.dal.ChangelogNews;
import com.strato.hidrive.api.interfaces.DataReader;

public class ChangelogGateway extends SingleResultGateway<List<ChangelogNews>> {
	private final String language;

	public ChangelogGateway(String language) {
		super();
		this.language = language;
	}

	@Override
	protected List<ChangelogNews> prepareObject(DataReader dataReader) {
		List<ChangelogNews> setResult = new ArrayList<ChangelogNews>();
		List<DataReader> newsEntries = dataReader.readDataReaderListWithName("entries");

		for (DataReader newsDataReader : newsEntries) {
			setResult.add(new ChangelogNews(newsDataReader));
		}

		return setResult;
	}
	
	protected String unescapeResponseData(String responseData){
		return responseData;
	}

	@Override
	protected Request prepareRequest() {
		return new GetRequest("changelog", new Param("lang", this.language));
	}

}
