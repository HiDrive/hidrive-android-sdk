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
package com.strato.hidrive.api.session;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.strato.hidrive.api.connection.gateway.HiDriveGatewaySettings;
import com.strato.hidrive.api.dal.TokenEntity;
import com.strato.hidrive.api.oauth.OAuthTokenManager;
import com.strato.hidrive.api.session.LoginView.LoginViewListener;

/**
 *	HiDrive session incapsulation. Should be used for linking to hidrive services.
 *	Keeps track of a logged in user. 
 *	Contains configuration options for the {@link com.strato.hidrive.api.HiDriveRestClient HiDriveRestClient}.
 */
public class HiDriveSession {
	private HiDriveSessionListener listener;
	private String clientId;
	private String secret;
	private OAuthTokenManager tokenManager = null;
	private Context context;

	/**
	 *	Interface definition for a callback to be invoked after authorization.
	 */
	public interface HiDriveSessionListener {
		/**
		 * Called after user's success authorisation
		 * @param userName user login
		 * @param token oauth token info
		 */
		void onAuthorizationComplete(String userName, TokenEntity token);
		/**
		 * Called on an connection or authorization problem
		 * @param error caused exception
		 */
		void onConnectionProblem(Exception error);
		/**
		 * Called when user discards authorization (e.g. back button pressed)
		 */
		void onAuthorizationCancelled();
	}

	/**
	 * Constructs session object configured for linking
	 * @param context activity context, used for this session. After linking or if session already linked can be used application context.
	 * @param clientId HiDrive application clientId
	 * @param secret HiDrive application secret
	 * @param listener session listener object
	 */
	public HiDriveSession(Context context, String clientId, String secret,
			HiDriveSessionListener listener) {
		super();
		this.context = context;
		this.clientId = clientId;
		this.secret = secret;
		this.listener = listener;
		HiDriveGatewaySettings.getInstance().initialize(context, clientId, secret);
		tokenManager = new OAuthTokenManager(context, clientId, secret);
	}

	/**
	 * Unlinks the session by removing any stored access token.
	 */
	public void unlink() {
		if (isLinked()) {
			tokenManager.revoke();
		}
	};

	
	/**
	 * Returns whether or not this session has a user's access token and secret.
	 */
	public boolean isLinked() {
		return tokenManager.isAuthorized();
	};

	/**
	 * Connecting to HiDrive service using oauth2.0. Will display authorization dialog. 
	 * After successful authorization access token will be stored. 
	 */
	public void link() {
		if (!isInternetReachable()) {
			if (listener != null) {
				listener.onConnectionProblem(new Exception("Internet not reachable"));
			}
			return;
		}
		LoginDialog loginDialog = new LoginDialog(context, clientId,
				secret, loginViewListener, onDismissLIstener);
		loginDialog.show();
	};

	/**
	 * Allows to determine is internet reachable
	 */
	public boolean isInternetReachable() {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	};

	/**
	 * Returns instance of OAuthTokenManager - object containing oauth token information.
	 */
	public OAuthTokenManager getTokenManager() {
		return tokenManager;
	}

	/**
	 * Returns currently linked user name or empty string
	 */
	public String getUserName() {
		return tokenManager.getUserName();
	}

	private OnCancelListener onDismissLIstener = new DialogInterface.OnCancelListener() {
		@Override
		public void onCancel(DialogInterface dialog) {
			if (listener != null) {
				listener.onAuthorizationCancelled();
			}
		}
	};

	private LoginViewListener loginViewListener = new LoginViewListener() {

		@Override
		public void onConnectionProblem(Exception error) {
			if (listener != null) {
				listener.onConnectionProblem(error);
			}
		}

		@Override
		public void onAuthSuccess(String userName, TokenEntity token) {
			if (listener != null) {
				listener.onAuthorizationComplete(userName, token);
			}
		}
	};

	/**
	 * Get context used by this session
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * Set context used by this session
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * Get associated session events listener
	 */
	public HiDriveSessionListener getListener() {
		return listener;
	}

	/**
	 * Set HiDriveSessionListener callback for this session
	 * @param listener callback for listen session events
	 */
	public void setListener(HiDriveSessionListener listener) {
		this.listener = listener;
	}
}
