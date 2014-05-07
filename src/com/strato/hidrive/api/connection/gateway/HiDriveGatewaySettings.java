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

import com.strato.hidrive.api.connection.gateway.HiDriveDomainGateway.ErrorCodesLocalizer;
import com.strato.hidrive.api.connection.gateway.HiDriveDomainGateway.RefreshTokenErrorGlobalHandler;

import android.content.Context;

public class HiDriveGatewaySettings {
	private static HiDriveGatewaySettings instance = null;
	
	private boolean initialized = false;
	private Context appContext;
	private String clientId;
	private String clientSecret;
	private String defaultUserAgentString = "HiDriveAndroidSDKv1.0";
	private RefreshTokenErrorGlobalHandler globalRefreshTokenErrorHandler = null;
	private ErrorCodesLocalizer errorCodesLocalizer = null;

	public static HiDriveGatewaySettings getInstance() {
		if (instance == null) {
			instance = new HiDriveGatewaySettings();
		}
		return instance;
	}

	public HiDriveGatewaySettings() {
		super();
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void initialize(Context appContext, String clientId, String clientSecret) {
		this.appContext = appContext;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.initialized = true;
	}

	public Context getAppContext() {
		return appContext;
	}

	public void setAppContext(Context appContext) {
		this.appContext = appContext;
	}

	public String getDefaultUserAgentString() {
		return defaultUserAgentString;
	}

	public void setDefaultUserAgentString(String defaultUserAgentString) {
		this.defaultUserAgentString = defaultUserAgentString;
	}

	public RefreshTokenErrorGlobalHandler getGlobalRefreshTokenErrorHandler() {
		return globalRefreshTokenErrorHandler;
	}

	public void setGlobalRefreshTokenErrorHandler(
			RefreshTokenErrorGlobalHandler globalRefreshTokenErrorHandler) {
		this.globalRefreshTokenErrorHandler = globalRefreshTokenErrorHandler;
	}

	public ErrorCodesLocalizer getErrorCodesLocalizer() {
		return errorCodesLocalizer;
	}

	public void setErrorCodesLocalizer(ErrorCodesLocalizer errorCodesLocalizer) {
		this.errorCodesLocalizer = errorCodesLocalizer;
	}

	public String getClientId() {
		return clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}
}
