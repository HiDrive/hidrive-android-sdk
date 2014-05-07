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
package com.strato.hidrive.api.connection.gateway;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;

import com.strato.hidrive.api.interfaces.DataReader;

public abstract class StreamReadingGateway extends SingleResultGateway<Boolean> {
	private static final String DONE_JSON_RESULT = "{\"done\":true}";

	private StreamReadingGatewayListener listener;

	private boolean isStop = false;
	private boolean isDownloading = false;
	protected boolean isDownloaded = false;

	public interface StreamReadingGatewayListener {
		OutputStream onPrepareOutputStream();

		void onDownloadProgress(long downloaded, long totalSize);
	}

	public StreamReadingGateway(StreamReadingGatewayListener listener) {
		this.listener = listener;
	}

	@Override
	protected ResponseHandler<String> createResponseHandler() {
		super.createResponseHandler();
		return new ResponseHandler<String>() {
			@Override
			public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != 200) {
					isDownloaded = false;
					return DONE_JSON_RESULT;
				}
				OutputStream outStream = listener.onPrepareOutputStream();
				if (outStream == null) {
					isDownloaded = false;
					return DONE_JSON_RESULT;
				}
				isDownloading = true;
				InputStream in = response.getEntity().getContent();
				byte[] buffer = new byte[1024];
				int readLen = 0;
				long downloadedSize = 0;
				long totalSize = response.getEntity().getContentLength();
				isDownloaded = true;
				while ((readLen = in.read(buffer)) >= 0) {
					downloadedSize += readLen;
					listener.onDownloadProgress(downloadedSize, totalSize);
					if (isStop) {
						isStop = isDownloaded = false;
						break;
					}
					outStream.write(buffer, 0, readLen);
				}
				isDownloading = false;
				outStream.close();
				in.close();
				return DONE_JSON_RESULT;
			}
		};
	}

	@Override
	protected Boolean prepareObject(DataReader datareader) {
		return isDownloaded;
	}

	public boolean isDownloading() {
		return isDownloading;
	}

	public void stop() {
		isStop = true;
	}
}
