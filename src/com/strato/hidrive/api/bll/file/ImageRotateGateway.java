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
package com.strato.hidrive.api.bll.file;

import java.util.ArrayList;
import java.util.List;

import com.strato.hidrive.api.connection.gateway.SingleResultGateway;
import com.strato.hidrive.api.connection.httpgateway.request.BaseParam;
import com.strato.hidrive.api.connection.httpgateway.request.FloatParam;
import com.strato.hidrive.api.connection.httpgateway.request.Param;
import com.strato.hidrive.api.connection.httpgateway.request.PostRequest;
import com.strato.hidrive.api.connection.httpgateway.request.Request;
import com.strato.hidrive.api.dal.FileInfo;
import com.strato.hidrive.api.dal.RemoteFileInfo;
import com.strato.hidrive.api.interfaces.DataReader;

public class ImageRotateGateway extends SingleResultGateway<FileInfo> {
	
	private FileInfo source;
	private String destination;
	private RotateDirection rotateDirection;

	public enum RotateDirection {
		ROTATE_RIGHT, ROTATE_LEFT;

		float toDegrees() {
			return this == ROTATE_RIGHT ? 90f : -90f;
		}
	}

	public ImageRotateGateway(FileInfo source, String destination, RotateDirection rotateDirection) {
		this.source = source;
		this.destination = destination;
		this.rotateDirection = rotateDirection;
	}

	@Override
	protected FileInfo prepareObject(DataReader datareader) {
		return new RemoteFileInfo(datareader);
	}

	@Override
	protected Request prepareRequest() {
		List<BaseParam<?>> params = new ArrayList<BaseParam<?>>();
		params.add(new Param("path", this.source.getPath()));
		params.add(new FloatParam("degrees", this.rotateDirection.toDegrees()));
		if(destination != null && !"".equals(this.destination)){
			params.add(new Param("dst", this.destination));
		}
		return new PostRequest("file/image/rotate", params);
	}

}
