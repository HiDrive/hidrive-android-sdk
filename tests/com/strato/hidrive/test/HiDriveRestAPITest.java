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
package com.strato.hidrive.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.http.HttpStatus;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.test.AndroidTestCase;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.strato.hidrive.api.HiDriveRestClient;
import com.strato.hidrive.api.connection.gateway.DomainGatewayResult;
import com.strato.hidrive.api.connection.gateway.StreamReadingGateway;
import com.strato.hidrive.api.connection.gateway.interfaces.DomainGatewayHandler;
import com.strato.hidrive.api.connection.httpgateway.HTTPGateway;
import com.strato.hidrive.api.connection.httpgateway.StaticUriRedirector;
import com.strato.hidrive.api.dal.FileInfo;
import com.strato.hidrive.api.dal.RemoteFileInfo;
import com.strato.hidrive.api.session.HiDriveSession;

public class HiDriveRestAPITest extends AndroidTestCase {

	public static final String CLIENT_ID = "replace_me";
	public static final String CLIENT_SECRET = "replace_me";
	
	private static final int MILLISECONDS_IN_SECOND = 1000;
	private static final String SAMPLE_FILE_DATA = "{\"ctime\" : 1398761384,\"mtime\" : 1398761384,\"writable\" : true,\"name\" : \"test\", \"members\" : [{\" name\" : \"abra\"}]}";
	private static final String TEST_TEXT_FILE_CONTENT = TEST_FILE_NAME;
	private static final String TEST_FILE_NAME = "test";

