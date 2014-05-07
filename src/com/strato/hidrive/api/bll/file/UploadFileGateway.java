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

import java.io.InputStream;

import com.strato.hidrive.api.connection.gateway.SingleResultGateway;
import com.strato.hidrive.api.connection.httpgateway.request.BaseInputStreamRequest;
import com.strato.hidrive.api.connection.httpgateway.request.Request;
import com.strato.hidrive.api.dal.RemoteFileInfo;
import com.strato.hidrive.api.interfaces.DataReader;

public abstract class UploadFileGateway extends SingleResultGateway<RemoteFileInfo> {

	protected String dir;
	protected String fileName;
	private InputStream inputStream;
	private long streamLength;

	public UploadFileGateway(String dir, String fileName, InputStream inputStream, long streamLength) {
		super();
		this.dir = dir;
		this.fileName = fileName;
		this.inputStream = inputStream;
		this.streamLength = streamLength;
	}

	@Override
	protected RemoteFileInfo prepareObject(DataReader datareader) {
		return new RemoteFileInfo(datareader, null);
	}

	@Override
	protected Request prepareRequest() {
		BaseInputStreamRequest request = getInputStreamRequest();
		request.setInputStream(inputStream, streamLength);
		return request;
	}
	
	protected abstract BaseInputStreamRequest getInputStreamRequest();

}
