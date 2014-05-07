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
package com.strato.hidrive.api.connection.httpgateway.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.strato.hidrive.api.connection.httpgateway.interfaces.HttpRequestParamsVisitor;

public class Method {
	private String name;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	private List<BaseParam<?>> params;

	public List<BaseParam<?>> getParams() {
		return params;
	}

	public Method(String name) {
		this(name, new ArrayList<BaseParam<?>>());
	}

	public Method(String name, List<BaseParam<?>> params) {
		this.name = name;
		this.params = params;
	}

	public Method(String name, BaseParam<?>... params) {
		this(name, new ArrayList<BaseParam<?>>(Arrays.asList(params)));
	}

	public void addParam(String name, String value) {
		this.addParam(new Param(name, value));
	}

	public void addParam(BaseParam<?> param) {
		params.add(param);
	}

	public void accept(HttpRequestParamsVisitor<?> visitor) {
		visitor.visit(this);
	}
}
