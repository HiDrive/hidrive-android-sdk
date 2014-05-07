package com.strato.hidrive.api.bll.oauth;

import java.util.ArrayList;
import java.util.List;

import com.strato.hidrive.api.connection.gateway.SingleResultGateway;
import com.strato.hidrive.api.connection.httpgateway.request.BaseParam;
import com.strato.hidrive.api.connection.httpgateway.request.Param;
import com.strato.hidrive.api.connection.httpgateway.request.PostRequest;
import com.strato.hidrive.api.connection.httpgateway.request.Request;
import com.strato.hidrive.api.dal.TokenEntity;
import com.strato.hidrive.api.interfaces.DataReader;
import com.strato.hidrive.api.oauth.OAuthConst;

public class RefreshTokenGateway extends SingleResultGateway<TokenEntity> {
	private final String refreshToken;
	private final String clientId;
	private final String clientSecret;
	private final String grandType;

	public RefreshTokenGateway(String refreshToken, String clientId, String clientSecret, String grandType) {
		this.refreshToken = refreshToken;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.grandType = grandType;
	}

	@Override
	protected TokenEntity prepareObject(DataReader datareader) {
		return new TokenEntity(datareader);
	}

	@Override
	protected Request prepareRequest() {
		List<BaseParam<?>> params = new ArrayList<BaseParam<?>>();
		params.add(new Param("refresh_token", this.refreshToken));
		params.add(new Param("client_id", this.clientId));
		params.add(new Param("client_secret", this.clientSecret));
		params.add(new Param("grant_type", this.grandType));

		return new PostRequest("oauth2/token", params);
	}
	
	@Override
	protected String getBaseUri() {
		return OAuthConst.OAUTH_TOKEN_URL;
	}
}
