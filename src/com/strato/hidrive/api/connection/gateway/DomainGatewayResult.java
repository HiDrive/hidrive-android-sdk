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

import com.strato.hidrive.api.connection.httpgateway.result.GatewayResult;

/**
 * Defines a result of remote method call containing a corresponding domain object ({@link DomainGatewayResult#getResult()}) or an gateway error ({@link DomainGatewayResult#getGatewayError()}).
 *
 * @param <T> type of requested domain object
 */
public class DomainGatewayResult<T> extends GatewayResult {

	private T result;
	private GatewayError gatewayError;
	
	/**
	 * Returns corresponding domain object
	 * 
	 * @return requested domain object or null
	 */
	public T getResult() {
		return result;
	}
	
	/**
	 * Returns gateway error if any
	 * 
	 * @return gateway error if any
	 */
	public GatewayError getGatewayError() {
		return gatewayError;
	}

	/**
	 * Constructs DomainGatewayResult object with given state
	 */
	public DomainGatewayResult(Exception error, boolean canceled, GatewayError gatewayError, T result) {
		super(error, canceled);
		this.result=result;
		this.gatewayError=gatewayError;
	}
}
