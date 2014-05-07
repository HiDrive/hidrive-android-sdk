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
package com.strato.hidrive.api.oauth;

import android.content.Context;
import android.util.Log;

import com.strato.hidrive.api.bll.oauth.RefreshTokenGateway;
import com.strato.hidrive.api.bll.oauth.RevokeTokenGateway;
import com.strato.hidrive.api.connection.gateway.DomainGatewayResult;
import com.strato.hidrive.api.connection.gateway.interfaces.DomainGatewayHandler;
import com.strato.hidrive.api.dal.TokenEntity;

/**
 *	Class responded for oauth token operations.
 *	You shouldn't need to use this class directly in your app.  Instead, simply configure instance of {@link  com.strato.hidrive.api.session.HiDriveSession  HiDriveSession}  to match your preferences.
 */
public class OAuthTokenManager {
	private String clientId;
	private String clientSecret;

	private OAuthPreferenceManger preferenceManger;

	/**
	 * Constructs OAuthTokenManager with given client id and client secret.
	 * @param context associated android context
	 * @param clientId associated client id
	 * @param clientSecret associated client secret
	 */
	public OAuthTokenManager(Context context, String clientId, String clientSecret) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.preferenceManger = new OAuthPreferenceManger(context);
	}

	/**
	 * Refresh acces token asynchronously
	 * 
	 * @param listener callback to listen operation results
	 */
	public void asyncRefreshToken(OAuthListener listener) {
		getGateway().executeAsync(getResultHandler(listener));
	}

	/**
	 * Refresh acces token synchronously
	 *  
	 * @param listener callback to listen operation results
	 */
	public void refreshToken(final OAuthListener listener) {
		getResultHandler(listener).handleDomainGatewayResult(getGateway().execute());
	}

	private DomainGatewayHandler<TokenEntity> getResultHandler(final OAuthListener listener) {
		return new DomainGatewayHandler<TokenEntity>() {

			@Override
			public void handleDomainGatewayResult(DomainGatewayResult<TokenEntity> result) {
				if (result.getError() != null || result.getGatewayError() != null) {
					String error = result.getError() != null ? result.getError().toString() : result.getGatewayError().getErrorMessage();
					listener.handleError(new Error(error));

					Log.e("Access token updating error. ", error);
				} else {
					TokenEntity token = result.getResult();
					saveAccessToken(token);
					listener.successRefreshed(token);
				}
			}
		};
	}

	private RefreshTokenGateway getGateway() {
		return new RefreshTokenGateway(this.preferenceManger.getRefreshToken(), clientId, clientSecret, OAuthConst.GRAND_TYPE);
	}

	/**
	 * Revoke oauth token asynchronously
	 */
	public void revoke() {
		RevokeTokenGateway gateway = new RevokeTokenGateway(preferenceManger.getRefreshToken());
		gateway.executeAsync(null);
		this.preferenceManger.removeTokens();
	}

	/**
	 * Check is oauth refresh token retrieved and stored so session authorized
	 * @return true if session authorized
	 */
	public boolean isAuthorized() {
		return this.preferenceManger.isAuthorize();
	}

	/**
	 * Save an oauth refresh token
	 * 
	 * @param token token to save
	 */
	public void saveRefreshToken(String token) {
		this.preferenceManger.saveRefreshToken(token);
	}

	/**
	 * Save an oauth access token
	 * 
	 * @param token token to save
	 */
	public void saveAccessToken(TokenEntity token) {
		this.preferenceManger.saveAccessToken(token);
	}

	/**
	 * Get stored oauth access token
	 * 
	 * @return stored access token
	 */
	public String getAccessToken() {
		return this.preferenceManger.getAccessToken();
	}
	
	/**
	 * Get stored user name retrieved from last stored access token
	 * 
	 * @return currently authorized user name
	 */
	public String getUserName() {
		return this.preferenceManger.getUsername();
	}
}
