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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.strato.hidrive.api.connection.gateway.GatewayError;
import com.strato.hidrive.api.connection.gateway.SingleResultGateway;
import com.strato.hidrive.api.connection.httpgateway.request.BaseParam;
import com.strato.hidrive.api.connection.httpgateway.request.Param;
import com.strato.hidrive.api.connection.httpgateway.request.PostRequest;
import com.strato.hidrive.api.connection.httpgateway.request.Request;
import com.strato.hidrive.api.connection.httpgateway.response.Response;
import com.strato.hidrive.api.dal.FileInfo;
import com.strato.hidrive.api.dal.RemoteFileInfo;
import com.strato.hidrive.api.interfaces.DataReader;

public class MoveGateway extends SingleResultGateway<FileInfo> {

	private final FileInfo source;
	private final String destinationDirectoryPath;

	public MoveGateway(FileInfo source, String destinationDirectoryPath) {
		this.source = source;
		this.destinationDirectoryPath = destinationDirectoryPath;
	}

	@Override
	protected FileInfo prepareObject(DataReader datareader) {
		return new RemoteFileInfo(datareader.readDataReaderListWithName("done").get(0));
	}

	@Override
	protected Request prepareRequest() {
		List<BaseParam<?>> params = (List)Arrays.asList(new Param("src", this.source.getPath()), new Param("dst", this.destinationDirectoryPath), new Param("on_exist",
				"autoname"));

		return new PostRequest("fs/move", params);
	}

	protected GatewayError checkResponseForError(Response<String> response) {
		GatewayError error = new GatewayError(0, "", "");

		try {
			JSONObject responseJsonObject = new JSONObject(response.getResponseData());
			JSONArray failed = responseJsonObject.getJSONArray("failed");
			JSONArray done = responseJsonObject.getJSONArray("done");

			if (failed.length() == 0 && done.length() == 1) {
				return null;
			}

			int resultCode = ((JSONObject) failed.get(0)).getInt("code");
			String details = ((JSONObject) failed.get(0)).getString("msg");

			error = new GatewayError(resultCode, "Operation error", details);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return error;
	}
}
