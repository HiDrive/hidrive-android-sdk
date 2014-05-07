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
package com.example.filemanager;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.strato.hidrive.api.HiDriveRestClient;
import com.strato.hidrive.api.connection.gateway.DomainGatewayResult;
import com.strato.hidrive.api.connection.gateway.interfaces.DomainGatewayHandler;
import com.strato.hidrive.api.dal.FileInfo;
import com.strato.hidrive.api.dal.ShareLinkEntity;

public class SharelinksContainer {

	private static SharelinksContainer instance;
	
	private List<ShareLinkEntity> shareLinks = new ArrayList<ShareLinkEntity>();
	private OnSharelinksLoadedListener listener;
	
	public interface OnSharelinksLoadedListener {
		void onSharelinksLoaded();
	}
	
	private SharelinksContainer() {
		super();
	}

	public static SharelinksContainer getInstance() {
		if (instance == null) {
			instance = new SharelinksContainer();
		}
		return instance;
	}
	
	public void loadShareLinks(HiDriveRestClient hdRestClient) {
		hdRestClient.loadShareLinks(new DomainGatewayHandler<List<ShareLinkEntity>>() {
			@Override
			public void handleDomainGatewayResult(DomainGatewayResult<List<ShareLinkEntity>> result) {
				if (result.getGatewayError() != null) {
					Log.e(SharelinksContainer.class.getSimpleName(), result.getGatewayError().getErrorMessage());
				} else {
					SharelinksContainer.this.shareLinks = result.getResult();
					if (listener != null) {
						listener.onSharelinksLoaded();
					}
				}
			}
		});
	}

	public List<ShareLinkEntity> getShareLinks() {
		return shareLinks;
	}

	public void setListener(OnSharelinksLoadedListener listener) {
		this.listener = listener;
	}
	
	public ShareLinkEntity getSharelinkForFile(FileInfo fileInfo) {
		for (ShareLinkEntity sharelink : shareLinks) {
			if (sharelink.getPath().equals(fileInfo.getFullPath())) {
				return sharelink;
			}
		}
		return null;
	}
}
