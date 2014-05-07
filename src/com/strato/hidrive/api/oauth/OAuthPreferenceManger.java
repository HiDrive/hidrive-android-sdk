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

import com.strato.hidrive.api.dal.TokenEntity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class OAuthPreferenceManger {
	private static final String ACCESS_TOKEN_KEY = "access_token";
	private static final String USER_NAME_KEY = "username";
	private static final String REFRESH_TOKEN_KEY = "refresh_token";
	private static final String OAUTH_PREFERENCE_NAME = "oauth_preference";

	private final SharedPreferences preference;

	public OAuthPreferenceManger(Context context) {
		this.preference = context.getSharedPreferences(OAUTH_PREFERENCE_NAME, Activity.MODE_PRIVATE);
	}

	public void saveAccessToken(TokenEntity token) {
		this.preference.edit().putString(ACCESS_TOKEN_KEY, token.getAccessToken()).commit();
		this.preference.edit().putString(USER_NAME_KEY, token.getAlias()).commit();
	}

	public void saveRefreshToken(String refreshToken) {
		this.preference.edit().putString(REFRESH_TOKEN_KEY, refreshToken).commit();
	}

	public String getAccessToken() {
		return this.preference.getString(ACCESS_TOKEN_KEY, "");
	}

	public String getRefreshToken() {
		return this.preference.getString(REFRESH_TOKEN_KEY, "");
	}

	public boolean isAuthorize() {
		return this.preference.contains(REFRESH_TOKEN_KEY);
	}
	
	public String getUsername() {
		return this.preference.getString(USER_NAME_KEY, "");
	}

	public void removeTokens() {
		Editor editor = this.preference.edit();
		editor.remove(ACCESS_TOKEN_KEY);
		editor.remove(REFRESH_TOKEN_KEY);
		editor.commit();
	}
}
