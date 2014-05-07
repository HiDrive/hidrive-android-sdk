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

public final class OAuthConst {
	public static final String OAUTH_CALLBACK_HOST = "localhost";
	public static final String GRAND_TYPE = "refresh_token";
	public static final String AUTH_CODE_QUERY_KEY = "code";
	public static final String AUTH_SCOPE_QUERY_KEY = "scope";
	public static final String LOGIN_URL = "https://www.hidrive.strato.com/oauth2/authorize?response_type=code&client_id=%s&lang=%s&scope=%s";
	public static final String OAUTH_TOKEN_URL = "https://www.hidrive.strato.com";
	public static final String MINIMAL_SCOPE = "rw";
	public static final String[] LOGIN_PAGE_SUPPORTED_LANGUAGES = new String[] {"de", "en", "es", "fr", "it", "nl", "pt"};
	public static final String LOGIN_PAGE_DEFAULT_LANGUAGE = "en";
}
