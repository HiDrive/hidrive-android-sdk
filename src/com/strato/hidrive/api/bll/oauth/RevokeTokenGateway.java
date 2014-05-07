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

public class RevokeTokenGateway extends SingleResultGateway<TokenEntity> {

	private String token;

	public RevokeTokenGateway(String token) {
		this.token = token;
	}

	@Override
	protected TokenEntity prepareObject(DataReader datareader) {
		return null;
	}

	@Override
	protected Request prepareRequest() {
		List<BaseParam<?>> params = new ArrayList<BaseParam<?>>();
		params.add(new Param("token", this.token));
		params.add(new Param("type", "refresh"));

		return new PostRequest("oauth2/revoke", params);
	}

	@Override
	protected String getBaseUri() {
		return OAuthConst.OAUTH_TOKEN_URL;
	}
}
