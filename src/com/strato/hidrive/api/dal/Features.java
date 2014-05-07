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
package com.strato.hidrive.api.dal;

import com.strato.hidrive.api.interfaces.DataReader;

public class Features {
	private final static int MAX_SHARELINKS_DOWNLOADS = 1000;

	private final boolean sharelinkPassword;
	private final long sharelinkTimeToLive;
	private final int sharelinkDownloads;
	private final boolean shareGalleryEnabled;

	public Features(DataReader dataReader) {
		super();

		this.sharelinkPassword = dataReader.readBooleanWithName("sharelink_password");
		this.sharelinkTimeToLive = dataReader.readLongWithName("sharelink_ttl");
		this.shareGalleryEnabled = dataReader.readBooleanWithName("gallery");
		
		int sharelinkDownloadsCount = dataReader.readIntWithName("sharelink_downloads");
		this.sharelinkDownloads = sharelinkDownloadsCount != 0 ? sharelinkDownloadsCount : MAX_SHARELINKS_DOWNLOADS;
	}

	public boolean isSharelinkPassword() {
		return this.sharelinkPassword;
	}

	public boolean isShareeGalleryEnabled() {
		return this.shareGalleryEnabled;
	}
	
	public long getSharelinkTimeToLive() {
		return sharelinkTimeToLive;
	}

	public int getSharelinkDownloads() {
		return sharelinkDownloads;
	}

}
