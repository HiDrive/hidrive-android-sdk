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

public class GetRefreshTokenGateway extends SingleResultGateway<TokenEntity> {
	private static final String GRAND_TYPE_AUTH_CODE = "authorization_code";
	private final String authCode;
	private final String clientId;
	private final String clientSecret;

	public GetRefreshTokenGateway(String authCode, String clientId, String clientSecret) {
		this.authCode = authCode;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}

	@Override
	protected TokenEntity prepareObject(DataReader datareader) {
		return new TokenEntity(datareader);
	}

	@Override
	protected Request prepareRequest() {
		List<BaseParam<?>> params = new ArrayList<BaseParam<?>>();
		params.add(new Param("code", this.authCode));
		params.add(new Param("client_id", this.clientId));
		params.add(new Param("client_secret", this.clientSecret));
		params.add(new Param("grant_type", GRAND_TYPE_AUTH_CODE));

		return new PostRequest("oauth2/token", params);
	}

	@Override
	protected String getBaseUri() {
		return OAuthConst.OAUTH_TOKEN_URL;
	}
}