	private HiDriveSession mockedSession;
	private HiDriveRestClient restClient;
	private MockWebServer server;
	protected DomainGatewayResult<?> asyncronizedMethodResult;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		mockedSession = new HiDriveSession(getContext(), null, null, null);
		restClient = new HiDriveRestClient(mockedSession);
		server = new MockWebServer();
		server.play();
		HTTPGateway.installURIRedirector(new StaticUriRedirector(server.getUrl("/").toString()));
	}
	
	@Override
	protected void tearDown() throws Exception {
		server.shutdown();
		super.tearDown();
	}
	
	public void testCreateDirectoryMethod() {
		server.enqueue(new MockResponse().setBody(SAMPLE_FILE_DATA));
		
		DomainGatewayResult<RemoteFileInfo> response = restClient.createDirectorySync("/root/public");
		RemoteFileInfo directory = response.getResult();
		
		assertNotNull(directory);
		assertEquals(TEST_FILE_NAME, directory.getName());
	}
	
	public void testAnRemoteMethodFail() {
		server.enqueue(new MockResponse()
			.setStatus("HTTP/1.1 500 Internal Server Error"));
		
		DomainGatewayResult<RemoteFileInfo> response = restClient.createDirectorySync("/root/public");

		assertNull(response.getResult());
		assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getGatewayError().getErrorCode());
	}
	
	public void testAnAsynchronousRequest() throws InterruptedException {
		server.enqueue(new MockResponse().setBody(SAMPLE_FILE_DATA));
		
		DomainGatewayHandler<RemoteFileInfo> domainGatewayResultListener = new DomainGatewayHandler<RemoteFileInfo>() {
			@Override
			public void handleDomainGatewayResult(DomainGatewayResult<RemoteFileInfo> result) {
				HiDriveRestAPITest.this.asyncronizedMethodResult = result;
				synchronized (this) {
					notifyAll();
				}
			}
		};
		
		restClient.createDirectory("/root/public", domainGatewayResultListener);
		synchronized (domainGatewayResultListener) {
			domainGatewayResultListener.wait(10000);
		}
		
		assertNotNull(asyncronizedMethodResult);
		
		RemoteFileInfo directory = (RemoteFileInfo) asyncronizedMethodResult.getResult();
		
		assertNotNull(directory);
		assertEquals(TEST_FILE_NAME, directory.getName());
	}

	public void testGetDirectoryMethod() {
		server.enqueue(new MockResponse()
			.setBody(SAMPLE_FILE_DATA));
		
		DomainGatewayResult<RemoteFileInfo> response = restClient.getDirectorySync("/root/public");
		RemoteFileInfo directory = response.getResult();
		
		assertNotNull(directory);
		assertEquals(TEST_FILE_NAME, directory.getName());
		assertEquals(1398761384l * MILLISECONDS_IN_SECOND, directory.getCreationTime());
		assertEquals(1398761384l * MILLISECONDS_IN_SECOND, directory.getLastModified());
		assertEquals(false, directory.isReadOnly());
		assertNotNull(directory.getChilds());
		assertEquals(1, directory.getChilds().size());
		assertNull(directory.getShares(false));
		assertNotNull(directory.getShares(true));
	}
	
	public void testLoadFileForPathMethod() {
		server.enqueue(new MockResponse().setBody(TEST_TEXT_FILE_CONTENT));
		
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		DomainGatewayResult<Boolean> response = restClient.loadFileForPathSync("/root/public/test.txt", new StreamReadingGateway.StreamReadingGatewayListener() {
			
			@Override
			public OutputStream onPrepareOutputStream() {
				return baos;
			}
			
			@Override
			public void onDownloadProgress(long downloaded, long totalSize) {
				assertTrue(downloaded <= totalSize);
			}
		});
		
		
		assertEquals(true, response.getResult().booleanValue());
		assertEquals(new String(baos.toByteArray()), TEST_TEXT_FILE_CONTENT);
	}
	
	public void testUploadFileMethod() {
		server.enqueue(new MockResponse().setBody(SAMPLE_FILE_DATA));
		
		byte[] fileBytes = TEST_TEXT_FILE_CONTENT.getBytes();
		
		DomainGatewayResult<RemoteFileInfo> response = restClient.uploadFileSync("/root/public", "test.txt", new ByteArrayInputStream(fileBytes), fileBytes.length);
		RemoteFileInfo fileInfo = response.getResult();
		
		assertNotNull(fileInfo);
	}
	
	public void testDeleteFileMethod() {
		server.enqueue(new MockResponse().setBody("[" + SAMPLE_FILE_DATA + "]"));
		
		FileInfo mockFileInfo = new RemoteFileInfo("/" + TEST_FILE_NAME);
		DomainGatewayResult<List<FileInfo>> response = restClient.deleteFileSync(mockFileInfo);
		List<FileInfo> resultArray = response.getResult();
		
		assertNotNull(resultArray);
		assertEquals(1, resultArray.size());
		assertEquals(TEST_FILE_NAME, resultArray.get(0).getName());
	}
	
	public void testMoveFileMethod() {
		server.enqueue(new MockResponse().setBody("{\"done\":[" + SAMPLE_FILE_DATA + "]}"));
		
		FileInfo mockFileInfo = new RemoteFileInfo("/" + TEST_FILE_NAME);
		DomainGatewayResult<FileInfo> response = restClient.moveFileSync(mockFileInfo, "/");
		FileInfo fileInfo = response.getResult();
		
		assertNotNull(fileInfo);
		assertEquals(TEST_FILE_NAME, fileInfo.getName());
	}
	
	public void testCopyFileMethod() {
		server.enqueue(new MockResponse().setBody("{\"done\":[" + SAMPLE_FILE_DATA + "]}"));
		
		FileInfo mockFileInfo = new RemoteFileInfo("/" + TEST_FILE_NAME);
		DomainGatewayResult<FileInfo> response = restClient.copyFileSync(mockFileInfo, "/");
		FileInfo fileInfo = response.getResult();
		
		assertNotNull(fileInfo);
		assertEquals(TEST_FILE_NAME, fileInfo.getName());
	}
	
	public void testRenameFileMethod() {
		server.enqueue(new MockResponse().setBody(SAMPLE_FILE_DATA));
		
		FileInfo mockFileInfo = new RemoteFileInfo("/" + TEST_FILE_NAME);
		DomainGatewayResult<FileInfo> response = restClient.renameFileSync(mockFileInfo, "/");
		FileInfo fileInfo = response.getResult();
		
		assertNotNull(fileInfo);
		assertEquals(TEST_FILE_NAME, fileInfo.getName());
	}
	
	public void testLoadThumbnailForFileMethod() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888).compress(CompressFormat.PNG, 100, baos);
		server.enqueue(new MockResponse().setBody(baos.toByteArray()));
		
		DomainGatewayResult<Bitmap> response = restClient.loadThumbnailForFileSync("/", 32, 32);
		Bitmap bitmap = response.getResult();
		
		assertNotNull(bitmap);
		assertEquals(32, bitmap.getWidth());
	}
	
	
}
