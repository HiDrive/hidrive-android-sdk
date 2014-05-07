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
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.strato.hidrive.api.JSONDataReader;
import com.strato.hidrive.api.bll.oauth.RefreshTokenGateway;
import com.strato.hidrive.api.connection.httpgateway.HTTPGateway;
import com.strato.hidrive.api.connection.httpgateway.response.Response;
import com.strato.hidrive.api.connection.httpgateway.result.HTTPGatewayResult;
import com.strato.hidrive.api.dal.TokenEntity;
import com.strato.hidrive.api.interfaces.DataReader;
import com.strato.hidrive.api.oauth.OAuthListener;
import com.strato.hidrive.api.oauth.OAuthTokenManager;

public abstract class HiDriveDomainGateway<T> extends DomainGateway<T, String> {
	public static final String HIDRIVE_API_URL = "https://api.hidrive.strato.com/";
	
	public static final int CONNECTION_PROBLEM = 1;
	protected JsonObject jsonObject;
	private OAuthTokenManager tokenManager;

	public interface RefreshTokenErrorGlobalHandler {
		void onError(Throwable error);
	}
	
	public interface ErrorCodesLocalizer {
		String getErrorCodeLocalizedMessage(Context context, int code);
	}

	public HiDriveDomainGateway() {
		super();
		if (!HiDriveGatewaySettings.getInstance().isInitialized()) {
			throw new IllegalStateException("Gateways are not initialized. Initialize HiDriveGatewaySettings singleton first.");
		}
		setAccessToken(getOAuthToken());
	}

	protected String getBaseUri() {
		return HIDRIVE_API_URL + getHiDriveAPIVersionCode();
	}
	
	protected String getHiDriveAPIVersionCode() {
		return "2.1";
	}

	private String getOAuthToken() {
		if (tokenManager == null) {
			tokenManager = new OAuthTokenManager(HiDriveGatewaySettings.getInstance().getAppContext(), 
					HiDriveGatewaySettings.getInstance().getClientId(),
					HiDriveGatewaySettings.getInstance().getClientSecret());
		}
		return tokenManager.getAccessToken();
	}

	@Override
	protected HTTPGateway<String> createHTTPGateway() {
		HTTPGateway<String> httpGateway = super.createHTTPGateway();
		httpGateway.setUserAgentString(HiDriveGatewaySettings.getInstance().getDefaultUserAgentString());
		return httpGateway;
	}

	protected DataReader prepareDataReader(Response<String> response) throws Exception {
		if (response.getResponseData() == null) {
			return null;
		}

		String responseString = unescapeResponseData(response.getResponseData());
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonElement = jsonParser.parse(responseString);
		if (jsonElement.isJsonObject()) {
			return new JSONDataReader((JsonObject) jsonElement);
		}
		return null;
	}

	protected String unescapeResponseData(String responseData) {
		return URLDecoder.decode(responseData);
	}

	protected List<DataReader> prepareDataReaderList(Response<String> response) throws Exception {
		if (response.getResponseData() == null) {
			return new ArrayList<DataReader>();
		}

		String responseString = unescapeResponseData(response.getResponseData());

		JsonParser jsonParser = new JsonParser();
		JsonElement jsonElement = jsonParser.parse(responseString);

		List<DataReader> dataReaders = new ArrayList<DataReader>();
		if (jsonElement.isJsonArray()) {
			for (JsonElement jsonObject : (JsonArray) jsonElement) {
				dataReaders.add(new JSONDataReader((JsonObject) jsonObject));
			}
		} else if (jsonElement.isJsonObject()) {
			dataReaders.add(new JSONDataReader((JsonObject) jsonElement));
		}

		return dataReaders;
	}

	protected GatewayError checkResponseForError(HTTPGatewayResult<String> result) {
		if (result.getError() == null) {
			return null;
		}

		if (result.getError().getMessage() != null) {
			Log.e("Http exception", result.getError().getMessage());
		}

		int resultCode = 0;

		if (result.getError() instanceof HttpResponseException) {
			resultCode = ((HttpResponseException) result.getError()).getStatusCode();

			if (resultCode == HttpStatus.SC_UNAUTHORIZED) {
				result.setCanceled(true);
				refreshSession(getBaseUri(), result.getError());
			}
		} else if (result.getError() instanceof IOException) {
			resultCode = CONNECTION_PROBLEM;
		}

		return new GatewayError(resultCode, this.getLocalizedErrorMessage(HiDriveGatewaySettings.getInstance().getAppContext(), resultCode), result
				.getError().getMessage());
	}

	protected String getLocalizedErrorMessage(Context context, int code) {
		if (HiDriveGatewaySettings.getInstance().getErrorCodesLocalizer() != null) {
			return HiDriveGatewaySettings.getInstance().getErrorCodesLocalizer().getErrorCodeLocalizedMessage(context, code);
		}
		
		return String.format("Communication error. Error code: %d", code);
	}

	protected void refreshSession(String baseUrl, Exception exception) {
		if (this instanceof RefreshTokenGateway) {
			processRefreshTokenError(exception);
			return;
		}
		this.tokenManager.asyncRefreshToken(new OAuthListener() {

			@Override
			public void successRefreshed(TokenEntity accessToken) {
				restart();
			}

			@Override
			public void handleError(Error error) {
				processRefreshTokenError(error);
			}
		});
	}

	protected void restart() {
		setAccessToken(getOAuthToken());
		this.executeAsync(domainGatewayHandler);
	}

	private void processRefreshTokenError(Throwable error) {
		if (HiDriveGatewaySettings.getInstance().getGlobalRefreshTokenErrorHandler() != null) {
			HiDriveGatewaySettings.getInstance().getGlobalRefreshTokenErrorHandler().onError(error);
		}
	}
}
