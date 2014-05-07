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

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.strato.hidrive.api.connection.gateway.SingleResultGateway;
import com.strato.hidrive.api.connection.httpgateway.request.GetRequest;
import com.strato.hidrive.api.connection.httpgateway.request.LongParam;
import com.strato.hidrive.api.connection.httpgateway.request.Param;
import com.strato.hidrive.api.connection.httpgateway.request.Request;
import com.strato.hidrive.api.interfaces.DataReader;

public class GetThumbnailGateway extends SingleResultGateway<Bitmap> {
	private static final String DONE_JSON_RESULT = "{\"done\":true}";

	private String filePath;
	private int width;
	private int height;

	private Bitmap bitmap = null;

	public GetThumbnailGateway(String filePath, int width, int height) {
		super();
		this.filePath = filePath;
		this.width = width;
		this.height = height;
	}

	public GetThumbnailGateway(int width, int height) {
		this("", width, height);
	}

	public void setFilePath(String path) {
		this.filePath = path;
		this.bitmap = null;
	}

	@Override
	protected ResponseHandler<String> createResponseHandler() {
		super.createResponseHandler();
		return new ResponseHandler<String>() {
			@Override
			public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {
					HttpEntity entity = response.getEntity();
					byte[] bytes = EntityUtils.toByteArray(entity);
					bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
				}
				return DONE_JSON_RESULT;
			}
		};
	}

	@Override
	protected Bitmap prepareObject(DataReader datareader) {
		return bitmap;
	}

	@Override
	protected Request prepareRequest() {
		return new GetRequest("file/thumbnail", new Param("path", filePath), new LongParam("width", width), new LongParam("height", height));
	}

}
