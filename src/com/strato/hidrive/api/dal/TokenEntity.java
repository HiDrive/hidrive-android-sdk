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

/**
 *	A entity that describes a OAuth refresh or acces token information.
 */
public class TokenEntity {

	private final String accessToken;
	private final String tokenType;
	private final int expiresIn;
	private final String refreshToken;
	private final String scope;
	private final String alias;

	/**
	 * Constructs instance from DataReader
	 * 
	 * @param dataReader DataReader with data about fields values
	 */
	public TokenEntity(DataReader dataReader) {
		if(dataReader == null){
			this.accessToken = "";
			this.tokenType = "";
			this.expiresIn = 0;
			this.refreshToken = "";
			this.scope = "";
			this.alias = "";
			return;
		}
		this.accessToken = dataReader.readStringWithName("access_token");
		this.tokenType = dataReader.readStringWithName("token_type");
		this.expiresIn = dataReader.readIntWithName("expires_in");
		this.refreshToken = dataReader.readStringWithName("refresh_token");
		this.scope = dataReader.readStringWithName("scope");
		this.alias = dataReader.readStringWithName("alias");
	}

	/**
	 * Get string representation of access token
	 * 
	 * @return string representation of access token
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * Get string representation of token type
	 * 
	 * @return string representation of token type
	 */
	public String getTokenType() {
		return tokenType;
	}

	/**
	 * Get time when token will be expired.
	 * 
	 * @return time when token will be expired.
	 */
	public int getExpiresIn() {
		return expiresIn;
	}

	/**
	 * Get string representation of refresh token.
	 * 
	 * @return string representation of refresh token.
	 */
	public String getRefreshToken() {
		return refreshToken;
	}

	/**
	 * Get token scope string representation.
	 * 
	 * @return token scope string representation.
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * Get associated user name alias.
	 * 
	 * @return associated user name alias.
	 */
	public String getAlias() {
		return alias;
	}
}
