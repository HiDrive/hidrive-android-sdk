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

import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.BasicResponseHandler;

import com.strato.hidrive.api.connection.gateway.interfaces.DomainGatewayHandler;
import com.strato.hidrive.api.connection.httpgateway.DefaultHTTPGatewayVisitor;
import com.strato.hidrive.api.connection.httpgateway.HTTPGateway;
import com.strato.hidrive.api.connection.httpgateway.interfaces.HTTPGatewayHandler;
import com.strato.hidrive.api.connection.httpgateway.interfaces.HTTPGatewayVisitor;
import com.strato.hidrive.api.connection.httpgateway.request.Request;
import com.strato.hidrive.api.connection.httpgateway.response.Response;
import com.strato.hidrive.api.connection.httpgateway.result.HTTPGatewayResult;

public abstract class DomainGateway<T, E> implements HTTPGatewayHandler<E> {

	private String accessToken;
	private HTTPGateway<E> httpGateway;

	public DomainGateway() {
	}

	protected DomainGatewayHandler<T> domainGatewayHandler;

	protected HTTPGatewayVisitor createHTTPGatewayVisitor() {
		return new DefaultHTTPGatewayVisitor();
	}

	@SuppressWarnings("unchecked")
	protected ResponseHandler<E> createResponseHandler() {
		return (ResponseHandler<E>) new BasicResponseHandler();
	}

	protected HTTPGateway<E> createHTTPGateway() {
		return new HTTPGateway<E>(accessToken, createHTTPGatewayVisitor());
	}

	public void executeAsync(DomainGatewayHandler<T> domainGatewayHandler) {
		this.domainGatewayHandler = domainGatewayHandler;
		this.httpGateway = createHTTPGateway();
		this.httpGateway.sendRequestAsync(this.getBaseUri(), this.createRequest(), createResponseHandler(), this);
	}
	
	public void cancel(){
		if (this.httpGateway != null){
			this.httpGateway.cancelAsyncRequest();
		}
	}

	public DomainGatewayResult<T> execute() {
		HTTPGateway<E> httpGateway = createHTTPGateway();
		HTTPGatewayResult<E> response = httpGateway.sendRequest(this.getBaseUri(), this.createRequest(), this.createResponseHandler());
		return createDomainGatewayResult(response);
	}

	protected Request createRequest() {
		return addMandatoryParams(this.prepareRequest());
	}

	protected Request addMandatoryParams(Request request) {
		return request;
	}

	protected abstract String getBaseUri();
	
	protected abstract T prepareResult(Response<E> response) throws Exception;

	protected abstract Request prepareRequest();

	protected abstract GatewayError checkResponseForError(HTTPGatewayResult<E> result);

	private void onHandleDomainGatewayResult(DomainGatewayResult<T> domainGatewayResult) {
		if (domainGatewayHandler != null && !domainGatewayResult.isCancelled()) {
			domainGatewayHandler.handleDomainGatewayResult(domainGatewayResult);
		}
	}

	public void callBack(DomainGatewayResult<T> state) {
		this.onHandleDomainGatewayResult(state);
	}

	public void handleHTTPGatewayResult(HTTPGatewayResult<E> result) {
		onHandleDomainGatewayResult(createDomainGatewayResult(result));
	}
	
	public DomainGatewayResult<T> createDomainGatewayResult(HTTPGatewayResult<E> result) {
		GatewayError gatewayError = checkResponseForError(result);
	
		if (result.isCancelled() || result.getError() != null || gatewayError != null) {
			return new DomainGatewayResult<T>(result.getError(), result.isCancelled(), gatewayError, null);
		}
		
		try {
			T preparedResult = this.prepareResult(result.getResponse());
			return new DomainGatewayResult<T>(result.getError(), result.isCancelled(), gatewayError,
					preparedResult);
		} catch (Exception e) {
			e.printStackTrace();
			return new DomainGatewayResult<T>(e, result.isCancelled(), gatewayError, null);
		}
		
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
}
